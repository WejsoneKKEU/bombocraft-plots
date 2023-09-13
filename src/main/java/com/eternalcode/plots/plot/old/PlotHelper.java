package com.eternalcode.plots.plot.old;

import com.eternalcode.plots.adventure.LegacyUtils;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.util.old.VariablesUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import panda.std.Option;

import java.util.Set;

@Deprecated
@ApiStatus.ScheduledForRemoval
public class PlotHelper {

    public static String formatMessage(String message, String playerName, Option<Plot> plot) {
        String formatted = message;
        formatted = formatted.replace("{PLAYER}", playerName);

        if (plot.isPresent()) {
            formatted = VariablesUtils.parsePlotVars(plot.get(), formatted);
        }

        return LegacyUtils.color(formatted);
    }

    public static String formatMessage(String message, Player player, Option<Plot> plot) {
        return formatMessage(message, player.getName(), plot);
    }

    public static String formatMessage(String message, User user, Option<Plot> plot) {
        return formatMessage(message, user.getName(), plot);
    }

}
