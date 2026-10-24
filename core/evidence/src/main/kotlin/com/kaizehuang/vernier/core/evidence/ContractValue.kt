package com.kaizehuang.vernier.core.evidence

/**
 * Marks a value type of the SDK contract. Poko generates its `equals`, `hashCode` and `toString` from
 * the primary constructor's properties, as for a data class, but no `copy` or `componentN`: their
 * signatures would change whenever a property is added, breaking hosts compiled against an earlier
 * release. A module that declares such types applies `vernier.contract-values`.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public annotation class ContractValue
