package com.bs.krxstockservice.service

import com.bs.krxstockservice.repository.StockQueryRepository
import com.bs.krxstockservice.repository.dto.StockOHLCV
import org.springframework.stereotype.Service


@Service
class StockService(
    private val stockQueryRepository: StockQueryRepository
) {

    fun findStockByTickerAndPeriod(ticker:String,period:String):List<StockOHLCV>{
        return when(period){
            "day" -> stockQueryRepository.findDailyStockByTicker(ticker)
            "week" -> stockQueryRepository.findWeeklyStockByTicker(ticker)
            "month" -> stockQueryRepository.findMonthlyStockByTicker(ticker)
            "year" -> stockQueryRepository.findYearlyStockByTicker(ticker)
            else -> arrayListOf()
        }
    }

}