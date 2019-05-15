package org.cc.util

/**
 * 作業系統有關
 *
 * @author william
 */
object CCOS {

    private val OS = System.getProperty("os.name").toLowerCase()

    val isWindows: Boolean
        get() = OS.contains("win")

    val isMac: Boolean
        get() = 0 < OS.indexOf("mac")

    val isUnix: Boolean
        get() = OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0

    val isSolaris: Boolean
        get() = OS.contains("sunos")

    @JvmStatic
    fun main(args: Array<String>) {

        println(OS)

        if (isWindows) {
            println("This is Windows")
        } else if (isMac) {
            println("This is Mac")
        } else if (isUnix) {
            println("This is Unix or Linux")
        } else if (isSolaris) {
            println("This is Solaris")
        } else {
            println("Your OS is not support!!")
        }
    }

}
