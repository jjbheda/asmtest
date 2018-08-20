package com.asm.test

import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.gradle.api.DefaultTask
import org.gradle.api.Project

class ContextProvider {
    Project project;
    DefaultTask compileTask
    String varNameCap
    ArrayList<String> packageNameList = new ArrayList()

    ContextProvider(Project project, String varNameCap) {
        this.project = project
        this.varNameCap = varNameCap
        compileTask = project.tasks.findByName("compile${varNameCap}JavaWithJavac")
    }

    Collection<File> getCompileTaskInputFile() {
        Collection<File> inputs = compileTask.outputs.files.files
        //获取到的是Map对象
        File file = new File("injectconifg.json")
        ArrayList<LazyMap> packageNameConfigList = new ArrayList<>()

        if (file.exists()) {
            def jsonSlurper = new JsonSlurper()
            def map = jsonSlurper.parse(file)
            packageNameConfigList = map.getAt("packageconfig")
        }

        for (LazyMap lazyMap : packageNameConfigList) {
            String packageName = lazyMap.getAt("packagename")
            String pg = packageName.replace('.', File.separator)
            println 'packageName----->' + pg
            packageNameList.add(pg)
        }
        inputs.each { dir ->
            recursionDir(dir)
        }
    }

    void recursionDir(File dirFile) {
        dirFile.eachFile { file ->
            if (file.isFile()) {
                String fileName = file.getName()

                if (!fileName.startsWith("R\$") &&
                        !"R.class".equals(fileName) && !"BuildConfig.class".equals(fileName)
                        && file.getAbsolutePath().endsWith(".class")) {
                    println 'file.getParent()-----' + file.getParent()
                    println "FILE: ${file.getCanonicalPath()}"
                    println "file.getAbsolutePath(): ${file.getAbsolutePath()}"

                    for (String packageName : packageNameList) {
                        if (file.getParent().endsWith(packageName)) {
                            println "hack 的class 名字" + file.getName()
                            InjectUtil.processClass(file)
                        }
                    }

                }
            } else if (file.isDirectory()) {
//                 println "DIR:  ${file}"
                recursionDir(file)
            }
        }
    }

}