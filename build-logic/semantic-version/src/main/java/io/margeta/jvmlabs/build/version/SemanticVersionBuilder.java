package io.margeta.jvmlabs.build.version;

import javax.annotation.Nullable;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import static java.time.temporal.WeekFields.ISO;
import static io.margeta.jvmlabs.build.version.Preconditions.checkArgument;
import static io.margeta.jvmlabs.build.version.Preconditions.checkMatches;
import static io.margeta.jvmlabs.build.version.Preconditions.checkNotNull;
import static io.margeta.jvmlabs.build.version.Strings.isNotBlank;

final class SemanticVersionBuilder {
    private static final int DEFAULT_MAJOR = 0;
    private static final int DEFAULT_MINOR = 1;
    private static final int DEFAULT_PATCH = 0;
    private static final String DEFAULT_PRE_RELEASE = "SNAPSHOT";
    private static final String DEFAULT_METADATA = "";
    private static final String MUST_BE_GTE_ZERO = "must be greater than or equal to zero";
    private static final String MUST_BE_AFTER_1999_12_31 = "must be after 1999-12-31";
    private static final Pattern semVerPattern = Pattern.compile("^[0-9a-zA-Z-.]+$");
    private static final Pattern calVerPattern = Pattern.compile("^(?:YYYY|[0Y]Y|[0M]M|[0W]W|[0D]D)$");

    private int major = DEFAULT_MAJOR;
    private int minor = DEFAULT_MINOR;
    private int patch = DEFAULT_PATCH;
    private @Nullable String preRelease = DEFAULT_PRE_RELEASE;
    private @Nullable String metadata = DEFAULT_METADATA;
    private LocalDate date;
    private @Nullable String majorFormat;
    private @Nullable String minorFormat;
    private @Nullable String patchFormat;

    public SemanticVersionBuilder() {
        date = LocalDate.ofInstant(Clock.systemUTC().instant(), ZoneId.of("Z"));
    }

    public SemanticVersionBuilder major(int major) {
        checkArgument(major >= 0, MUST_BE_GTE_ZERO);
        this.major = major;
        return this;
    }

    public SemanticVersionBuilder minor(int minor) {
        checkArgument(minor >= 0, MUST_BE_GTE_ZERO);
        this.minor = minor;
        return this;
    }

    public SemanticVersionBuilder patch(int patch) {
        checkArgument(patch >= 0, MUST_BE_GTE_ZERO);
        this.patch = patch;
        return this;
    }

    public SemanticVersionBuilder preRelease(String preRelease) {
        checkMatches(semVerPattern, preRelease);
        this.preRelease = preRelease;
        return this;
    }

    public SemanticVersionBuilder metadata(String metadata) {
        checkMatches(semVerPattern, metadata);
        this.metadata = metadata;
        return this;
    }

    public SemanticVersionBuilder date(LocalDate date) {
        checkNotNull(date, "date");
        checkArgument(date.getYear() >= 2000, MUST_BE_AFTER_1999_12_31);
        this.date = date;
        return this;
    }

    public SemanticVersionBuilder majorFormat(String majorFormat) {
        checkMatches(calVerPattern, majorFormat);
        this.majorFormat = majorFormat;
        return this;
    }

    public SemanticVersionBuilder minorFormat(String minorFormat) {
        checkMatches(calVerPattern, minorFormat);
        this.minorFormat = minorFormat;
        return this;
    }

    public SemanticVersionBuilder patchFormat(String patchFormat) {
        checkMatches(calVerPattern, patchFormat);
        this.patchFormat = patchFormat;
        return this;
    }

    private String major() {
        return isNotBlank(majorFormat) ? calendarVersion(majorFormat) : String.valueOf(major);
    }

    private String calendarVersion(String format) {
        return switch (format) {
            case "YYYY" -> Integer.toString(date.getYear());
            case "YY" -> Integer.toString(date.getYear() - 2000);
            case "0Y" -> {
                var year = Integer.toString(date.getYear() - 2000);
                yield year.length() == 1 ? '0' + year : year;
            }
            case "MM" -> Integer.toString(date.getMonthValue());
            case "0M" -> {
                var month = Integer.toString(date.getMonthValue());
                yield month.length() == 1 ? '0' + month : month;
            }
            case "WW" -> Integer.toString(date.get(ISO.weekOfWeekBasedYear()));
            case "0W" -> {
                var week = Integer.toString(date.get(ISO.weekOfWeekBasedYear()));
                yield week.length() == 1 ? '0' + week : week;
            }
            case "DD" -> Integer.toString(date.getDayOfMonth());
            case "0D" -> {
                var day = Integer.toString(date.getDayOfMonth());
                yield day.length() == 1 ? '0' + day : day;
            }
            default -> throw new UnsupportedOperationException("Unsupported format '%s'.".formatted(format));
        };
    }

    private String minor() {
        return isNotBlank(minorFormat) ? calendarVersion(minorFormat) : String.valueOf(minor);
    }

    private String patch() {
        return isNotBlank(patchFormat) ? calendarVersion(patchFormat) : String.valueOf(patch);
    }

    private String preRelease() {
        return isNotBlank(preRelease) ? preRelease : DEFAULT_PRE_RELEASE;
    }

    @Nullable
    private String metadata() {
        return isNotBlank(metadata) ? metadata : DEFAULT_METADATA;
    }

    public SemanticVersion build() {
        return new SemanticVersion(major(), minor(), patch(), preRelease(), metadata());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof SemanticVersionBuilder that
                && major == that.major
                && minor == that.minor
                && patch == that.patch
                && Objects.equals(preRelease, that.preRelease)
                && Objects.equals(metadata, that.metadata)
                && Objects.equals(date, that.date)
                && Objects.equals(majorFormat, that.majorFormat)
                && Objects.equals(minorFormat, that.minorFormat)
                && Objects.equals(patchFormat, that.patchFormat);
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        result = 31 * result + (preRelease != null ? preRelease.hashCode() : 0);
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (majorFormat != null ? majorFormat.hashCode() : 0);
        result = 31 * result + (minorFormat != null ? minorFormat.hashCode() : 0);
        result = 31 * result + (patchFormat != null ? patchFormat.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SemanticVersionBuilder.class.getSimpleName() + "(", ")")
                .add("major=" + major)
                .add("minor=" + minor)
                .add("patch=" + patch)
                .add("preRelease='" + preRelease + "'")
                .add("metadata='" + metadata + "'")
                .add("date=" + date)
                .add("majorFormat='" + majorFormat + "'")
                .add("minorFormat='" + minorFormat + "'")
                .add("patchFormat='" + patchFormat + "'")
                .toString();
    }
}
