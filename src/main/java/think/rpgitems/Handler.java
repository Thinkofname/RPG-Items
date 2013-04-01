package think.rpgitems;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import think.rpgitems.commands.CommandDocumentation;
import think.rpgitems.commands.CommandHandler;
import think.rpgitems.commands.CommandString;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class Handler implements CommandHandler {
    
    @CommandString("rpgitem list")
    @CommandDocumentation("$COMMAND_RPGITEM_LIST")
    public void listItems(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "RPGItems:");
        for (RPGItem item : ItemManager.itemByName.values()) {
            sender.sendMessage(ChatColor.GREEN + item.getName() + " - " + item.getQuality().colour + ChatColor.BOLD + item.getDisplay());
        }
    }
}
