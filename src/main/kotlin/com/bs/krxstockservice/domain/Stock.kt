package com.bs.krxstockservice.domain

import jakarta.persistence.*


@Table(name = "STOCK",
    indexes = [Index(name = "IDX_TICKER", columnList = "TICKER", unique = false)]
)
@Entity
class Stock(
    @EmbeddedId
    var stockId:StockId?=null,
    @Column(name = "NAME")
    val stockName:String?=null,
    @Column(name= "MARKET")
    val market:String?=null,
    @Column(name= "CHANGE_CODE")
    val changeCode:String?=null,
    @Column(name= "CHANGES")
    val changes:String?=null,
    @Column(name= "CHANGES_RATIO")
    val changesRatio:String?=null,
    @Column(name= "OPEN_PRICE")
    val openPrice:String?=null,
    @Column(name= "HIGH_PRICE")
    val highPrice:String?=null,
    @Column(name= "LOW_PRICE")
    val lowPrice:String?=null,
    @Column(name= "CLOSE_PRICE")
    val closePrice:String?=null,
    @Column(name="VOLUME")
    val volume:String?=null,
    @Column(name="AMOUNT")
    val amount:String?=null,
    @Column(name="MARKET_CAP")
    val marketCap:String?=null,
    @Column(name="STOCKS")
    val stocks:String?=null,
    @Column(name="MARKET_Id")
    val marketId:String?=null,
)



