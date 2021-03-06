package io.kotless.plugin.gradle.tasks

import io.kotless.DSLType
import io.kotless.parser.LocalParser
import io.kotless.plugin.gradle.dsl.KotlessDSL
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.utils._local
import io.kotless.plugin.gradle.utils.myKtSourceSet
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.dependencies
import java.io.File

/**
 * KotlessLocal task runs Kotless application locally via Ktor
 *
 * @see kotless
 *
 * Note: Task is cacheable and will regenerate code only if sources or configuration has changed.
 */
@CacheableTask
open class KotlessLocal : DefaultTask() {
    init {
        group = "kotless"
    }

    @get:Input
    val myKotless: KotlessDSL
        get() = project.kotless

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val allSources: Set<File>
        get() = project.myKtSourceSet.toSet()

    @TaskAction
    fun generate() = with(myKotless.webapp.project(project)) {
        require(myKotless.config.dsl.type == DSLType.Ktor) { "Local runs are supported only for Ktor." }

        val local = LocalParser.parse(myKtSourceSet, configurations.getByName(kotless.config.configurationName).files.toSet())

        val version = configurations.getByName(myKotless.config.configurationName).allDependencies.find { it.group == "io.kotless" && it.name == "ktor-lang" }?.version

        require(version != null) { "Cannot find ktor-lang library in dependencies. It is required for ktor-lang" }


        dependencies {
            _local("io.kotless", "ktor-lang-local", version)
        }

        (tasks.getByName("run") as JavaExec).apply {
            classpath += files(_local().files)

            this.environment["KTOR_PORT"] = myKotless.extensions.local.port
            this.environment["CLASS_TO_START"] = local.entrypoint.qualifiedName.substringBefore("::")
        }
    }
}
