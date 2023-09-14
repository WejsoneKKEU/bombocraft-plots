package com.eternalcode.plots;

import com.eternalcode.plots.adventure.LegacyColorProcessor;
import com.eternalcode.plots.command.oldcode.arguments.InvitePlotArg;
import com.eternalcode.plots.command.oldcode.arguments.PlayerArg;
import com.eternalcode.plots.command.oldcode.arguments.PlotArg;
import com.eternalcode.plots.command.oldcode.arguments.SenderPlotArg;
import com.eternalcode.plots.command.oldcode.arguments.UserArg;
import com.eternalcode.plots.command.oldcode.contextual.AudienceContextual;
import com.eternalcode.plots.command.oldcode.contextual.PlayerContextual;
import com.eternalcode.plots.command.oldcode.contextual.UserContextual;
import com.eternalcode.plots.command.recoded.InvalidUsage;
import com.eternalcode.plots.command.recoded.implementation.BorderCommand;
import com.eternalcode.plots.command.recoded.implementation.DeleteCommand;
import com.eternalcode.plots.command.recoded.implementation.InviteCommand;
import com.eternalcode.plots.command.recoded.implementation.JoinCommand;
import com.eternalcode.plots.command.recoded.implementation.KickCommand;
import com.eternalcode.plots.command.recoded.implementation.PanelCommand;
import com.eternalcode.plots.command.recoded.implementation.PlotCommand;
import com.eternalcode.plots.command.recoded.implementation.RejectCommand;
import com.eternalcode.plots.configuration.ConfigurationManager;
import com.eternalcode.plots.configuration.implementation.BlocksConfiguration;
import com.eternalcode.plots.configuration.implementation.MessageConfiguration;
import com.eternalcode.plots.configuration.implementation.PluginConfiguration;
import com.eternalcode.plots.configuration.implementation.ProtectionConfiguration;
import com.eternalcode.plots.configuration.implementation.command.CommandsConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.PlotExtendConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.PlotFlagsConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.PlotMenuConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.PlotPanelConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.PlotPlayersConfiguration;
import com.eternalcode.plots.database.recoded.DatabaseManager;
import com.eternalcode.plots.database.recoded.wrapper.PlotFlagRepositoryOrmLite;
import com.eternalcode.plots.database.recoded.wrapper.PlotMemberRepositoryOrmLite;
import com.eternalcode.plots.database.recoded.wrapper.PlotRepositoryOrmLite;
import com.eternalcode.plots.database.recoded.wrapper.RegionRepositoryOrmLite;
import com.eternalcode.plots.database.recoded.wrapper.UserRepositoryOrmLite;
import com.eternalcode.plots.feature.border.BorderService;
import com.eternalcode.plots.feature.border.BorderTask;
import com.eternalcode.plots.feature.invite.InviteManager;
import com.eternalcode.plots.feature.limit.PlotsLimit;
import com.eternalcode.plots.gui.InventoryActions;
import com.eternalcode.plots.gui.PlotExtendInventory;
import com.eternalcode.plots.gui.PlotFlagInventory;
import com.eternalcode.plots.gui.PlotListInventory;
import com.eternalcode.plots.gui.PlotMembersInventory;
import com.eternalcode.plots.gui.PlotPanelInventory;
import com.eternalcode.plots.hook.VaultProvider;
import com.eternalcode.plots.listener.PlayerJoinListener;
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
import com.eternalcode.plots.notification.NotificationBroadcaster;
import com.eternalcode.plots.plot.old.PlotManager;
import com.eternalcode.plots.plot.PlotRepository;
import com.eternalcode.plots.plot.recoded.member.PlotMemberService;
import com.eternalcode.plots.plot.recoded.member.PlotMembersRepository;
import com.eternalcode.plots.plot.old.plotblock.old.PlotBlockMatcher;
import com.eternalcode.plots.plot.old.plotblock.old.PlotBlockService;
import com.eternalcode.plots.plot.old.plotblock.recoded.PlotBlockController;
import com.eternalcode.plots.plot.old.protection.ProtectionManager;
import com.eternalcode.plots.plot.old.protection.ProtectionRepository;
import com.eternalcode.plots.plot.old.region.RegionManager;
import com.eternalcode.plots.plot.old.region.RegionRepository;
import com.eternalcode.plots.scheduler.BukkitSchedulerImpl;
import com.eternalcode.plots.scheduler.Scheduler;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserManager;
import com.eternalcode.plots.user.UserRepository;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
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
        this.audienceProvider = BukkitAudiences.create(this);
        this.configurationManager = new ConfigurationManager(this.getDataFolder());
        this.miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyColorProcessor())
            .build();
        this.scheduler = new BukkitSchedulerImpl(this);

        PlotMemberService plotMemberService = new PlotMemberService();
        NotificationBroadcaster notificationBroadcaster = new NotificationBroadcaster(this.audienceProvider, miniMessage);

        /* Configurations (by alphabetical order) */
        BlocksConfiguration blocksConfig = this.configurationManager.getBlocksConfiguration();
        CommandsConfiguration commandsConfig = this.configurationManager.getCommandsConfiguration();
        MessageConfiguration languageConfig = this.configurationManager.getLanguageConfiguration();
        PluginConfiguration pluginConfig = this.configurationManager.getPluginConfiguration();
        PlotExtendConfiguration plotExtendConfig = this.configurationManager.getPlotExtendConfiguration();
        PlotFlagsConfiguration plotFlagsConfig = this.configurationManager.getPlotFlagsConfiguration();
        PlotPanelConfiguration plotPanelConfig = this.configurationManager.getPlotPanelConfiguration();
        PlotPlayersConfiguration plotPlayersConfig = this.configurationManager.getPlotPlayersConfiguration();
        PlotMenuConfiguration plotSelectorConfig = this.configurationManager.getPlotMenuConfiguration();
        ProtectionConfiguration protectionConfig = this.configurationManager.getProtectionConfiguration();

        /* Repositories */
        ProtectionRepository protectionRepository;
        PlotMembersRepository plotMembersRepository;
        PlotRepository plotRepository;
        RegionRepository regionRepository;
        UserRepository userRepository;

        try {
            this.databaseManager = new DatabaseManager(pluginConfig, this.getLogger(), this.getDataFolder());
            this.databaseManager.connect();

            protectionRepository = PlotFlagRepositoryOrmLite.create(this.databaseManager, this.scheduler);
            plotMembersRepository = PlotMemberRepositoryOrmLite.create(this.databaseManager, this.scheduler);
            plotRepository = PlotRepositoryOrmLite.create(this.databaseManager, this.scheduler);
            regionRepository = RegionRepositoryOrmLite.create(this.databaseManager, this.scheduler);
            userRepository = UserRepositoryOrmLite.create(this.databaseManager, this.scheduler);
        } catch (Exception exception) {
            this.getLogger().severe("Failed to connect to database");
            exception.printStackTrace();
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        /* Managers */
        this.inviteManager = new InviteManager(this.plotManager);
        this.plotManager = new PlotManager(plotMembersRepository,plotRepository, this.regionManager, protectionConfig, plotMemberService);
        this.plotsLimit = new PlotsLimit(this.userManager, this.plotManager, pluginConfig);
        this.protectionManager = new ProtectionManager(protectionRepository, protectionConfig, this.userManager, this.plotManager);
        this.regionManager = new RegionManager();
        this.userManager = new UserManager(userRepository);

        /* Services and Matchers */
        BorderService borderService = new BorderService();
        PlotBlockMatcher plotBlockMatcher = new PlotBlockMatcher(blocksConfig);

        this.plotBlockService = new PlotBlockService(blocksConfig, pluginConfig, this.plotManager, plotBlockMatcher);
        this.plotBlockService.setupPlotBlocks(this);

        BorderTask borderTask = new BorderTask(this.plotManager, server, borderService);
        this.scheduler.timerAsync(borderTask, Duration.ZERO, Duration.ofMillis(10L));

        // shitty inventory
        InventoryActions inventoryActions = new InventoryActions(this.vaultProvider, this.plotManager, this.userManager, this.configurationManager, this, notificationBroadcaster);

        // inventories
        PlotListInventory plotListInventory = new PlotListInventory();
        PlotPanelInventory plotPanelInventory = new PlotPanelInventory();
        PlotExtendInventory plotExtendInventory = new PlotExtendInventory();
        PlotFlagInventory plotFlagInventory = new PlotFlagInventory();
        PlotMembersInventory plotMembersInventory = new PlotMembersInventory();



        /* Commands */

        // TODO: Fully re-code this command, but recode after release of litecommands v3.0
        this.liteCommands = LiteBukkitFactory.builder(this.getServer(), "eternalplots")

            // arguments
            .argument(Player.class, new PlayerArg(this.getServer()))
            .argument(User.class, new UserArg(this.getServer(), this.userManager))
            .argument(Plot.class, new PlotArg(this.plotManager))
            .argument(Plot.class, "sender", new SenderPlotArg(this.plotManager, plotMemberService, this.userManager))
            .argument(Plot.class, "invite", new InvitePlotArg(this.plotManager, this.userManager, this.inviteManager))

            // contextual binds
            .contextualBind(User.class, new UserContextual(this.userManager))
            .contextualBind(Player.class, new PlayerContextual())
            .contextualBind(Audience.class, new AudienceContextual(this.audienceProvider))

            // commands
            .commandInstance(
                new BorderCommand(borderTask, languageConfig, notificationBroadcaster),
                new DeleteCommand(languageConfig, notificationBroadcaster, this.plotManager),
                new InviteCommand(this.plotManager, notificationBroadcaster, this.inviteManager, languageConfig, plotMemberService),
                new JoinCommand(inviteManager, notificationBroadcaster, plotMemberService, languageConfig),
                new KickCommand(plotManager, notificationBroadcaster, languageConfig, plotMemberService),
                new PanelCommand(),
                new PlotCommand(notificationBroadcaster, languageConfig),
                new RejectCommand(inviteManager, notificationBroadcaster, languageConfig)
            )

            .invalidUsageHandler(new InvalidUsage(commandsConfig, notificationBroadcaster))

/*            // commands configuration
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
            })*/

            .register();

        /* Listeners */
        Stream.of(
            // Recoded

            // almost-recoded
            new PlotBlockController(pluginConfig, languageConfig, this.plotBlockService, this.userManager, this.plotManager, this.regionManager, this.plotsLimit, notificationBroadcaster, this),


            // old
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
