package com.bs.krxstockservice.repository.dto

import com.querydsl.core.annotations.QueryProjection

data class StockHLV @QueryProjection constructor(
    var ticker:String,
    var firstDay:String,
    var maxHighPrice:String,
    var minLowPrice:String,
    var volume:String,
)
