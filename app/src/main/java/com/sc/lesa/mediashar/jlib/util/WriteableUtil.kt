package com.sc.lesa.mediashar.jlib.util

import com.sc.lesa.mediashar.jlib.io.DataInputStreamBuffer
import com.sc.lesa.mediashar.jlib.io.DataOutputStreamBuffer
import com.sc.lesa.mediashar.jlib.io.Writable

class WriteableUtil {
    companion object{
        fun toByteArray(w: Writable):ByteArray{
            val tmp = DataOutputStreamBuffer()
            w.write(tmp)
            tmp.flush()
            return tmp.toByteArray()
        }

        fun <T> parse(byteArray: ByteArray,clazz: Class<T>):T{
            val tmp = clazz.newInstance()
            val bytes = DataInputStreamBuffer(byteArray)
            tmp as Writable
            tmp.readFields(bytes)
            return tmp
        }
    }
}

fun Writable.toByteArray():ByteArray{
    return WriteableUtil.toByteArray(this)
}