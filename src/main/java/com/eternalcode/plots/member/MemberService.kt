package com.eternalcode.plots.member

import com.eternalcode.plots.plot.Plot
import com.eternalcode.plots.user.User
import java.util.concurrent.CompletableFuture

class MemberService(private val repository: MemberRepository) {
    fun isMember(plot: Plot, user: User): CompletableFuture<Boolean?>? {
        return repository.isMember(plot.plotId, user.uuid())
    }
}
