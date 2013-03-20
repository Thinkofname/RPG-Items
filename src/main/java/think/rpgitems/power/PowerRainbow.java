package think.rpgitems.power;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.data.RPGValue;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerRainbow extends Power {

    long cd = 20;
    int count = 5;
    Random random = new Random();

    @Override
    public void rightClick(Player player) {
        long cooldown;
        RPGValue value = RPGValue.get(player, item, "arrow.rainbow");
        if (value == null) {
            cooldown = System.currentTimeMillis() / 50;
            value = new RPGValue(player, item, "arrow.rainbow", cooldown);
        } else {
            cooldown = value.asLong();
        }
        if (cooldown <= System.currentTimeMillis() / 50) {
            value.set(System.currentTimeMillis() / 50 + cd);
            player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1.0f, 1.0f);
            final ArrayList<FallingBlock> blocks = new ArrayList<FallingBlock>();
            for (int i = 0; i < count; i++) {
                FallingBlock block = player.getWorld().spawnFallingBlock(player.getLocation().add(0, 1.8, 0), Material.WOOL, (byte) random.nextInt(16));
                block.setVelocity(player.getLocation().getDirection().multiply(new Vector(random.nextDouble() * 2d + 0.5, random.nextDouble() * 2d + 0.5, random.nextDouble() * 2d + 0.5)));
                block.setDropItem(false);
                blocks.add(block);
            }
            (new BukkitRunnable() {

                ArrayList<Location> fallLocs = new ArrayList<Location>();
                Random random = new Random();

                public void run() {

                    Iterator<Location> l = fallLocs.iterator();
                    while (l.hasNext()) {
                        Location loc = l.next();
                        if (random.nextBoolean()) {
                            Block b = loc.getBlock();
                            if (b.getType() == Material.WOOL) {
                                loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.WOOL.getId(), b.getData());
                                b.setType(Material.AIR);
                            }
                            l.remove();
                        }
                        if (random.nextInt(5) == 0) {
                            break;
                        }
                    }

                    Iterator<FallingBlock> it = blocks.iterator();
                    while (it.hasNext()) {
                        FallingBlock block = it.next();
                        if (block.isDead()) {
                            fallLocs.add(block.getLocation());
                            it.remove();
                        }
                    }

                    if (fallLocs.isEmpty() && blocks.isEmpty()) {
                        cancel();
                    }

                }
            }).runTaskTimer(Plugin.plugin, 0, 5);
        } else {
            player.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_COOLDOWN"), ((double) (cooldown - System.currentTimeMillis() / 50)) / 20d));
        }
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + String.format(Locale.get("POWER_RAINBOW"), count, (double) cd / 20d);
    }

    @Override
    public String getName() {
        return "rainbow";
    }

    @Override
    public void init(ConfigurationSection s) {
        cd = s.getLong("cooldown", 20);
        count = s.getInt("count", 5);
    }

    @Override
    public void save(ConfigurationSection s) {
        s.set("cooldown", cd);
        s.set("count", count);
    }

    static {
        Commands.add("rpgitem $n[] power rainbow", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_RAINBOW");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerRainbow pow = new PowerRainbow();
                pow.cd = 20;
                pow.count = 5;
                pow.item = item;
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
        Commands.add("rpgitem $n[] power rainbow $COOLDOWN:i[] $COUNT:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_RAINBOW_FULL");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerRainbow pow = new PowerRainbow();
                pow.cd = (Integer) args[1];
                pow.count = (Integer) args[2];
                pow.item = item;
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }
}
