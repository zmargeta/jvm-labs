package io.margeta.jvmlabs.build.version;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.regex.Pattern;

import static io.margeta.jvmlabs.build.version.Preconditions.checkNotNull;
import static io.margeta.jvmlabs.build.version.Strings.isNotBlank;

public final class SemanticVersion {
    private static final Pattern parsePattern =
            Pattern.compile("^(\\d{1,4})\\.(\\d{1,2})\\.(\\d{1,2})(?:-([0-9a-zA-Z-.]+)(?:\\+([0-9a-zA-Z-.]+))?)?$");

    private final String major;
    private final String minor;
    private final String patch;
    private final @Nullable String preRelease;
    private final @Nullable String metadata;

    SemanticVersion(String major, String minor, String patch, @Nullable String preRelease, @Nullable String metadata) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.metadata = metadata;
    }

    public static SemanticVersionBuilder makeBuilder() {
        return new SemanticVersionBuilder();
    }

    public static SemanticVersion parse(String input) {
        checkNotNull(input, "input");
        final var matcher = parsePattern.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Illegal argument, not a valid semantic version representation.");
        }
        return new SemanticVersion(
                matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public String getPatch() {
        return patch;
    }

    @Nullable
    public String getPreRelease() {
        return preRelease;
    }

    @Nullable
    public String getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof SemanticVersion that
                && Objects.equals(major, that.major)
                && Objects.equals(minor, that.minor)
                && Objects.equals(patch, that.patch)
                && Objects.equals(preRelease, that.preRelease)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        int result = major.hashCode();
        result = 31 * result + minor.hashCode();
        result = 31 * result + patch.hashCode();
        result = 31 * result + (preRelease != null ? preRelease.hashCode() : 0);
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return format(false);
    }

    private String format(boolean includeExtensions) {
        final var version = new StringBuilder();
        version.append(major).append('.').append(minor).append('.').append(patch);
        if (!includeExtensions) {
            return version.toString();
        }
        if (isNotBlank(preRelease)) {
            version.append('-').append(preRelease);
        }
        if (isNotBlank(metadata)) {
            version.append('+').append(metadata);
        }
        return version.toString();
    }

    public String toExtendedString() {
        return format(true);
    }
}
