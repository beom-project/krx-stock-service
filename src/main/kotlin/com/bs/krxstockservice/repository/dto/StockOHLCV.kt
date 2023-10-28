package com.bs.krxstockservice.repository.dto

import com.querydsl.core.annotations.QueryProjection

data class StockOHLCV @QueryProjection constructor(
    var ticker:String,
    var name:String,
    var day:String,
    var openPrice:String,
    var highPrice:String,
    var lowPrice:String,
    var closePrice:String,
    var volume:String,
){
}