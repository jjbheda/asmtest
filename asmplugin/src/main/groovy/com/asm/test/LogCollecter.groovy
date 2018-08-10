package com.asm.test

import org.gradle.api.Plugin
import org.gradle.api.Project

class LogCollecter implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.tasks.all { task ->
            ContextProvider contextProvider
            println 'task ---' + task.name
            if (task.name.startsWith("compileReleaseJavaWithJavac")) {
                 contextProvider = new ContextProvider(project, 'Release')
            } else if (task.name.startsWith("compileDebugJavaWithJavac")) {
                contextProvider = new ContextProvider(project, 'Debug')
            }
            if (contextProvider != null){
                doWhenCompileFirst(project,contextProvider)
            }

        }

    }

    void doWhenCompileFirst(Project project, ContextProvider contextProvider) {
//        String intermediatesPath = Utils.joinPath(project.buildDir.absolutePath, "intermediates")
        contextProvider.compileTask.doLast {
            long time1 = System.currentTimeMillis()
            Collection<File> inputFile = contextProvider.getCompileTaskInputFile()

//            inputFile.each { file ->
//                PrintUtil.info("start scan the input: " + file)
//                if (file.name.endsWith(".class")) {
//                    println "class name " + file
//                }
//
//            }


        }
    }

}