package com.sc.lesa.mediashar.jlib.io

import java.io.DataInput
import java.io.DataOutput

class DataPack() :Writable {
    var type:Int=0
    lateinit var byteArray: ByteArray

    companion object{
        val TYPE_VIDEO=1
        val TYPE_VOICE=2
    }

    constructor(var1: DataInput):this(){
        readFields(var1)
    }

    constructor(type:Int,byteArray: ByteArray) : this() {
        this.type=type
        this.byteArray=byteArray
    }

    override fun write(var1: DataOutput) {
        var1.writeInt(type)
        var1.writeInt(byteArray.size)
        var1.write(byteArray)
    }

    override fun readFields(var1: DataInput) {
        type=var1.readInt()
        val len = var1.readInt()
        byteArray= ByteArray(len)
        for (i in 0 until len ){
            byteArray[i]=var1.readByte()
        }
    }

    fun isVideoPack():Boolean{
        if (type == TYPE_VIDEO){
            return true
        }
        return false
    }


    fun isVoicePack():Boolean{
        if (type == TYPE_VOICE){
            return true
        }
        return false
    }

}