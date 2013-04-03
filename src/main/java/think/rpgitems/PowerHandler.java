package think.rpgitems;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import think.rpgitems.commands.CommandDocumentation;
import think.rpgitems.commands.CommandGroup;
import think.rpgitems.commands.CommandHandler;
import think.rpgitems.commands.CommandString;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;
import think.rpgitems.power.PowerArrow;

public class PowerHandler implements CommandHandler{
    
    @CommandString("rpgitem $n[] power arrow")
    @CommandDocumentation("$COMMAND_RPGITEM_ARROW")
    @CommandGroup("power_arrow")
    public void arrow(CommandSender sender, RPGItem item) {
        PowerArrow pow = new PowerArrow();
        pow.cooldownTime = 20;
        pow.item = item;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power arrow $COOLDOWN:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_ARROW_FULL")
    @CommandGroup("power_arrow")
    public void arrow(CommandSender sender, RPGItem item, int cooldown) {
        PowerArrow pow = new PowerArrow();
        pow.item = item;
        pow.cooldownTime = cooldown;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
}
