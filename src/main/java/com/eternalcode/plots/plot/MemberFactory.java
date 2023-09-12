package com.eternalcode.plots.plot;

import com.eternalcode.plots.user.User;

import java.util.UUID;

public class MemberFactory {

    public Member create(UUID memberUUID, User user) {
        return new Member(memberUUID, user);
    } // do czytania db

    public Member createNew(User user) {
        return new Member(UUID.randomUUID(), user);
    } // do tworzenia nowych
}
