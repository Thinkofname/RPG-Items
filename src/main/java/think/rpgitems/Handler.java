package think.rpgitems;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import think.rpgitems.commands.CommandDocumentation;
import think.rpgitems.commands.CommandGroup;
import think.rpgitems.commands.CommandHandler;
import think.rpgitems.commands.CommandString;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.Quality;
import think.rpgitems.item.RPGItem;
import think.rpgitems.support.WorldGuard;

public class Handler implements CommandHandler {

    @CommandString("rpgitem list")
    @CommandDocumentation("$COMMAND_RPGITEM_LIST")
    @CommandGroup("list")
    public void listItems(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "RPGItems:");
        for (RPGItem item : ItemManager.itemByName.values()) {
            sender.sendMessage(ChatColor.GREEN + item.getName() + " - " + item.getQuality().colour + ChatColor.BOLD + item.getDisplay());
        }
    }

    @CommandString("rpgitem worldguard")
    @CommandDocumentation("$COMMAND_RPGITEM_WORLDGUARD")
    @CommandGroup("worldguard")
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
    @CommandGroup("item")
    public void printItem(CommandSender sender, RPGItem item) {
        item.print(sender);
    }

    @CommandString("rpgitem $NAME:s[] create")
    @CommandDocumentation("$COMMAND_RPGITEM_CREATE")
    @CommandGroup("item")
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
    @CommandGroup("item_give")
    public void giveItem(CommandSender sender, RPGItem item) {
        if (sender instanceof Player) {
            item.give((Player) sender);
        } else {
            sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_GIVE_CONSOLE"));
        }
    }

    @CommandString("rpgitem $n[] give $p[]")
    @CommandDocumentation("$COMMAND_RPGITEM_GIVE_PLAYER")
    @CommandGroup("item_give")
    public void giveItemPlayer(CommandSender sender, RPGItem item, Player player) {
        item.give(player);
    }

    @CommandString("rpgitem $n[] give $p[] $COUNT:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_GIVE_PLAYER_COUNT")
    @CommandGroup("item_give")
    public void giveItemPlayerCount(CommandSender sender, RPGItem item, Player player, int count) {
        for (int i = 0; i < count; i++) {
            item.give(player);
        }
    }

    @CommandString("rpgitem $n[] remove")
    @CommandDocumentation("$COMMAND_RPGITEM_REMOVE")
    @CommandGroup("item_remove")
    public void removeItem(CommandSender sender, RPGItem item) {
        ItemManager.remove(item);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_REMOVE_OK"), item.getName()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] display")
    @CommandDocumentation("$COMMAND_RPGITEM_DISPLAY")
    @CommandGroup("item_display")
    public void getItemDisplay(CommandSender sender, RPGItem item) {
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_DISPLAY_GET"), item.getName(), item.getDisplay()));
    }

    @CommandString("rpgitem $n[] display $DISPLAY:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_DISPLAY_SET")
    @CommandGroup("item_display")
    public void setItemDisplay(CommandSender sender, RPGItem item, String display) {
        item.setDisplay(display);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_DISPLAY_SET"), item.getName(), item.getDisplay()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] quality")
    @CommandDocumentation("$COMMAND_RPGITEM_QUALITY")
    @CommandGroup("item_quality")
    public void getItemQuality(CommandSender sender, RPGItem item) {
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_QUALITY_GET"), item.getName(), item.getQuality().toString().toLowerCase()));
    }

    @CommandString("rpgitem $n[] quality $QUALITY:o[trash,common,uncommon,rare,epic,legendary]")
    @CommandDocumentation("$COMMAND_RPGITEM_QUALITY_SET")
    @CommandGroup("item_quality")
    public void setItemQuality(CommandSender sender, RPGItem item, String quality) {
        item.setQuality(Quality.valueOf(quality.toUpperCase()));
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_QUALITY_SET"), item.getName(), item.getQuality().toString().toLowerCase()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] damage")
    @CommandDocumentation("$COMMAND_RPGITEM_DAMAGE")
    @CommandGroup("item_damage")
    public void getItemDamage(CommandSender sender, RPGItem item) {
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_DAMAGE_GET"), item.getName(), item.getDamageMin(), item.getDamageMax()));
    }
    
    @CommandString("rpgitem $n[] damage $DAMAGE:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_DAMAGE_SET")
    @CommandGroup("item_damage")
    public void setItemDamage(CommandSender sender, RPGItem item, int damage) {
        item.setDamage(damage, damage);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_DAMAGE_SET"), item.getName(), item.getDamageMin()));
        ItemManager.save(Plugin.plugin);
    }
    
    @CommandString("rpgitem $n[] damage $MIN:i[] $MAX:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_DAMAGE_SET_RANAGE")
    @CommandGroup("item_damage")
    public void setItemDamage(CommandSender sender, RPGItem item, int min, int max) {
        item.setDamage(min, max);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_DAMAGE_SET_RANGE"), item.getName(), item.getDamageMin(), item.getDamageMax()));
        ItemManager.save(Plugin.plugin);
    }
    
    @CommandString("rpgitem $n[] armour")
    @CommandDocumentation("$COMMAND_RPGITEM_ARMOUR")
    @CommandGroup("item_armour")
    public void getItemArmour(CommandSender sender, RPGItem item) {
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ARMOUR_GET"), item.getName(), item.getArmour()));        
    }
    
    @CommandString("rpgitem $n[] armour $ARMOUR:i[0,100]")
    @CommandDocumentation("$COMMAND_RPGITEM_ARMOUR_SET")
    @CommandGroup("item_armour")
    public void setItemArmour(CommandSender sender, RPGItem item, int armour) {
        item.setArmour(armour);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ARMOUR_SET"), item.getName(), item.getArmour()));
        ItemManager.save(Plugin.plugin);
    }
    
    @CommandString("rpgitem $n[] type")
    @CommandDocumentation("$COMMAND_RPGITEM_TYPE")
    @CommandGroup("item_type")
    public void getItemType(CommandSender sender, RPGItem item) {
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_TYPE_GET"), item.getName(), item.getType()));        
    }
    
    @CommandString("rpgitem $n[] type $TYPE:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_TYPE_SET")
    @CommandGroup("item_type")
    public void setItemType(CommandSender sender, RPGItem item, String type) {
        item.setType(type);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_TYPE_SET"), item.getName(), item.getType()));
        ItemManager.save(Plugin.plugin);
    }
    
    @CommandString("rpgitem $n[] hand")
    @CommandDocumentation("$COMMAND_RPGITEM_HAND")
    @CommandGroup("item_hand")
    public void getItemHand(CommandSender sender, RPGItem item) {
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_HAND_GET"), item.getName(), item.getHand()));        
    }
    
    @CommandString("rpgitem $n[] hand $HAND:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_HAND_SET")
    @CommandGroup("item_hand")
    public void setItemHand(CommandSender sender, RPGItem item, String hand) {
        item.setHand(hand);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_HAND_SET"), item.getName(), item.getHand()));
        ItemManager.save(Plugin.plugin);        
    }
    
    @CommandString("rpgitem $n[] lore")
    @CommandDocumentation("$COMMAND_RPGITEM_LORE")
    @CommandGroup("item_lore")
    public void getItemLore(CommandSender sender, RPGItem item) {
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_LORE_GET"), item.getName(), item.getLore()));        
    }
    
    @CommandString("rpgitem $n[] lore $LORE:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_LORE_SET")
    @CommandGroup("item_lore")
    public void setItemLore(CommandSender sender, RPGItem item, String lore) {
        item.setLore(lore);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_LORE_SET"), item.getName(), item.getLore()));
        ItemManager.save(Plugin.plugin);
    }
}
