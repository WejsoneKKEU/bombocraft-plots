package com.eternalcode.plots.member;

import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.user.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }


    public CompletableFuture<Boolean> isMember(Plot plot, User user) {
        return repository.isMember(plot.plotId(), user.getUuid());
    }

}
