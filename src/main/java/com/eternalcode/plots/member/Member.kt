package com.eternalcode.plots.member

import java.util.*

@JvmRecord
data class Member(val memberId: UUID, val plotId: UUID, val userId: UUID, val role: String) 
