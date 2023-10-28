package com.bs.krxstockservice.repository

import com.bs.krxstockservice.config.H2CustomAlias
import com.bs.krxstockservice.domain.QStock
import com.bs.krxstockservice.domain.Stock
import com.bs.krxstockservice.domain.StockId
import com.bs.krxstockservice.repository.dto.*
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
internal class StockQueryRepositoryTest(
){
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private lateinit var queryFactory: JPAQueryFactory

    val stocks = listOf<Stock>(
        Stock(
            StockId(
                ticker = "000001",
                date = "20231027",
            ),
            stockName = "test",
            openPrice = "20000",
            highPrice = "25000",
            lowPrice  = "24000",
            closePrice = "23000",
            volume = "22222222"),
        Stock(
            StockId(
                ticker = "000001",
                date = "20231026"
            ),
            stockName = "test",
            openPrice = "10000",
            highPrice = "15000",
            lowPrice  = "14000",
            closePrice = "13000",
            volume = "11111111"),
        Stock(
            StockId(
                ticker = "000001",
                date = "20231020"
            ),
            stockName = "test",
            openPrice = "10000",
            highPrice = "15000",
            lowPrice  = "14000",
            closePrice = "13000",
            volume = "11111111"),
        Stock(
            StockId(
                ticker = "000001",
                date = "20231019"
            ),
            stockName = "test",
            openPrice = "10000",
            highPrice = "14000",
            lowPrice  = "13000",
            closePrice = "12000",
            volume = "11111111"))

    @BeforeEach
    fun setUp() {
        queryFactory = JPAQueryFactory(entityManager)
        entityManager!!
            .createNativeQuery("CREATE ALIAS IF NOT EXISTS YEARWEEK FOR \"com.bs.krxstockservice.config.H2CustomAlias.YEARWEEK\"")
            .executeUpdate()
    }

    @AfterEach
    fun deleteStock(){
        for (data:Stock in stocks){
            val find = entityManager.find(Stock::class.java, data.stockId)?:break
            entityManager.remove(find)
        }
    }

    @Test
    fun yearWeekH2CustomFunctionTest(){
        //given
        val day = "20230101"
        val expect = "202253"
        //when
        val result = H2CustomAlias.YEARWEEK(day)
        //then
        assertEquals(expect,result)
    }

    @Test
    fun findDailyStockByTickerTest(){
        //given
        for (data:Stock in stocks){
            entityManager.persist(data)
        }
        val ticker = "000001"
        //when
        val result = queryFactory.select(
            QStockOHLCV(
                QStock.stock.stockId.ticker.`as`("ticker"),
                QStock.stock.stockName.`as`("name"),
                QStock.stock.stockId.date.`as`("day"),
                QStock.stock.openPrice.`as`("openPrice"),
                QStock.stock.highPrice.`as`("highPrice"),
                QStock.stock.lowPrice.`as`("lowPrice"),
                QStock.stock.closePrice.`as`("closePrice"),
                QStock.stock.volume.`as`("volume")
            )
        ).from(QStock.stock)
            .where(QStock.stock.stockId.ticker.eq(ticker))
            .orderBy(OrderSpecifier(Order.DESC, QStock.stock.stockId.date))
            .fetch()
        //then
        assertEquals(4,result.size)
        assertEquals("20231027",result[0].day)
    }

    @Test
    @Rollback
    @DisplayName("다른 주에 있으므로 값이 2개 여야한다.")
    fun getStockHLVOfWeekTest(){
        //given
        for (data:Stock in stocks){
            entityManager.persist(data)
        }
        //when
        var stockHLVOfWeeks = queryFactory
            .select(
                QStockHLV(
                    QStock.stock.stockId.ticker.`as`("ticker"),
                    QStock.stock.stockId.date.min().`as`("firstDayOfWeek"),
                    QStock.stock.highPrice.max().`as`("maxHighOfWeek"),
                    QStock.stock.lowPrice.min().`as`("minLowOfWeek"),
                    QStock.stock.volume.castToNum(Long::class.java).sum().stringValue().`as`("volume"),
                )
            )
            .from(QStock.stock)
            .where(QStock.stock.stockId.ticker.eq("000001"))
            .groupBy(
                QStock.stock.stockId.ticker,
                Expressions.stringTemplate("YEARWEEK({0})", QStock.stock.stockId.date)
            )
            .orderBy(OrderSpecifier(Order.DESC,QStock.stock.stockId.date.min()))
            .fetch()
        //then
        assertEquals(2, stockHLVOfWeeks.size)
    }

    @Test
    @Rollback
    @DisplayName("모두 같은 달이므로 값은 1개이다.")
    fun getStockHLVOfMonthTest(){
        //given
        for (data:Stock in stocks){
            entityManager.persist(data)
        }
        //when
        var stockHLVOfWeeks = queryFactory
            .select(
                QStockHLV(
                    QStock.stock.stockId.ticker.`as`("ticker"),
                    QStock.stock.stockId.date.min().`as`("firstDayOfWeek"),
                    QStock.stock.highPrice.max().`as`("maxHighOfWeek"),
                    QStock.stock.lowPrice.min().`as`("minLowOfWeek"),
                    QStock.stock.volume.castToNum(Long::class.java).sum().stringValue().`as`("volume"),
                )
            )
            .from(QStock.stock)
            .where(QStock.stock.stockId.ticker.eq("000001"))
            .groupBy(
                QStock.stock.stockId.ticker,
                QStock.stock.stockId.date.substring(0,6)
            )
            .orderBy(OrderSpecifier(Order.DESC,QStock.stock.stockId.date.min()))
            .fetch()
        //then
        assertEquals(1, stockHLVOfWeeks.size)
    }

    @Test
    @Rollback
    fun getStockOCInDateTest(){
        //given
        for (data:Stock in stocks){
            entityManager.persist(data)
        }
        val firstDayOfWeek = listOf<String>(
            "20231026",
            "20231019"
        )
        //when
        val stockOCWeeks = queryFactory
            .select(
                QStockOC(
                    QStock.stock.stockName.`as`("name"),
                    QStock.stock.stockId.date.`as`("day"),
                    QStock.stock.openPrice.`as`("open"),
                    QStock.stock.closePrice.`as`("close"),
                )
            )
            .from(QStock.stock)
            .where(
                QStock.stock.stockId.ticker.eq("000001")
                .and(QStock.stock.stockId.date.`in`(firstDayOfWeek)))
            .fetch()
        //then
        assertEquals(2, stockOCWeeks.size)
        assertEquals("20231019", stockOCWeeks[0].day)
    }

    @Test
    @Rollback
    fun findWeeklyStockByTickerTest(){
        //given
        for (data:Stock in stocks){
            entityManager.persist(data)
        }

        var stockHLVOfWeeks = queryFactory
            .select(
                QStockHLV(
                    QStock.stock.stockId.ticker.`as`("ticker"),
                    QStock.stock.stockId.date.min().`as`("firstDayOfWeek"),
                    QStock.stock.highPrice.max().`as`("maxHighOfWeek"),
                    QStock.stock.lowPrice.min().`as`("minLowOfWeek"),
                    QStock.stock.volume.castToNum(Long::class.java).sum().stringValue().`as`("volume"),
                )
            )
            .from(QStock.stock)
            .where(QStock.stock.stockId.ticker.eq("000001"))
            .groupBy(
                QStock.stock.stockId.ticker,
                Expressions.stringTemplate("YEARWEEK({0})", QStock.stock.stockId.date)
            )
            .orderBy(OrderSpecifier(Order.DESC,QStock.stock.stockId.date.min()))
            .fetch()

        val firstDays = stockHLVOfWeeks.parallelStream().map { it.firstDayOfWeek }.toList()

        val stockOC = queryFactory
            .select(
                QStockOC(
                    QStock.stock.stockName.`as`("name"),
                    QStock.stock.stockId.date.`as`("day"),
                    QStock.stock.openPrice.`as`("open"),
                    QStock.stock.closePrice.`as`("close"),
                )
            )
            .from(QStock.stock)
            .where(
                QStock.stock.stockId.ticker.eq("000001")
                    .and(QStock.stock.stockId.date.`in`(firstDays)))
            .fetch()

        val stockOCMap = stockOC.toList().associateBy { it.day }
        //when
        val stockOHLCVOfWeeks = stockHLVOfWeeks
            .stream()
            .filter { stockOCMap.containsKey(it.firstDayOfWeek) }
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
        //then
        assertEquals(2,stockOHLCVOfWeeks.size)
        assertEquals("000001",stockOHLCVOfWeeks[1].ticker)
        assertEquals("20231019",stockOHLCVOfWeeks[1].day)
        assertEquals("10000",stockOHLCVOfWeeks[1].openPrice)
        assertEquals("15000",stockOHLCVOfWeeks[1].highPrice)
    }

    @Test
    @Rollback
    fun findMonthlyStockByTickerTest(){
        //given
        for (data:Stock in stocks){
            entityManager.persist(data)
        }

        var stockHLVOfMonth = queryFactory
            .select(
                QStockHLV(
                    QStock.stock.stockId.ticker.`as`("ticker"),
                    QStock.stock.stockId.date.min().`as`("firstDayOfWeek"),
                    QStock.stock.highPrice.max().`as`("maxHighOfWeek"),
                    QStock.stock.lowPrice.min().`as`("minLowOfWeek"),
                    QStock.stock.volume.castToNum(Long::class.java).sum().stringValue().`as`("volume"),
                )
            )
            .from(QStock.stock)
            .where(QStock.stock.stockId.ticker.eq("000001"))
            .groupBy(
                QStock.stock.stockId.ticker,
                QStock.stock.stockId.date.substring(0,6)
            )
            .orderBy(OrderSpecifier(Order.DESC,QStock.stock.stockId.date.min()))
            .fetch()

        val firstDays= stockHLVOfMonth.parallelStream().map { it.firstDayOfWeek }.toList()

        val stockOC = queryFactory
            .select(
                QStockOC(
                    QStock.stock.stockName.`as`("name"),
                    QStock.stock.stockId.date.`as`("day"),
                    QStock.stock.openPrice.`as`("open"),
                    QStock.stock.closePrice.`as`("close"),
                )
            )
            .from(QStock.stock)
            .where(
                QStock.stock.stockId.ticker.eq("000001")
                    .and(QStock.stock.stockId.date.`in`(firstDays)))
            .fetch()

        val stockOCMap = stockOC.toList().associateBy { it.day }
        //when
        val stockOHLCVOfMonth = stockHLVOfMonth
            .stream()
            .filter { stockOCMap.containsKey(it.firstDayOfWeek) }
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
        //then
        assertEquals(1, stockOHLCVOfMonth.size)
        assertEquals("25000", stockOHLCVOfMonth[0].highPrice)
        assertEquals("13000", stockOHLCVOfMonth[0].lowPrice)
    }

    @Test
    @Rollback
    fun findYearlyStockByTickerTest(){
        //given
        for (data:Stock in stocks){
            entityManager.persist(data)
        }

        var stockHLVOfYear = queryFactory
            .select(
                QStockHLV(
                    QStock.stock.stockId.ticker.`as`("ticker"),
                    QStock.stock.stockId.date.min().`as`("firstDayOfWeek"),
                    QStock.stock.highPrice.max().`as`("maxHighOfWeek"),
                    QStock.stock.lowPrice.min().`as`("minLowOfWeek"),
                    QStock.stock.volume.castToNum(Long::class.java).sum().stringValue().`as`("volume"),
                )
            )
            .from(QStock.stock)
            .where(QStock.stock.stockId.ticker.eq("000001"))
            .groupBy(
                QStock.stock.stockId.ticker,
                QStock.stock.stockId.date.substring(0,4)
            )
            .orderBy(OrderSpecifier(Order.DESC,QStock.stock.stockId.date.min()))
            .fetch()

        val firstDays= stockHLVOfYear.parallelStream().map { it.firstDayOfWeek }.toList()

        val stockOC = queryFactory
            .select(
                QStockOC(
                    QStock.stock.stockName.`as`("name"),
                    QStock.stock.stockId.date.`as`("day"),
                    QStock.stock.openPrice.`as`("open"),
                    QStock.stock.closePrice.`as`("close"),
                )
            )
            .from(QStock.stock)
            .where(
                QStock.stock.stockId.ticker.eq("000001")
                    .and(QStock.stock.stockId.date.`in`(firstDays)))
            .fetch()

        val stockOCMap = stockOC.toList().associateBy { it.day }
        //when
        val stockOHLCVOfMonth = stockHLVOfYear
            .stream()
            .filter { stockOCMap.containsKey(it.firstDayOfWeek) }
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
        //then
        assertEquals(1, stockOHLCVOfMonth.size)
        assertEquals("2023", stockOHLCVOfMonth[0].day.substring(0,4))
    }
}