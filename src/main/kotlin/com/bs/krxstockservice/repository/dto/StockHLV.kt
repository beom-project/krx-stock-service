package com.bs.krxstockservice.repository.dto

import com.querydsl.core.annotations.QueryProjection

data class StockHLV @QueryProjection constructor(
    var ticker:String,
    var firstDayOfWeek:String,
    var maxHighOfWeek:String,
    var minLowOfWeek:String,
    var volume:String,
)
