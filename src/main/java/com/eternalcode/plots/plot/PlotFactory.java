package com.eternalcode.plots.plot;

import com.eternalcode.plots.plot.protection.Protection;
import com.eternalcode.plots.plot.protection.ProtectionFactory;
import com.eternalcode.plots.region.Region;
import com.eternalcode.plots.user.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlotFactory {

    private final MemberFactory memberFactory;
    private final ProtectionFactory protectionFactory;

    public PlotFactory(MemberFactory memberFactory, ProtectionFactory protectionFactory) {
        this.memberFactory = memberFactory;
        this.protectionFactory = protectionFactory;
    }

    public Plot create(UUID plotUUID, String name, Member owner, Region region, Protection protection, Date creation, Date validity, Set<Member> members) { // wczytywanie z orm

        Plot plot = new Plot(
            plotUUID,
            name,
            owner,
            region,
            creation,
            validity,
            members
        );

        plot.setProtection(protection);

        return plot;
    }

    public Plot createNew(UUID plotUUID, String name, User owner, Region region, Date creation, Date expires) { // tworzenie nowej

        Member memberOwner = memberFactory.createNew(owner);

        Plot plot = new Plot(
            plotUUID,
            name,
            memberOwner,
            region,
            creation,
            expires,
            new HashSet<>()
        );

        Protection protection = protectionFactory.createNew(); // default protection
        plot.setProtection(protection);

        return plot;
    }

}
