package com.eternalcode.plots.plot;

import com.eternalcode.plots.plot.protection.Protection;
import com.eternalcode.plots.region.Region;
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
    private final Member owner;
    private final Region region;
    private final Set<Member> members = new HashSet<>();
    private final HashMap<UUID, Member> membersByUserUUID = new HashMap<>();
    private String name;
    private Date expires;
    private Protection protection;

    Plot(UUID uuid, String name, Member owner, Region region, Date created, Date expires, Set<Member> members) {
        this.uuid = uuid;
        this.name = name;
        this.owner = owner;
        this.created = created;
        this.expires = expires;
        this.region = region;

        this.addMember(owner);

        for (Member member : members) {
            this.addMember(member);
        }
    }

    void addMember(Member member) {
        this.members.add(member);
        this.membersByUserUUID.put(member.getUser().getUuid(), member);
    }

    void removeMember(Member member) {
        this.members.remove(member);
        this.membersByUserUUID.remove(member.getUser().getUuid(), member);
    }

    public Set<Member> getMembers() {
        return Collections.unmodifiableSet(this.members);
    }

    public Option<Member> getMember(User user) {
        if (this.membersByUserUUID.containsKey(user.getUuid())) {
            return Option.of(this.membersByUserUUID.get(user.getUuid()));
        }
        return Option.none();
    }

    public boolean isMember(Member member) {
        return this.members.contains(member);
    }

    public boolean isMember(User user) {
        return this.membersByUserUUID.containsKey(user.getUuid());
    }

    public boolean isOwner(Member member) {
        return this.owner.equals(member);
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

    public Member getOwner() {
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

    void setProtection(Protection protection) {
        this.protection = protection;
    }
}
