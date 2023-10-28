package com.bs.krxstockservice.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class StockId(
    @Column(name = "TICKER")
    val ticker: String?="",
    @Column(name = "DATE")
    val date: String?=""
) : Serializable
