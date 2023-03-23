package io.margeta.jvmlabs.build.version;

import org.gradle.api.Project;
import org.gradle.api.provider.Provider;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static io.margeta.jvmlabs.build.version.Preconditions.checkNotNull;

final class SemanticVersionProviderBuilder {
    private final Project project;
    private @Nullable Supplier<Provider<Integer>> major;
    private @Nullable Supplier<Provider<Integer>> minor;
    private @Nullable Supplier<Provider<Integer>> patch;
    private @Nullable Supplier<Provider<String>> preRelease;
    private @Nullable Supplier<Provider<String>> metadata;
    private @Nullable Supplier<Provider<LocalDate>> date;
    private @Nullable Supplier<Provider<String>> majorFormat;
    private @Nullable Supplier<Provider<String>> minorFormat;
    private @Nullable Supplier<Provider<String>> patchFormat;

    public SemanticVersionProviderBuilder(Project project) {
        checkNotNull(project, "project");
        this.project = project;
    }

    public SemanticVersionProviderBuilder major(Supplier<Provider<Integer>> major) {
        checkNotNull(major, "major");
        this.major = major;
        return this;
    }

    public SemanticVersionProviderBuilder minor(Supplier<Provider<Integer>> minor) {
        checkNotNull(minor, "minor");
        this.minor = minor;
        return this;
    }

    public SemanticVersionProviderBuilder patch(Supplier<Provider<Integer>> patch) {
        checkNotNull(patch, "patch");
        this.patch = patch;
        return this;
    }

    public SemanticVersionProviderBuilder preRelease(Supplier<Provider<String>> preRelease) {
        checkNotNull(preRelease, "preRelease");
        this.preRelease = preRelease;
        return this;
    }

    public SemanticVersionProviderBuilder metadata(Supplier<Provider<String>> metadata) {
        checkNotNull(metadata, "metadata");
        this.metadata = metadata;
        return this;
    }

    public SemanticVersionProviderBuilder date(Supplier<Provider<LocalDate>> date) {
        checkNotNull(date, "date");
        this.date = date;
        return this;
    }

    public SemanticVersionProviderBuilder majorFormat(Supplier<Provider<String>> majorFormat) {
        checkNotNull(majorFormat, "majorFormat");
        this.majorFormat = majorFormat;
        return this;
    }

    public SemanticVersionProviderBuilder minorFormat(Supplier<Provider<String>> minorFormat) {
        checkNotNull(minorFormat, "minorFormat");
        this.minorFormat = minorFormat;
        return this;
    }

    public SemanticVersionProviderBuilder patchFormat(Supplier<Provider<String>> patchFormat) {
        checkNotNull(patchFormat, "patchFormat");
        this.patchFormat = patchFormat;
        return this;
    }

    public Provider<SemanticVersion> build() {
        var provider = project.getProviders().provider(SemanticVersionBuilder::new);
        provider = apply(provider, major, SemanticVersionBuilder::major);
        provider = apply(provider, minor, SemanticVersionBuilder::minor);
        provider = apply(provider, patch, SemanticVersionBuilder::patch);
        provider = apply(provider, preRelease, SemanticVersionBuilder::preRelease);
        provider = apply(provider, metadata, SemanticVersionBuilder::metadata);
        provider = apply(provider, date, SemanticVersionBuilder::date);
        provider = apply(provider, majorFormat, SemanticVersionBuilder::majorFormat);
        provider = apply(provider, minorFormat, SemanticVersionBuilder::minorFormat);
        provider = apply(provider, patchFormat, SemanticVersionBuilder::patchFormat);
        return provider.map(SemanticVersionBuilder::build);
    }

    private <T> Provider<SemanticVersionBuilder> apply(
            Provider<SemanticVersionBuilder> p,
            @Nullable Supplier<Provider<T>> s,
            BiFunction<SemanticVersionBuilder, T, SemanticVersionBuilder> m) {
        return s != null ? p.flatMap(b -> s.get().map(v -> m.apply(b, v)).orElse(b)) : p;
    }
}
