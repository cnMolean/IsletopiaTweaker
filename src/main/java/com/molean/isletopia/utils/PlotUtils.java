package com.molean.isletopia.utils;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.location.BlockLoc;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;

import java.util.*;

public class PlotUtils {
    private static final PlotAPI plotAPI = new PlotAPI();

    public static Plot getCurrentPlot(Player player) {
        PlotPlayer plotPlayer = plotAPI.wrapPlayer(player.getUniqueId());
        return plotPlayer.getCurrentPlot();
    }

    public static boolean hasCurrentPlotPermission(Player player) {
        Plot currentPlot = getCurrentPlot(player);
        if (currentPlot == null)
            return false;
        List<UUID> builder = new ArrayList<>();
        UUID owner = currentPlot.getOwner();
        builder.add(owner);
        HashSet<UUID> trusted = currentPlot.getTrusted();
        builder.addAll(trusted);
        return builder.contains(player.getUniqueId());
    }

    public static boolean isCurrentPlotOwner(Player player) {
        Plot currentPlot = getCurrentPlot(player);
        if (currentPlot == null)
            return false;
        UUID owner = currentPlot.getOwner();
        return player.getUniqueId().equals(owner);
    }


    public static Plot getPlot(Player player){
        PlotPlayer wrap = plotAPI.wrapPlayer(player.getName());
        Set<Plot> playerPlots = plotAPI.getPlayerPlots(wrap);
        if(playerPlots.size() < 1)
            return null;
        return playerPlots.iterator().next();
    }

    public static List<String> getTrusted(Plot plot){
        HashSet<UUID> trusted = plot.getTrusted();
        List<String> names = new ArrayList<>();
        for (UUID uuid : trusted) {
            PlotSquared.get().getImpromptuUUIDPipeline().getSingle(uuid, (s, throwable) -> names.add(s));
        }
        return names;
    }
}