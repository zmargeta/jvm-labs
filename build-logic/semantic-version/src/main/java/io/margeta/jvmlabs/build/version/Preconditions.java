package io.margeta.jvmlabs.build.version;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public final class Preconditions {
    private Preconditions() {
        throw new UnsupportedOperationException("Not meant to be instantiated.");
    }

    public static <T> T checkNotNull(@Nullable T argument, @Nullable String argumentName) {
        if (argument == null) {
            throw nullPointer(argumentName);
        }
        return argument;
    }

    private static RuntimeException nullPointer(@Nullable String argumentName) {
        return new NullPointerException(
                argumentName != null ? "%s must not be null".formatted(argumentName) : "Argument must not be null.");
    }

    public static void checkArgument(boolean predicate, @Nullable String errorMessage) {
        if (!predicate) {
            throw illegalArgument(errorMessage);
        }
    }

    private static RuntimeException illegalArgument(@Nullable String errorMessage) {
        return new IllegalArgumentException(
                errorMessage != null ? "Illegal argument, %s.".formatted(errorMessage) : "Illegal argument.");
    }

    public static void checkState(boolean predicate, @Nullable String errorMessage) {
        if (!predicate) {
            throw illegalState(errorMessage);
        }
    }

    private static RuntimeException illegalState(@Nullable String errorMessage) {
        return new IllegalStateException(
                errorMessage != null ? "Illegal state, %s.".formatted(errorMessage) : "Illegal state.");
    }

    public static String checkMatches(String pattern, String argument) {
        checkNotNull(pattern, "pattern");
        checkNotNull(argument, "argument");
        if (!argument.matches(pattern)) {
            throw illegalArgument("does not match the pattern '%s'".formatted(pattern));
        }
        return argument;
    }

    public static String checkMatches(Pattern pattern, String argument) {
        checkNotNull(pattern, "pattern");
        checkNotNull(argument, "argument");
        if (!pattern.matcher(argument).matches()) {
            throw illegalArgument("does not match the pattern '%s'".formatted(pattern));
        }
        return argument;
    }
}
