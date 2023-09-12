package com.eternalcode.plots.plot;

import com.eternalcode.plots.user.User;
import com.eternalcode.plots.adventure.LegacyUtils;
import com.eternalcode.plots.utils.VariablesUtils;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.Set;

public class PlotHelper {

    /**
     * Gets the only player plot or empty option if he has more
     *
     * @param user    user object
     * @param plotOpt a present option if player gave us a plot in command args, otherwise empty option
     */
    public static Option<Plot> getPlot(PlotManager plotManager, User user, Option<Plot> plotOpt) {
        if (plotOpt.isEmpty()) {

            Set<Plot> userPlots = plotManager.getPlots(user);

            if (userPlots.size() != 1) {
                return Option.none();
            }

            return Option.of(userPlots.iterator().next());

        }

        return Option.of(plotOpt.get());
    }

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
