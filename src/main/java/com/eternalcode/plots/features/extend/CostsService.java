package com.eternalcode.plots.features.extend;

import org.bukkit.entity.Player;

public interface CostsService {

    boolean hasCosts(Player player);

    void removeCosts(Player player);

    String getCosts();

    String noCostsMessage();
}
