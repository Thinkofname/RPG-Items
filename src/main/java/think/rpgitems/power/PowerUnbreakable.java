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

public class PowerUnbreakable extends Power {

    @SuppressWarnings("deprecation")
    @Override
    public void hit(Player player, LivingEntity e) {
        player.getItemInHand().setDurability((short) 0);
        player.updateInventory();
    }

    @Override
    public void init(ConfigurationSection s) {

    }

    @Override
    public void save(ConfigurationSection s) {

    }

    @Override
    public String getName() {
        return "unbreakable";
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + Locale.get("POWER_UNBREAKABLE");
    }

    static {
        Commands.add("rpgitem $n[] power unbreakable", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_UNBREAKABLE");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerUnbreakable pow = new PowerUnbreakable();
                pow.item = item;
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }

}
