package com.tomgregory.plugins.filediff

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class FileDiffPluginFunctionalTest extends Specification {
    @TempDir
    File testProjectDir
    File buildFile

    def setup() {
        buildFile = new File(testProjectDir, 'build.gradle')
        buildFile << """
            plugins {
                id 'com.tomgregory.file-diff'
            }
        """
    }

    def "can  diff 2 files of same length"() {
        given:
        File testFile1 = new File(testProjectDir, 'testFile1.txt')
        testFile1.createNewFile()
        File testFile2 = new File(testProjectDir, 'testFile2.txt')
        testFile2.createNewFile()

        buildFile << """
            fileDiff {
                file1 = file('${testFile1.getName()}')
                file2 = file('${testFile2.getName()}')
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments('fileDiff')
                .withPluginClasspath()
                .build()

        then:
        result.output.contains("Files have the same size")
        result.task(":fileDiff").outcome == SUCCESS
    }

    def "can  diff 2 files of differing length"() {
        given:
        File testFile1 = new File(testProjectDir, 'testFile1.txt')
        testFile1 << 'Short text'
        File testFile2 = new File(testProjectDir, 'testFile2.txt')
        testFile2 << 'Longer text'

        buildFile << """
            fileDiff {
                file1 = file('${testFile1.getName()}')
                file2 = file('${testFile2.getName()}')
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments('fileDiff')
                .withPluginClasspath()
                .build()

        then:
        result.output.contains('testFile2.txt was the largest file at ' + 'Longer text'.bytes.length + ' bytes')
        result.task(":fileDiff").outcome == SUCCESS
    }
}