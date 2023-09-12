package com.eternalcode.plots.plot.protection;

import com.eternalcode.plots.configuration.implementations.ProtectionConfiguration;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.region.Region;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import panda.std.Option;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProtectionManager {

    private final Cache<Block, UUID> placed = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();
    private final ProtectionRepository protectionRepository;
    private final ProtectionFactory protectionFactory;
    private final ProtectionConfiguration protectionConfiguration;
    private final UserManager userManager;
    private final PlotManager plotManager;

    public ProtectionManager(ProtectionFactory protectionFactory, ProtectionRepository protectionRepository, ProtectionConfiguration protectionConfiguration, UserManager userManager, PlotManager plotManager) {
        this.protectionRepository = protectionRepository;
        this.protectionFactory = protectionFactory;
        this.protectionConfiguration = protectionConfiguration;
        this.userManager = userManager;
        this.plotManager = plotManager;
    }

    public Protection create(Plot plot) {
        Protection protection = this.protectionFactory.createNew();

        this.protectionRepository.saveProtection(plot, protection);

        return protection;
    }

    public void setProtection(Plot plot, Protection protection, FlagType flagType, boolean status) {

        protection.setProtection(flagType, status);

        this.protectionRepository.saveProtection(plot, protection);
    }

    public boolean isProtection(Plot plot, FlagType flagType) {

        Protection protection = plot.getProtection();

        Option<Boolean> booleanOpt = protection.getFlagState(flagType);

        if (booleanOpt.isEmpty()) {

            boolean defaultState = this.getDefaultState(flagType);

            this.setProtection(plot, protection, flagType, defaultState);

            return defaultState;

        }

        return booleanOpt.get();
    }

    private boolean getDefaultState(FlagType flagType) {
        try {

            for (ProtectionConfiguration.ConfigProtection configProtection : this.protectionConfiguration.getProtections().values()) {
                if (configProtection.getType() == flagType) {
                    return configProtection.getDefaultProtection();
                }
            }
            throw new Exception("Tried to get flag '" + flagType + "' default state from config but found null");

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Metody do listenerów
     **/

    public boolean hasBypass(Player player) {
        return player.hasPermission("eternalplots.bypass");
    }

    public boolean isAllowed(Player player, Location location) {
        User user = this.userManager.getOrCreate(player.getUniqueId(), player.getName());
        Option<Region> regionOpt = this.plotManager.getRegion(location);

        if (regionOpt.isEmpty()) {
            return true;
        }
        Region region = regionOpt.get();

        Plot plot = this.plotManager.getPlot(region);

        if (plot.isExpired()) {
            return true;
        }

        return plot.isMember(user);
    }

    public Option<Plot> getPlot(Location location) {
        Option<Region> regionOpt = this.plotManager.getRegion(location);

        if (regionOpt.isEmpty()) {
            return Option.none();
        }

        Region region = regionOpt.get();

        Plot plot = this.plotManager.getPlot(region);

        return Option.of(plot);
    }

    public Cache<Block, UUID> getPlaced() {
        return this.placed;
    }

    public boolean isBadPotion(Collection<PotionEffect> effects) {
        for (NegativeEffects effect : NegativeEffects.values()) {
            for (PotionEffect potionEffect : effects) {
                if (effect.name().equalsIgnoreCase(potionEffect.getType().getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    public enum NegativeEffects {
        BAD_OMEN, BLINDNESS, CONFUSION, HARM, HUNGER, INCREASE_DAMAGE, POISON, SLOW, SLOW_DIGGING, UNLUCK, WEAKNESS, WITHER
    }
}
