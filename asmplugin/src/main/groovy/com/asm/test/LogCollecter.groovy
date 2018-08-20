package com.asm.test

import org.gradle.api.Plugin
import org.gradle.api.Project

class LogCollecter implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.tasks.all { task ->
            TaskHooker contextProvider
            println 'task ---' + task.name

            if (task.name.startsWith("compileReleaseJavaWithJavac")) {
                 contextProvider = new TaskHooker(project, 'Release')
            } else if (task.name.startsWith("compileDebugJavaWithJavac")) {
                contextProvider = new TaskHooker(project, 'Debug')
            }
            if (contextProvider != null) {
                contextProvider.compileTask.doLast {
                    contextProvider.taskHook()
                }
            }
        }
    }

}