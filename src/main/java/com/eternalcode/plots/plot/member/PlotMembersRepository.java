package com.eternalcode.plots.plot.member;

import com.eternalcode.plots.plot.Plot;

import java.util.Set;

public interface PlotMembersRepository {

    void saveMembers(Plot plot, Set<PlotMember> plotMembers);

    void addMember(Plot plot, PlotMember plotMember);

    void removeMember(Plot plot, PlotMember plotMember);
}
