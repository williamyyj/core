package org.cc.util

import org.cc.CCList
import org.cc.CCMap
import org.cc.ICCMap
import org.cc.ICCList

/**
 *
 * @author william
 */
object CCPath {

    fun path(jo: ICCMap, jopath: String): Any? {
        val path = jopath.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return path(jo, path)
    }

    fun map(jo: ICCMap, jopath: String): ICCMap? {
        val ret = path(jo, jopath)
        return if (ret is ICCMap) {
            ret
        } else null
    }

    fun list(jo: ICCMap, jopath: String): ICCList? {
        val ret = path(jo, jopath)
        if (ret is ICCList) {
            return ret
        } else if (ret is ICCMap) {
            val arr = CCList()
            arr.add(ret)
            return arr
        }
        return null
    }

    private fun opt(m: Any, k: String): Any? {
        if (m is ICCMap) {
            return m[k]
        } else if (m is ICCList) {
            return m[Integer.parseInt(k.trim { it <= ' ' })]
        }
        return null
    }

    private fun path(jo: ICCMap, path: Array<String>): Any? {
        var p: Any? = jo
        for (key in path) {
            p = opt(p, key)
            if (p == null) {
                break
            }
        }
        return p
    }

    operator fun set(target: ICCMap, path: String, o: Any) {
        val items = path.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        set(target, items, 0, o)
    }

    private operator fun set(parent: ICCMap, items: Array<String>, level: Int, o: Any) {
        if (level >= items.size - 1) {
            parent.put(items[level], o)
        } else {
            val key = items[level]
            var p: ICCMap? = null
            if (parent.containsKey(key)) {
                p = parent.map(key)
            } else {
                p = CCMap()
                parent.put(key, p)
            }
            set(p, items, level + 1, o)
        }
    }

    fun setJA(jo: ICCMap, path: String, o: Any) {
        val item = path(jo, path)
        if (item is ICCList) {
            item.add(o)
        } else if (item == null) {
            val ja = CCList()
            set(jo, path, ja)
            ja.add(o)
        }
    }
}
