package com.eternalcode.plots.command.implementations;

import com.eternalcode.plots.configuration.implementations.LanguageConfiguration;
import com.eternalcode.plots.configuration.implementations.commands.CommandsConfiguration;
import com.eternalcode.plots.features.border.BorderTask;
import com.eternalcode.plots.features.delete.PlotDelete;
import com.eternalcode.plots.features.invite.InviteManager;
import com.eternalcode.plots.gui.GuiFactory;
import com.eternalcode.plots.plot.Member;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotHelper;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.adventure.LegacyUtils;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.By;
import dev.rollczi.litecommands.argument.option.Opt;
import dev.rollczi.litecommands.command.amount.Min;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.section.Section;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Option;

@Section(route = "plot")
public class PlotCommand {

    private final GuiFactory guiFactory;
    private final InviteManager inviteManager;
    private final BukkitAudiences bukkitAudiences;
    private final PlotManager plotManager;
    private final CommandsConfiguration commandsConfiguration;
    private final LanguageConfiguration lang;
    private final MiniMessage miniMessage;
    private final PlotDelete plotDelete;
    private final BorderTask borderTask;

    public PlotCommand(GuiFactory guiFactory, InviteManager inviteManager, BukkitAudiences bukkitAudiences, PlotManager plotManager, CommandsConfiguration commandsConfiguration, LanguageConfiguration languageConfiguration, MiniMessage miniMessage, PlotDelete plotDelete, BorderTask borderTask) {
        this.guiFactory = guiFactory;
        this.inviteManager = inviteManager;
        this.bukkitAudiences = bukkitAudiences;
        this.plotManager = plotManager;
        this.commandsConfiguration = commandsConfiguration;
        this.lang = languageConfiguration;
        this.miniMessage = miniMessage;
        this.plotDelete = plotDelete;
        this.borderTask = borderTask;
    }

    @Execute(required = 0)
    public void main(Player player, User user) {
        Gui gui = this.guiFactory.createSelectorGui(user).getGui();

        gui.open(player);
    }

    @Execute(route = "border")
    public void border(Player player) {

        if (!this.borderTask.hasShowed(player)) {
            this.borderTask.show(player);
            player.sendMessage(LegacyUtils.color(lang.commands.showBorder));
            return;
        }

        this.borderTask.hide(player);
        player.sendMessage(LegacyUtils.color(lang.commands.hideBorder));
    }

    @Execute(route = "panel")
    public void panel(Player player, User user, @Opt Option<Plot> plotOpt) {
        if (plotOpt.isEmpty()) {
            Gui gui = this.guiFactory.createSelectorGui(user).getGui();
            gui.open(player);
            return;
        }

        Plot plot = plotOpt.get();
        Gui gui = this.guiFactory.createPlotPanelGui(plot).getGui();

        gui.open(player);
    }

    @Execute(route = "delete")
    @Min(1)
    public void delete(Player player, User user, @Arg @By("sender") Plot plot) {

        if (!plot.isOwner(user)) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.notOwner, player, Option.of(plot)));
            return;
        }

        this.plotDelete.deletePlot(plot, player);
    }

    @Execute(route = "invite")
    @Min(1)
    public void invite(Player player, User user, @Arg User invited, @Opt Option<Plot> plotOpt) {

        if (PlotHelper.getPlot(this.plotManager, user, plotOpt).isEmpty()) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.needPlot, player, plotOpt));
            return;
        }

        Plot plot = PlotHelper.getPlot(this.plotManager, user, plotOpt).get();

        if (!plot.isOwner(user)) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.notOwner, player, Option.of(plot)));
            return;
        }

        if (plot.isMember(invited)) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.isAlreadyMember, invited, Option.of(plot)));
            return;
        }

        if (this.inviteManager.hasInvite(invited, plot)) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.hasAlreadyInvite, invited, Option.of(plot)));
            return;
        }

        this.inviteManager.create(invited, plot);

        player.sendMessage(PlotHelper.formatMessage(lang.commands.successInvite, invited, Option.of(plot)));

        Audience invitedPlayer = this.bukkitAudiences.player(invited.getUuid());

        //String text =
        //    "<color:#22ff00>Otrzymałeś zaproszenie do {PLOT} " +
        //    "<b><color:#00ffaa><click:run_command:'/{PLOT_CMD} {JOIN} {PLOT}'>" +
        //    "<hover:show_text:'Kliknij aby dołączyć'>[Zaakceptuj]</hover></click></color></b>" +
        //    " <b><color:#ff6040><hover:show_text:'Kliknij aby odrzucić'><click:run_command:" +
        //    "'/{PLOT_CMD} {REJECT} {PLOT}'>[Odrzuć]</click></hover></color></b></color>";

        String text =
            "<#14E36C>Otrzymałeś zaproszenie do działki {PLOT}, wpisz &2/{PLOT_CMD} {JOIN} {PLOT} <#14E36C>aby dołączyć";

        text = text.replace("{PLOT_CMD}", this.commandsConfiguration.plotCommand.name);
        text = text.replace("{PLOT}", plot.getName());
        text = text.replace("{JOIN}", this.commandsConfiguration.subcommands.get("join").name);
        text = text.replace("{REJECT}", this.commandsConfiguration.subcommands.get("reject").name);

        Bukkit.getLogger().info(text);

        invitedPlayer.sendMessage(miniMessage.deserialize(text));
    }

    @Execute(route = "kick")
    @Min(1)
    public void kick(Player player, User user, @Arg User kicked, @Opt Option<Plot> plotOpt) {
        if (PlotHelper.getPlot(this.plotManager, user, plotOpt).isEmpty()) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.needPlot, player, plotOpt));
            return;
        }

        Plot plot = PlotHelper.getPlot(this.plotManager, user, plotOpt).get();

        if (!plot.isOwner(user)) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.notOwner, player, Option.of(plot)));
            return;
        }

        if (!plot.isMember(kicked)) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.notMember, kicked, Option.of(plot)));
            return;
        }

        if (kicked.equals(user)) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.kickOwner, kicked, Option.of(plot)));
            return;
        }

        this.plotManager.removeMember(plot, kicked);

        player.sendMessage(PlotHelper.formatMessage(lang.commands.successKick, kicked, Option.of(plot)));

        Audience kickedAudience = this.bukkitAudiences.player(kicked.getUuid());

        kickedAudience.sendMessage(miniMessage.deserialize("<red>Zostałeś wyrzucony z " + plot.getName()));
    }

    @Execute(route = "join")
    @Min(1)
    public void join(Player player, User user, @Arg @By("invite") Plot plot) {
        if (plot.isMember(user)) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.isAlreadyMember2, player, Option.of(plot)));
            return;
        }

        if (!this.inviteManager.hasInvite(user, plot)) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.noInvite, player, Option.of(plot)));
            return;
        }

        this.inviteManager.accept(user, plot);

        player.sendMessage(PlotHelper.formatMessage(lang.commands.successAcceptInvite, player, Option.of(plot)));

        for (Member member : plot.getMembers()) {
            User memberUser = member.getUser();
            Audience audience = bukkitAudiences.player(memberUser.getUuid());

            audience.sendMessage(miniMessage.deserialize("<#14E36C>Gracz " + player.getName() + " dołączył do " + plot.getName()));
        }

    }

    @Execute(route = "reject")
    @Min(1)
    public void reject(Player player, User user, @Arg @By("invite") Plot plot) {
        if (!this.inviteManager.hasInvite(user, plot)) {
            player.sendMessage(PlotHelper.formatMessage(lang.commands.noInvite, player, Option.of(plot)));
            return;
        }

        player.sendMessage(PlotHelper.formatMessage(lang.commands.successRejectInvite, player, Option.of(plot)));

        Audience audience = this.bukkitAudiences.player(plot.getOwner().getUuid());
        audience.sendMessage(miniMessage.deserialize("<red>Gracz " + player.getName() + " odrzucił zaproszenie do " + plot.getName()));

        this.inviteManager.decline(user, plot);
    }
}
