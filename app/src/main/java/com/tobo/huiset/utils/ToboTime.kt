package com.tobo.huiset.utils

import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Wrapper around the java calendar with the extra feature of being able to easily compare time of day(WITHOUT considering the date).
 *
 * To make this possible, there are 2 constuctors, one with millis to use with full time points, and one to use with only
 * hours mins and seconds.
 */

data class ToboDay(val dayOfYear:Int, val year:Int) {

    fun UnixOnDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear)
        calendar.set(Calendar.HOUR_OF_DAY,0)
        calendar.set(Calendar.MINUTE,0)
        calendar.set(Calendar.SECOND,0)

        return calendar.timeInMillis
    }
}

data class ToboTime(private val calendar: Calendar){

    private val unix by lazy { calendar.timeInMillis }

    //normal hour is -12 ( the weird american AM/PM shit)
    val hour by lazy { calendar.get(Calendar.HOUR_OF_DAY)}
    val min by lazy{ calendar.get(Calendar.MINUTE)}
    val sec by lazy{ calendar.get(Calendar.SECOND)}
    val dayOfMonth by lazy { calendar.get(Calendar.DAY_OF_MONTH) }
    val month by lazy { calendar.get(Calendar.MONTH) }
    val year by lazy { calendar.get(Calendar.YEAR) }

    val dayOfYear by lazy { calendar.get(Calendar.DAY_OF_YEAR) }
    val toboDay by lazy { ToboDay(this.dayOfYear, this.year) }


    constructor(millis:Long) : this(Calendar.getInstance()) {
        calendar.timeInMillis = millis
    }

    constructor(hour:Int, min:Int, sec:Int) : this(Calendar.getInstance()) {
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar.set(Calendar.SECOND, sec)
    }


    /**
     * Returns the "drinking day" for a timestamp. This avoids the issue of a drinking night ending at 12 in the app,
     * while IRL it doesn't duh
     *
     * Basically, the night is still treated as the day before until 6am
     */
    fun getZuipDay():ToboDay{
        val zuipTurnoverPoint = ToboTime(6,0,0)

        if(this.timeOfDayBefore(zuipTurnoverPoint)){
            val yesterday = ToboTime(this.calendar)
            yesterday.calendar.set(Calendar.DAY_OF_YEAR, this.dayOfYear -1)
            return yesterday.toboDay
        }
        else return this.toboDay
    }


    fun zuipDayHasEnded(): Boolean {
        val now= ToboTime(System.currentTimeMillis())
        return this.getZuipDay() != now.getZuipDay()
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


    /**
     * The following functions can only be called if this toboTime was created using the millis from epoch, NOT if using
     * the hours mins seconds.
     */

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

    fun is1DayLaterThan(thanThis: ToboTime): Boolean {
        val maybe1DayLater = Calendar.getInstance()
        maybe1DayLater.timeInMillis = thanThis.unix
        maybe1DayLater.add(Calendar.DAY_OF_YEAR,1)
        return this.calendar.get(Calendar.DAY_OF_YEAR) == maybe1DayLater.get(Calendar.DAY_OF_YEAR)
    }


}
