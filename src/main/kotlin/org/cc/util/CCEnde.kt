package org.cc.util

import java.io.BufferedInputStream
import java.io.InputStream
import java.security.MessageDigest

/**
 *
 * @author william
 */
object CCEnde {

    private val buf_size = 4096

    fun toHexString(data: ByteArray): String {
        val hexString = StringBuilder()
        for (i in data.indices) {
            val b = 0xff and data[i]
            hexString.append(if (b > 15) Integer.toHexString(0xFF and data[i]) else '0' + Integer.toHexString(0xFF and data[i]))
        }
        return hexString.toString()
    }

    @Throws(Exception::class)
    fun digest(algorithm: MessageDigest, `is`: InputStream): String {
        var bis: BufferedInputStream? = null
        val buf = ByteArray(buf_size)
        var num = 0
        try {
            bis = BufferedInputStream(`is`)
            algorithm.reset()
            algorithm.update(buf, num, num)
            while ((num = bis.read(buf)) > 0) {
                algorithm.update(buf, 0, num)
            }
            return toHexString(algorithm.digest())
        } finally {
            bis?.close()
        }
    }

    @Throws(Exception::class)
    fun loadData(`is`: InputStream): ByteArray? {
        var bis: BufferedInputStream? = null
        var data: ByteArray? = null
        val tmp = ByteArray(buf_size)
        var num = 0
        try {
            bis = BufferedInputStream(`is`)
            while ((num = bis.read(tmp)) > 0) {
                if (data == null) {
                    data = ByteArray(num)
                    System.arraycopy(tmp, 0, data, 0, num)
                } else {
                    val old = data
                    data = ByteArray(old.size + num)
                    System.arraycopy(old, 0, data, 0, old.size)
                    System.arraycopy(tmp, 0, data, old.size, num)
                }
            }
        } finally {
            bis!!.close()
        }
        return data
    }

}
