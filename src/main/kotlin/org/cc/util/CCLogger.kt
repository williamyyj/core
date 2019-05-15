package org.cc.util

import org.cc.org.apache.log4j.Level
import org.cc.org.apache.log4j.LogManager
import org.cc.org.apache.log4j.Logger
import org.cc.org.apache.log4j.PropertyConfigurator
import org.cc.org.apache.log4j.spi.LoggerFactory

import java.net.URL

/**
 * @author William
 */
object CCLogger {

    private val FQCN = CCLogger::class.java.name

    private val log = Logger.getLogger(CCLogger::class.java)

    val rootLogger: Logger
        get() = LogManager.getRootLogger()

    init {
        val url = CCLogger::class.java.classLoader.getResource("jo.properties")
        if (url != null) {
            PropertyConfigurator.configure(url)
        }
    }

    fun info(msg: Any) {
        log.log(FQCN, Level.INFO, msg, null)
    }

    fun info(msg: Any, t: Throwable) {
        log.log(FQCN, Level.INFO, msg, t)
    }

    fun error(msg: Any) {
        log.log(FQCN, Level.ERROR, msg, null)
    }

    fun error(msg: Any, t: Throwable) {
        log.log(FQCN, Level.ERROR, msg, t)
    }

    fun debug(msg: Any) {
        log.log(FQCN, Level.DEBUG, msg, null)
    }

    fun debug(msg: Any, t: Throwable) {
        log.log(FQCN, Level.DEBUG, msg, t)
    }

    fun warn(msg: Any) {
        log.log(FQCN, Level.WARN, msg, null)
    }

    fun warn(msg: Any, t: Throwable) {
        log.log(FQCN, Level.WARN, msg, t)
    }

    fun fatal(msg: Any, t: Throwable) {
        log.log(FQCN, Level.FATAL, msg, t)
    }

    fun fatal(msg: Any) {
        log.log(FQCN, Level.FATAL, msg, null)
    }

    fun getLogger(name: String): Logger {
        return LogManager.getLogger(name)
    }

    fun getLogger(clazz: Class<*>): Logger {
        return LogManager.getLogger(clazz.name)
    }

    fun getLogger(name: String, factory: LoggerFactory): Logger {
        return LogManager.getLogger(name, factory)
    }

}
