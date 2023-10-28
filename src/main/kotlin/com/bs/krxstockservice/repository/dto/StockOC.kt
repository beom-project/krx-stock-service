package com.bs.krxstockservice.repository.dto

import com.querydsl.core.annotations.QueryProjection

data class StockOC @QueryProjection constructor(
    var name:String,
    var day:String,
    var open:String,
    var close:String,
)
