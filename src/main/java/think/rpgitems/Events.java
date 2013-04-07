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

import java.util.Iterator;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;
import think.rpgitems.support.WorldGuard;

public class Events implements Listener {

    public static TIntByteHashMap removeArrows = new TIntByteHashMap();
    public static TIntIntHashMap rpgProjectiles = new TIntIntHashMap();

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
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
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
        for (int i = 0; i < in.getSize(); i++) {
            ItemStack item = in.getItem(i);
            try {
                int id = ItemManager.decodeId(item.getItemMeta().getDisplayName());
                RPGItem rItem = ItemManager.getItemById(id);
                item.setType(rItem.item.getType());
                // if (!(rItem.meta instanceof LeatherArmorMeta))
                // item.setDurability(rItem.item.getDurability());
                item.setItemMeta(rItem.meta);
            } catch (Exception ex) {

            }
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();
        RPGItem rItem = ItemManager.toRPGItem(item);
        if (rItem == null)
            return;
        item.setType(rItem.item.getType());
        if (!(rItem.meta instanceof LeatherArmorMeta))
            item.setDurability(rItem.item.getDurability());
        item.setItemMeta(rItem.meta);
        e.getItem().setItemStack(item);

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Inventory in = e.getInventory();
        Iterator<ItemStack> it = in.iterator();
        while (it.hasNext()) {
            ItemStack item = it.next();
            RPGItem rItem = ItemManager.toRPGItem(item);
            if (rItem == null)
                return;
            item.setType(rItem.item.getType());
            if (!(rItem.meta instanceof LeatherArmorMeta))
                item.setDurability(rItem.item.getDurability());
            item.setItemMeta(rItem.meta);
        }
    }

    Random random = new Random();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        int damage = e.getDamage();
        if (e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            ItemStack item = player.getItemInHand();
            if (item.getType() == Material.BOW || item.getType() == Material.SNOW_BALL || item.getType() == Material.EGG || item.getType() == Material.POTION)
                return;

            RPGItem rItem = ItemManager.toRPGItem(item);
            if (rItem == null)
                return;
            if (!WorldGuard.canPvP(player.getLocation()) && !rItem.ignoreWorldGuard)
                return;
            damage = rItem.getDamageMin() != rItem.getDamageMax() ? (rItem.getDamageMin() + random.nextInt(rItem.getDamageMax() - rItem.getDamageMin())) : rItem.getDamageMin();
            if (e.getEntity() instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) e.getEntity();
                rItem.hit(player, le);
            }
        } else if (e.getDamager() instanceof Projectile) {
            Projectile entity = (Projectile) e.getDamager();
            if (rpgProjectiles.contains(entity.getEntityId())) {
                RPGItem rItem = ItemManager.getItemById(rpgProjectiles.get(entity.getEntityId()));
                if (rItem == null)
                    return;
                damage = rItem.getDamageMin() != rItem.getDamageMax() ? (rItem.getDamageMin() + random.nextInt(rItem.getDamageMax() - rItem.getDamageMin())) : rItem.getDamageMin();
                if (e.getEntity() instanceof LivingEntity) {
                    LivingEntity le = (LivingEntity) e.getEntity();
                    rItem.hit((Player) entity.getShooter(), le);
                }
            }
        }
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (e.isCancelled() || !WorldGuard.canPvP(p.getLocation()))
                return;
            for (ItemStack pArmour : p.getInventory().getArmorContents()) {
                RPGItem pRItem = ItemManager.toRPGItem(pArmour);
                if (pRItem == null)
                    continue;
                if (!WorldGuard.canPvP(p.getLocation()) && !pRItem.ignoreWorldGuard)
                    return;
                if (pRItem.getArmour() > 0) {
                    damage -= Math.round(((double) damage) * (((double) pRItem.getArmour()) / 100d));
                }
            }
        }
        e.setDamage(damage);
    }
}