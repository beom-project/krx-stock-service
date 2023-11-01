package com.bs.krxstockservice.controller


import com.bs.krxstockservice.repository.dto.StockOHLCV
import com.bs.krxstockservice.service.StockService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class StockController(
    private val stockService: StockService,
) {

    @GetMapping("stocks/{period}/{ticker}")
    suspend fun findStockByTickerAndPeriod(@PathVariable("period") period:String, @PathVariable("ticker") ticker:String):ResponseEntity<List<StockOHLCV>>{
        val stockOHLCV = stockService.findStockByTickerAndPeriod(ticker = ticker, period = period)
        return  ResponseEntity.status(HttpStatus.OK).body(stockOHLCV)
    }
}