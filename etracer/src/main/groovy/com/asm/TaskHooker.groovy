package com.asm

import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.gradle.api.DefaultTask
import org.gradle.api.Project

class TaskHooker {
    Project project;
    DefaultTask compileTask
    String varNameCap
    ArrayList<String> packageNameList = new ArrayList()

    TaskHooker(Project project, String varNameCap) {
        this.project = project
        this.varNameCap = varNameCap
        compileTask = project.tasks.findByName("compile${varNameCap}JavaWithJavac")
    }

    Collection<File> taskHook() {
        Collection<File> inputs = compileTask.outputs.files.files
        //获取到的是Map对象
        File file = new File("config/etracer/etracerconf.json")
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
        print "scan packageNameList start-------------------------=----------"
        println packageNameList
        println "scan packageNameList end--------------------------------------"
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
                    println  "路径" + file.getAbsolutePath()

                    for (String packageName : packageNameList) {

                        if (file.getAbsolutePath().contains(packageName)) {
                            println "hack 的class 名字" + file.getName()
                            ExceptionInjectUtil.processClass(file)
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