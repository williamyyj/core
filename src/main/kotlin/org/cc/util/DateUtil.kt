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
object DateUtil {

    fun to_date(fmt: String, text: String): Date? {
        try {
            return SimpleDateFormat(fmt).parse(text)
        } catch (ex: ParseException) {
            CCLogger.info("Can't parser date : $text")
        }

        return null
    }

    fun to_date(o: Any): Date? {
        if (o is Date) {
            return o
        } else if (o is String) {
            val text = o.toString().trim { it <= ' ' }
            val sfmt = "yyyyMMdd"
            val lfmt = "yyyyMMddHHmmss"
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
            if (text.contains("CST ")) {
                try {
                    return sdf.parse(text)
                } catch (ex: ParseException) {
                    ex.printStackTrace()
                }

            }

            val str = text.replace("[^0-9\\.]+".toRegex(), "")
            val len = str.length
            when (len) {
                7 -> return cdate(str)
                8 -> return to_date(sfmt, str)
                14 -> return to_date(lfmt, str)
            }
        }
        return null

    }

    fun add_date(src: Date, field: Int, value: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = src
        cal.add(field, value)
        return cal.time
    }

    fun range(ts: Date?, te: Date?, curr: Date?): Boolean {
        if (ts == null || te == null || curr == null) {
            return false
        }
        val t1 = ts.time
        val t2 = te.time
        val t = curr.time
        return t1 < t && t < t2
    }

    fun diff(a: Date, b: Date): Long {
        return (a.time - b.time) / 1000
    }

    fun diff(b: Any): Long {
        return diff(Date(), b as Date)
    }

    fun op_gte(a: Date?, b: Date?): Boolean {
        if (a == null || b == null) {
            return false
        }
        val t1 = a.time
        val t2 = b.time
        return t1 <= t2
    }

    fun op_lt(a: Date?, b: Date?): Boolean {
        if (a == null || b == null) {
            return false
        }
        val t1 = a.time
        val t2 = b.time
        return t1 < t2
    }

    fun op_gt(a: Date?, b: Date?): Boolean {
        if (a == null || b == null) {
            return false
        }
        val t1 = a.time
        val t2 = b.time
        return t1 > t2
    }

    fun yesterday_min(): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    fun yesterday_max(): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal.time
    }

    fun tomorrow(): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    fun date_min(d: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = d
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun date_max(d: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = d
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal.time
    }

    fun date_next(d: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = d
        cal.add(Calendar.DATE, 1)
        return cal.time
    }

    @Throws(Exception::class)
    fun date_next(fmt: String, text: String): String {
        val sdf = SimpleDateFormat(fmt)
        val d = sdf.parse(text)
        val cal = Calendar.getInstance()
        cal.time = d
        cal.add(Calendar.DATE, 1)
        return sdf.format(cal.time)
    }

    fun year(d: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = d
        return cal.get(Calendar.YEAR)
    }

    fun month(d: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = d
        return cal.get(Calendar.MONTH) + 1
    }

    fun date(d: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = d
        return cal.get(Calendar.DATE)
    }

    fun lastDayOfMonth(d: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = d
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal.time
    }

    fun firstDayOfMonth(d: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = d
        cal.set(Calendar.DATE, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    fun getFirstDayOfSeason(offset: Int): Date {
        return getFirstDayOfSeason(Date(), offset)
    }

    fun getFirstDayOfSeason(current: Date, offset: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = current
        cal.add(Calendar.MONTH, offset * 3)
        val month = cal.get(Calendar.MONTH)
        var mm = 0
        when (month) {
            0, 1, 2 -> mm = 0
            3, 4, 5 -> mm = 3
            6, 7, 8 -> mm = 6
            9, 10, 11 -> mm = 9
        }
        cal.set(Calendar.MONTH, mm)
        cal.set(Calendar.DATE, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    fun getFirstDayOfMonth2(offset: Int): Date {
        return getFirstDayOfSeason(Date(), offset)
    }

    fun getFirstDayOfMonth2(current: Date, offset: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = current
        cal.add(Calendar.MONTH, offset * 2)
        val month = cal.get(Calendar.MONTH)
        var mm = 0
        when (month) {
            0, 1 -> mm = 0
            2, 3 -> mm = 2
            4, 5 -> mm = 4
            6, 7 -> mm = 6
            8, 9 -> mm = 8
            10, 11 -> mm = 10
        }
        cal.set(Calendar.MONTH, mm)
        cal.set(Calendar.DATE, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    fun nextMonth(d: Date, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = d
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1)
        cal.set(Calendar.DATE, day)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    fun cdate(text: Any): Date? {
        try {
            val nf = DecimalFormat("0000000")
            val dv = nf.parse(text.toString()).toInt()
            val year = (dv / 10000 + 1911) * 10000 + dv % 10000
            val sdf = SimpleDateFormat("yyyyMMdd")
            sdf.isLenient = false
            return sdf.parse(year.toString())
        } catch (ex: ParseException) {
            CCLogger.debug("Can't convet cdate : $text")
            return null
        }

    }

    @JvmOverloads
    fun newInstance(year: Int, month: Int, date: Int, hourOfDay: Int = 0, minute: Int = 0, second: Int = 0): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(year, month, date, hourOfDay, minute, second)
        return cal.time
    }

    fun offset(fld: Int, d: Date, offset: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = d
        cal.set(fld, cal.get(fld) + offset)
        return cal.time
    }

    fun monthOfDate(d: Date, i: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = d
        cal.set(Calendar.DATE, i)
        return cal.time
    }
}
