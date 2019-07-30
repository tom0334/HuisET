package com.tobo.huiset.utils

import java.lang.IllegalStateException
import java.util.*


class ToboTime{

    private val calendar: Calendar = Calendar.getInstance()

    private val unix by lazy { calendar.timeInMillis }

    //normal hour is -12 ( the weird american AM/PM shit)
    val hour by lazy { calendar.get(Calendar.HOUR_OF_DAY)}
    val min by lazy{ calendar.get(Calendar.MINUTE)}
    val sec by lazy{ calendar.get(Calendar.SECOND)}



    constructor(millis:Long){
        calendar.timeInMillis = millis
    }

    constructor(hour:Int, min:Int, sec:Int){
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar.set(Calendar.SECOND, sec)
    }



    fun timeOfDayBefore(other:ToboTime): Boolean {
        if(this.hour == other.hour && this.min == other.min) return this.sec < other.sec
        else if(this.hour == other.hour) return this.min < other.min
        else return this.hour < other.hour
    }

    fun timeOfDayAfter(other:ToboTime): Boolean {
        return other.timeOfDayBefore(this)
    }

    fun timeOfDayBetween(a:ToboTime, b:ToboTime):Boolean{
        return this.timeOfDayAfter(a) && this.timeOfDayBefore(b)
    }





    fun isWeekDay(): Boolean {
        if(this.unix == 0L) throw IllegalStateException("Moment used without setting unix timestamp.")
        val dow = calendar.get(Calendar.DAY_OF_WEEK)
        return dow >= Calendar.MONDAY && dow <= Calendar.FRIDAY
    }


    fun momentBefore(other: ToboTime): Boolean{
        if(this.unix == 0L) throw IllegalStateException("Moment used without setting unix timestamp.")
        return this.calendar.before(other.calendar)
    }

    fun momentAfter(other:ToboTime): Boolean {
        if(this.unix == 0L) throw IllegalStateException("Moment used without setting unix timestamp.")
        return this.calendar.after(other.calendar)
    }

}
