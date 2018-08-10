package com.asm.test

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * 重新修改class
 */
public class RedefineClass {

    public static void processClass(File file) {
        println "start process class " + file.getPath()
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

    public static class ChangeVisitor extends ClassVisitor {
        private String owner;
        private ActivityAnnotationVisitor FileAnnotationVisitor = null;

        public ChangeVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
            println "ChangeVisitor: created!!!"
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.owner = name;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            println "visitAnnotation: desc=" + desc + " visible=" + visible
            AnnotationVisitor annotationVisitor = super.visitAnnotation(desc, visible);
            if (desc != null) {
                FileAnnotationVisitor = new ActivityAnnotationVisitor(Opcodes.ASM5, annotationVisitor, desc);
                return FileAnnotationVisitor;
            }
            return annotationVisitor;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
//            if (FileAnnotationVisitor != null) {
            return new RedefineAdvice(mv, access, owner, name, desc);
//            }
//            return mv;
        }
    }

    public static class RedefineAdvice extends AdviceAdapter {
        String owner = "";
        ActivityAnnotationVisitor activityAnnotationVisitor = null;
        private HashMap<Label, ArrayList<String>> matchedHandle = new HashMap<>();
        HashSet<String> targetException = new HashMap<String>();
        protected RedefineAdvice(MethodVisitor mv, int access, String className, String name, String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
            owner = name;
            targetException.add("java/lang/Exception")
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handle, String type) {
            println '进入try catch 块!!!'
            //目标exception，在本文中为java/lang/IndexOutOfBoundsException
            println "type --------" + (type == null ? "null" : "nonull，") + type
            println "targetException" + targetException
            if (type != null && targetException.contains(type)) {
                ArrayList<String> handles = matchedHandle.get(handle);
                if(handles == null) handles = new ArrayList<>();
                handles.add(type);
                matchedHandle.put(handle, handles);
            }
            println "matchedHandle  --- >" + matchedHandle
            super.visitTryCatchBlock(start, end, handle, type)

        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            println "visitAnnotation: desc=" + desc + " visible=" + visible
            AnnotationVisitor annotationVisitor = super.visitAnnotation(desc, visible);
            if (desc != null) {
                activityAnnotationVisitor = new ActivityAnnotationVisitor(Opcodes.ASM5, annotationVisitor, desc);
                return activityAnnotationVisitor;
            }
            return annotationVisitor;
        }

        @Override
        protected void onMethodEnter() {
//            if (activityAnnotationVisitor == null) {
//                return;
//            }
            super.onMethodEnter();
            mv.visitLdcInsn(owner)
            mv.visitMethodInsn(INVOKESTATIC, "com/example/printer/LogPrinter",
                    "printBefore", "(Ljava/lang/String;)V", false);
        }

        @Override
        protected void onMethodExit(int opcode) {
//            if (activityAnnotationVisitor == null) {
//                return;
//            }
            super.onMethodExit(opcode);
            mv.visitLdcInsn(owner)
            mv.visitMethodInsn(INVOKESTATIC, "com/example/printer/LogPrinter",
                    "printAfter", "(Ljava/lang/String;)V", false);
        }

        @Override
        void visitLabel(Label label) {
            super.visitLabel(label)

            ArrayList<String> exceptions;

            if(label != null && (exceptions = matchedHandle.get(label)) != null){
                println " matchedHandle.get(label) isnot null  --- >"
                Label matched = new Label();
                Label end = new Label();
                //捕获的是目标exception的实例才进行处理
                final int N = exceptions.size() - 1;
                println " N== " + N
                if (N >= 1) {
                    for (int i = 0; i < N; i++) {
                        compareInstance(IFNE, exceptions.get(i), matched);
                    }
                }
                println  'exception.get(N)' + exceptions.get(N)
//                compareInstance(IFEQ, exceptions.get(N), end);
                visitLabel(matched);
                dup();
                //调用pushException方法
                mv.visitMethodInsn(INVOKESTATIC, "com/example/printer/LogPrinter",
                        "printException", "(Ljava/lang/Exception;)V", false);
                visitLabel(end);

            }
        }

        private void compareInstance(int mode, String type, Label to){
            dup();
//            instanceOf(Type.getObjectType(type));
            visitJumpInsn(mode, to);
        }
    }

    public static class ActivityAnnotationVisitor extends AnnotationVisitor {
        public String desc;
        public String name;
        public String value;

        public ActivityAnnotationVisitor(int api, AnnotationVisitor av, String paramDesc) {
            super(api, av);
            this.desc = paramDesc;
        }

        public void visit(String paramName, Object paramValue) {
            this.name = paramName;
            this.value = paramValue.toString();
            println "visitAnnotation: name=" + name + " value=" + value
        }

    }
}
