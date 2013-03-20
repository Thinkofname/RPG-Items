package think.rpgitems.power;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.data.RPGValue;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerTNTCannon extends Power {

    long cd = 20;

    @Override
    public void rightClick(Player player) {
        long cooldown;
        RPGValue value = RPGValue.get(player, item, "tnt.cooldown");
        if (value == null) {
            cooldown = System.currentTimeMillis() / 50;
            value = new RPGValue(player, item, "tnt.cooldown", cooldown);
        } else {
            cooldown = value.asLong();
        }
        if (cooldown <= System.currentTimeMillis() / 50) {
            value.set(System.currentTimeMillis() / 50 + cd);
            player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1.0f, 1.0f);
            TNTPrimed tnt = player.getWorld().spawn(player.getLocation().add(0, 1.8, 0), TNTPrimed.class);
            tnt.setVelocity(player.getLocation().getDirection().multiply(2d));
        } else {
            player.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_COOLDOWN"), ((double) (cooldown - System.currentTimeMillis() / 50)) / 20d));
        }
    }

    public String displayText() {
        return ChatColor.GREEN + String.format(Locale.get("POWER_TNTCANNON"), (double) cd / 20d);
    }

    public String getName() {
        return "tntcannon";
    }

    public void init(ConfigurationSection s) {
        cd = s.getLong("cooldown", 20);
    }

    public void save(ConfigurationSection s) {
        s.set("cooldown", cd);
    }

    static {
        Commands.add("rpgitem $n[] power tntcannon", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_TNTCANNON");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerTNTCannon pow = new PowerTNTCannon();
                pow.item = item;
                pow.cd = 20;
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
        Commands.add("rpgitem $n[] power tntcannon $COOLDOWN:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_TNTCANNON_FULL");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerTNTCannon pow = new PowerTNTCannon();
                pow.item = item;
                pow.cd = (Integer) args[1];
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }
}
