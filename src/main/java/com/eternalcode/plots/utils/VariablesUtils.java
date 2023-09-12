package com.eternalcode.plots.utils;

import com.eternalcode.plots.plot.Plot;

import java.util.ArrayList;
import java.util.List;

public final class VariablesUtils {

    private VariablesUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static List<String> parsePlotVars(Plot plot, List<String> texts) {
        List<String> output = new ArrayList<>();

        for (String text : texts) {
            output.add(parsePlotVars(plot, text));
        }

        return output;
    }

    public static String parsePlotVars(Plot plot, String text) {
        String format = text;

        format = format.replace("{PLOT_NAME}", plot.getName());
        format = format.replace("{PLOT_UUID}", plot.getUuid().toString());
        format = format.replace("{PLOT_OWNER_NAME}", plot.getOwner().getUser().getName());
        format = format.replace("{PLOT_OWNER_UUID}", plot.getOwner().getUser().getUuid().toString());
        format = format.replace("{PLOT_MEMBERS}", String.valueOf(plot.getMembers().size()));
        format = format.replace("{PLOT_CREATED}", DateUtils.format(plot.getCreated()));
        format = format.replace("{PLOT_EXPIRES}", DateUtils.format(plot.getExpires()));

        format = format.replace("{PLOT_SIZE}", String.valueOf(plot.getRegion().getSize()));
        format = format.replace("{PLOT_RANGE}", String.valueOf(plot.getRegion().getRange()));
        format = format.replace("{PLOT_MIN-X}", String.valueOf(plot.getRegion().getPosMin().getX()));
        format = format.replace("{PLOT_MIN-Z}", String.valueOf(plot.getRegion().getPosMin().getZ()));
        format = format.replace("{PLOT_MAX-X}", String.valueOf(plot.getRegion().getPosMax().getX()));
        format = format.replace("{PLOT_MAX-Z}", String.valueOf(plot.getRegion().getPosMax().getZ()));
        format = format.replace("{PLOT_CENTER-X}", String.valueOf(plot.getRegion().getCenter().getX()));
        format = format.replace("{PLOT_CENTER-Y}", String.valueOf(plot.getRegion().getPosMax().getY()));
        format = format.replace("{PLOT_CENTER-Z}", String.valueOf(plot.getRegion().getPosMax().getZ()));

        return format;
    }

    public static List<String> parseVariable(List<String> list, String target, String replacement) {
        List<String> output = new ArrayList<>();

        for (String text : list) {
            output.add(text.replace(target, replacement));
        }

        return output;
    }

}
