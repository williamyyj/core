package org.cc.util

import org.cc.ICCMap
import org.mvel2.MVEL

/**
 * @author william
 */
object CCMvel {

    fun eval(p: ICCMap, id: String): Any {
        return MVEL.eval(p.asString(id), p)
    }
}
