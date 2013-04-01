package think.rpgitems;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import think.rpgitems.commands.CommandDocumentation;
import think.rpgitems.commands.CommandHandler;
import think.rpgitems.commands.CommandString;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;
import think.rpgitems.support.WorldGuard;

public class Handler implements CommandHandler {
    
    @CommandString("rpgitem list")
    @CommandDocumentation("$COMMAND_RPGITEM_LIST")
    public void listItems(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "RPGItems:");
        for (RPGItem item : ItemManager.itemByName.values()) {
            sender.sendMessage(ChatColor.GREEN + item.getName() + " - " + item.getQuality().colour + ChatColor.BOLD + item.getDisplay());
        }
    }
    
    
    @CommandString("rpgitem worldguard")
    @CommandDocumentation("$COMMAND_RPGITEM_WORLDGUARD")
    public void toggleWorldGuard(CommandSender sender) {
        if (!WorldGuard.isEnabled()) {
            sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_WORLDGUARD_ERROR"));
            return;
        }
        if (WorldGuard.useWorldGuard) {
            sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_WORLDGUARD_DISABLE"));
        } else {
            sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_WORLDGUARD_ENABLE"));
        }
        WorldGuard.useWorldGuard = !WorldGuard.useWorldGuard;
        Plugin.plugin.getConfig().set("support.worldguard", WorldGuard.useWorldGuard);
        Plugin.plugin.saveConfig();        
    }
    
    @CommandString("rpgitem $n[]")
    @CommandDocumentation("$COMMAND_RPGITEM_PRINT")
    public void printItem(CommandSender sender, RPGItem item) {
        item.print(sender);
    }
    
    @CommandString("rpgitem $NAME:s[] create")
    @CommandDocumentation("$COMMAND_RPGITEM_CREATE")
    public void createItem(CommandSender sender, String itemName) {
        if (ItemManager.newItem(itemName.toLowerCase()) != null) {
            sender.sendMessage(String.format(ChatColor.GREEN + Locale.get("MESSAGE_CREATE_OK"), itemName));
            ItemManager.save(Plugin.plugin);
        } else {
            sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_CREATE_FAIL"));
        }        
    }
    
    @CommandString("rpgitem $n[] give")
    @CommandDocumentation("$COMMAND_RPGITEM_GIVE")
    public void giveItem(CommandSender sender, RPGItem item) {
        if (sender instanceof Player) {
            item.give((Player) sender);
        } else {
            sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_GIVE_CONSOLE"));
        }
    }
}
