package com.eternalcode.plots.plotblock;

import com.eternalcode.plots.configuration.implementations.BlocksConfiguration;
import com.eternalcode.plots.configuration.implementations.PluginConfiguration;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.position.PositionAdapter;
import com.eternalcode.plots.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import panda.std.Option;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

// TODO: Refactor this class
public class PlotBlockService {

    private final BlocksConfiguration blocksConfiguration;
    private final PluginConfiguration pluginConfiguration;
    private final PlotManager plotManager;

    private HashMap<BlocksConfiguration.PlotBlock, Integer> plotBlocks = new HashMap<>();

    public PlotBlockService(BlocksConfiguration blocksConfiguration, PluginConfiguration pluginConfiguration, PlotManager plotManager) {
        this.blocksConfiguration = blocksConfiguration;
        this.pluginConfiguration = pluginConfiguration;
        this.plotManager = plotManager;
    }

    public boolean isPlotBlock(ItemStack itemStack) {
        return getPlotBlock(itemStack).isPresent();
    }

    private Option<BlocksConfiguration.PlotBlock> getPlotBlock(ItemStack itemStack) {
        for (BlocksConfiguration.PlotBlock plotBlock : this.plotBlocks.keySet()) {

            ItemStack block = BlocksConfiguration.getItemStack(plotBlock);

            if (itemStack.getType() != block.getType()) {
                continue; // nie zgadza sie
            }
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta == null || block.getItemMeta() == null) {

                if (itemMeta == null && block.getItemMeta() == null) {
                    return Option.of(plotBlock);
                }
                continue; // nie zgadza sie
            }

            if (!itemMeta.getDisplayName().equalsIgnoreCase(block.getItemMeta().getDisplayName())) {
                continue; // nie zgadza sie
            }

            if (!Objects.equals(itemMeta.getLore(), block.getItemMeta().getLore())) {
                continue; // nie zgadza sie
            }
            return Option.of(plotBlock);

        }

        return Option.none();

    }

    public int getPlotBlockStartSize(ItemStack itemStack) {
        return this.plotBlocks.get(getPlotBlock(itemStack).get());
    }

    public void setupPlotBlocks(Plugin plugin) {

        this.plotBlocks = new HashMap<>();

        int id = 0;
        for (BlocksConfiguration.PlotBlock plotBlock : this.blocksConfiguration.plotBlocks.values()) {

            BlocksConfiguration.addRecipe(plotBlock, "EternalPlotsCuboid" + id, plugin);
            this.plotBlocks.put(plotBlock, plotBlock.startSize);

            id++;
        }
    }

    public boolean canSetupPlot(Location loc, int startSize) {

        if (this.plotManager.getRegion(loc).isPresent()) {
            return false;
        }

        int maxSize = this.pluginConfiguration.plot.maxSize;
        int addonSize = this.pluginConfiguration.spaceBlocks;
        int size = (maxSize / 2) + addonSize; // range i dodatkowe bloki

        List<Location> locations = Arrays.asList(
            new Location(loc.getWorld(), loc.getX() + size, loc.getY(), loc.getZ() + size),
            new Location(loc.getWorld(), loc.getX() - size, loc.getY(), loc.getZ() - size),
            new Location(loc.getWorld(), loc.getX() - size, loc.getY(), loc.getZ() + size),
            new Location(loc.getWorld(), loc.getX() + size, loc.getY(), loc.getZ() - size),
            new Location(loc.getWorld(), loc.getX() + size, loc.getY(), loc.getZ()),
            new Location(loc.getWorld(), loc.getX() - size, loc.getY(), loc.getZ()),
            new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + size),
            new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() - size),
            new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ())
        );

        for (Plot plot : this.plotManager.getPlots()) {

            Location center = PositionAdapter.convert(plot.getRegion().getCenter());

            Location plotPos1 = new Location(center.getWorld(), center.getX() + size, loc.getY(), center.getZ() + size);
            Location plotPos2 = new Location(center.getWorld(), center.getX() - size, loc.getY(), center.getZ() - size);

            for (Location location : locations) {

                if (LocationUtils.isIn(plotPos1, plotPos2, location)) {
                    return false;
                }

            }
        }

        return true;
    }

    public boolean isSafeRegion(Location location) {

        return !((location.getX() < 250 && location.getX() > -250) && (location.getZ() < 250 && location.getZ() > -250));
    }
}
