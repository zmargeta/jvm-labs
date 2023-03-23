package io.margeta.jvmlabs.build.extensions

import org.gradle.api.JavaVersion
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion

val Any.toImageTag: String
    get() = toString().replace("+", "_")

val Provider<JavaLanguageVersion>.toJavaVersion: Provider<JavaVersion>
    get() = map { JavaVersion.toVersion(it.asInt()) }
