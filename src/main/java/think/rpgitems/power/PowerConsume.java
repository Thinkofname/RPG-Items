package think.rpgitems.power;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerConsume extends Power {

    @Override
    public void rightClick(Player player) {
        ItemStack item = player.getInventory().getItemInHand();
        int count = item.getAmount() - 1;
        if (count == 0) {
            player.getInventory().setItemInHand(null);
        } else {
            item.setAmount(count);
        }
    }

    @Override
    public void init(ConfigurationSection s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(ConfigurationSection s) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        return "consume";
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + Locale.get("POWER_CONSUME");
    }

    static {
        Commands.add("rpgitem $n[] power consume", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_CONSUME");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerConsume pow = new PowerConsume();
                pow.item = item;
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }

}
