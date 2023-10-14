package com.zareshahi.myreport.util

import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat







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
}