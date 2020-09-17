package com.molean.isletopia.prompter.prompter;

import com.molean.isletopia.prompter.IsletopiaPrompters;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChestPrompter implements Prompter {


    private final Player player;
    private final List<Pair<ItemStack, String>> itemStacks;
    private final Inventory inventory;
    private Consumer<String> consumer;
    private Runnable runnable;
    private int page = 0;

    public ChestPrompter(Player player, String title) {
        this.player = player;
        this.itemStacks = new ArrayList<>();
        inventory = Bukkit.createInventory(player, 54, title);
        IsletopiaPrompters.getChestPrompterList().add(this);
    }

    public void addItemStacks(Pair<ItemStack, String> itemStack) {
        itemStacks.add(itemStack);
    }

    public void freshPage() {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            inventory.clear();
            for (int i = 0; i < itemStacks.size(); i++) {
                if (i == 45) break;
                inventory.setItem(i, itemStacks.get(page * 45 + i).getKey());
            }
            //prev page button
            ItemStack prev = new ItemStack(Material.FEATHER);
            ItemMeta prevMeta = prev.getItemMeta();
            assert prevMeta != null;
            prevMeta.setDisplayName("§f<=");
            prev.setItemMeta(prevMeta);
            inventory.setItem(9 * 5 + 2, prev);

            //next page button
            ItemStack next = new ItemStack(Material.FEATHER);
            ItemMeta nextMeta = next.getItemMeta();
            assert nextMeta != null;
            nextMeta.setDisplayName("§f=>");
            next.setItemMeta(nextMeta);
            inventory.setItem(9 * 5 + 6, next);
        });
    }

    @Override
    public void open() {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            player.openInventory(inventory);
            freshPage();
        });
    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory))
            return;
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(),() -> IsletopiaPrompters.getChestPrompterList().remove(this),100);
        if (runnable != null) {
            runnable.run();
        }

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory))
            return;
        event.setCancelled(true);
        if (event.getClick() != ClickType.LEFT) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        ItemStack currentItem = event.getCurrentItem();
        assert currentItem != null;
        if (currentItem.getType() != Material.FEATHER) {
            consumer.accept(itemStacks.get(page * 45 + event.getSlot()).getValue());
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> IsletopiaPrompters.getChestPrompterList().remove(ChestPrompter.this),100);
        } else {
            ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
            assert itemMeta != null;
            String name = itemMeta.getDisplayName();
            if (name.equals("=>")) {
                nextPage();
            } else if (name.equals("<=")) {
                prevPage();
            }
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    protected void nextPage() {
        int maxPage = itemStacks.size() / 45;
        if (itemStacks.size() % 45 != 0) maxPage++;
        if (page < maxPage) page++;
        freshPage();
    }

    protected void prevPage() {
        if (page > 0) page--;
        freshPage();
    }

    @Override
    public void onComplete(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onEscape(Runnable runnable) {
        this.runnable = runnable;
    }
}