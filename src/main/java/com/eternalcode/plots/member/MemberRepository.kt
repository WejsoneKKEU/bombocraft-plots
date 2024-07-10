package com.eternalcode.plots.member

import java.util.*
import java.util.concurrent.CompletableFuture

interface MemberRepository {
    fun saveMember(member: Member): CompletableFuture<Void?>?

    fun removeMember(member: Member): CompletableFuture<Void?>?

    fun isMember(plotId: UUID, userId: UUID): CompletableFuture<Boolean>?
}
