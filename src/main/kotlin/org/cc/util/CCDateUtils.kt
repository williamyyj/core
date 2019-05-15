package org.cc.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 *
 * @author william
 */
object CCDateUtils {

    @JvmOverloads
    fun newInstance(year: Int, month: Int, date: Int, hourOfDay: Int = 0, minute: Int = 0, second: Int = 0): Date {
        val cal = Calendar.getInstance()
        cal.set(year, month, date, hourOfDay, minute, second)
        return cal.time
    }

    fun toDate(fv: Any): Date? {
        if (fv is Date) {
            return fv
        } else if (fv is String) {
            return to_date(fv)
        } else if (fv is Number) {
            return Date(fv.toLong())
        }
        return null
    }

    fun to_date(text: String): Date? {
        try {
            val sfmt = "yyyyMMdd"
            val lfmt = "yyyyMMddHHmmss"
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
            if (text.contains("CST ")) {
                return sdf.parse(text)
            }

            val str = text.replace("[^0-9\\.]+".toRegex(), "")
            val len = str.length
            when (len) {
                7 -> return cdate(str)
                8 -> return to_date(sfmt, str)
                14 -> return to_date(lfmt, str)
                else -> {
                    return if (str.length > 14) {
                        to_date(lfmt, str.substring(0, 14))
                    } else null
                }
            }
        } catch (ex: ParseException) {
            //JOLogger.info("Can't parser Date : "+text);
        }

        return null
    }

    fun cdate(text: Any): Date? {
        try {
            val nf = DecimalFormat("0000000")
            val dv = nf.parse(text.toString()).toInt()
            val year = (dv / 10000 + 1911) * 10000 + dv % 10000
            val sdf = SimpleDateFormat("yyyyMMdd")
            sdf.isLenient = false
            return sdf.parse(year.toString())
        } catch (ex: Exception) {
            //JOLogger.debug("Can't convet cdate : " + text);
            return null
        }

    }

    fun to_date(fmt: String, text: String): Date? {
        try {
            return SimpleDateFormat(fmt).parse(text)
        } catch (ex: ParseException) {
            //JOLogger.info("Can't parser date : " + text);
        }

        return null
    }

    fun add(d: Date, field: Int, amount: Int): Date {
        val c = Calendar.getInstance()
        c.time = d
        c.add(field, amount)
        return c.time
    }

    fun addDate(d: Date, amount: Int): Date {
        return add(d, Calendar.DATE, amount)
    }

}
