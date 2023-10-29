package com.bs.krxstockservice.repository


import com.bs.krxstockservice.domain.QStock
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import com.bs.krxstockservice.domain.QStock.stock
import com.bs.krxstockservice.repository.dto.*
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.StringExpression


@Repository
class StockQueryRepository(
    private val queryFactory: JPAQueryFactory,
) {
    /**
     * 일별 OHLCV
     */
    fun findDailyStockByTicker(ticker:String):List<StockOHLCV>{
        return queryFactory.select(
            QStockOHLCV(
                stock.stockId.ticker.`as`("ticker"),
                stock.stockName.`as`("name"),
                stock.stockId.date.`as`("day"),
                stock.openPrice.`as`("openPrice"),
                stock.highPrice.`as`("highPrice"),
                stock.lowPrice.`as`("lowPrice"),
                stock.closePrice.`as`("closePrice"),
                stock.volume.`as`("volume")
            )
        ).from(stock)
            .where(eqTicker(ticker))
            .orderBy(OrderSpecifier(Order.DESC, stock.stockId.date))
            .fetch()
    }
    /**
     * 주별 OHLCV
     */
    fun findWeeklyStockByTicker(ticker:String, period: String?="week"): List<StockOHLCV>{
        return findStockByTickerAndPeriod(ticker,period!!)
    }
    /**
     *  월별 OHLCV
     */
    fun findMonthlyStockByTicker(ticker:String, period: String?="month"):List<StockOHLCV>{
        return findStockByTickerAndPeriod(ticker,period!!)
    }

    /**
     * 연도별 OHLCV
     */
    fun findYearlyStockByTicker(ticker:String, period: String?="year"):List<StockOHLCV>{
        return findStockByTickerAndPeriod(ticker,period!!)
    }

    //-------------------------------------------------------
    fun findStockByTickerAndPeriod(ticker:String, period: String):List<StockOHLCV>{
        val stockHLVC = getStockHLV(ticker, period)
        //get StockOC
        val stockOCMap = convertStockOCToMap(getStockOCs(ticker, getFirstDays(stockHLVC)))
        //합치기
        return stockHLVC
            .stream()
            .filter{stockOCMap.containsKey(it.firstDayOfWeek)}
            .map {
                val stockOC = stockOCMap[it.firstDayOfWeek]
                StockOHLCV(
                    ticker = it.ticker,
                    name = stockOC!!.name,
                    day = it.firstDayOfWeek,
                    openPrice = stockOC.open,
                    highPrice = it.maxHighOfWeek,
                    lowPrice = it.minLowOfWeek,
                    closePrice = stockOC.close,
                    volume = it.volume,
                )
            }.toList()
    }

    private fun getStockHLV(ticker: String, period: String):List<StockHLV>{
        return queryFactory
            .select(
                QStockHLV(
                    stock.stockId.ticker,
                    stock.stockId.date.min(),
                    stock.highPrice.max(),
                    stock.lowPrice.min(),
                    stock.volume.castToNum(Long::class.java).sum().stringValue().`as`("volume"),
                )
            )
            .from(stock)
            .where(
                eqTicker(ticker)
            )
            .groupBy(
                stock.stockId.ticker,
                groupByDateType(period)
            )
            .orderBy(OrderSpecifier(Order.DESC, stock.stockId.date.min()))
            .fetch()
    }

    private fun eqTicker(ticker: String):BooleanExpression{
        return stock.stockId.ticker.eq(ticker)
    }

    private fun groupByDateType(type:String):StringExpression{
        return when (type) {
            "week" -> Expressions.stringTemplate("YEARWEEK({0}, 7)", stock.stockId.date)
            "month" -> stock.stockId.date.substring(0, 6)
            else -> stock.stockId.date.substring(0, 4)
        }
    }

    private fun getFirstDays(stockHLVS:List<StockHLV>): List<String>{
        return stockHLVS.parallelStream().map { it.firstDayOfWeek }.toList()
    }

    private fun getStockOCs(ticker: String,days:List<String>): List<StockOC>{
        return queryFactory
            .select(
                QStockOC(
                    stock.stockName.`as`("name"),
                    stock.stockId.date.`as`("day"),
                    stock.openPrice.`as`("open"),
                    stock.closePrice.`as`("close"),
                )
            )
            .from(stock)
            .where(eqTicker(ticker)
                .and(stock.stockId.date.`in`(days)))
            .fetch()
    }

    private fun convertStockOCToMap(stockOCWeeks : List<StockOC>): Map<String, StockOC>{
        return stockOCWeeks.toList().associateBy { it.day }
    }



}

