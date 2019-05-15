/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc.util

/**
 *
 * @author william
 */
class CCCacheItem<E> {

    internal var id: String? = null
    internal var lastModified: Long = 0
    internal var value: E? = null

}
