package com.zareshahi.myreport.util

import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date


class PersianDateTime {
    fun getCurrentDate(): String {
        val pdate = PersianDate()
        val pdformater = PersianDateFormat("j/F/Y")
        return pdformater.format(pdate)
    }

    fun getCurrentTime(): String {
        val pdate = PersianDate()
        val pdformater = PersianDateFormat("H:i")
        return pdformater.format(pdate)
    }

    fun convertDateToPersianDate(localDateTime: LocalDateTime): String? {
        val date =Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        val pDate =PersianDate(date)
        val pdformater = PersianDateFormat("j/F/Y l")
        return pdformater.format(pDate)
    }
}