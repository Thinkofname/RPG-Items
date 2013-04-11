package think.rpgitems;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import think.rpgitems.commands.CommandDocumentation;
import think.rpgitems.commands.CommandGroup;
import think.rpgitems.commands.CommandHandler;
import think.rpgitems.commands.CommandString;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.Quality;
import think.rpgitems.item.RPGItem;
import think.rpgitems.power.Power;
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

    @CommandString("rpgitem option worldguard")
    @CommandDocumentation("$COMMAND_RPGITEM_WORLDGUARD")
    @CommandGroup("option_worldguard")
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
    
    @CommandString("rpgitem option giveperms")
    @CommandDocumentation("Toggles give requiring permissions")
    @CommandGroup("option_giveperms")
    public void givePerms(CommandSender sender) {
        Plugin.plugin.getConfig().set("give-perms", !Plugin.plugin.getConfig().getBoolean("give-perms", false));
        if (Plugin.plugin.getConfig().getBoolean("give-perms", false)) {
            sender.sendMessage(ChatColor.AQUA + "Give now requires permissions");
        } else {
            sender.sendMessage(ChatColor.AQUA + "Give now does not require permissions");            
        }
        Plugin.plugin.saveConfig();
    }

    @CommandString(value = "rpgitem $n[] give",
                    handlePermissions = true)
    @CommandDocumentation("$COMMAND_RPGITEM_GIVE")
    @CommandGroup("item_give")
    public void giveItem(CommandSender sender, RPGItem item) {
        if (sender instanceof Player) {
            if ((!Plugin.plugin.getConfig().getBoolean("give-perms", false) && sender.hasPermission("rpgitem")) || (Plugin.plugin.getConfig().getBoolean("give-perms", false) && sender.hasPermission("rpgitem.give." + item.getName()))) {
                item.give((Player) sender);
                sender.sendMessage(ChatColor.AQUA + "You were given " + item.getDisplay());
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission");
            }
        } else {
            sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_GIVE_CONSOLE"));
        }
    }

    @CommandString("rpgitem $n[] give $p[]")
    @CommandDocumentation("$COMMAND_RPGITEM_GIVE_PLAYER")
    @CommandGroup("item_give")
    public void giveItemPlayer(CommandSender sender, RPGItem item, Player player) {
        item.give(player);
        sender.sendMessage(ChatColor.AQUA + "Gave " + item.getDisplay() + ChatColor.AQUA + " to " + player.getName());
        player.sendMessage(ChatColor.AQUA + "You were given " + item.getDisplay());
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

    @CommandString("rpgitem $n[] item")
    @CommandDocumentation("$COMMAND_RPGITEM_ITEM")
    @CommandGroup("item_item")
    public void getItemItem(CommandSender sender, RPGItem item) {
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_GET"), item.getName(), item.getItem().toString()));
    }

    @CommandString("rpgitem $n[] item $m[]")
    @CommandDocumentation("$COMMAND_RPGITEM_ITEM_SET")
    @CommandGroup("item_item")
    public void setItemItem(CommandSender sender, RPGItem item, Material material) {
        item.setItem(material);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_SET"), item.getName(), item.getItem(), item.item.getDurability()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] item $m[] $DATA:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_ITEM_SET_DATA")
    @CommandGroup("item_item")
    public void setItemItem(CommandSender sender, RPGItem item, Material material, int data) {
        item.setItem(material, false);
        item.meta = item.item.getItemMeta();
        if (item.meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) item.meta).setColor(Color.fromRGB(data));
        } else {
            item.setDataValue((short) data);
        }
        item.rebuild();
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_SET"), item.getName(), item.getItem(), item.item.getDurability()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] item $m[] hex $HEXCOLOUR:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_ITEM_SET_DATA_HEX")
    @CommandGroup("item_item")
    public void setItemItem(CommandSender sender, RPGItem item, Material material, String hexColour) {
        int dam;
        try {
            dam = Integer.parseInt((String) hexColour, 16);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Failed to parse " + hexColour);
            return;
        }
        item.setItem(material, false);
        item.meta = item.item.getItemMeta();
        if (item.meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) item.meta).setColor(Color.fromRGB(dam));
        } else {
            item.setDataValue((short) dam);
        }
        item.rebuild();
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_SET"), item.getName(), item.getItem(), item.item.getDurability()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] item $ITEMID:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_ITEM_SET_ID")
    @CommandGroup("item_item")
    public void setItemItem(CommandSender sender, RPGItem item, int id) {
        Material mat = Material.getMaterial(id);
        if (mat == null) {
            sender.sendMessage(ChatColor.RED + "Cannot find item");
            return;
        }
        item.setItem(mat);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_SET"), item.getName(), item.getItem(), item.item.getDurability()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] item $ITEMID:i[] $DATA:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_ITEM_SET_ID_DATA")
    @CommandGroup("item_item")
    public void setItemItem(CommandSender sender, RPGItem item, int id, int data) {
        Material mat = Material.getMaterial(id);
        if (mat == null) {
            sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_ITEM_CANT_FIND"));
            return;
        }
        item.setItem(mat, false);
        item.meta = item.item.getItemMeta();
        if (item.meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) item.meta).setColor(Color.fromRGB(data));
        } else {
            item.setDataValue((short) data);
        }
        item.rebuild();
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_SET"), item.getName(), item.getItem(), item.item.getDurability()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] removepower $POWER:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_REMOVEPOWER")
    @CommandGroup("item_removepower")
    public void itemRemovePower(CommandSender sender, RPGItem item, String power) {
        if (item.removePower(power)) {
            Power.powerUsage.put(power, Power.powerUsage.get(power) - 1);
            sender.sendMessage(ChatColor.GREEN + String.format(Locale.get("MESSAGE_POWER_REMOVED"), power));
            ItemManager.save(Plugin.plugin);
        } else {
            sender.sendMessage(ChatColor.RED + String.format(Locale.get("MESSAGE_POWER_UNKNOWN"), power));
        }
    }

    @CommandString("rpgitem $n[] description add $DESCRIPTIONLINE:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_DESCRIPTION_ADD")
    @CommandGroup("item_description")
    public void itemAddDescription(CommandSender sender, RPGItem item, String line) {
        item.addDescription(ChatColor.WHITE + line);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_DESCRIPTION_OK"));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] description set $LINENO:i[] $DESCRIPTIONLINE:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_DESCRIPTION_SET")
    @CommandGroup("item_description")
    public void itemSetDescription(CommandSender sender, RPGItem item, int lineNo, String line) {
        if (lineNo < 0 || lineNo >= item.description.size()) {
            sender.sendMessage(ChatColor.RED + String.format(Locale.get("MESSAGE_DESCRIPTION_OUT_OF_RANGE"), line));
            return;
        }
        item.description.set(lineNo, ChatColor.translateAlternateColorCodes('&', ChatColor.WHITE + line));
        item.rebuild();
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_DESCRIPTION_CHANGE"));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] description remove $LINENO:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_DESCRIPTION_REMOVE")
    @CommandGroup("item_description")
    public void itemRemoveDescription(CommandSender sender, RPGItem item, int lineNo) {
        if (lineNo < 0 || lineNo >= item.description.size()) {
            sender.sendMessage(ChatColor.RED + String.format(Locale.get("MESSAGE_DESCRIPTION_OUT_OF_RANGE"), lineNo));
            return;
        }
        item.description.remove(lineNo);
        item.rebuild();
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_DESCRIPTION_REMOVE"));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] worldguard")
    @CommandDocumentation("$COMMAND_RPGITEM_ITEM_WORLDGUARD")
    @CommandGroup("item_worldguard")
    public void itemToggleWorldGuard(CommandSender sender, RPGItem item) {
        if (!WorldGuard.isEnabled()) {
            sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_WORLDGUARD_ERROR"));
            return;
        }
        item.ignoreWorldGuard = !item.ignoreWorldGuard;
        if (item.ignoreWorldGuard) {
            sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_WORLDGUARD_OVERRIDE_ACTIVE"));
        } else {
            sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_WORLDGUARD_OVERRIDE_DISABLED"));
        }
    }
    
    @CommandString("rpgitem $n[] removerecipe")
    @CommandDocumentation("Removes the @[Item]#'s recipe")
    @CommandGroup("item_recipe")
    public void itemRemoveRecipe(CommandSender sender, RPGItem item) {
        item.hasRecipe = false;
        item.resetRecipe(true);
        sender.sendMessage(ChatColor.AQUA + "Recipe removed");
    }
    
    @CommandString("rpgitem $n[] recipe")
    @CommandDocumentation("Sets the @[Item]#'s recipe")
    @CommandGroup("item_recipe")
    public void itemSetRecipe(CommandSender sender, RPGItem item) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Inventory recipeInventory = Bukkit.createInventory(player, 27, "RPGItems - " + item.getDisplay());
            if (item.hasRecipe) {
                ItemStack blank = new ItemStack(Material.WALL_SIGN);
                ItemMeta meta = blank.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Do not change. Use the empty slots");
                ArrayList<String> lore = new ArrayList<String>();
                lore.add(ChatColor.WHITE + "Place items in the empty spaces");
                lore.add(ChatColor.WHITE + "in the shape of the crafting");
                lore.add(ChatColor.WHITE + "recipe that you want the item to");
                lore.add(ChatColor.WHITE + "have");
                meta.setLore(lore);
                blank.setItemMeta(meta);
                for (int i = 0; i < 27; i++) {
                    recipeInventory.setItem(i, blank);
                }
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        int i = x + y * 9;
                        ItemStack it = item.recipe.get(x + y * 3);
                        if (it != null)
                            recipeInventory.setItem(i, it);
                        else
                            recipeInventory.setItem(i, null);
                    }
                }
            }
            player.openInventory(recipeInventory);
            Events.recipeWindows.put(player.getName(), item.getID());
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players");
        }
    }
}
