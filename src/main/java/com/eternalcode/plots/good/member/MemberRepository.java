package com.eternalcode.plots.good.member;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MemberRepository {

    CompletableFuture<Void> saveMember(Member member);

    CompletableFuture<Void> removeMember(Member member);

    CompletableFuture<Boolean> isMember(UUID plotId, UUID userId);

}
