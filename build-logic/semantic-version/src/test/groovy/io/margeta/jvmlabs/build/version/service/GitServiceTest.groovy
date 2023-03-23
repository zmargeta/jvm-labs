package io.margeta.jvmlabs.build.version.service

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

import static TreeState.CLEAN
import static TreeState.DIRTY

@Subject(GitService)
@Title("git service")
class GitServiceTest extends Specification {
    @Subject(GitService.ObjectName)
    @Title("git service object name")
    static class ObjectNameTest extends Specification {
        @Subject(GitService.ObjectName)
        @Title("git service object name factory")
        static class FactoryTest extends Specification {
            def "parses the input string as git repository object name"() {
                given: "a git repository object name string representation"
                def input = n

                when: "parsing the input string"
                def actual = GitService.ObjectName.parse(input)

                then: "makes a git repository object name representing the input string"
                with(actual) {
                    tag() == t
                    depth() == d
                    commitId() == c
                    treeState() == s
                }

                where:
                n                            | t           | d    | c         | s
                "1234567"                    | null        | null | "1234567" | CLEAN
                "1234567-dirty"              | null        | null | "1234567" | DIRTY
                "1.0.0-0-g1234567"           | "1.0.0"     | 0    | "1234567" | CLEAN
                "svc-1.0.0-1-g1234567-dirty" | "svc-1.0.0" | 1    | "1234567" | DIRTY
                "svc_1.0.0-1-g1234567-dirty" | "svc_1.0.0" | 1    | "1234567" | DIRTY
                "svc/1.0.0-1-g1234567-dirty" | "svc/1.0.0" | 1    | "1234567" | DIRTY
                "svc@1.0.0-1-g1234567-dirty" | "svc@1.0.0" | 1    | "1234567" | DIRTY
            }

            def "does not parse an invalid input string"() {
                given: "an invalid git repository object name string representation"
                def input = i

                when: "parsing the input string"
                GitService.ObjectName.parse(input)

                then: "throws an exception"
                thrown IllegalArgumentException

                where:
                i                | _
                ""               | _
                "1.0.0"          | _
                "g1234567"       | _
                "1.0.0-0"        | _
                "1.0.0-g1234567" | _
                "g1234567-0"     | _
                "dirty"          | _
            }
        }
    }
}
