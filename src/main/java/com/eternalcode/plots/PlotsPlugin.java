package com.eternalcode.plots;

import com.eternalcode.plots.adventure.LegacyColorProcessor;
import com.eternalcode.plots.command.oldcode.implementations.PlotCommand;
import com.eternalcode.plots.command.recoded.InvalidUsageMessage;
import com.eternalcode.plots.command.oldcode.arguments.InvitePlotArg;
import com.eternalcode.plots.command.oldcode.arguments.PlayerArg;
import com.eternalcode.plots.command.oldcode.arguments.PlotArg;
import com.eternalcode.plots.command.oldcode.arguments.SenderPlotArg;
import com.eternalcode.plots.command.oldcode.arguments.UserArg;
import com.eternalcode.plots.command.oldcode.contextual.AudienceContextual;
import com.eternalcode.plots.command.oldcode.contextual.PlayerContextual;
import com.eternalcode.plots.command.oldcode.contextual.UserContextual;
import com.eternalcode.plots.configuration.ConfigurationManager;
import com.eternalcode.plots.configuration.implementation.BlocksConfiguration;
import com.eternalcode.plots.configuration.implementation.LanguageConfiguration;
import com.eternalcode.plots.configuration.implementation.PluginConfiguration;
import com.eternalcode.plots.configuration.implementation.ProtectionConfiguration;
import com.eternalcode.plots.configuration.implementation.command.CommandsConfiguration;
import com.eternalcode.plots.configuration.implementation.command.ConfigCommand;
import com.eternalcode.plots.configuration.implementation.gui.PlotExtendConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.PlotFlagsConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.PlotMenuConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.PlotPanelConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.PlotPlayersConfiguration;
import com.eternalcode.plots.database.DatabaseManager;
import com.eternalcode.plots.database.OrmliteRepositoryPlot;
import com.eternalcode.plots.feature.border.BorderTask;
import com.eternalcode.plots.feature.delete.PlotDelete;
import com.eternalcode.plots.feature.invite.InviteManager;
import com.eternalcode.plots.feature.limit.PlotsLimit;
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
import com.eternalcode.plots.notification.NotificationAnnouncer;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.plot.protection.ProtectionManager;
import com.eternalcode.plots.plot.plotblock.PlotBlockMatcher;
import com.eternalcode.plots.plot.plotblock.PlotBlockService;
import com.eternalcode.plots.plot.region.RegionManager;
import com.eternalcode.plots.scheduler.BukkitSchedulerImpl;
import com.eternalcode.plots.scheduler.Scheduler;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserManager;
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

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class PlotsPlugin extends JavaPlugin {

    /**
     * Managers/Services
     **/
    private ProtectionManager protectionManager;
    private PlotBlockService plotBlockService;
    private RegionManager regionManager;
    private PlotManager plotManager;
    private UserManager userManager;
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
    private BukkitAudiences audienceProvider;
    private MiniMessage miniMessage;

    /**
     * Commands
     **/
    private LiteCommands<CommandSender> liteCommands;

    /**
     * Hooks
     **/
    private VaultProvider vaultProvider;

    @Override
    public void onEnable() {
        Stopwatch started = Stopwatch.createStarted();
        Server server = this.getServer();

        /* Hooks */

        this.vaultProvider = new VaultProvider(this);

        /* Managers/Services */
        this.scheduler = new BukkitSchedulerImpl(this);
        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyColorProcessor())
            .build();

        NotificationAnnouncer notificationAnnouncer = new NotificationAnnouncer(this.audienceProvider, miniMessage);

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


        OrmliteRepositoryPlot ormliteRepository = new OrmliteRepositoryPlot(this.databaseManager, this.scheduler);

        this.regionManager = new RegionManager();
        this.userManager = new UserManager(ormliteRepository);
        this.plotManager = new PlotManager(ormliteRepository, ormliteRepository, this.regionManager, protectionConfig);
        this.inviteManager = new InviteManager(this.plotManager);
        this.protectionManager = new ProtectionManager(ormliteRepository, protectionConfig, this.userManager, this.plotManager);
        this.plotDelete = new PlotDelete(this.plotManager);
        this.plotsLimit = new PlotsLimit(this.userManager, this.plotManager, pluginConfig);


        PlotBlockMatcher plotBlockMatcher = new PlotBlockMatcher(blocksConfig);
        this.plotBlockService = new PlotBlockService(blocksConfig, pluginConfig, this.plotManager, plotBlockMatcher);
        this.plotBlockService.setupPlotBlocks(this);

        BorderTask borderTask = new BorderTask(this.plotManager, server);
        this.scheduler.runTaskTimerAsynchronously(borderTask, 0L, 10L);

        /* Commands */

        // TODO: Fully re-code this command, but recode after release of litecommands v3.0
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
            .contextualBind(Audience.class, new AudienceContextual(this.audienceProvider))

            // type binds
            .typeBind(InviteManager.class, () -> this.inviteManager)
            .typeBind(BukkitAudiences.class, () -> this.audienceProvider)
            .typeBind(PlotManager.class, () -> this.plotManager)
            .typeBind(InviteManager.class, () -> this.inviteManager)
            .typeBind(MiniMessage.class, () -> this.miniMessage)
            .typeBind(PlotDelete.class, () -> this.plotDelete)
            .typeBind(BorderTask.class, () -> borderTask)
            .typeBind(LanguageConfiguration.class, () -> languageConfig)
            .typeBind(CommandsConfiguration.class, () -> commandsConfig)

            // commands
            .command(PlotCommand.class)

            .invalidUsageHandler(new InvalidUsageMessage(commandsConfig, notificationAnnouncer))

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
            new PlotBlockListener(pluginConfig, languageConfig, this.plotBlockService, this.userManager, this.plotManager, this.regionManager, this.plotsLimit, notificationAnnouncer, this),
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
            new LingeringPotionSplashListener(protectionConfig, this.plotManager),
            new PlayerBucketEmptyListener(this.protectionManager, protectionConfig),
            new PlayerEggThrowListener(this.protectionManager, protectionConfig),
            new PlayerFishListener(this.protectionManager, protectionConfig),
            new PlayerInteractAtEntityListener(this.protectionManager, protectionConfig),
            new PlayerInteractEntityListener(this.protectionManager, protectionConfig),
            new PlayerInteractListener(this.protectionManager, protectionConfig),
            new PotionSplashListener(protectionConfig, this.plotManager),
            new VehicleDamageListener(this.protectionManager, protectionConfig),
            new PlotMoveListener(this.plotManager)
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        new Metrics(this, 19792);

        long millis = started.elapsed(TimeUnit.MILLISECONDS);


        this.getLogger().info("Successfully loaded EternalPlots in " + millis + "ms");
    }

    @Override
    public void onDisable() {
        if (this.liteCommands != null) {
            this.liteCommands.getPlatform().unregisterAll();
        }

        if (this.databaseManager != null) {
            this.databaseManager.close();
        }
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

    public BukkitAudiences getAudienceProvider() {
        return this.audienceProvider;
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
