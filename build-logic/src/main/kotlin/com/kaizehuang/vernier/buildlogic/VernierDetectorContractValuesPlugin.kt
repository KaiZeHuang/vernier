package com.kaizehuang.vernier.buildlogic

import dev.drewhamilton.poko.gradle.PokoPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Lets a contract module declare value types with `@ContractValue`. Poko generates their `equals`,
 * `hashCode` and `toString` as for a data class, but no `copy` or `componentN`, so adding a property
 * does not change a signature that SDK hosts compiled against.
 */
class VernierContractValuesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("dev.drewhamilton.poko")
            extensions.configure<PokoPluginExtension> {
                pokoAnnotation.set("com/kaizehuang/vernier/core/evidence/ContractValue")
            }
        }
    }
}
