package think.rpgitems.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.power.Power;
import think.rpgitems.support.WorldGuard;

import gnu.trove.map.hash.TIntObjectHashMap;

public class ItemManager {
    private static TIntObjectHashMap<RPGItem> itemById = new TIntObjectHashMap<RPGItem>();
    public static HashMap<String, RPGItem> itemByName = new HashMap<String, RPGItem>();
    private static int currentPos = 0;

    static {
        Commands.add("rpgitem list", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_LIST");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                sender.sendMessage(ChatColor.GREEN + "RPGItems:");
                for (RPGItem item : ItemManager.itemByName.values()) {
                    sender.sendMessage(ChatColor.GREEN + item.getName() + " - " + item.getQuality().colour + ChatColor.BOLD + item.getDisplay());
                }

            }
        });
        Commands.add("rpgitem worldguard", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_WORLDGUARD");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
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
        });
        Commands.add("rpgitem $n[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_PRINT");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.print(sender);
            }
        });
        Commands.add("rpgitem $NAME:s[] create", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_CREATE");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                if (ItemManager.newItem(((String) args[0]).toLowerCase()) != null) {
                    sender.sendMessage(String.format(ChatColor.GREEN + Locale.get("MESSAGE_CREATE_OK"), args[0]));
                    ItemManager.save(Plugin.plugin);
                } else {
                    sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_CREATE_FAIL"));
                }
            }
        });
        Commands.add("rpgitem $n[] give", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_GIVE");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                if (sender instanceof Player) {
                    ((RPGItem) args[0]).give((Player) sender);
                } else {
                    sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_GIVE_CONSOLE"));
                }
            }
        });
        Commands.add("rpgitem $n[] give $p[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_GIVE_PLAYER");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                ((RPGItem) args[0]).give(((Player) args[1]));
            }
        });
        Commands.add("rpgitem $n[] give $p[] $COUNT:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_GIVE_PLAYER_COUNT");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                int count = (Integer) args[2];
                for (int i = 0; i < count; i++)
                    ((RPGItem) args[0]).give(((Player) args[1]));
            }
        });
        Commands.add("rpgitem $n[] remove", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_REMOVE");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                ItemManager.remove(item);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_REMOVE_OK"), item.getName()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] display", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_DISPLAY");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_DISPLAY_GET"), item.getName(), item.getDisplay()));
            }
        });
        Commands.add("rpgitem $n[] display $DISPLAY:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_DISPLAY_SET");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.setDisplay((String) args[1]);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_DISPLAY_SET"), item.getName(), item.getDisplay()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] quality", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_QUALITY");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_QUALITY_GET"), item.getName(), item.getQuality().toString().toLowerCase()));
            }
        });
        Commands.add("rpgitem $n[] quality $QUALITY:o[@trash,common,uncommon,rare,epic,legendary]", new Commands() {
            @Override
            public String getDocs() {
                StringBuilder out = new StringBuilder();
                for (Quality q : Quality.values()) {
                    String name = normalCase(q.toString());
                    out.append(name).append(" (&").append(q.cCode).append(name).append("&r) ");
                }
                return String.format(Locale.get("COMMAND_RPGITEM_QUALITY_SET"), out.toString());
            }

            private String normalCase(String str) {
                return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
            }

            @Override
            public String getNote() {
                StringBuilder out = new StringBuilder();
                out.append(Locale.get("MESSAGE_QUALITY_NOTE"));
                for (Quality q : Quality.values()) {
                    out.append(q.colour).append(q.toString()).append(", ");
                }
                return out.toString();
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                Quality q = Quality.valueOf(((String) args[1]).toUpperCase());
                RPGItem item = ((RPGItem) args[0]);
                item.setQuality(q);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_QUALITY_SET"), item.getName(), item.getQuality().toString().toLowerCase()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] damage", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_DAMAGE");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_DAMAGE_GET"), item.getName(), item.getDamageMin(), item.getDamageMax()));
            }
        });
        Commands.add("rpgitem $n[] damage $DAMAGE:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_DAMAGE_SET");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.setDamage((Integer) args[1], (Integer) args[1]);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_DAMAGE_SET"), item.getName(), item.getDamageMin()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] damage $MIN:i[] $MAX:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_DAMAGE_SET_RANAGE");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.setDamage((Integer) args[1], (Integer) args[2]);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_DAMAGE_SET_RANGE"), item.getName(), item.getDamageMin(), item.getDamageMax()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] armour", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ARMOUR");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ARMOUR_GET"), item.getName(), item.getArmour()));
            }
        });
        Commands.add("rpgitem $n[] armour $ARMOUR:i[0,100]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ARMOUR_SET");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.setArmour((Integer) args[1]);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ARMOUR_SET"), item.getName(), item.getArmour()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] type", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_TYPE");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_TYPE_GET"), item.getName(), item.getType()));
            }
        });
        Commands.add("rpgitem $n[] type $TYPE:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_TYPE_SET");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.setType((String) args[1]);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_TYPE_SET"), item.getName(), item.getType()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] hand", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_HAND");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_HAND_GET"), item.getName(), item.getHand()));
            }
        });
        Commands.add("rpgitem $n[] hand $HAND:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_HAND_SET");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.setHand((String) args[1]);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_HAND_SET"), item.getName(), item.getHand()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] lore", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_LORE");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_LORE_GET"), item.getName(), item.getLore()));
            }
        });
        Commands.add("rpgitem $n[] lore $LORE:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_LORE_SET");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.setLore((String) args[1]);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_LORE_SET"), item.getName(), item.getLore()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] item", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ITEM");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_GET"), item.getName(), item.getItem().toString()));
            }
        });
        Commands.add("rpgitem $n[] item $m[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ITEM_SET");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.setItem((Material) args[1]);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_SET"), item.getName(), item.getItem(), item.item.getDurability()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] item $m[] $DATA:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ITEM_SET_DATA");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.setItem((Material) args[1], false);
                item.meta = item.item.getItemMeta();
                int dam = (Integer) args[2];
                if (item.meta instanceof LeatherArmorMeta) {
                    ((LeatherArmorMeta) item.meta).setColor(Color.fromRGB(dam));
                } else {
                    item.setDataValue((short) dam);
                }
                item.rebuild();
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_SET"), item.getName(), item.getItem(), item.item.getDurability()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] item $m[] hex $HEXCOLOUR:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ITEM_SET_DATA_HEX");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                int dam;
                try {
                    dam = Integer.parseInt((String) args[2], 16);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Failed to parse " + args[2]);
                    return;
                }
                RPGItem item = (RPGItem) args[0];
                item.setItem((Material) args[1], false);
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
        });
        Commands.add("rpgitem $n[] item $ITEMID:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ITEM_SET_ID");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                Material mat = Material.getMaterial((Integer) args[1]);
                if (mat == null) {
                    sender.sendMessage(ChatColor.RED + "Cannot find item");
                    return;
                }
                item.setItem(mat);
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_SET"), item.getName(), item.getItem(), item.item.getDurability()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] item $ITEMID:i[] $DATA:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ITEM_SET_ID_DATA");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                Material mat = Material.getMaterial((Integer) args[1]);
                if (mat == null) {
                    sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_ITEM_CANT_FIND"));
                    return;
                }
                item.setItem(mat, false);
                item.meta = item.item.getItemMeta();
                int dam = (Integer) args[2];
                if (item.meta instanceof LeatherArmorMeta) {
                    ((LeatherArmorMeta) item.meta).setColor(Color.fromRGB(dam));
                } else {
                    item.setDataValue((short) dam);
                }
                item.rebuild();
                sender.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_ITEM_SET"), item.getName(), item.getItem(), item.item.getDurability()));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] removepower $POWER:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_REMOVEPOWER");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                String pow = (String) args[1];
                if (item.removePower(pow)) {
                    Power.powerUsage.put(pow, Power.powerUsage.get(pow) - 1);
                    sender.sendMessage(ChatColor.GREEN + String.format(Locale.get("MESSAGE_POWER_REMOVED"), pow));
                    ItemManager.save(Plugin.plugin);
                } else {
                    sender.sendMessage(ChatColor.RED + String.format(Locale.get("MESSAGE_POWER_UNKNOWN"), pow));
                }
            }
        });
        Commands.add("rpgitem $n[] description add $DESCRIPTIONLINE:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_DESCRIPTION_ADD");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                item.addDescription(ChatColor.WHITE + (String) args[1]);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_DESCRIPTION_OK"));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] description set $LINENO:i[] $DESCRIPTIONLINE:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_DESCRIPTION_SET");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                int line = (Integer) args[1];
                if (line < 0 || line >= item.description.size()) {
                    sender.sendMessage(ChatColor.RED + String.format(Locale.get("MESSAGE_DESCRIPTION_OUT_OF_RANGE"), line));
                    return;
                }
                item.description.set(line, ChatColor.translateAlternateColorCodes('&', ChatColor.WHITE + (String) args[2]));
                item.rebuild();
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_DESCRIPTION_CHANGE"));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] description remove $LINENO:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_DESCRIPTION_REMOVE");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                int line = (Integer) args[1];
                if (line < 0 || line >= item.description.size()) {
                    sender.sendMessage(ChatColor.RED + String.format(Locale.get("MESSAGE_DESCRIPTION_OUT_OF_RANGE"), line));
                    return;
                }
                item.description.remove(line);
                item.rebuild();
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_DESCRIPTION_REMOVE"));
                ItemManager.save(Plugin.plugin);
            }
        });
        Commands.add("rpgitem $n[] worldguard", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_ITEM_WORLDGUARD");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                if (!WorldGuard.isEnabled()) {
                    sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_WORLDGUARD_ERROR"));
                    return;
                }
                RPGItem item = (RPGItem) args[0];
                item.ignoreWorldGuard = !item.ignoreWorldGuard;
                if (item.ignoreWorldGuard) {
                    sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_WORLDGUARD_OVERRIDE_ACTIVE"));
                } else {
                    sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_WORLDGUARD_OVERRIDE_DISABLED"));
                }
            }

        });
        // Kinda of a hack to cause the static blocks of the powers to load
        for (Class<? extends Power> p : Power.powers.values()) {
            try {
                p.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void load(Plugin plugin) {
        try {
            FileInputStream in = null;
            YamlConfiguration itemStorage = null;
            try {
                File f = new File(plugin.getDataFolder(), "items.yml");
                in = new FileInputStream(f);
                byte[] data = new byte[(int) f.length()];
                in.read(data);
                itemStorage = new YamlConfiguration();
                String str = new String(data, "UTF-8");
                itemStorage.loadFromString(str);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            currentPos = itemStorage.getInt("pos", 0);
            ConfigurationSection section = itemStorage.getConfigurationSection("items");
            for (String key : section.getKeys(false)) {
                RPGItem item = new RPGItem(section.getConfigurationSection(key));
                itemById.put(item.getID(), item);
                itemByName.put(item.getName(), item);
                for (Power power : item.powers) {
                    Power.powerUsage.put(power.getName(), Power.powerUsage.get(power.getName()) + 1);
                }
            }
        } catch (Exception e) {
        }
    }

    public static void save(Plugin plugin) {

        YamlConfiguration itemStorage = new YamlConfiguration();

        itemStorage.set("items", null);
        itemStorage.set("pos", currentPos);
        ConfigurationSection newSection = itemStorage.createSection("items");
        for (RPGItem item : itemById.valueCollection()) {
            ConfigurationSection itemSection = newSection.getConfigurationSection(item.getName());
            if (itemSection == null) {
                itemSection = newSection.createSection(item.getName());
            }
            item.save(itemSection);
        }

        FileOutputStream out = null;
        try {
            File f = new File(plugin.getDataFolder(), "items.yml");
            out = new FileOutputStream(f);
            out.write(itemStorage.saveToString().getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static RPGItem newItem(String name) {
        if (itemByName.containsKey(name))
            return null;
        int free = 0;
        while (true) {
            free = currentPos++;
            if (!itemById.containsKey(free))
                break;
        }
        RPGItem item = new RPGItem(name, free);
        itemById.put(free, item);
        itemByName.put(name, item);
        return item;
    }

    public static RPGItem getItemById(int id) {
        return itemById.get(id);
    }

    public static RPGItem getItemByName(String uid) {
        return itemByName.get(uid);
    }

    public static int decodeId(String str) throws Exception {
        if (str.length() < 16) {
            throw new Exception();
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (str.charAt(i) != ChatColor.COLOR_CHAR)
                throw new Exception();
            i++;
            out.append(str.charAt(i));
        }
        return Integer.parseInt(out.toString(), 16);
    }

    public static void remove(RPGItem item) {
        itemByName.remove(item.getName());
        itemById.remove(item.getID());
    }

}
