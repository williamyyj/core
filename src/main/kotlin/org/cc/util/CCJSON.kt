package org.cc.util

import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.Comparator
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import org.cc.CCMap
import org.cc.ICCList
import org.cc.ICCMap

/**
 * @author william
 */
object CCJSON {

    private var cache: ConcurrentHashMap<String, CCCacheItem<*>>? = null

    @JvmOverloads
    fun toString(m: MutableMap<*, *>, indentFactor: Int = 0): String {
        val w = StringWriter()
        synchronized(w.buffer) {
            try {
                toString(m, w, indentFactor, 0)
                return w.toString()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

        }
        return ""
    }

    @JvmOverloads
    fun toString(data: List<*>, indentFactor: Int = 0): String {
        val w = StringWriter()
        synchronized(w.buffer) {
            try {
                toString(data, w, indentFactor, 0)
                return w.toString()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

        }
        return ""
    }

    @Throws(IOException::class)
    private fun toString(m: MutableMap<*, *>?, writer: Writer, indentFactor: Int, indent: Int) {
        var indentFactor = indentFactor
        var indent = indent
        if (m == null) {
            return
        }
        val isIndent = !CCCast._bool(m["__indent__"], true)

        if (isIndent) {
            m.remove("__indent__")
            indentFactor = 0
            indent = 0
        }

        var commanate = false
        val length = m.size
        val keys = m.keys.iterator()

        val list = ArrayList<String>(m.keys)
        Collections.sort(list, Comparator<String> { o1, o2 -> if ("id" == o1 || "id" == o2) -1 else o1.compareTo(o2) })

        writer.write('{')

        if (length == 1) {
            val key = keys.next()
            writer.write(quote(key.toString()))
            writer.write(':')
            if (indentFactor > 0) {
                writer.write(' ')
            }
            print(writer, m[key], indentFactor, indent)
        } else if (length > 1) {

            val newindent = indent + indentFactor
            //while (keys.hasNext()) {
            //  Object key = keys.next();
            for (key in list) {
                if (commanate) {
                    writer.write(',')
                }
                if (indentFactor > 0) {
                    writer.write('\n')
                }
                indent(writer, newindent)
                writer.write(quote(key.toString()))
                writer.write(':')
                if (indentFactor > 0) {
                    writer.write(' ')
                }
                print(writer, m[key], indentFactor, newindent)
                commanate = true
            }
            if (indentFactor > 0) {
                writer.write('\n')
            }
            indent(writer, indent)
        }
        writer.write('}')
        if (isIndent) {
            m["__indent__"] = false
        }

    }


    @Throws(IOException::class)
    private fun toString(list: List<*>, writer: Writer, indentFactor: Int, indent: Int) {
        var commanate = false
        val length = list.size
        writer.write('[')

        if (length == 1) {
            print(writer, list[0], indentFactor, indent)
        } else if (length != 0) {
            val newindent = indent + indentFactor

            var i = 0
            while (i < length) {
                if (commanate) {
                    writer.write(',')
                }
                if (indentFactor > 0) {
                    writer.write('\n')
                }
                indent(writer, newindent)
                print(writer, list[i], indentFactor, newindent)
                commanate = true
                i += 1
            }
            if (indentFactor > 0) {
                writer.write('\n')
            }
            indent(writer, indent)
        }
        writer.write(']')
    }

    fun quote(string: String): String {
        val sw = StringWriter()
        synchronized(sw.buffer) {
            try {
                return quote(string, sw).toString()
            } catch (ignored: IOException) {
                // will never happen - we are writing to a string writer
                return ""
            }

        }
    }

    @Throws(IOException::class)
    fun quote(string: String?, w: Writer): Writer {
        if (string == null || string.length == 0) {
            w.write("\"\"")
            return w
        }

        var b: Char
        var c: Char = 0.toChar()
        var hhhh: String
        var i: Int
        val len = string.length

        w.write('"')
        i = 0
        while (i < len) {
            b = c
            c = string[i]
            when (c) {
                '\\', '"' -> {
                    w.write('\\')
                    w.write(c.toInt())
                }
                '/' -> {
                    if (b == '<') {
                        w.write('\\')
                    }
                    w.write(c.toInt())
                }
                '\b' -> w.write("\\b")
                '\t' -> w.write("\\t")
                '\n' -> w.write("\\n")
                '\f' -> w.write("\\f")
                '\r' -> w.write("\\r")
                else -> if (c < ' ' || c >= '\u0080' && c < '\u00a0'
                    || c >= '\u2000' && c < '\u2100'
                ) {
                    w.write("\\u")
                    hhhh = Integer.toHexString(c.toInt())
                    w.write("0000", 0, 4 - hhhh.length)
                    w.write(hhhh)
                } else {
                    w.write(c.toInt())
                }
            }
            i += 1
        }
        w.write('"')
        return w
    }

    @Throws(IOException::class)
    private fun print(writer: Writer, value: Any?, indentFactor: Int, indent: Int) {
        if (value == null) {
            writer.write("null")
        } else if (value is Map<*, *>) {
            toString(value as Map<*, *>?, writer, indentFactor, indent)
        } else if (value is List<*>) {
            toString((value as List<*>?)!!, writer, indentFactor, indent)
        } else if (value is Collection<*>) {
            toString(ArrayList((value as Collection<Any>?)!!), writer, indentFactor, indent)
        } else if (value.javaClass.isArray) {
            toString(Arrays.asList(value), writer, indentFactor, indent)
        } else if (value is Number) {
            writer.write(numberToString((value as Number?)!!))
        } else if (value is Boolean) {
            writer.write(value.toString())
        } else if (value is Date) {
            val sdf = SimpleDateFormat("yyyyMMddHHmmss")
            quote(sdf.format(value as Date?), writer)
        } else {
            quote(value.toString(), writer)
        }
    }

    @Throws(IOException::class)
    internal fun indent(writer: Writer, indent: Int) {
        var i = 0
        while (i < indent) {
            writer.write(' ')
            i += 1
        }
    }

    private fun numberToString(number: Number): String {
        var string = number.toString()
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
            && string.indexOf('E') < 0
        ) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length - 1)
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length - 1)
            }
        }
        return string
    }

    fun cache(): ConcurrentHashMap<*, *> {
        if (cache == null) {
            cache = ConcurrentHashMap()
        }
        return cache
    }

    fun load(base: String, id: String, suffix: String): ICCMap? {
        var id = id
        id = id.replace(".", "/")
        return load(File(base, "$id.$suffix"), "UTF-8")
    }

    fun load(base: String, fid: String): ICCMap? {
        return load(base, fid, "json")
    }

    fun load(f: File?, enc: String): ICCMap? {
        println(f)
        if (f == null || !f.exists()) {
            return null
        }
        try {
            val id = f.canonicalPath
            val item = cache()[id] as CCCacheItem<ICCMap>
            if (item != null && item.lastModified >= f.lastModified()) {
                CCLogger.debug("using cache " + item.id + " lastload : " + item.lastModified)
                return item.value
            } else if (item != null) {
                return reload(f, enc)
            } else if (item == null && f.exists()) {
                return reload(f, enc)
            }
            return null
        } catch (e: IOException) {
            return null
        }

    }

    @Synchronized
    private fun reload(f: File, enc: String): ICCMap? {
        val item = CCCacheItem<ICCMap>()
        try {
            item.id = f.canonicalPath
            item.value = CCJsonParser(f, enc).parser_obj()
            item.lastModified = System.currentTimeMillis()
            cache()[item.id!!] = item
            return item.value
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    fun loadString(text: String): ICCMap? {
        return CCJsonParser(text).parser_obj()
    }

    fun loadJA(text: String): ICCList? {
        return CCJsonParser(text).parser_list()
    }

    fun line(line: Any): ICCMap? {
        if (line is ICCMap) {
            return line
        } else if (line is String) {
            var text = line.trim { it <= ' ' }
            text = if (text[0] == '{') text else "{$text}"
            return loadString(text)
        }
        return null
    }

    fun mix(p: ICCMap, c: ICCMap?): ICCMap {
        val ret = CCMap(p)
        c?.forEach { k, v -> ret.put(k, v) }
        return ret
    }

    fun data(p: ICCMap, c: ICCMap): ICCMap {
        val ret = CCMap()
        p.forEach { k, v ->
            if (k[0] != '$') {
                ret.put(k, v)
            }
        }
        c.forEach { k, v -> ret.put(k, v) }
        return ret
    }


    fun data(p: ICCMap, cid: String): ICCMap {

        return if (p.containsKey(cid)) {
            data(p, p.map(cid)!!)
        } else p
    }

    operator fun get(ja: ICCList?, name: String, value: String): ICCMap? {
        if (ja != null) {
            for (i in ja.indices) {
                val row = ja.map(i)
                if (row != null) {
                    if (value.equals(row.asString("name"), ignoreCase = true)) {
                        return row
                    }
                }
            }
        }
        return null
    }

    fun mix(p: ICCMap, m: ICCMap, items: Array<String>) {
        for (item in items) {
            if (m.containsKey(item)) {
                p.put(item, m[item])
            }
        }
    }

}
