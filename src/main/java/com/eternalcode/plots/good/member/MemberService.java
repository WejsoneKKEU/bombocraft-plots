package com.eternalcode.plots.good.member;

import com.eternalcode.plots.good.plot.Plot;
import com.eternalcode.plots.good.user.User;

import java.util.concurrent.CompletableFuture;

public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }


    public CompletableFuture<Boolean> isMember(Plot plot, User user) {
        return repository.isMember(plot.plotId(), user.uuid());
    }

}
