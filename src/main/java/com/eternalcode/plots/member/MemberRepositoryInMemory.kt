package com.eternalcode.plots.member

import java.util.*
import java.util.concurrent.CompletableFuture

class MemberRepositoryInMemory : MemberRepository {
    private val membersByPlotUUID: MutableMap<UUID, MutableSet<Member>> = HashMap()

    override fun saveMember(member: Member): CompletableFuture<Void?>? {
        val indexedMembers =
            membersByPlotUUID.computeIfAbsent(member.plotId) { key: UUID? -> HashSet() }
        indexedMembers.add(member)

        return CompletableFuture.completedFuture(null)
    }

    override fun removeMember(member: Member): CompletableFuture<Void?>? {
        val indexedMembers =
            membersByPlotUUID.computeIfAbsent(member.plotId) { key: UUID? -> HashSet() }
        indexedMembers.remove(member)

        return CompletableFuture.completedFuture(null)
    }

    override fun isMember(plotId: UUID, userId: UUID): CompletableFuture<Boolean>? {
        val indexedMembers: Set<Member> =
            membersByPlotUUID.computeIfAbsent(plotId) { key: UUID? -> HashSet() }

        return CompletableFuture.completedFuture(
            indexedMembers.stream().anyMatch { member: Member -> member.userId == userId })
    }
}
