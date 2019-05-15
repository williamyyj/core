package org.cc.util

import java.io.File
import java.io.Serializable
import org.cc.data.CCData

open class CCBuffer : Serializable, CharSequence {

    protected var data: CharArray
    protected var enc = "UTF-8"
    protected var ps = 0
    protected var line = 1
    protected var pos = 1
    protected var tab = 0
    protected var src: File

    val isWhiteSpace: Boolean
        get() {
            var c: Char = 0.toChar()
            return ps < data.size && (c =
                data[ps]).toInt() == 9 || c.toInt() == 10 || c.toInt() == 13 || c.toInt() == 32
        }

    constructor(data: CharArray) {
        this.data = data
    }

    constructor(text: String) {
        data = text.toCharArray()
    }

    constructor(f: File, enc: String) {
        try {
            data = CCData.loadClob(f, enc)
            src = f
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun length(): Int {
        return data.size
    }

    override fun charAt(index: Int): Char {
        return data[index]
    }

    override fun subSequence(start: Int, end: Int): CharSequence {
        val size = end - start
        val buf = CharArray(size)
        System.arraycopy(data, start, buf, 0, size)
        return CCBuffer(buf)
    }

    fun subString(start: Int, end: Int): String {
        return String(data, start, end - start)
    }

    fun subString(start: Int): String {
        return String(data, start, data.size - start)
    }

    fun m(c: Char): Boolean {
        return data[ps] == c
    }

    fun tk_init() {
        ps = -1
        pos = 0
        line = 1
        tab = 0
    }

    fun m(idx: Int, c: Char): Boolean {
        return ps + idx < data.size && c == data[ps + idx]
    }

    fun m(text: String): Boolean {
        return m(ps, text)
    }

    fun mi(text: String): Boolean {
        return mi(ps, text)
    }

    fun m(idx: Int, text: String): Boolean {
        var idx = idx
        val buf = text.toCharArray()
        for (c in buf) {
            if (idx >= data.size || c != data[idx++]) {
                return false
            }
        }
        return true
    }

    fun mi(idx: Int, text: String): Boolean {
        var idx = idx
        val buf = text.toCharArray()
        for (c in buf) {
            if (idx >= data.size) {
                return false
            }
            val a = Character.toLowerCase(c)
            val b = Character.toLowerCase(data[idx++])
            if (a != b) {
                return false
            }
        }
        return true
    }

    fun `in`(idx: Int, text: String): Boolean {
        return text.indexOf(data[ps + idx].toInt()) >= 0
    }

    fun has(): Boolean {
        return ps + 1 < data.size
    }

    protected fun _next(): Char {
        ps++
        return if (data.size > ps) data[ps] else 0
    }

    operator fun next(): Char {
        var c = _next()
        pos++
        if (c.toInt() == 9) {
            tab++
        }
        if (c.toInt() == 10 || c.toInt() == 13) {
            line++
            pos = 0
            tab = 0
            if (c.toInt() == 13 && ps + 1 < data.size && data[ps + 1].toInt() == 10) {
                ps++

            }
            c = 10.toChar()
        }

        return c
    }

    protected fun move(offset: Int) {
        ps += offset
        if (ps < data.size) {
            val ch = data[ps]
            if (ch.toInt() == 10 || ch.toInt() == 13) {
                //   linux  , os x
                line++
                pos = 1
                tab = 0
                ps++
                if (ch.toInt() == 13 && ps + 1 < data.size && data[ps + 1].toInt() == 10) {
                    //  m$
                    ps++
                }
            }
        }
    }

    fun tk_string(quote: Char): String {
        val sb = StringBuilder()
        var ch: Char = 0.toChar()
        while ((ch = next()).toInt() != 0 && !m(quote)) {
            if (ch == '\\') {
                ch = next()
                when (ch) {
                    'b' -> sb.append('\b')
                    't' -> sb.append('\t')
                    'n' -> sb.append('\n')
                    'f' -> sb.append('\f')
                    'r' -> sb.append('\r')
                    'u' -> {
                        sb.append(Integer.parseInt(subString(ps + 1, ps + 5), 16).toChar())
                        ps += 4
                        pos += 4
                    }
                    '"', '\'', '\\' -> sb.append(ch)
                }
            } else {
                sb.append(ch)
            }
        }
        //    m(quote)  check error
        next() // skip quote
        return sb.toString()
    }

    fun tk_text(pattern: String): String {
        val start = ps
        while (!m(pattern)) {
            next()
        }
        val text = subString(start, ps)
        if (m(pattern)) {
            ps += pattern.length
        }
        return text
    }

    fun tk_csp() {
        while (isWhiteSpace) {
            next()
        }
    }

    fun tk_m(str: String): Boolean {
        if (m(str)) {
            move(str.length)
            return true
        }
        return false
    }

    fun ch(): Char {
        return if (data.size > ps) data[ps] else 0
    }

    override fun toString(): String {
        return String.format("[line:%s,pos:%s,ps:%s]:%s", line, pos, ps, ch())
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val text = "<p>@{}</p>"
            val cbuf = text.toCharArray()
            val buf = CCBuffer(cbuf)
            val line = buf.tk_text("@{")

            println(line)
        }
    }

}
