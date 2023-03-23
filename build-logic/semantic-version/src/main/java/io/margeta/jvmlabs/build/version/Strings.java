package io.margeta.jvmlabs.build.version;

import javax.annotation.Nullable;

public final class Strings {
    private Strings() {
        throw new UnsupportedOperationException("Not meant to be instantiated.");
    }

    public static boolean isBlank(@Nullable String input) {
        return input == null || input.isBlank();
    }

    public static boolean isNotBlank(@Nullable String input) {
        return !isBlank(input);
    }
}
