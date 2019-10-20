package com.sc.lesa.mediashar.jlib.util

import java.util.concurrent.atomic.AtomicBoolean

class AtomicBoolean(boolean: Boolean) : AtomicBoolean(boolean) {

    @Synchronized
    override fun weakCompareAndSet(expect: Boolean, update: Boolean): Boolean {
        while (true){
            val value = this.compareAndSet(expect,update)
            if (value){
                return value
            }
        }
    }
}