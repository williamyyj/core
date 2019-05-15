package org.cc.util

import java.io.File
import org.cc.CCList
import org.cc.CCMap
import org.cc.ICCList
import org.cc.ICCMap

class CCJsonParser : CCBuffer {

    constructor(f: File, enc: String) : super(f, enc) {}

    constructor(text: String) : super(text) {}

    fun parser_obj(): ICCMap? {
        try {
            return cc_obj()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun parser_list(): ICCList? {

        try {
            return cc_list()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    protected fun tk_m(c: Char): Boolean {
        if (data.size > ps && data[ps] == c) {
            move(1)
            return true
        }
        return false
    }

    @Throws(Exception::class)
    fun cc_obj(): ICCMap {
        if (!tk_m('{')) {
            throw error("Parser  exception '{' ")
        }
        val xo = CCMap()
        while (true) {
            tk_csp()
            if (tk_m('}')) {
                return xo
            }
            val key = cc_next(idPattern)!!.toString()
            tk_csp()
            if (!tk_m(':')) {
                throw error("Parser  expected ':' ")
            }
            tk_csp()
            xo.put(key, cc_next(valuePattern))
            tk_csp()
            if (tk_m('}')) {
                return xo
            }
            if (!tk_m(',')) {
                throw error("Parser  expected ( ',' | '}' ) ")
            }
        }
    }

    @Throws(Exception::class)
    protected fun cc_list(): ICCList {
        if (!tk_m('[')) {
            throw error("ICCList expected '['")
        }
        val xa = CCList()
        while (true) {
            tk_csp()
            if (tk_m(']')) {
                return xa
            }
            xa.add(cc_next(valuePattern))
            tk_csp()
            if (tk_m(']')) {
                return xa
            }
            if (!tk_m(',')) {
                throw error("ICCList expected  (','|']') ")
            }
        }
    }

    @Throws(Exception::class)
    fun cc_next(pattern: String): Any? {
        if (m('\'') || m('"')) {
            return cc_string(data[ps])
        } else if (m('{')) {
            return cc_obj()
        } else if (m('[')) {
            return cc_list()
        }
        return cc_value(cc_word(pattern))
    }

    fun cc_word(pattern: String): String {
        val start = ps
        // 合理換行是區原素
        while (data[ps].toInt() >= 32 && !`in`(0, pattern)) {
            next()
        }
        return subString(start, ps).trim { it <= ' ' }
    }

    fun cc_value(s: String): Any? {
        if (s == "") {
            return s
        }
        if (s.equals("true", ignoreCase = true)) {
            return true
        }
        if (s.equals("false", ignoreCase = true)) {
            return false
        }
        if (s.equals("null", ignoreCase = true)) {
            return null
        }

        val b = s[0]
        if (b >= '0' && b <= '9' || b == '.' || b == '-' || b == '+') {
            if (b == '0' && s.length > 2
                && (s[1] == 'x' || s[1] == 'X')
            ) {
                try {
                    return Integer.parseInt(s.substring(2), 16)
                } catch (ignore: Exception) {
                }

            }
            try {
                if (s.indexOf('.') > -1 || s.indexOf('e') > -1
                    || s.indexOf('E') > -1
                ) {
                    return java.lang.Double.parseDouble(s)
                } else {
                    val myLong = Long(s)
                    return if (myLong == myLong.toInt().toLong()) {
                        myLong.toInt()
                    } else {
                        myLong
                    }
                }
            } catch (ignore: Exception) {
                //ignore.printStackTrace();
            }

        }
        return s
    }

    fun cc_string(quote: Char): String {
        return tk_string(quote)
    }

    fun error(error: String): Exception {
        val fmt = "Error (%s)%s in line:%s pos:%s "
        val message = String.format(fmt, src, error, line, pos)
        CCLogger.debug(message)
        return Exception(message)
    }

    companion object {

        private val serialVersionUID = 8384119638487566762L

        val QUOT = '"'
        /**
         * The pattern *
         */
        val idPattern = " :()[]{}\\\"'"
        val valuePattern = ",)]}>"
        val wordPattern = " ,)]}>"
        val opPattern = " ,)]}$"
        val tk_lstr_start = "$\""  // $".....""..."
    }

}
