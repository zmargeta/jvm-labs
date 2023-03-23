package jvmlabs.build.version

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

import static java.time.LocalDate.of

@Subject(SemanticVersion)
@Title("semantic version")
class SemanticVersionTest extends Specification {
    def "converts to extended string representation"() {
        given: "a semantic version"
        def sut = makeVersion(x, y, z, p, m)

        when: "converting to extended string representation"
        def actual = sut.toExtendedString()

        then: "converts to the extended string representation"
        actual == version

        where:
        x | y | z | p       | m       | version
        1 | 2 | 3 | null    | null    | "1.2.3-SNAPSHOT"
        1 | 2 | 3 | "0.aA-" | null    | "1.2.3-0.aA-"
        1 | 2 | 3 | "0.aA-" | "0.bB-" | "1.2.3-0.aA-+0.bB-"
    }

    def makeVersion(Integer x, Integer y, Integer z, String p = null, String m = null) {
        def version = SemanticVersion.makeBuilder().major(x).minor(y).patch(z)
        if (p != null) version.preRelease(p)
        if (m != null) version.metadata(m)
        version.build()
    }

    def "converts to string representation"() {
        given: "a semantic version"
        def sut = makeVersion(x, y, z, p, m)

        when: "converting to string representation"
        def actual = sut.toString()

        then: "converts to the string representation"
        actual == imageTag

        where:
        x | y | z | p       | m       | imageTag
        1 | 2 | 3 | null    | null    | "1.2.3"
        1 | 2 | 3 | "0.aA-" | null    | "1.2.3"
        1 | 2 | 3 | "0.aA-" | "0.bB-" | "1.2.3"
    }

    @Subject(SemanticVersion)
    @Title("semantic version factory")
    static class FactoryTest extends Specification {
        def "parses the input string as strict semantic version"() {
            given: "a strict semantic version string representation"
            def input = version

            when: "parsing the input string"
            def actual = SemanticVersion.parse(input)

            then: "makes a semantic version representing the input string"
            with(actual) {
                major == x
                minor == y
                patch == z
                preRelease == p
                metadata == m
            }

            where:
            version             | x   | y   | z   | p       | m
            "1.2.3"             | "1" | "2" | "3" | null    | null
            "1.2.3-0.aA-"       | "1" | "2" | "3" | "0.aA-" | null
            "1.2.3-0.aA-+0.bB-" | "1" | "2" | "3" | "0.aA-" | "0.bB-"
        }

        def "parses the input string as extended semantic version"() {
            given: "an extended semantic version string representation"
            def input = version

            when: "parsing the input string"
            def actual = SemanticVersion.parse(input)

            then: "makes an extended semantic version representing the input string"
            with(actual) {
                major == x
                minor == y
                patch == z
                preRelease == p
                metadata == m
            }

            where:
            version                | x    | y    | z    | p       | m
            "01.02.03"             | "01" | "02" | "03" | null    | null
            "01.02.03-0.aA-"       | "01" | "02" | "03" | "0.aA-" | null
            "01.02.03-0.aA-+0.bB-" | "01" | "02" | "03" | "0.aA-" | "0.bB-"
        }

        def "does not parse an invalid input string"() {
            given: "an invalid semantic version string representation"
            def input = invalid

            when: "parsing the input string"
            SemanticVersion.parse(input)

            then: "throws an exception"
            thrown IllegalArgumentException

            where:
            invalid     | _
            "1"         | _
            "1.1"       | _
            "1.1.a"     | _
            "1.a.1"     | _
            "a.1.1"     | _
            "1.1.1-"    | _
            "1.1.1-_"   | _
            "1.1.1-1+"  | _
            "1.1.1-1+_" | _
        }
    }

    @Subject(SemanticVersionBuilder)
    @Title("semantic version builder")
    static class BuilderTest extends Specification {
        def sut = SemanticVersion.makeBuilder()

        def "builds an initial semantic version"() {
            when: "building a semantic version"
            def actual = sut.build()

            then: "builds an initial semantic version"
            with(actual) {
                major == "0"
                minor == "1"
                patch == "0"
                preRelease == "SNAPSHOT"
                metadata == ""
            }
        }

        def "builds a semantic version with a major version"() {
            given: "a major version"
            sut.major(input)

            when: "building a semantic version"
            def actual = sut.build()

            then: "builds a semantic version with the given major version"
            with(actual) {
                major == majorVersion
                minor == "1"
                patch == "0"
                preRelease == "SNAPSHOT"
                metadata == ""
            }

            where:
            input | majorVersion
            0     | "0"
            1     | "1"
        }

        def "builds a semantic version with a minor version"() {
            given: "a minor version"
            sut.minor(input)

            when: "building a semantic version"
            def actual = sut.build()

            then: "builds a semantic version with the given minor version"
            with(actual) {
                major == "0"
                minor == minorVersion
                patch == "0"
                preRelease == "SNAPSHOT"
                metadata == ""
            }

            where:
            input | minorVersion
            0     | "0"
            1     | "1"
        }

        def "builds a semantic version with a patch version"() {
            given: "a patch version"
            sut.patch(input)

            when: "building a semantic version"
            def actual = sut.build()

            then: "builds a semantic version with the given patch version"
            with(actual) {
                major == "0"
                minor == "1"
                patch == patchVersion
                preRelease == "SNAPSHOT"
                metadata == ""
            }

            where:
            input | patchVersion
            0     | "0"
            1     | "1"
        }

        def "builds a semantic version with a pre-release version"() {
            given: "a pre-release version"
            sut.preRelease(input)

            when: "building a semantic version"
            def actual = sut.build()

            then: "builds a semantic version with the given pre-release version"
            with(actual) {
                major == "0"
                minor == "1"
                patch == "0"
                preRelease == preReleaseVersion
                metadata == ""
            }

            where:
            input | preReleaseVersion
            "0"   | "0"
            "a"   | "a"
            "A"   | "A"
            "-"   | "-"
        }

        def "builds a semantic version with build metadata"() {
            given: "build metadata version"
            sut.metadata(input)

            when: "building a semantic version"
            def actual = sut.build()

            then: "builds a semantic version with the given build metadata"
            with(actual) {
                major == "0"
                minor == "1"
                patch == "0"
                preRelease == "SNAPSHOT"
                metadata == buildMetadata
            }

            where:
            input | buildMetadata
            "0"   | "0"
            "a"   | "a"
            "A"   | "A"
            "-"   | "-"
            "."   | "."
        }

        def "builds a semantic version with a formatted major version"() {
            given: "a format of the major version"
            sut.majorFormat(input)

            and: "a date"
            sut.date(of(2001, 2, 3))

            when: "building a semantic version"
            def actual = sut.build()

            then: "builds a semantic version with the major version formatted according to the given format"
            with(actual) {
                major == majorVersion
                minor == "1"
                patch == "0"
                preRelease == "SNAPSHOT"
                metadata == ""
            }

            where:
            input  | majorVersion
            "YYYY" | "2001"
            "YY"   | "1"
            "0Y"   | "01"
            "MM"   | "2"
            "0M"   | "02"
            "WW"   | "5"
            "0W"   | "05"
            "DD"   | "3"
            "0D"   | "03"
        }

        def "builds a semantic version with a formatted minor version"() {
            given: "a format of the minor version"
            sut.minorFormat(input)

            and: "a date"
            sut.date(of(2001, 2, 3))

            when: "building a semantic version"
            def actual = sut.build()

            then: "builds a semantic version with the minor version formatted according to the given format"
            with(actual) {
                major == "0"
                minor == minorVersion
                patch == "0"
                preRelease == "SNAPSHOT"
                metadata == ""
            }

            where:
            input  | minorVersion
            "YYYY" | "2001"
            "YY"   | "1"
            "0Y"   | "01"
            "MM"   | "2"
            "0M"   | "02"
            "WW"   | "5"
            "0W"   | "05"
            "DD"   | "3"
            "0D"   | "03"
        }

        def "builds a semantic version with a formatted patch version"() {
            given: "a format of the patch version"
            sut.patchFormat(input)

            and: "a date"
            sut.date(of(2001, 2, 3))

            when: "building a semantic version"
            def actual = sut.build()

            then: "builds a semantic version with the patch version formatted according to the given format"
            with(actual) {
                major == "0"
                minor == "1"
                patch == patchVersion
                preRelease == "SNAPSHOT"
                metadata == ""
            }

            where:
            input  | patchVersion
            "YYYY" | "2001"
            "YY"   | "1"
            "0Y"   | "01"
            "MM"   | "2"
            "0M"   | "02"
            "WW"   | "5"
            "0W"   | "05"
            "DD"   | "3"
            "0D"   | "03"
        }

        def "does not allow building a negative major version"() {
            given: "a negative integer"
            def input = -1

            when: "setting the major version"
            sut.major(input)

            then: "throws an exception"
            thrown IllegalArgumentException
        }

        def "does not allow building a negative minor version"() {
            given: "a negative integer"
            def input = -1

            when: "setting the minor version"
            sut.minor(input)

            then: "throws an exception"
            thrown IllegalArgumentException
        }

        def "does not allow building a negative patch version"() {
            given: "a negative patch version"
            def input = -1

            when: "setting the patch version"
            sut.patch(input)

            then: "throws an exception"
            thrown IllegalArgumentException
        }

        def "does not allow building an invalid pre-release version"() {
            given: "an invalid string"
            def input = invalid

            when: "setting the pre-release version"
            sut.preRelease(input)

            then: "throws an exception"
            thrown IllegalArgumentException

            where:
            invalid | _
            "("     | _
            ")"     | _
            "*"     | _
            "+"     | _
            ","     | _
            "/"     | _
            ":"     | _
            ";"     | _
            "?"     | _
            "@"     | _
            "["     | _
            "\\"    | _
            "]"     | _
            "{"     | _
            "|"     | _
            "}"     | _
        }

        def "does not allow building invalid build metadata"() {
            given: "an invalid build metadata"
            def input = invalid

            when: "setting the build metadata"
            sut.metadata(input)

            then: "throws an exception"
            thrown IllegalArgumentException

            where:
            invalid | _
            "("     | _
            ")"     | _
            "*"     | _
            "+"     | _
            ","     | _
            "/"     | _
            ":"     | _
            ";"     | _
            "?"     | _
            "@"     | _
            "["     | _
            "\\"    | _
            "]"     | _
            "{"     | _
            "|"     | _
            "}"     | _
        }
    }
}
