package com.molean.isletopia.bungee.individual;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.infrastructure.individual.I18n;
import com.molean.isletopia.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniversalVisitHandler implements PluginMessageListener, Listener {
    private static final Map<String, String> visits = new HashMap<>();

    public UniversalVisitHandler() {
        Bukkit.getMessenger().registerIncomingPluginChannel(IsletopiaTweakers.getPlugin(), "BungeeCord", this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }


    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("visit")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

                String source = msgin.readUTF();
                String target = msgin.readUTF();
                Player sourcePlayer = Bukkit.getPlayer(source);

                if (sourcePlayer != null) {
                    PlotUtils.localServerTeleport(sourcePlayer, target);
                } else {
                    visits.put(source, target);
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @EventHandler
    public void preJoin(PlayerQuitEvent event){
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (visits.containsKey(player.getName())) {
            PlotUtils.localServerTeleport(player, visits.get(player.getName()));
            visits.remove(player.getName());

        }
        List<String> visits = UniversalParameter.getParameterAsList(event.getPlayer().getName(), "visits");
        if (visits.size() > 0) {
            player.sendMessage(I18n.getMessage("island.notify.offlineVisitors",event.getPlayer()));
            player.sendMessage("§7  " + String.join(",", visits));
            UniversalParameter.setParameter(event.getPlayer().getName(), "visits", "");
        }
    }
}