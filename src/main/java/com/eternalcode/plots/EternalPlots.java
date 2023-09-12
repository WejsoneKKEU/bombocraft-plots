package com.eternalcode.plots;

import com.eternalcode.plots.command.InvalidUsageMessage;
import com.eternalcode.plots.command.arguments.InvitePlotArg;
import com.eternalcode.plots.command.arguments.PlayerArg;
import com.eternalcode.plots.command.arguments.PlotArg;
import com.eternalcode.plots.command.arguments.SenderPlotArg;
import com.eternalcode.plots.command.arguments.UserArg;
import com.eternalcode.plots.command.contextual.AudienceContextual;
import com.eternalcode.plots.command.contextual.PlayerContextual;
import com.eternalcode.plots.command.contextual.UserContextual;
import com.eternalcode.plots.command.implementations.PlotCommand;
import com.eternalcode.plots.configuration.ConfigurationManager;
import com.eternalcode.plots.configuration.implementations.BlocksConfiguration;
import com.eternalcode.plots.configuration.implementations.LanguageConfiguration;
import com.eternalcode.plots.configuration.implementations.PluginConfiguration;
import com.eternalcode.plots.configuration.implementations.ProtectionConfiguration;
import com.eternalcode.plots.configuration.implementations.commands.CommandsConfiguration;
import com.eternalcode.plots.configuration.implementations.commands.ConfigCommand;
import com.eternalcode.plots.configuration.implementations.gui.PlotExtendConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotFlagsConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotMenuConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotPanelConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotPlayersConfiguration;
import com.eternalcode.plots.database.DatabaseManager;
import com.eternalcode.plots.database.OrmliteRepository;
import com.eternalcode.plots.features.border.BorderTask;
import com.eternalcode.plots.features.delete.PlotDelete;
import com.eternalcode.plots.features.invite.InviteManager;
import com.eternalcode.plots.features.limit.PlotsLimit;
import com.eternalcode.plots.gui.GuiFactory;
import com.eternalcode.plots.hook.VaultProvider;
import com.eternalcode.plots.listener.PlayerJoinListener;
import com.eternalcode.plots.listener.PlotBlockListener;
import com.eternalcode.plots.listener.PlotMoveListener;
import com.eternalcode.plots.listener.protection.BlockBreakListener;
import com.eternalcode.plots.listener.protection.BlockDispenseListener;
import com.eternalcode.plots.listener.protection.BlockExplodeListener;
import com.eternalcode.plots.listener.protection.BlockFertilizeListener;
import com.eternalcode.plots.listener.protection.BlockFromToListener;
import com.eternalcode.plots.listener.protection.BlockPistonExtendListener;
import com.eternalcode.plots.listener.protection.BlockPistonRetractListener;
import com.eternalcode.plots.listener.protection.BlockPlaceListener;
import com.eternalcode.plots.listener.protection.EntityDamageByEntityListener;
import com.eternalcode.plots.listener.protection.EntityDamageListener;
import com.eternalcode.plots.listener.protection.EntityExplodeListener;
import com.eternalcode.plots.listener.protection.ExplosionPrimeListener;
import com.eternalcode.plots.listener.protection.HangingBreakByEntityListener;
import com.eternalcode.plots.listener.protection.LingeringPotionSplashListener;
import com.eternalcode.plots.listener.protection.PlayerBucketEmptyListener;
import com.eternalcode.plots.listener.protection.PlayerEggThrowListener;
import com.eternalcode.plots.listener.protection.PlayerFishListener;
import com.eternalcode.plots.listener.protection.PlayerInteractAtEntityListener;
import com.eternalcode.plots.listener.protection.PlayerInteractEntityListener;
import com.eternalcode.plots.listener.protection.PlayerInteractListener;
import com.eternalcode.plots.listener.protection.PotionSplashListener;
import com.eternalcode.plots.listener.protection.VehicleDamageListener;
import com.eternalcode.plots.plot.MemberFactory;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotFactory;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.plot.protection.FlagFactory;
import com.eternalcode.plots.plot.protection.ProtectionFactory;
import com.eternalcode.plots.plot.protection.ProtectionManager;
import com.eternalcode.plots.plotblock.PlotBlockService;
import com.eternalcode.plots.region.RegionFactory;
import com.eternalcode.plots.region.RegionManager;
import com.eternalcode.plots.scheduler.BukkitSchedulerImpl;
import com.eternalcode.plots.scheduler.Scheduler;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserFactory;
import com.eternalcode.plots.user.UserManager;
import com.eternalcode.plots.utils.LegacyColorProcessor;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.factory.CommandEditor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class EternalPlots extends JavaPlugin {

    /**
     * Plugin Instance
     **/
    private static EternalPlots instance;

    /**
     * Managers/Services
     **/
    private ProtectionManager protectionManager;
    private PlotBlockService plotBlockService;
    private RegionManager regionManager;
    private PlotManager plotManager;
    private UserManager userManager;
    private GuiFactory guiFactory;
    private InviteManager inviteManager;
    private PlotDelete plotDelete;
    private PlotsLimit plotsLimit;
    private Scheduler scheduler;

    /**
     * Configuration
     **/
    private ConfigurationManager configurationManager;

    /**
     * Database
     **/
    private DatabaseManager databaseManager;

    /**
     * Adventure
     **/
    private BukkitAudiences adventureAudiences;
    private MiniMessage miniMessage;

    /**
     * Commands
     **/
    private LiteCommands<CommandSender> liteCommands;

    /**
     * Hooks
     **/
    private VaultProvider vaultProvider;

    public static EternalPlots getInstance() {
        return EternalPlots.instance;
    }

    @Override
    public void onEnable() {
        Stopwatch started = Stopwatch.createStarted();

        instance = this;
        Server server = this.getServer();

        /* Hooks */

        this.vaultProvider = new VaultProvider(this);

        /* Managers/Services */
        this.scheduler = new BukkitSchedulerImpl(this);
        this.adventureAudiences = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyColorProcessor())
            .build();

        this.configurationManager = new ConfigurationManager(this.getDataFolder());

        PlotMenuConfiguration plotSelectorConfig = this.configurationManager.getPlotMenuConfiguration();
        ProtectionConfiguration protectionConfig = this.configurationManager.getProtectionConfiguration();
        PlotPanelConfiguration plotPanelConfig = this.configurationManager.getPlotPanelConfiguration();
        PlotFlagsConfiguration plotFlagsConfig = this.configurationManager.getPlotFlagsConfiguration();
        BlocksConfiguration blocksConfig = this.configurationManager.getBlocksConfiguration();
        LanguageConfiguration languageConfig = this.configurationManager.getLanguageConfiguration();
        PluginConfiguration pluginConfig = this.configurationManager.getPluginConfiguration();
        PlotExtendConfiguration plotExtendConfig = this.configurationManager.getPlotExtendConfiguration();
        PlotPlayersConfiguration plotPlayersConfig = this.configurationManager.getPlotPlayersConfiguration();
        CommandsConfiguration commandsConfig = this.configurationManager.getCommandsConfiguration();

        this.databaseManager = new DatabaseManager(pluginConfig, this.getDataFolder());
        this.databaseManager.connect();

        ProtectionFactory protectionFactory = new ProtectionFactory(protectionConfig);
        MemberFactory memberFactory = new MemberFactory();
        FlagFactory flagFactory = new FlagFactory();
        PlotFactory plotFactory = new PlotFactory(memberFactory, protectionFactory);
        RegionFactory regionFactory = new RegionFactory();
        UserFactory userFactory = new UserFactory();

        OrmliteRepository ormliteRepository = new OrmliteRepository(this.databaseManager, regionFactory, plotFactory, userFactory, protectionFactory, memberFactory, flagFactory, this.scheduler);

        this.regionManager = new RegionManager(regionFactory);
        this.userManager = new UserManager(ormliteRepository, userFactory);
        this.plotManager = new PlotManager(plotFactory, ormliteRepository, ormliteRepository, this.regionManager, memberFactory);
        this.inviteManager = new InviteManager(this.plotManager);
        this.protectionManager = new ProtectionManager(protectionFactory, ormliteRepository, protectionConfig, this.userManager, this.plotManager);
        this.plotDelete = new PlotDelete(this.plotManager);
        this.plotsLimit = new PlotsLimit(this.userManager, this.plotManager, pluginConfig);
        this.guiFactory = new GuiFactory(this.vaultProvider, this.configurationManager, plotSelectorConfig, protectionConfig, plotFlagsConfig, plotPanelConfig, languageConfig, pluginConfig, plotExtendConfig, this.protectionManager, plotPlayersConfig, userManager, this.plotManager, this.miniMessage);


        this.plotBlockService = new PlotBlockService(blocksConfig, pluginConfig, this.plotManager);
        this.plotBlockService.setupPlotBlocks(this);

        BorderTask borderTask = new BorderTask(this.plotManager, server);
        this.scheduler.runTaskTimerAsynchronously(borderTask, 0L, 10L);

        /* Commands */

        this.liteCommands = LiteBukkitFactory.builder(this.getServer(), "eternalplots")

            // arguments
            .argument(Player.class, new PlayerArg(this.getServer()))
            .argument(User.class, new UserArg(this.getServer(), this.userManager))
            .argument(Plot.class, new PlotArg(this.plotManager))
            .argument(Plot.class, "sender", new SenderPlotArg(this.plotManager, this.userManager))
            .argument(Plot.class, "invite", new InvitePlotArg(this.plotManager, this.userManager, this.inviteManager))

            // contextual binds
            .contextualBind(User.class, new UserContextual(this.userManager))
            .contextualBind(Player.class, new PlayerContextual())
            .contextualBind(Audience.class, new AudienceContextual(this.adventureAudiences))

            // type binds
            .typeBind(GuiFactory.class, () -> this.guiFactory)
            .typeBind(InviteManager.class, () -> this.inviteManager)
            .typeBind(BukkitAudiences.class, () -> this.adventureAudiences)
            .typeBind(PlotManager.class, () -> this.plotManager)
            .typeBind(InviteManager.class, () -> this.inviteManager)
            .typeBind(MiniMessage.class, () -> this.miniMessage)
            .typeBind(PlotDelete.class, () -> this.plotDelete)
            .typeBind(BorderTask.class, () -> borderTask)
            .typeBind(LanguageConfiguration.class, () -> languageConfig)
            .typeBind(CommandsConfiguration.class, () -> commandsConfig)

            // commands
            .command(PlotCommand.class)

            .invalidUsageHandler(new InvalidUsageMessage(commandsConfig))

            // commands configuration
            .commandEditor(PlotCommand.class, editor -> {

                CommandEditor.State state = editor
                    .name(commandsConfig.plotCommand.name)
                    .aliases(commandsConfig.plotCommand.aliases)
                    .cancel(!commandsConfig.plotCommand.enabled);

                for (Entry<String, ConfigCommand> entry : commandsConfig.subcommands.entrySet()) {
                    ConfigCommand command = entry.getValue();

                    state = state.editChild(entry.getKey(), subeditor -> subeditor
                        .name(command.name)
                        .aliases(command.aliases)
                        .cancel(!command.enabled)
                    );
                }

                return state;
            })

            .register();

        /* Listeners */
        Stream.of(
            new PlotBlockListener(pluginConfig, languageConfig, this.plotBlockService, this.userManager, this.plotManager, this.regionManager, this.plotsLimit, plugin),
            new PlayerJoinListener(this.userManager),
            new BlockBreakListener(this.protectionManager, protectionConfig),
            new BlockDispenseListener(protectionConfig, this.plotManager),
            new BlockExplodeListener(protectionConfig, this.plotManager),
            new BlockFertilizeListener(this.protectionManager, protectionConfig),
            new BlockFromToListener(this.protectionManager, protectionConfig, this.plotManager, server),
            new BlockPistonExtendListener(protectionConfig, this.plotManager),
            new BlockPistonRetractListener(protectionConfig, this.plotManager),
            new BlockPlaceListener(this.protectionManager, protectionConfig),
            new EntityDamageByEntityListener(this.protectionManager, protectionConfig, this.plotManager),
            new EntityDamageListener(this.protectionManager, protectionConfig, this.plotManager, this.userManager),
            new EntityExplodeListener(protectionConfig, this.plotManager),
            new ExplosionPrimeListener(protectionConfig, this.plotManager),
            new HangingBreakByEntityListener(this.protectionManager, protectionConfig, this.plotManager, this.userManager),
            new LingeringPotionSplashListener(this.protectionManager, protectionConfig, this.plotManager),
            new PlayerBucketEmptyListener(this.protectionManager, protectionConfig),
            new PlayerEggThrowListener(this.protectionManager, protectionConfig),
            new PlayerFishListener(this.protectionManager, protectionConfig),
            new PlayerInteractAtEntityListener(this.protectionManager, protectionConfig),
            new PlayerInteractEntityListener(this.protectionManager, protectionConfig),
            new PlayerInteractListener(this.protectionManager, protectionConfig),
            new PotionSplashListener(this.protectionManager, protectionConfig, this.plotManager),
            new VehicleDamageListener(this.protectionManager, protectionConfig),
            new PlotMoveListener(this.plotManager)
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        /* End */

        Metrics metrics = new Metrics(this, 15091);

        long millis = started.elapsed(TimeUnit.MILLISECONDS);


        List.of(
            "Author: " + Joiner.on(", ").join(this.getDescription().getAuthors()),
            "Version: " + this.getDescription().getVersion(),
            "Website: " + this.getDescription().getWebsite(),
            "Successfully loaded EternalPlots in " + millis + "ms"
        ).forEach(this.getLogger()::info);
    }

    @Override
    public void onDisable() {
        this.liteCommands.getPlatform().unregisterAll();
        this.databaseManager.close();
    }

    public ProtectionManager getProtectionManager() {
        return this.protectionManager;
    }

    public PlotBlockService getPlotBlockService() {
        return this.plotBlockService;
    }

    public RegionManager getRegionManager() {
        return this.regionManager;
    }

    public PlotManager getPlotManager() {
        return this.plotManager;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public GuiFactory getGuiFactory() {
        return this.guiFactory;
    }

    public InviteManager getInviteManager() {
        return this.inviteManager;
    }

    public PlotDelete getPlotDelete() {
        return this.plotDelete;
    }

    public PlotsLimit getPlotsLimit() {
        return this.plotsLimit;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public BukkitAudiences getAdventureAudiences() {
        return this.adventureAudiences;
    }

    public MiniMessage getMiniMessage() {
        return this.miniMessage;
    }

    public LiteCommands<CommandSender> getLiteCommands() {
        return this.liteCommands;
    }

    public VaultProvider getVaultProvider() {
        return this.vaultProvider;
    }
}
