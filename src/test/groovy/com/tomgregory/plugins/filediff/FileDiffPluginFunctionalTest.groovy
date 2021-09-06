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

    def "can  diff 2 files of same length"() {
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

    def "can  diff 2 files of differing length"() {
        given:
        File testFile1 = testProjectDir.newFile('testFile1.txt')
        testFile1.write('Short text')
        File testFile2 = testProjectDir.newFile('testFile2.txt')
        testFile2.write('Longer text')

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
        result.output.contains('testFile2.txt was the largest file at ' + 'Longer text'.bytes.length + ' bytes')
        result.task(":fileDiff").outcome == SUCCESS
    }
}