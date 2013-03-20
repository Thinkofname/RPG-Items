package think.rpgitems.power;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerFlame extends Power {

    int burnTime = 20;

    @Override
    public void hit(Player player, LivingEntity e) {
        e.setFireTicks(burnTime);
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + String.format(Locale.get("POWER_FLAME"), (double) burnTime / 20d);
    }

    @Override
    public String getName() {
        return "flame";
    }

    @Override
    public void init(ConfigurationSection s) {
        burnTime = s.getInt("burntime");
    }

    @Override
    public void save(ConfigurationSection s) {
        s.set("burntime", burnTime);
    }

    static {
        Commands.add("rpgitem $n[] power flame", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_FLAME");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerFlame pow = new PowerFlame();
                pow.burnTime = 20;
                pow.item = item;
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
        Commands.add("rpgitem $n[] power flame $BURNTIME:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_FLAME_FULL");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerFlame pow = new PowerFlame();
                pow.item = item;
                pow.burnTime = (Integer) args[1];
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }
}
