package com.tomgregory.plugins.filediff

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class FileDiffPluginFunctionalTest extends Specification {
    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'com.tomgregory.file-diff'
            }
        """
    }

    def "can successfully diff 2 files"() {
        given:
        File testFile1 = testProjectDir.newFile('testFile1.txt')
        File testFile2 = testProjectDir.newFile('testFile2.txt')

        buildFile << """
            fileDiff {
                file1 = file('${testFile1.getName()}')
                file2 = file('${testFile2.getName()}')
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('fileDiff')
                .withPluginClasspath()
                .build()

        then:
        result.output.contains("Files have the same size")
        result.task(":fileDiff").outcome == SUCCESS
    }
}