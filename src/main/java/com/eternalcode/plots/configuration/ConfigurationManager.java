package com.eternalcode.plots.configuration;

import com.eternalcode.plots.configuration.implementations.BlocksConfiguration;
import com.eternalcode.plots.configuration.implementations.LanguageConfiguration;
import com.eternalcode.plots.configuration.implementations.PluginConfiguration;
import com.eternalcode.plots.configuration.implementations.ProtectionConfiguration;
import com.eternalcode.plots.configuration.implementations.commands.CommandsConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotExtendConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotFlagsConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotMenuConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotPanelConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotPlayersConfiguration;
import com.eternalcode.plots.configuration.serializer.ConfigExtendSerializer;
import com.eternalcode.plots.configuration.serializer.ConfigItemSerializer;
import com.eternalcode.plots.configuration.serializer.PlotSectionSerializer;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.postprocessor.SectionSeparator;
import eu.okaeri.configs.serdes.SerdesRegistry;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.Getter;

import java.io.File;

public class ConfigurationManager {

    @Getter
    private final PluginConfiguration pluginConfiguration;
    @Getter
    private final PlotPanelConfiguration plotPanelConfiguration;
    @Getter
    private final PlotMenuConfiguration plotMenuConfiguration;
    @Getter
    private final BlocksConfiguration blocksConfiguration;
    @Getter
    private final LanguageConfiguration languageConfiguration;
    @Getter
    private final ProtectionConfiguration protectionConfiguration;
    @Getter
    private final PlotFlagsConfiguration plotFlagsConfiguration;
    @Getter
    private final CommandsConfiguration commandsConfiguration;
    @Getter
    private final PlotExtendConfiguration plotExtendConfiguration;
    @Getter
    private final PlotPlayersConfiguration plotPlayersConfiguration;

    public ConfigurationManager(File dataFolder) {
        this.pluginConfiguration = (PluginConfiguration) loadAndRender(PluginConfiguration.class, dataFolder, "config.yml");
        this.blocksConfiguration = (BlocksConfiguration) loadAndRender(BlocksConfiguration.class, dataFolder, "blocks.yml");
        this.protectionConfiguration = (ProtectionConfiguration) loadAndRender(ProtectionConfiguration.class, dataFolder, "protection.yml");
        this.commandsConfiguration = (CommandsConfiguration) loadAndRender(CommandsConfiguration.class, dataFolder, "commands.yml");
        this.plotPanelConfiguration = (PlotPanelConfiguration) loadAndRender(PlotPanelConfiguration.class, dataFolder, "gui/plot-panel.yml");
        this.plotMenuConfiguration = (PlotMenuConfiguration) loadAndRender(PlotMenuConfiguration.class, dataFolder, "gui/plot-menu.yml");
        this.plotFlagsConfiguration = (PlotFlagsConfiguration) loadAndRender(PlotFlagsConfiguration.class, dataFolder, "gui/plot-flags.yml");
        this.plotExtendConfiguration = (PlotExtendConfiguration) loadAndRender(PlotExtendConfiguration.class, dataFolder, "gui/plot-extend.yml");
        this.plotPlayersConfiguration = (PlotPlayersConfiguration) loadAndRender(PlotPlayersConfiguration.class, dataFolder, "gui/plot-players.yml");
        this.languageConfiguration = (LanguageConfiguration) loadAndRender(LanguageConfiguration.class, dataFolder, "lang/messages.yml");
    }

    public <T extends OkaeriConfig> OkaeriConfig loadAndRender(Class<T> config, File dataFolder, String filePath) {

        SerdesRegistry serdesRegistry = new SerdesRegistry();
        serdesRegistry.register(new ConfigItemSerializer());
        serdesRegistry.register(new PlotSectionSerializer());
        serdesRegistry.register(new ConfigExtendSerializer());

        return ConfigManager.create(config)
            .withConfigurer(new YamlSnakeYamlConfigurer("# ", SectionSeparator.NEW_LINE))
            .withSerdesPack(serdesRegistry.allSerdes())
            .withBindFile(dataFolder + "/" + filePath)
            .saveDefaults()
            .load(true);
    }
}
