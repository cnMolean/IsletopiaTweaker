package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidTriggerEvent;

public class AnimalProtect implements Listener {

    public AnimalProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
        if (!IsletopiaTweakers.hasCurrentPlotPermission(event.getPlayer()))
            event.setCancelled(true);
    }
}
