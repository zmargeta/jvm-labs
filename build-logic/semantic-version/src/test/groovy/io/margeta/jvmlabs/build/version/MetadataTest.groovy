package io.margeta.jvmlabs.build.version

import io.margeta.jvmlabs.build.version.service.TreeState
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

@Subject(Metadata)
@Title(" metadata")
class MetadataTest extends Specification {
    def "converts to string representation"() {
        given: "metadata"
        def sut = Metadata.make(b, c, s)

        when: "converting to string representation"
        def actual = sut.toString()

        then: "converts to string representation"
        actual == m

        where:
        b    | c                  | s               | m
        null | null               | TreeState.CLEAN | ""
        0    | null               | TreeState.CLEAN | "0"
        null | "1234567890abcdef" | TreeState.CLEAN | "1234567890abcdef"
        null | "1234567890abcdef" | TreeState.CLEAN | "1234567890abcdef"
        null | null               | TreeState.DIRTY | "dirty"
        0    | null               | TreeState.DIRTY | "0.dirty"
        null | "1234567890abcdef" | TreeState.DIRTY | "1234567890abcdef.dirty"
        null | "1234567890abcdef" | TreeState.DIRTY | "1234567890abcdef.dirty"
        0    | "1234567890abcdef" | TreeState.DIRTY | "0.1234567890abcdef.dirty"
    }

    @Subject(Metadata)
    @Title("metadata factory")
    static class FactoryTest extends Specification {
        def "makes metadata"() {
            given: "a build number"
            def buildNumber = b

            and: "a commit id"
            def commitId = c

            and: "a tree state"
            def treeState = s

            when: "making metadata"
            def actual = Metadata.make(b, c, s)

            then: "makes metadata"
            with(actual) {
                buildNumber == b
                commitId == c
                treeState == s
            }

            where:
            b    | c                  | s
            null | null               | TreeState.CLEAN
            0    | null               | TreeState.CLEAN
            1    | null               | TreeState.DIRTY
            null | "1234567890abcdef" | TreeState.CLEAN
            0    | "1234567890abcdef" | TreeState.DIRTY
        }

        def "does not allow making metadata with negative build number"() {
            given: "a negative build number"
            def buildNumber = -1

            when: "making metadata"
            Metadata.make(buildNumber, null, TreeState.CLEAN)

            then: "throws exception"
            thrown IllegalArgumentException
        }

        def "does not allow making metadata with invalid commit id"() {
            given: "an invalid commit id"
            def commitId = i

            when: "making metadata"
            Metadata.make(null, commitId, TreeState.CLEAN)

            then: "throws exception"
            thrown IllegalArgumentException

            where:
            i                                           | _
            "g"                                         | _
            "-"                                         | _
            "+"                                         | _
            "012345"                                    | _
            "01234567890123456789012345678901234567890" | _
        }

        def "parses the input string as metadata"() {
            given: "a metadata string representation"
            def input = m

            when: "parsing the input string"
            def actual = Metadata.parse(input)

            then: "makes a metadata representing the input string"
            with(actual) {
                buildNumber == x
                commitId == y
                treeState == z
            }

            where:
            m                                            | x | y                                          | z
            "0.0123456.dirty"                            | 0 | "0123456"                                  | TreeState.DIRTY
            "0.0123456789abcdef"                         | 0 | "0123456789abcdef"                         | TreeState.CLEAN
            "0.0123456789ABCDEF"                         | 0 | "0123456789abcdef"                         | TreeState.CLEAN
            "0.0123456789012345678901234567890123456789" | 0 | "0123456789012345678901234567890123456789" | TreeState.CLEAN
        }

        def "does not parse an invalid input string"() {
            given: "an invalid metadata string representation"
            def input = i

            when: "parsing the input string"
            Metadata.parse(input)

            then: "throws an exception"
            thrown IllegalArgumentException

            where:
            i                                             | _
            "0"                                           | _
            "a"                                           | _
            "a.0"                                         | _
            "a.a"                                         | _
            "0.g"                                         | _
            "0.-"                                         | _
            "0.+"                                         | _
            "2147483648.a"                                | _
            "0.012345"                                    | _
            "0.01234567890123456789012345678901234567890" | _
        }
    }
}
