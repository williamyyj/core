package org.cc.util

import java.util.HashSet
import java.util.function.Consumer
import org.cc.json.JSONArray

/**
 *
 * @author william
 */
object CCTextUtils {

    fun lineToJA(o: Any): JSONArray? {
        if (o is JSONArray) {
            return o as JSONArray
        } else if (o is String) {
            var line = o.trim { it <= ' ' }
            line = if (line[0] == '[') line else "[$line]"
            return JSONArray(line)
        }
        return null
    }

    fun lineToSet(o: Any): Set<String>? {
        val ja = lineToJA(o)
        if (ja != null) {
            val ret = HashSet<String>()
            ja!!.forEach { it -> ret.add(it as String) }
            return ret
        }
        return null
    }

}
