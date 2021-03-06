package com.asm

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;


 class ExceptionInjectUtil {
     static void processClass(File file) {
        File optClass = new File(file.getParent(), file.getName() + ".opt");
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = new FileOutputStream(optClass);

            byte[] bytes = referHack(inputStream);
            outputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        if (file.exists()) {
            file.delete();
        }
        optClass.renameTo(file);
    }

    private static byte[] referHack(InputStream inputStream) {
        try {
            ClassReader classReader = new ClassReader(inputStream);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            ClassVisitor changeVisitor = new ChangeVisitor(classWriter);
            classReader.accept(changeVisitor, ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

     static class ChangeVisitor extends ClassVisitor {

         ChangeVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
        }

        @Override
         void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
         MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
            return new RedefineAdvice(mv, access, name, desc);

        }
    }

     static class RedefineAdvice extends AdviceAdapter {
        private HashMap<Label, String> matchedHandle = new HashMap<>();
        String owner = ""
        protected RedefineAdvice(MethodVisitor mv, int access, String name, String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
            owner = name
        }

        @Override
         void visitTryCatchBlock(Label start, Label end, Label handle, String type) {

            if (type != null) {
                String exception = matchedHandle.get(handle);

                if(exception == null) {
                    exception = type
                }
                matchedHandle.put(handle, exception);
            }
            super.visitTryCatchBlock(start, end, handle, type)
        }

        @Override
        void visitLabel(Label label) {
            super.visitLabel(label)
            if (label != null && (matchedHandle.get(label)) != null) {
                Label matched = new Label();
                Label end = new Label()
                visitLabel(matched)
                dup();
                //调用pushException方法
                mv.visitMethodInsn(INVOKESTATIC, "com/qiyi/loglibrary/LogStorer",
                        "e", "(Ljava/lang/Throwable;)V", false)
                visitLabel(end)
            }
        }
    }

}
