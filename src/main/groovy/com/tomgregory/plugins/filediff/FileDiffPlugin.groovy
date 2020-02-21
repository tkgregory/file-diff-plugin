package com.tomgregory.plugins.filediff

import org.gradle.api.Plugin
import org.gradle.api.Project

class FileDiffPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create('fileDiff', FileDiffExtension)

        project.tasks.create('fileDiff', FileDiffTask) {
            file1 = project.fileDiff.file1
            file2 = project.fileDiff.file2
        }
    }
}
