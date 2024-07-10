package com.eternalcode.plots.plot

import java.time.Instant
import java.util.*

@JvmRecord
data class Plot(
    @JvmField val plotId: UUID,
    val name: String,

    val createdAt: Instant,
    val expireAt: Instant
) 
