package com.example.playaudiotest1

import java.lang.StringBuilder

//拓展函数
/**
 * 毫秒数转为时间
 * @return 格式为：00:00的时间
 */
fun Int.toTime():String{
    val mm = String.format("%02d", this / 1000 / 60)
    val ss = String.format("%02d", this / 1000 % 60)
    return StringBuilder(mm).append(":").append(ss).toString()
}