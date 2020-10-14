package com.molean.isletopia.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public class HeadUtils {
    public static ItemStack getSkull(String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        assert headMeta != null;
        headMeta.setDisplayName("§f" + name);
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        String skinValue = UniversalParameter.getParameter(name, "skinValue");
        if (skinValue != null && !"".equalsIgnoreCase(skinValue)) {
            profile.getProperties().put("textures", new Property("textures", skinValue));
            try {
                Field profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);
            } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
                error.printStackTrace();
            }
        }
        head.setItemMeta(headMeta);
        return head;
    }
}