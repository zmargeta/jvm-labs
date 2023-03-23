package io.margeta.jvmlabs.build.version;

import org.gradle.api.Transformer;
import org.gradle.api.provider.Provider;
import org.gradle.api.specs.Spec;

import java.util.function.BiFunction;

import static io.margeta.jvmlabs.build.version.Preconditions.checkNotNull;

final class ExtendedVersionStringProvider implements Provider<SemanticVersion> {
    private final Provider<SemanticVersion> wrapped;

    private ExtendedVersionStringProvider(Provider<SemanticVersion> wrapped) {
        this.wrapped = wrapped;
    }

    public static ExtendedVersionStringProvider wrap(Provider<SemanticVersion> wrapped) {
        checkNotNull(wrapped, "wrapped");
        return new ExtendedVersionStringProvider(wrapped);
    }

    @Override
    public SemanticVersion get() {
        return wrapped.get();
    }

    @Override
    public SemanticVersion getOrNull() {
        return wrapped.getOrNull();
    }

    @Override
    public SemanticVersion getOrElse(SemanticVersion defaultValue) {
        return wrapped.getOrElse(defaultValue);
    }

    @Override
    public <S> Provider<S> map(Transformer<? extends S, ? super SemanticVersion> transformer) {
        return wrapped.map(transformer);
    }

    @Override
    public <S> Provider<S> flatMap(Transformer<? extends Provider<? extends S>, ? super SemanticVersion> transformer) {
        return wrapped.flatMap(transformer);
    }

    @Override
    public boolean isPresent() {
        return wrapped.isPresent();
    }

    @Override
    public Provider<SemanticVersion> orElse(SemanticVersion value) {
        return wrapped.orElse(value);
    }

    public Provider<SemanticVersion> orElse(Provider<? extends SemanticVersion> provider) {
        return wrapped.orElse(provider);
    }

    @Override
    public <U, R> Provider<R> zip(
            Provider<U> right, BiFunction<? super SemanticVersion, ? super U, ? extends R> combiner) {
        return wrapped.zip(right, combiner);
    }

    @Override
    public Provider<SemanticVersion> filter(Spec<? super SemanticVersion> spec) {
        return wrapped.filter(spec);
    }

    @Override
    public String toString() {
        final var value = wrapped.getOrNull();
        return value != null ? value.toExtendedString() : "unspecified";
    }
}
