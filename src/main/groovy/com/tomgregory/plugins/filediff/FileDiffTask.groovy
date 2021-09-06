package com.tomgregory.plugins.filediff

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class FileDiffTask extends DefaultTask {
    @InputFile
    abstract RegularFileProperty getFile1()
    @InputFile
    abstract RegularFileProperty getFile2()
    @OutputFile
    abstract RegularFileProperty getResultFile()

    FileDiffTask() {
        resultFile.convention(project.layout.buildDirectory.file('diff-result.txt'))
    }

    @TaskAction
    def diff() {
        String diffResult
        if (size(file1) == size(file2)) {
            diffResult = "Files have the same size at ${file1.get().asFile.size()} bytes."
        } else {
            File largestFile = size(file1) > size(file2) ? file1.get().asFile: file2.get().asFile
            diffResult = "${largestFile.toString()} was the largest file at ${largestFile.size()} bytes."
        }

        resultFile.get().asFile.write diffResult

        println "File written to $resultFile"
        println diffResult
    }

    private static long size(RegularFileProperty regularFileProperty) {
        return regularFileProperty.get().asFile.size()
    }
}
