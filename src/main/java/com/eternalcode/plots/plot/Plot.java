package com.eternalcode.plots.plot;

import com.eternalcode.plots.plot.member.PlotMember;
import com.eternalcode.plots.plot.protection.Protection;
import com.eternalcode.plots.plot.region.Region;
import com.eternalcode.plots.user.User;
import panda.std.Option;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Plot {

    private final UUID uuid;
    private final Date created;
    private final PlotMember owner;
    private final Region region;
    private final Set<PlotMember> plotMembers = new HashSet<>();
    private final HashMap<UUID, PlotMember> membersByUserUUID = new HashMap<>();
    private String name;
    private Date expires;
    private Protection protection;

    public Plot(UUID uuid, String name, PlotMember owner, Region region, Date created, Date expires, Set<PlotMember> plotMembers) {
        this.uuid = uuid;
        this.name = name;
        this.owner = owner;
        this.created = created;
        this.expires = expires;
        this.region = region;

        this.addMember(owner);

        for (PlotMember plotMember : plotMembers) {
            this.addMember(plotMember);
        }
    }

    public void addMember(PlotMember plotMember) {
        this.plotMembers.add(plotMember);
        this.membersByUserUUID.put(plotMember.getUser().getUuid(), plotMember);
    }

    public void removeMember(PlotMember plotMember) {
        this.plotMembers.remove(plotMember);
        this.membersByUserUUID.remove(plotMember.getUser().getUuid(), plotMember);
    }

    public Set<PlotMember> getMembers() {
        return Collections.unmodifiableSet(this.plotMembers);
    }

    public Option<PlotMember> getMember(User user) {
        if (this.membersByUserUUID.containsKey(user.getUuid())) {
            return Option.of(this.membersByUserUUID.get(user.getUuid()));
        }
        return Option.none();
    }

    public boolean isMember(PlotMember plotMember) {
        return this.plotMembers.contains(plotMember);
    }

    public boolean isMember(User user) {
        return this.membersByUserUUID.containsKey(user.getUuid());
    }

    public boolean isOwner(PlotMember plotMember) {
        return this.owner.equals(plotMember);
    }

    public boolean isOwner(User user) {
        return this.owner.getUser().equals(user);
    }

    public boolean isExpired() {
        return new Date().after(this.expires);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plot plot = (Plot) o;
        return this.uuid.equals(plot.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Date getCreated() {
        return this.created;
    }

    public PlotMember getOwner() {
        return this.owner;
    }

    public Region getRegion() {
        return this.region;
    }

    public String getName() {
        return this.name;
    }

    void setName(String name) {
        this.name = name;
    }

    public Date getExpires() {
        return this.expires;
    }

    void setExpires(Date expires) {
        this.expires = expires;
    }

    public Protection getProtection() {
        return this.protection;
    }

    public void setProtection(Protection protection) {
        this.protection = protection;
    }
}
