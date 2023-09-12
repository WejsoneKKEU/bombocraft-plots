package com.eternalcode.plots.plot;

import java.util.Set;

public interface MembersRepository {

    void saveMembers(Plot plot, Set<Member> members);

    void addMember(Plot plot, Member member);

    void removeMember(Plot plot, Member member);
}
