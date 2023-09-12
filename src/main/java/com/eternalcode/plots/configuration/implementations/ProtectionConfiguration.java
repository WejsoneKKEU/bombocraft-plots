package com.eternalcode.plots.configuration.implementations;

import com.eternalcode.plots.plot.protection.FlagType;
import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Map;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class ProtectionConfiguration extends OkaeriConfig {

    @Comment({ "ConfigProtection flags for plots (not plot members)", "enabled - is protection enabled", "displayInGui - is protection visible in flags plot gui" })
    @Getter
    Map<String, ConfigProtection> protections = new ImmutableMap.Builder<String, ConfigProtection>()
        .put("pvp", new ConfigProtection(FlagType.PVP, true, true, Material.DIAMOND_SWORD, "Players PVP", "PVP between players on plot"))
        .put("block-place", new ConfigProtection(FlagType.BLOCK_PLACE, true, true, Material.DIAMOND_SHOVEL, "Placing Blocks", "Placing blocks on plot"))
        .put("block-break", new ConfigProtection(FlagType.BLOCK_BREAK, true, true, Material.DIAMOND_PICKAXE, "Breaking Blocks", "Breaking blocks on plot"))
        .put("friendly-mobs", new ConfigProtection(FlagType.FRIENDLY_MOBS, true, true, Material.CARROT, "Friendly Mobs", "Attacking friendly mobs/nametag mobs on plot"))
        .put("monsters", new ConfigProtection(FlagType.MONSTERS, false, true, Material.ZOMBIE_HEAD, "Monsters", "Attacking monsters on plot"))
        .put("vehicle-destroy", new ConfigProtection(FlagType.VEHICLE_DESTROY, true, true, Material.OAK_BOAT, "Vehicles Destroying", "Destroying vehicles on plot"))
        .put("clicked-entities", new ConfigProtection(FlagType.CLICKED_ENTITIES, true, true, Material.VILLAGER_SPAWN_EGG, "Clicking Mobs", "NPC, armor-stands protection on plot"))
        .put("chest-access", new ConfigProtection(FlagType.CHEST_ACCESS, true, true, Material.CHEST, "Chest-like Access", "Open chests, furnaces, craftings etc. on plot"))
        .put("use", new ConfigProtection(FlagType.USE, true, true, Material.DARK_OAK_DOOR, "Use Blocks", "Open doors, click buttons, levers etc. on plot"))
        .build();

    @Comment({ "", "Griefing protection flags for plots (not plot members)", "enabled - is protection enabled", "displayInGui - is protection visible in flags plot gui" })
    @Getter
    SectionGriefing griefing = new SectionGriefing();

    public static class SectionGriefing extends OkaeriConfig {

        @Comment({ "", "Can friendly mobs + nametag mobs get damage by explosion, freezing, potions etc. on plot?" })
        @Getter
        Griefing friendlyMobs = new Griefing(true);

        @Comment({ "", "Can monsters get damage by explosion, freezing, potions etc. on plot?" })
        @Getter
        Griefing monsters = new Griefing(false);

        @Comment({ "", "Can water/lava spill over to plot?" })
        @Getter
        Griefing waterLavaFlow = new Griefing(true);

        @Comment({ "", "Can guests spawn chickens by egg throwing on plot?" })
        @Getter
        Griefing eggThrow = new Griefing(true);

        @Comment({ "", "Can guests fish other entities by fishing rod on plot?" })
        @Getter
        Griefing fishingRod = new Griefing(true);

        @Comment({ "", "Can tnt, creeper etc. explode on plot?" })
        @Getter
        Griefing explosion = new Griefing(true);

        @Comment({ "", "Can guests destroy farm by jumping on plot?" })
        @Getter
        Griefing farm = new Griefing(true);

        @Comment({ "", "Can guests move blocks from/to plot using pistons?" })
        @Getter
        Griefing piston = new Griefing(true);

        @Comment({ "", "Can guests use dispensers behind plot border?" })
        @Getter
        Griefing dispenser = new Griefing(true);

        @Comment({ "", "Can guests use bone meal etc. behind plot border?" })
        @Getter
        Griefing fertilize = new Griefing(true);

        @Comment({ "", "Can all players receive negative effects on plot?" })
        @Getter
        Griefing negativeEffects = new Griefing(true);


    }

    public static class ConfigProtection extends OkaeriConfig {

        @Getter
        private FlagType type;

        @CustomKey("default-protection")
        private boolean defaultProtection = true;
        @Getter
        private boolean editInGui = true;
        @Getter
        private Material guiMaterial = Material.STONE;
        @Getter
        private String guiName = "flag name in gui";
        @Getter
        private String guiDescription = "flag description in gui";

        private ConfigProtection() {
        }

        private ConfigProtection(FlagType flagType, boolean defaultProtection, boolean editInGui, Material guiMaterial, String guiName, String guiDescription) {
            this.type = flagType;
            this.editInGui = editInGui;
            this.defaultProtection = defaultProtection;
            this.guiMaterial = guiMaterial;
            this.guiName = guiName;
            this.guiDescription = guiDescription;
        }

        public boolean getDefaultProtection() {
            return defaultProtection;
        }
    }

    public static class Griefing extends OkaeriConfig {

        @Getter
        @CustomKey("protected")
        private boolean protection = true;

        private Griefing() {

        }

        private Griefing(boolean protection) {
            this.protection = protection;
        }
    }

}






