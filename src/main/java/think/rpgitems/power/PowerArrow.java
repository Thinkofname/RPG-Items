package think.rpgitems.power;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;

import think.rpgitems.Events;
import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.data.RPGValue;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerArrow extends Power {

    private long cd = 20;

    @Override
    public void rightClick(Player player) {
        long cooldown;
        RPGValue value = RPGValue.get(player, item, "arrow.cooldown");
        if (value == null) {
            cooldown = System.currentTimeMillis() / 50;
            value = new RPGValue(player, item, "arrow.cooldown", cooldown);
        } else {
            cooldown = value.asLong();
        }
        if (cooldown <= System.currentTimeMillis() / 50) {
            value.set(System.currentTimeMillis() / 50 + cd);
            player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1.0f, 1.0f);
            Arrow arrow = player.launchProjectile(Arrow.class);
            Events.removeArrows.put(arrow.getEntityId(), (byte) 1);
        } else {
            player.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_COOLDOWN"), ((double) (cooldown - System.currentTimeMillis() / 50)) / 20d));
        }
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + String.format(Locale.get("POWER_ARROW"), (double) cd / 20d);
    }

    @Override
    public String getName() {
        return "arrow";
    }

    @Override
    public void init(ConfigurationSection s) {
        cd = s.getLong("cooldown", 20);
    }

    @Override
    public void save(ConfigurationSection s) {
        s.set("cooldown", cd);
    }

    static {
        Commands.add("rpgitem $n[] power arrow", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ARROW");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerArrow pow = new PowerArrow();
                pow.cd = 20;
                pow.item = item;
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
        Commands.add("rpgitem $n[] power arrow $COOLDOWN:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ARROW_FULL");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerArrow pow = new PowerArrow();
                pow.item = item;
                pow.cd = (Integer) args[1];
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }
}
