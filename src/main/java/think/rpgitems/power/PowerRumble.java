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
package think.rpgitems.power;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.data.RPGValue;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerRumble extends Power {

    private long cd = 20;
    private int power = 2;
    private int distance = 15;

    @Override
    public void init(ConfigurationSection s) {
        cd = s.getLong("cooldown", 20);
        power = s.getInt("power", 2);
        distance = s.getInt("distance", 15);
    }

    @Override
    public void save(ConfigurationSection s) {
        s.set("cooldown", cd);
        s.set("power", power);
        s.set("distance", distance);
    }

    @Override
    public void rightClick(final Player player) {
        long cooldown;
        RPGValue value = RPGValue.get(player, item, "rumble.cooldown");
        if (value == null) {
            cooldown = System.currentTimeMillis() / 50;
            value = new RPGValue(player, item, "rumble.cooldown", cooldown);
        } else {
            cooldown = value.asLong();
        }
        if (cooldown <= System.currentTimeMillis() / 50) {
            value.set(System.currentTimeMillis() / 50 + cd);
            final Location location = player.getLocation().add(0, -0.2, 0);
            final Vector direction = player.getLocation().getDirection();
            direction.setY(0);
            direction.normalize();
            BukkitRunnable task = new BukkitRunnable() {

                private int count = 0;

                public void run() {
                    Location above = location.clone().add(0, 1, 0);
                    if (above.getBlock().getType().isSolid() || !location.getBlock().getType().isSolid()) {
                        cancel();
                        return;
                    }

                    Location temp = location.clone();
                    for (int x = -2; x <= 2; x++) {
                        for (int z = -2; z <= 2; z++) {
                            temp.setX(x + location.getBlockX());
                            temp.setZ(z + location.getBlockZ());
                            Block block = temp.getBlock();
                            temp.getWorld().playEffect(temp, Effect.STEP_SOUND, block.getTypeId());
                        }
                    }
                    Entity[] near = getNearbyEntities(location, 1.5);
                    boolean hit = false;
                    Random random = new Random();
                    for (Entity e : near) {
                        if (e != player) {
                            hit = true;
                            break;
                        }
                    }
                    if (hit) {
                        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), power, false, false);
                        near = getNearbyEntities(location, 2.5);
                        for (Entity e : near) {
                            if (e != player)
                                e.setVelocity(new Vector(random.nextGaussian() / 4d, 1d + random.nextDouble() * (double) power, random.nextGaussian() / 4d));
                        }
                        cancel();
                        return;
                    }
                    location.add(direction);
                    if (count >= distance) {
                        cancel();
                    }
                    count++;
                }
            };
            task.runTaskTimer(Plugin.plugin, 0, 3);
        } else {
            player.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_COOLDOWN"), ((double) (cooldown - System.currentTimeMillis() / 50)) / 20d));
        }
    }

    @Override
    public String getName() {
        return "rumble";
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + String.format(Locale.get("POWER_RUMBLE"), (double) cd / 20d);
    }

    static {
        Commands.add("rpgitem $n[] power rumble $COOLDOWN:i[] $POWER:i[] $DISTANCE:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_RUMBLE");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerRumble pow = new PowerRumble();
                pow.item = item;
                pow.cd = (Integer) args[1];
                pow.power = (Integer) args[2];
                pow.distance = (Integer) args[3];
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }

}
