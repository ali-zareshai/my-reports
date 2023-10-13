package com.zareshahi.myreport.model

import java.time.LocalTime
import java.time.ZonedDateTime

data class TempNote(
    var zonedDateTime: ZonedDateTime?=null,
    var localTime: LocalTime?=null,
    var note:String?=null
)
