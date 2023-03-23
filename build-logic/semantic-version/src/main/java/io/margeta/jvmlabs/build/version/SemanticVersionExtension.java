package io.margeta.jvmlabs.build.version;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

import java.time.LocalDate;

public interface SemanticVersionExtension {
    Property<Integer> getMajor();

    Property<Integer> getMinor();

    Property<Integer> getPatch();

    Property<String> getPreRelease();

    Property<String> getMetadata();

    Property<LocalDate> getDate();

    Property<String> getMajorFormat();

    Property<String> getMinorFormat();

    Property<String> getPatchFormat();

    Property<String> getTagPattern();

    @Nested
    VersionInfo getVersionInfo();

    default void versionInfo(Action<? super VersionInfo> action) {
        action.execute(getVersionInfo());
    }

    interface VersionInfo {
        Property<String> getFileName();

        Property<String> getFormatter();
    }
}
