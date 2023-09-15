package com.eternalcode.plots.notgood;

import com.eternalcode.plots.good.adventure.LegacyColorProcessor;
import com.eternalcode.plots.database.recoded.DatabaseManager;
import com.eternalcode.plots.database.recoded.wrapper.PlotFlagRepositoryOrmLite;
import com.eternalcode.plots.database.recoded.wrapper.PlotMemberRepositoryOrmLite;
import com.eternalcode.plots.database.recoded.wrapper.PlotRepositoryOrmLite;
import com.eternalcode.plots.database.recoded.wrapper.RegionRepositoryOrmLite;
import com.eternalcode.plots.database.recoded.wrapper.UserRepositoryOrmLite;
import com.eternalcode.plots.notgood.hook.VaultProvider;
import com.eternalcode.plots.notgood.plot.recoded.member.PlotMemberService;
import com.eternalcode.plots.notgood.plot.recoded.member.PlotMembersRepository;
import com.eternalcode.plots.good.scheduler.BukkitSchedulerImpl;
import com.eternalcode.plots.good.scheduler.Scheduler;
import com.google.common.base.Stopwatch;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class PlotsPlugin extends JavaPlugin {

    private BukkitAudiences audienceProvider;
    private MiniMessage miniMessage;
    private Scheduler scheduler;

    private VaultProvider vaultProvider;

    @Override
    public void onEnable() {
        Stopwatch started = Stopwatch.createStarted();
        Server server = this.getServer();


        this.vaultProvider = new VaultProvider(this);

        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyColorProcessor())
            .build();

        this.scheduler = new BukkitSchedulerImpl(this);


        Stream.of(

        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        new Metrics(this, 19792);

        long millis = started.elapsed(TimeUnit.MILLISECONDS);
        this.getLogger().info("Successfully loaded EternalPlots in " + millis + "ms");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
