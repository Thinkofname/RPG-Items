/*
 *  This file is part of RPG Items.
 *
 *  RPG Items is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  RPG Items is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with RPG Items.  If not, see <http://www.gnu.org/licenses/>.
 */
package think.rpgitems;

import gnu.trove.map.hash.TIntByteHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import think.rpgitems.data.Locale;
import think.rpgitems.data.RPGMetadata;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.LocaleInventory;
import think.rpgitems.item.RPGItem;
import think.rpgitems.support.WorldGuard;

public class Events implements Listener {

    private static TIntByteHashMap removeArrows = new TIntByteHashMap();
    private static TIntIntHashMap rpgProjectiles = new TIntIntHashMap();
    private static TObjectIntHashMap<String> recipeWindows = new TObjectIntHashMap<String>();
    private static HashMap<String, Set<Integer>> drops = new HashMap<String, Set<Integer>>();
    private static boolean useLocaleInv = false;

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        String type = e.getEntity().getType().toString();
        Random random = new Random();
        if (drops.containsKey(type)) {
            Set<Integer> items = drops.get(type);
            Iterator<Integer> it = items.iterator();
            while (it.hasNext()) {
                int id = it.next();
                RPGItem item = ItemManager.getItemById(id);
                if (item == null) {
                    it.remove();
                    continue;
                }
                double chance = item.dropChances.get(type);
                if (random.nextDouble() < chance / 100d) {
                    e.getDrops().add(item.toItemStack("en_GB"));
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        final Entity entity = e.getEntity();
        if (removeArrows.contains(entity.getEntityId())) {
            entity.remove();
            removeArrows.remove(entity.getEntityId());
        } else if (rpgProjectiles.contains(entity.getEntityId())) {
            RPGItem item = ItemManager.getItemById(rpgProjectiles.get(entity.getEntityId()));
            new BukkitRunnable() {

                public void run() {
                    rpgProjectiles.remove(entity.getEntityId());

                }
            }.runTask(Plugin.plugin);
            if (item == null)
                return;
            item.projectileHit((Player) ((Projectile) entity).getShooter(), (Projectile) entity);
        }
    }

    @EventHandler
    public void onProjectileFire(ProjectileLaunchEvent e) {
        LivingEntity shooter = e.getEntity().getShooter();
        if (shooter instanceof Player) {
            Player player = (Player) shooter;
            ItemStack item = player.getItemInHand();
            RPGItem rItem = ItemManager.toRPGItem(item);
            if (rItem == null)
                return;
            if (!WorldGuard.canPvP(player.getLocation()) && !rItem.ignoreWorldGuard)
                return;
            rpgProjectiles.put(e.getEntity().getEntityId(), rItem.getID());
        }
    }

    @EventHandler
    public void onPlayerAction(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || (e.getAction() == Action.RIGHT_CLICK_BLOCK) && !e.isCancelled())) {
            ItemStack item = player.getItemInHand();
            if (item.getType() == Material.BOW || item.getType() == Material.SNOW_BALL || item.getType() == Material.EGG || item.getType() == Material.POTION)
                return;

            RPGItem rItem = ItemManager.toRPGItem(item);
            if (rItem == null)
                return;
            if (!WorldGuard.canPvP(player.getLocation()) && !rItem.ignoreWorldGuard)
                return;
            rItem.rightClick(player);
        } else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {

            ItemStack item = player.getItemInHand();
            if (item.getType() == Material.BOW || item.getType() == Material.SNOW_BALL || item.getType() == Material.EGG || item.getType() == Material.POTION)
                return;

            RPGItem rItem = ItemManager.toRPGItem(item);
            if (rItem == null)
                return;
            if (!WorldGuard.canPvP(player.getLocation()) && !rItem.ignoreWorldGuard)
                return;
            rItem.leftClick(player);
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getPlayer().getItemInHand();
        if (item == null)
            return;

        RPGItem rItem = ItemManager.toRPGItem(item);
        if (rItem == null)
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerInventory in = player.getInventory();
        String locale = Locale.getPlayerLocale(player);
        for (int i = 0; i < in.getSize(); i++) {
            ItemStack item = in.getItem(i);
            RPGItem rItem = ItemManager.toRPGItem(item);
            if (rItem == null)
                continue;
            item.setType(rItem.getItem());
            ItemMeta meta = rItem.getLocaleMeta(locale);
            if (!(meta instanceof LeatherArmorMeta) && rItem.getItem().isBlock())
                item.setDurability(rItem.getDataValue());
            RPGMetadata rpgMeta = RPGMetadata.parseLoreline(item.getItemMeta().getLore().get(0));
            List<String> lore = meta.getLore();
            lore.set(0, meta.getLore().get(0) + rpgMeta.toMCString());
            rItem.addExtra(rpgMeta, item, lore);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        for (ItemStack item : player.getInventory().getArmorContents()) {
            RPGItem rItem = ItemManager.toRPGItem(item);
            if (rItem == null)
                continue;
            item.setType(rItem.getItem());
            ItemMeta meta = rItem.getLocaleMeta(locale);
            if (!(meta instanceof LeatherArmorMeta) && rItem.getItem().isBlock())
                item.setDurability(rItem.getDataValue());
            RPGMetadata rpgMeta = RPGMetadata.parseLoreline(item.getItemMeta().getLore().get(0));
            List<String> lore = meta.getLore();
            lore.set(0, meta.getLore().get(0) + rpgMeta.toMCString());
            rItem.addExtra(rpgMeta, item, lore);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();
        RPGItem rItem = ItemManager.toRPGItem(item);
        if (rItem == null)
            return;
        String locale = Locale.getPlayerLocale(e.getPlayer());
        item.setType(rItem.getItem());
        ItemMeta meta = rItem.getLocaleMeta(locale);
        if (!(meta instanceof LeatherArmorMeta) && rItem.getItem().isBlock())
            item.setDurability(rItem.getDataValue());
        RPGMetadata rpgMeta = RPGMetadata.parseLoreline(item.getItemMeta().getLore().get(0));
        List<String> lore = meta.getLore();
        lore.set(0, meta.getLore().get(0) + rpgMeta.toMCString());
        rItem.addExtra(rpgMeta, item, lore);
        meta.setLore(lore);
        item.setItemMeta(meta);
        e.getItem().setItemStack(item);

    }
    
    private HashSet<LocaleInventory> localeInventories = new HashSet<LocaleInventory>();


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (recipeWindows.containsKey(e.getPlayer().getName())) {
            int id = recipeWindows.remove(e.getPlayer().getName());
            RPGItem item = ItemManager.getItemById(id);
            if (item.recipe == null) {
                item.recipe = new ArrayList<ItemStack>();
            }
            item.recipe.clear();
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    int i = x + y * 9;
                    ItemStack it = e.getInventory().getItem(i);
                    item.recipe.add(it);
                }
            }
            item.hasRecipe = true;
            item.resetRecipe(true);
            ItemManager.save(Plugin.plugin);
            ((Player) e.getPlayer()).sendMessage(ChatColor.AQUA + "Recipe set for " + item.getName());
        } else if (useLocaleInv && e.getView() instanceof LocaleInventory) {
            localeInventories.remove(e.getView());
            ((LocaleInventory) e.getView()).getView().close();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        if (useLocaleInv && e.getView() instanceof LocaleInventory) {
            LocaleInventory inv = (LocaleInventory) e.getView();
            InventoryClickEvent clickEvent = new InventoryClickEvent(inv.getView(), e.getSlotType(), e.getRawSlot(), e.isRightClick(), e.isShiftClick());
            Bukkit.getServer().getPluginManager().callEvent(clickEvent);
            if (clickEvent.isCancelled()) {
                e.setCancelled(true);
            } else {
                switch(clickEvent.getResult()) {
                case DEFAULT: //Can't really do this with current events
                case ALLOW:
                    System.out.println("ok...");
                    System.out.println(inv.getView().getItem(e.getRawSlot()));
                    inv.getView().setItem(e.getRawSlot(), clickEvent.getCursor());
                    System.out.println(inv.getView().getItem(e.getRawSlot()));
                    break;
                case DENY:
                    break;
                }
            }
            for (LocaleInventory localeInv : localeInventories) {
                if (localeInv != inv)
                    localeInv.reload();
            }
        }
    }
    

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryOpen(final InventoryOpenEvent e) {
        if (e.getView() instanceof LocaleInventory) return;
        if (e.getInventory().getType() != InventoryType.CHEST || !useLocaleInv) {
            Inventory in = e.getInventory();
            Iterator<ItemStack> it = in.iterator(); 
            String locale = Locale.getPlayerLocale((Player) e.getPlayer());        
            try {
                while (it.hasNext()) {
                    ItemStack item = it.next();
                    RPGItem rItem = ItemManager.toRPGItem(item);
                    if (rItem == null)
                        continue;
                    item.setType(rItem.getItem());
                    ItemMeta meta = rItem.getLocaleMeta(locale);
                    if (!(meta instanceof LeatherArmorMeta) && rItem.getItem().isBlock())
                        item.setDurability(rItem.getDataValue());
                    RPGMetadata rpgMeta = RPGMetadata.parseLoreline(item.getItemMeta().getLore().get(0));
                    List<String> lore = meta.getLore();
                    lore.set(0, meta.getLore().get(0) + rpgMeta.toMCString());
                    rItem.addExtra(rpgMeta, item, lore);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                // Fix for the bug with anvils in craftbukkit
            } 
        } else { // Always is "true"
            LocaleInventory localeInv = new LocaleInventory((Player) e.getPlayer(), e.getView());
            e.setCancelled(true);
            e.getPlayer().openInventory(localeInv);
            localeInventories.add(localeInv);
        }
    }

    private Random random = new Random();
    
    private int playerDamager(EntityDamageByEntityEvent e, int damage) {
        Player player = (Player) e.getDamager();
        ItemStack item = player.getItemInHand();
        if (item.getType() == Material.BOW || item.getType() == Material.SNOW_BALL || item.getType() == Material.EGG || item.getType() == Material.POTION)
            return damage;

        RPGItem rItem = ItemManager.toRPGItem(item);
        if (rItem == null)
            return damage;
        if (!WorldGuard.canPvP(player.getLocation()) && !rItem.ignoreWorldGuard)
            return damage;
        damage = rItem.getDamageMin() != rItem.getDamageMax() ? (rItem.getDamageMin() + random.nextInt(rItem.getDamageMax() - rItem.getDamageMin())) : rItem.getDamageMin();
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) e.getEntity();
            rItem.hit(player, le);
        }
        return damage;
    }
    
    private int projectileDamager(EntityDamageByEntityEvent e, int damage) {
        Projectile entity = (Projectile) e.getDamager();
        if (rpgProjectiles.contains(entity.getEntityId())) {
            RPGItem rItem = ItemManager.getItemById(rpgProjectiles.get(entity.getEntityId()));
            if (rItem == null)
                return damage;
            damage = rItem.getDamageMin() != rItem.getDamageMax() ? (rItem.getDamageMin() + random.nextInt(rItem.getDamageMax() - rItem.getDamageMin())) : rItem.getDamageMin();
            if (e.getEntity() instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) e.getEntity();
                rItem.hit((Player) entity.getShooter(), le);
            }
        }
        return damage;
    }
    
    private int playerHit(EntityDamageByEntityEvent e, int damage) {
        Player p = (Player) e.getEntity();
        if (e.isCancelled() || !WorldGuard.canPvP(p.getLocation()))
            return damage;
        for (ItemStack pArmour : p.getInventory().getArmorContents()) {
            RPGItem pRItem = ItemManager.toRPGItem(pArmour);
            if (pRItem == null)
                continue;
            if (!WorldGuard.canPvP(p.getLocation()) && !pRItem.ignoreWorldGuard)
                return damage;
            if (pRItem.getArmour() > 0) {
                damage -= Math.round(((double) damage) * (((double) pRItem.getArmour()) / 100d));
            }
        }
        return damage;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        int damage = e.getDamage();
        if (e.getDamager() instanceof Player) {
            damage = playerDamager(e, damage);
        } else if (e.getDamager() instanceof Projectile) {
            damage = projectileDamager(e, damage);
        }
        if (e.getEntity() instanceof Player) {
            damage = playerHit(e, damage);
        }
        e.setDamage(damage);
    }
}
