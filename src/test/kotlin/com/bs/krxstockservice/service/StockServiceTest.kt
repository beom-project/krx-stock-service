package com.bs.krxstockservice.service

import com.bs.krxstockservice.repository.StockQueryRepository
import com.bs.krxstockservice.repository.dto.StockOHLCV
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
internal class StockServiceTest {

    @Mock private lateinit var stockQueryRepository: StockQueryRepository
    @InjectMocks private lateinit var stockService: StockService

    @Test
    @DisplayName("day")
    fun findStockByTickerAndPeriod_day() {
        //given
        val expect = listOf<StockOHLCV>(
            StockOHLCV(
                ticker = "000001",
                name = "test",
                day = "20231031",
                openPrice = "100000",
                highPrice = "120000",
                lowPrice = "90000",
                closePrice = "110000",
                volume = "1000000"
            ),
            StockOHLCV(
                ticker = "000001",
                name = "test",
                day = "20231101",
                openPrice = "100000",
                highPrice = "120000",
                lowPrice = "90000",
                closePrice = "110000",
                volume = "1000000"
            )
        )

        Mockito.`when`(stockQueryRepository.findDailyStockByTicker("000001")).thenReturn(expect)
        //when
        val result = stockService.findStockByTickerAndPeriod(ticker = "000001", period = "day")
        //then
        assertEquals(expect,result)
    }

    @Test
    @DisplayName("week")
    fun findStockByTickerAndPeriod_week() {
        //given
        val expect = listOf<StockOHLCV>(
            StockOHLCV(
                ticker = "000001",
                name = "test",
                day = "20231027",
                openPrice = "100000",
                highPrice = "120000",
                lowPrice = "90000",
                closePrice = "110000",
                volume = "1000000"
            ),
            StockOHLCV(
                ticker = "000001",
                name = "test",
                day = "20231101",
                openPrice = "100000",
                highPrice = "120000",
                lowPrice = "90000",
                closePrice = "110000",
                volume = "1000000"
            )
        )

        Mockito.`when`(stockQueryRepository.findWeeklyStockByTicker("000001")).thenReturn(expect)
        //when
        val result = stockService.findStockByTickerAndPeriod(ticker = "000001", period = "week")
        //then
        assertEquals(expect,result)
    }

    @Test
    @DisplayName("month")
    fun findStockByTickerAndPeriod_month() {
        //given
        val expect = listOf<StockOHLCV>(
            StockOHLCV(
                ticker = "000001",
                name = "test",
                day = "20231027",
                openPrice = "100000",
                highPrice = "120000",
                lowPrice = "90000",
                closePrice = "110000",
                volume = "1000000"
            ),
            StockOHLCV(
                ticker = "000001",
                name = "test",
                day = "20231031",
                openPrice = "100000",
                highPrice = "120000",
                lowPrice = "90000",
                closePrice = "110000",
                volume = "1000000"
            )
        )

        Mockito.`when`(stockQueryRepository.findMonthlyStockByTicker("000001")).thenReturn(expect)
        //when
        val result = stockService.findStockByTickerAndPeriod(ticker = "000001", period = "month")
        //then
        assertEquals(expect,result)
    }

    @Test
    @DisplayName("year")
    fun findStockByTickerAndPeriod_year() {
        //given
        val expect = listOf<StockOHLCV>(
            StockOHLCV(
                ticker = "000001",
                name = "test",
                day = "20231027",
                openPrice = "100000",
                highPrice = "120000",
                lowPrice = "90000",
                closePrice = "110000",
                volume = "1000000"
            ),
            StockOHLCV(
                ticker = "000001",
                name = "test",
                day = "20231031",
                openPrice = "100000",
                highPrice = "120000",
                lowPrice = "90000",
                closePrice = "110000",
                volume = "1000000"
            )
        )

        Mockito.`when`(stockQueryRepository.findYearlyStockByTicker("000001")).thenReturn(expect)
        //when
        val result = stockService.findStockByTickerAndPeriod(ticker = "000001", period = "year")
        //then
        assertEquals(expect,result)
    }
    @Test
    @DisplayName("day, week, month, year 이외의 값은 빈 배열 리턴")
    fun findStockByTickerAndPeriod_else() {
        //given
        val expect = listOf<StockOHLCV>()
        //when
        val result = stockService.findStockByTickerAndPeriod(ticker = "000001", period = "year")
        //then
        assertEquals(expect,result)
    }


}