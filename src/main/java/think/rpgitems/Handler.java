package think.rpgitems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
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
    @CommandDocumentation("$command.rpgitem.list")
    @CommandGroup("list")
    public void listItems(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "RPGItems:");
        for (RPGItem item : ItemManager.itemByName.values()) {
            sender.sendMessage(ChatColor.GREEN + item.getName() + " - " + item.getDisplay());
        }
    }

    @CommandString("rpgitem option worldguard")
    @CommandDocumentation("$command.rpgitem.worldguard")
    @CommandGroup("option_worldguard")
    public void toggleWorldGuard(CommandSender sender) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        if (!WorldGuard.isEnabled()) {
            sender.sendMessage(ChatColor.RED + Locale.get("message.worldguard.error", locale));
            return;
        }
        if (WorldGuard.useWorldGuard) {
            sender.sendMessage(ChatColor.AQUA + Locale.get("message.worldguard.disable", locale));
        } else {
            sender.sendMessage(ChatColor.AQUA + Locale.get("message.worldguard.enable", locale));
        }
        WorldGuard.useWorldGuard = !WorldGuard.useWorldGuard;
        Plugin.plugin.getConfig().set("support.worldguard", WorldGuard.useWorldGuard);
        Plugin.plugin.saveConfig();
    }

    @CommandString("rpgitem $n[]")
    @CommandDocumentation("$command.rpgitem.print")
    @CommandGroup("item")
    public void printItem(CommandSender sender, RPGItem item) {
        item.print(sender);
    }

    @CommandString("rpgitem $name:s[] create")
    @CommandDocumentation("$command.rpgitem.create")
    @CommandGroup("item")
    public void createItem(CommandSender sender, String itemName) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        if (ItemManager.newItem(itemName.toLowerCase()) != null) {
            sender.sendMessage(String.format(ChatColor.GREEN + Locale.get("message.create.ok", locale), itemName));
            ItemManager.save(Plugin.plugin);
        } else {
            sender.sendMessage(ChatColor.RED + Locale.get("message.create.fail", locale));
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
    @CommandDocumentation("$command.rpgitem.give")
    @CommandGroup("item_give")
    public void giveItem(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        if (sender instanceof Player) {
            if ((!Plugin.plugin.getConfig().getBoolean("give-perms", false) && sender.hasPermission("rpgitem")) || (Plugin.plugin.getConfig().getBoolean("give-perms", false) && sender.hasPermission("rpgitem.give." + item.getName()))) {
                item.give((Player) sender);
                sender.sendMessage(ChatColor.AQUA + "You were given " + item.getDisplay());
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission");
            }
        } else {
            sender.sendMessage(ChatColor.RED + Locale.get("message.give.console", locale));
        }
    }

    @CommandString("rpgitem $n[] give $p[]")
    @CommandDocumentation("$command.rpgitem.give.player")
    @CommandGroup("item_give")
    public void giveItemPlayer(CommandSender sender, RPGItem item, Player player) {
        item.give(player);
        sender.sendMessage(ChatColor.AQUA + "Gave " + item.getDisplay() + ChatColor.AQUA + " to " + player.getName());
        player.sendMessage(ChatColor.AQUA + "You were given " + item.getDisplay());
    }

    @CommandString("rpgitem $n[] give $p[] $count:i[]")
    @CommandDocumentation("$command.rpgitem.give.player.count")
    @CommandGroup("item_give")
    public void giveItemPlayerCount(CommandSender sender, RPGItem item, Player player, int count) {
        for (int i = 0; i < count; i++) {
            item.give(player);
        }
    }

    @CommandString("rpgitem $n[] remove")
    @CommandDocumentation("$command.rpgitem.remove")
    @CommandGroup("item_remove")
    public void removeItem(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        ItemManager.remove(item);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.remove.ok", locale), item.getName()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] display")
    @CommandDocumentation("$command.rpgitem.display")
    @CommandGroup("item_display")
    public void getItemDisplay(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.display.get", locale), item.getName(), item.getDisplay()));
    }

    @CommandString("rpgitem $n[] display $display:s[]")
    @CommandDocumentation("$command.rpgitem.display.set")
    @CommandGroup("item_display")
    public void setItemDisplay(CommandSender sender, RPGItem item, String display) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.setDisplay(display);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.display.set", locale), item.getName(), item.getDisplay()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] quality")
    @CommandDocumentation("$command.rpgitem.quality")
    @CommandGroup("item_quality")
    public void getItemQuality(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.quality.get", locale), item.getName(), item.getQuality().toString().toLowerCase()));
    }

    @CommandString("rpgitem $n[] quality $quality:o[trash,common,uncommon,rare,epic,legendary]")
    @CommandDocumentation("$command.rpgitem.quality.set")
    @CommandGroup("item_quality")
    public void setItemQuality(CommandSender sender, RPGItem item, String quality) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.setQuality(Quality.valueOf(quality.toUpperCase()));
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.quality.set", locale), item.getName(), item.getQuality().toString().toLowerCase()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] damage")
    @CommandDocumentation("$command.rpgitem.damage")
    @CommandGroup("item_damage")
    public void getItemDamage(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.damage.get", locale), item.getName(), item.getDamageMin(), item.getDamageMax()));
    }

    @CommandString("rpgitem $n[] damage $damage:i[]")
    @CommandDocumentation("$command.rpgitem.damage.set")
    @CommandGroup("item_damage")
    public void setItemDamage(CommandSender sender, RPGItem item, int damage) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.setDamage(damage, damage);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.damage.set", locale), item.getName(), item.getDamageMin()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] damage $min:i[] $max:i[]")
    @CommandDocumentation("$command.rpgitem.damage.set.range")
    @CommandGroup("item_damage")
    public void setItemDamage(CommandSender sender, RPGItem item, int min, int max) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.setDamage(min, max);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.damage.set.range", locale), item.getName(), item.getDamageMin(), item.getDamageMax()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] armour")
    @CommandDocumentation("$command.rpgitem.armour")
    @CommandGroup("item_armour")
    public void getItemArmour(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.armour.get", locale), item.getName(), item.getArmour()));
    }

    @CommandString("rpgitem $n[] armour $armour:i[0,100]")
    @CommandDocumentation("$command.rpgitem.armour.set")
    @CommandGroup("item_armour")
    public void setItemArmour(CommandSender sender, RPGItem item, int armour) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.setArmour(armour);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.armour.set", locale), item.getName(), item.getArmour()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] type")
    @CommandDocumentation("$command.rpgitem.type")
    @CommandGroup("item_type")
    public void getItemType(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.type.get", locale), item.getName(), item.getType()));
    }

    @CommandString("rpgitem $n[] type $type:s[]")
    @CommandDocumentation("$command.rpgitem.type.set")
    @CommandGroup("item_type")
    public void setItemType(CommandSender sender, RPGItem item, String type) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.setType(type);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.type.set", locale), item.getName(), item.getType()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] hand")
    @CommandDocumentation("$command.rpgitem.hand")
    @CommandGroup("item_hand")
    public void getItemHand(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.hand.get", locale), item.getName(), item.getHand()));
    }

    @CommandString("rpgitem $n[] hand $hand:s[]")
    @CommandDocumentation("$command.rpgitem.hand.set")
    @CommandGroup("item_hand")
    public void setItemHand(CommandSender sender, RPGItem item, String hand) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.setHand(hand);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.hand.set", locale), item.getName(), item.getHand()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] lore")
    @CommandDocumentation("$command.rpgitem.lore")
    @CommandGroup("item_lore")
    public void getItemLore(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.lore.get", locale), item.getName(), item.getLore()));
    }

    @CommandString("rpgitem $n[] lore $lore:s[]")
    @CommandDocumentation("$command.rpgitem.lore.set")
    @CommandGroup("item_lore")
    public void setItemLore(CommandSender sender, RPGItem item, String lore) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.setLore(lore);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.lore.set", locale), item.getName(), item.getLore()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] item")
    @CommandDocumentation("$command.rpgitem.item")
    @CommandGroup("item_item")
    public void getItemItem(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.item.get", locale), item.getName(), item.getItem().toString()));
    }

    @CommandString("rpgitem $n[] item $m[]")
    @CommandDocumentation("$command.rpgitem.item.set")
    @CommandGroup("item_item")
    public void setItemItem(CommandSender sender, RPGItem item, Material material) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.setItem(material);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.item.set", locale), item.getName(), item.getItem(), item.item.getDurability()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] item $m[] $data:i[]")
    @CommandDocumentation("$command.rpgitem.item.set.data")
    @CommandGroup("item_item")
    public void setItemItem(CommandSender sender, RPGItem item, Material material, int data) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.setItem(material, false);
        ItemMeta meta = item.item.getItemMeta();
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(data));
        } else {
            item.setDataValue((short) data);
        }
        for (String locales : Locale.getLocales()) {
            item.setLocaleMeta(locales, meta);
        }
        item.rebuild();
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.item.set", locale), item.getName(), item.getItem(), item.item.getDurability()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] item $m[] hex $hexcolour:s[]")
    @CommandDocumentation("$command.rpgitem.item.set.data.hex")
    @CommandGroup("item_item")
    public void setItemItem(CommandSender sender, RPGItem item, Material material, String hexColour) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        int dam;
        try {
            dam = Integer.parseInt((String) hexColour, 16);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Failed to parse " + hexColour);
            return;
        }
        item.setItem(material, false);
        ItemMeta meta = item.item.getItemMeta();
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(dam));
        } else {
            item.setDataValue((short) dam);
        }
        for (String locales : Locale.getLocales()) {
            item.setLocaleMeta(locales, meta);
        }
        item.rebuild();
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.item.set", locale), item.getName(), item.getItem(), item.item.getDurability()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] item $itemid:i[]")
    @CommandDocumentation("$command.rpgitem.item.set.id")
    @CommandGroup("item_item")
    public void setItemItem(CommandSender sender, RPGItem item, int id) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        Material mat = Material.getMaterial(id);
        if (mat == null) {
            sender.sendMessage(ChatColor.RED + "Cannot find item");
            return;
        }
        item.setItem(mat);
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.item.set", locale), item.getName(), item.getItem(), item.item.getDurability()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] item $itemid:i[] $data:i[]")
    @CommandDocumentation("$command.rpgitem.item.set.id.data")
    @CommandGroup("item_item")
    public void setItemItem(CommandSender sender, RPGItem item, int id, int data) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        Material mat = Material.getMaterial(id);
        if (mat == null) {
            sender.sendMessage(ChatColor.RED + Locale.get("message.item.cant.find", locale));
            return;
        }
        item.setItem(mat, false);
        ItemMeta meta = item.item.getItemMeta();
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(data));
        } else {
            item.setDataValue((short) data);
        }
        for (String locales : Locale.getLocales()) {
            item.setLocaleMeta(locales, meta);
        }
        item.rebuild();
        sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("message.item.set", locale), item.getName(), item.getItem(), item.item.getDurability()));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] removepower $power:s[]")
    @CommandDocumentation("$command.rpgitem.removepower")
    @CommandGroup("item_removepower")
    public void itemRemovePower(CommandSender sender, RPGItem item, String power) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        if (item.removePower(power)) {
            Power.powerUsage.put(power, Power.powerUsage.get(power) - 1);
            sender.sendMessage(ChatColor.GREEN + String.format(Locale.get("message.power.removed", locale), power));
            ItemManager.save(Plugin.plugin);
        } else {
            sender.sendMessage(ChatColor.RED + String.format(Locale.get("message.power.unknown", locale), power));
        }
    }

    @CommandString("rpgitem $n[] description add $descriptionline:s[]")
    @CommandDocumentation("$command.rpgitem.description.add")
    @CommandGroup("item_description")
    public void itemAddDescription(CommandSender sender, RPGItem item, String line) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        item.addDescription(ChatColor.WHITE + line);
        sender.sendMessage(ChatColor.AQUA + Locale.get("message.description.ok", locale));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] description set $lineno:i[] $descriptionline:s[]")
    @CommandDocumentation("$command.rpgitem.description.set")
    @CommandGroup("item_description")
    public void itemSetDescription(CommandSender sender, RPGItem item, int lineNo, String line) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        if (lineNo < 0 || lineNo >= item.description.size()) {
            sender.sendMessage(ChatColor.RED + String.format(Locale.get("message.description.out.of.range", locale), line));
            return;
        }
        item.description.set(lineNo, ChatColor.translateAlternateColorCodes('&', ChatColor.WHITE + line));
        item.rebuild();
        sender.sendMessage(ChatColor.AQUA + Locale.get("message.description.change", locale));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] description remove $lineno:i[]")
    @CommandDocumentation("$command.rpgitem.description.remove")
    @CommandGroup("item_description")
    public void itemRemoveDescription(CommandSender sender, RPGItem item, int lineNo) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        if (lineNo < 0 || lineNo >= item.description.size()) {
            sender.sendMessage(ChatColor.RED + String.format(Locale.get("message.description.out.of.range", locale), lineNo));
            return;
        }
        item.description.remove(lineNo);
        item.rebuild();
        sender.sendMessage(ChatColor.AQUA + Locale.get("message.description.remove", locale));
        ItemManager.save(Plugin.plugin);
    }

    @CommandString("rpgitem $n[] worldguard")
    @CommandDocumentation("$command.rpgitem.item.worldguard")
    @CommandGroup("item_worldguard")
    public void itemToggleWorldGuard(CommandSender sender, RPGItem item) {
        String locale = sender instanceof Player ? Locale.getPlayerLocale((Player) sender) : "en_GB";
        if (!WorldGuard.isEnabled()) {
            sender.sendMessage(ChatColor.AQUA + Locale.get("message.worldguard.error", locale));
            return;
        }
        item.ignoreWorldGuard = !item.ignoreWorldGuard;
        if (item.ignoreWorldGuard) {
            sender.sendMessage(ChatColor.AQUA + Locale.get("message.worldguard.override.active", locale));
        } else {
            sender.sendMessage(ChatColor.AQUA + Locale.get("message.worldguard.override.disabled", locale));
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
            String title = "RPGItems - " + item.getDisplay();
            if (title.length() > 32) {
                title = title.substring(0, 32);
            }
            Inventory recipeInventory = Bukkit.createInventory(player, 27, title);
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
    
    @CommandString("rpgitem $n[] drop $e[org.bukkit.entity.EntityType]")
    @CommandDocumentation("Gets the chance that @[Item]# will drop from @[EntityType]#. 0% means it doesn't drop")
    @CommandGroup("item_drop")
    public void getItemDropChance(CommandSender sender, RPGItem item, EntityType type) {
        sender.sendMessage(String.format(ChatColor.AQUA + "The chance that '%s" + ChatColor.AQUA + "' will drop from '%s' is %.2f%%", item.getDisplay(), type.toString().toLowerCase(), item.dropChances.get(type.toString())));
    }

    @CommandString("rpgitem $n[] drop $e[org.bukkit.entity.EntityType] $chance:f[]")
    @CommandDocumentation("Sets the chance that @[Item]# will drop from @[EntityType]# to @[Chance]#%. 0% prevents it from dropping")
    @CommandGroup("item_drop")
    public void setItemDropChance(CommandSender sender, RPGItem item, EntityType type, double chance) {
        chance = Math.min(chance, 100.0);
        String typeS = type.toString();
        if (chance > 0) {
            item.dropChances.put(typeS, chance);
            if (!Events.drops.containsKey(typeS)) {
                Events.drops.put(typeS, new HashSet<Integer>());
            }
            Set<Integer> set = Events.drops.get(typeS);
            set.add(item.getID());
        } else {
            item.dropChances.remove(typeS);
            if (Events.drops.containsKey(typeS)) {
                Set<Integer> set = Events.drops.get(typeS);
                set.remove(item.getID());
            }
        }
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(String.format(ChatColor.AQUA + "The chance that '%s" + ChatColor.AQUA + "' will drop from '%s' was set to %.2f%%", item.getDisplay(), typeS.toLowerCase(), item.dropChances.get(typeS)));
    }
}
