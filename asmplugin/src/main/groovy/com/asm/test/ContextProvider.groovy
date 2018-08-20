package com.asm.test


import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.objectweb.asm.*

import static org.objectweb.asm.Opcodes.INVOKESTATIC

class ContextProvider {
    Project project;
    DefaultTask compileTask
    String varNameCap

    public ContextProvider(Project project, String varNameCap) {
        this.project = project
        boolean isHighVersion = isHighVersion()
        this.varNameCap = varNameCap
        compileTask = project.tasks.findByName("compile${varNameCap}JavaWithJavac")
    }

    boolean isHighVersion() {
        boolean isHighVersion = true
        try {
            Class clazz = com.android.build.gradle.tasks.Dex
            isHighVersion = false
        } catch (Throwable throwable) {
        }
        return isHighVersion
    }

    Collection<File> getCompileTaskInputFile() {
        Collection<File> inputs = compileTask.outputs.files.files
        inputs.each { dir ->
            println 'dir' + dir
            recursionDir(dir)
        }
    }

    void recursionDir(File dirFile) {
        dirFile.eachFile { file ->
            if (file.isFile()) {
                if (!file.getName().startsWith("R\$") &&
                        !"R.class".equals(file.getName()) && !"BuildConfig.class".equals(file.getName()) &&
                        file.getAbsolutePath().contains("com/example/jiangjingbo/asmtest/") &&
                        file.getAbsolutePath().endsWith(".class")) {
                    println "FILE: ${file.getCanonicalPath()}"
                    processClass(file)
//                    RedefineClass.processClass(file)
                }
            } else if (file.isDirectory()) {
//                 println "DIR:  ${file}"
                recursionDir(file)
            } else {
                println "Uh, I'm not sure what it is..."
            }
        }
    }

    void processClass(File classFile) {
        File tempFile = new File(classFile.parentFile, classFile.name + "_bak")
        InputStream originIns = classFile.newInputStream()
        byte[] bytes = Utils.toByteArray(originIns)
        originIns.close()
        bytes = hackMethod(bytes)
//        if (thinRProcessor.needKeepEntry(classFile.absolutePath)) {
            OutputStream outputStream = tempFile.newOutputStream()
            outputStream.write(bytes, 0, bytes.length)
            outputStream.flush()
            outputStream.close()
            Utils.delFile(classFile)
            Utils.renameFile(tempFile, classFile)
//        }
//        else {
//            Utils.delFile(classFile)
//        }
    }

    public static byte[] hackMethod(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes)
        ClassWriter cw = new ClassWriter(cr, 0)

        String className = ''
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
            @Override
            void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                className = name
                super.visit(version, access, name, signature, superName, interfaces)
            }

            @Override
            MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//                return super.visitMethod(access, name, desc, signature, exceptions)
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
                def methondName = name

                mv = new MethodVisitor(Opcodes.ASM4, mv) {

                    @Override
                    void visitMethodInsn(int opcode, String owner, String md_name, String md_desc, boolean itf) {
                        super.visitMethodInsn(opcode, owner, md_name, md_desc, itf)
                    }

                    @Override
                    void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                        println  '-------------------》: visitTryCatchBlock'
                        super.visitTryCatchBlock(start, end, handler, type)
                    }

                    @Override
                    void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
                        println  '-------------------》: visitTableSwitchInsn'

                        super.visitTableSwitchInsn(min, max, dflt, labels)
                    }

                    @Override
                    void visitInsn(int opcode) {
                        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN && methondName.contains("onCreate")) {
                            println(methondName + ': visit Insn' )

                            //add time collector end
                            visitLdcInsn(methondName)
                            visitMethodInsn(INVOKESTATIC, "com/example/printer/LogPrinter",
                                    "printAfter", "(Ljava/lang/String;)V", false);
                        }

                        super.visitInsn(opcode)
                    }

                    @Override
                    void visitLabel(Label label) {
                        super.visitLabel(label)
                    }

                    @Override
                    void visitCode() {
                        visitLdcInsn(methondName)
                        visitMethodInsn(INVOKESTATIC, "com/example/printer/LogPrinter",
                                "printBefore", "(Ljava/lang/String;)V", false);
                        super.visitCode()
                    }

                    @Override
                    void visitInvokeDynamicInsn(String name_dynamic, String desc_dynamic, Handle bsm_dynamic, Object... bsmArgs) {
                        println 'name_dynamic --- ' + name_dynamic
                        println 'desc_dynamic --- ' + desc_dynamic
                        println  'bsmArgs --- ' + bsmArgs
                        super.visitInvokeDynamicInsn(name_dynamic, desc_dynamic, bsm_dynamic, bsmArgs)
                    }

                    @Override
                    void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
                        println '》》》》》》》》》》》》》》》》》》》visitLookupSwitchInsn --- '
                        println keys
                        println labels
                        println "dflt" +  dflt
                        super.visitLookupSwitchInsn(dflt, keys, labels)
                    }

                }
                return mv
            }
        }
        cr.accept(cv, 0);
        return cw.toByteArray()

    }

}