package com.coldfier.core_utils.di

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment

interface Dependencies

typealias DepsMap = Map<Class<out Dependencies>, @JvmSuppressWildcards Dependencies>

interface HasDependencies {
    val depsMap: DepsMap
}

inline fun <reified D: Dependencies> Fragment.findDependencies(): D {
    return findDependenciesByClassKey(D::class.java)
}

@Deprecated(
    "This function is only used for internal work, use findDependencies() function instead.",
    ReplaceWith("findDependencies()", "com.coldfier.core_utils.di"),
    DeprecationLevel.WARNING
)
@Suppress("UNCHECKED_CAST")
fun <D: Dependencies> Fragment.findDependenciesByClassKey(key: Class<D>): D {
    return parents
        .mapNotNull { it.depsMap[key] }
        .firstOrNull() as D?
        ?: throw IllegalStateException("No dependencies $key in ${allParents.joinToString()}")
}

private val Fragment.parents: Iterable<HasDependencies>
    get() = allParents.mapNotNull { it as? HasDependencies }

private val Fragment.allParents: Iterable<Any>
    get() = object : Iterable<Any> {
        override fun iterator() = object : Iterator<Any> {
            var currentParentFragment: Fragment? = parentFragment
            var parentActivity: Activity? = activity
            var parentApplication: Application? = activity?.application

            override fun hasNext(): Boolean {
                return currentParentFragment != null
                        || parentActivity != null
                        || parentApplication != null
            }

            override fun next(): Any {
                currentParentFragment?.let {
                    currentParentFragment = it.parentFragment
                    return it
                }

                parentActivity?.let {
                    parentActivity = null
                    return it
                }

                parentApplication?.let {
                    parentApplication = null
                    return it
                }

                throw NoSuchElementException()
            }
        }
    }