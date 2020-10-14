package com.molean.isletopia.utils;

import com.destroystokyo.paper.Title;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.distribute.individual.TellCommand;
import com.molean.isletopia.distribute.individual.VisitCommand;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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

    public static Plot getPlot(Player player) {
        return getPlot(player.getName());
    }

    public static Plot getPlot(String player) {
        String server = ServerInfoUpdater.getServerName();
        PlotId plotId = PlotDao.getPlotPosition(server, player);
        return PlotSquared.get().getFirstPlotArea().getPlot(plotId);
    }

    public static List<String> getTrusted(Plot plot) {
        HashSet<UUID> trusted = plot.getTrusted();
        List<String> names = new ArrayList<>();
        for (UUID uuid : trusted) {
            PlotSquared.get().getImpromptuUUIDPipeline().getSingle(uuid, (s, throwable) -> names.add(s));
        }
        return names;
    }

    public static void localServerTeleport(Player player, String target) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            PlotPlayer plotPlayer = plotAPI.wrapPlayer(target);
            Plot plot = getPlot(target);
            plot.teleportPlayer(plotPlayer, TeleportCause.PLUGIN, aBoolean -> {
                PlotId id = plot.getId();
                String localServerName = getLocalServerName();
                String title = "§6%1%:%2%,%3%"
                        .replace("%1%", localServerName)
                        .replace("%2%", id.getX() + "")
                        .replace("%3%", id.getY() + "");
                String subtitle = "§3由 %1% 所有".replace("%1%", player.getName());
                player.sendTitle(new Title(title, subtitle, 20, 40, 20));
            });
        });
    }

    public static void universalTeleport(Player player, String target) {
        VisitCommand.visit(player, target);
    }

    public static void universalMessage(String target, String message) {
        TellCommand.sendMessageToPlayer(target, message);
    }

    private static String getLocalServerName() {
        String localName;
        switch (ServerInfoUpdater.getServerName()) {
            case "server1": {
                localName = "马尔代夫";
                break;
            }
            case "server2": {
                localName = "东沙群岛";
                break;
            }
            case "server3": {
                localName = "西沙群岛";
                break;
            }
            case "server4": {
                localName = "南沙群岛";
                break;
            }
            case "server5": {
                localName = "夏威夷";
                break;
            }
            default: {
                localName = "未知";
                break;
            }
        }
        return localName;
    }

}
