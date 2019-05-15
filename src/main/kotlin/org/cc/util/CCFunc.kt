package org.cc.util

import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function

/**
 *
 * @author william
 */
object CCFunc {

    private var cache: ConcurrentHashMap<String, Any>? = null

    private val prefix = "org.cc.fun"

    private fun cache(): ConcurrentHashMap<String, Any> {
        if (cache == null) {
            cache = ConcurrentHashMap()
        }
        return cache
    }

    private fun classId(id: String?): String? {
        return if (id != null && !id.startsWith(prefix)) {
            "$prefix.$id"
        } else {
            id
        }
    }

    private fun load(classId: String?): Any? {
        try {
            var `fun`: Any? = cache()[classId!!]
            if (`fun` == null) {
                val cls = Class.forName(classId)
                CCLogger.debug("Load function  $classId")
                `fun` = cls.newInstance()
                cache()[classId] = `fun`!!
            }
            return `fun`
        } catch (e: Exception) {
            CCLogger.error("Can't find classId : " + e.message)
            return null
        }

    }

    fun apply(id: String, p: Any): Any? {
        val classId = classId(id)
        val `fun` = load(classId) as Function<*, *>?
        return `fun`?.apply(p)
    }

    fun apply2(id: String, p: Any, u: Any): Any? {
        val classId = classId(id)
        val `fun` = load(classId) as BiFunction<*, *, *>?
        return `fun`?.apply(p, u)
    }

    fun accept(id: String, p: Any) {
        val classId = classId(id)
        val `fun` = load(classId) as Consumer<*>?
        `fun`?.accept(p)
    }

    fun accept2(id: String, p: Any, u: Any) {
        val classId = classId(id)
        val `fun` = load(classId) as BiConsumer<*, *>?
        `fun`?.accept(p, u)
    }

}
