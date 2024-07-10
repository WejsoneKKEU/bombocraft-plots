package com.eternalcode.plots.flag

import java.util.*

@JvmRecord
data class Flag(val plotId: UUID, val flagType: FlagType, val value: Boolean) 
