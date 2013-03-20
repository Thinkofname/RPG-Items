package think.rpgitems.item;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.map.MinecraftFont;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Font;
import think.rpgitems.power.Power;

public class RPGItem {
    public ItemStack item;
    public ItemMeta meta;
    int id;
    String name;
    String encodedID;

    String displayName;
    Quality quality = Quality.TRASH;
    int damageMin = 0, damageMax = 3;
    int armour = 0;
    String loreText = "";
    String type = Plugin.plugin.getConfig().getString("defaults.sword", "Sword");
    String hand = Plugin.plugin.getConfig().getString("defaults.hand", "One handed");
    public boolean ignoreWorldGuard = false;

    public List<String> description = new ArrayList<String>();

    ArrayList<Power> powers = new ArrayList<Power>();

    public RPGItem(String name, int id) {
        this.name = name;
        this.id = id;
        encodedID = getMCEncodedID(id);
        item = new ItemStack(Material.WOOD_SWORD);
        meta = item.getItemMeta();

        displayName = item.getType().toString();
        rebuild();
    }

    public RPGItem(ConfigurationSection s) {

        name = s.getString("name");
        id = s.getInt("id");
        try {
            if (s.contains("display")) {
                setDisplay(s.getString("display"), false);
            } else {
                setDisplay(new String(byte[].class.cast(s.get("display_bin", "")), "UTF-8"), false);
            }
            if (s.contains("type")) {
                setType(s.getString("type", Plugin.plugin.getConfig().getString("defaults.sword", "Sword")), false);
            } else {
                if (s.contains("type_bin")) {
                    setType(new String(byte[].class.cast(s.get("type_bin", "")), "UTF-8"), false);
                } else {
                    setType(Plugin.plugin.getConfig().getString("defaults.sword", "Sword"), false);
                }
            }
            if (s.contains("hand")) {
                setHand(s.getString("hand", Plugin.plugin.getConfig().getString("defaults.hand", "One handed")), false);
            } else {
                if (s.contains("hand_bin")) {
                    setHand(new String(byte[].class.cast(s.get("hand_bin", "")), "UTF-8"), false);
                } else {
                    setHand(Plugin.plugin.getConfig().getString("defaults.hand", "One handed"), false);
                }
            }
            if (s.contains("lore")) {
                setLore(s.getString("lore"), false);
            } else {
                if (s.contains("lore_bin")) {
                    setLore(new String(byte[].class.cast(s.get("lore_bin", "")), "UTF-8"), false);
                } else {
                    setLore("", false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        description = (List<String>) s.getList("description", new ArrayList<String>());
        for (int i = 0; i < description.size(); i++) {
            description.set(i, ChatColor.translateAlternateColorCodes('&', description.get(i)));
        }
        quality = Quality.valueOf(s.getString("quality"));
        damageMin = s.getInt("damageMin");
        damageMax = s.getInt("damageMax");
        armour = s.getInt("armour", 0);
        item = new ItemStack(Material.valueOf(s.getString("item")));
        meta = item.getItemMeta();
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(s.getInt("item_colour", 0)));
        } else {
            item.setDurability((short) s.getInt("item_data", 0));
        }
        ignoreWorldGuard = s.getBoolean("ignoreWorldGuard", false);

        ConfigurationSection power = s.getConfigurationSection("powers");
        if (power != null) {
            for (String key : power.getKeys(false)) {
                try {
                    Power pow = Power.powers.get(key).newInstance();
                    pow.init(power.getConfigurationSection(key));
                    pow.item = this;
                    powers.add(pow);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        encodedID = getMCEncodedID(id);
        rebuild();
    }

    public void leftClick(Player player) {
        for (Power power : powers) {
            power.leftClick(player);
        }
    }

    public void rightClick(Player player) {
        for (Power power : powers) {
            power.rightClick(player);
        }
    }

    public void projectileHit(Player player, Projectile arrow) {
        for (Power power : powers) {
            power.projectileHit(player, arrow);
        }
    }

    public void hit(Player player, LivingEntity e) {
        for (Power power : powers) {
            power.hit(player, e);
        }
    }

    public void setDisplay(String str) {
        setDisplay(str, true);
    }

    public void setDisplay(String str, boolean update) {
        displayName = ChatColor.translateAlternateColorCodes('&', str);
        if (update)
            rebuild();
    }

    public String getDisplay() {
        return displayName;
    }

    public void setType(String str) {
        setType(str, true);
    }

    public void setType(String str, boolean update) {
        type = ChatColor.translateAlternateColorCodes('&', str);
        if (update)
            rebuild();
    }

    public String getType() {
        return type;
    }

    public void setHand(String h) {
        setHand(h, true);
    }

    public void setHand(String h, boolean update) {
        hand = ChatColor.translateAlternateColorCodes('&', h);
        if (update)
            rebuild();
    }

    public String getHand() {
        return hand;
    }

    public void setDamage(int min, int max) {
        setDamage(min, max, true);
    }

    public void setDamage(int min, int max, boolean update) {
        damageMin = min;
        damageMax = max;
        armour = 0;
        if (update)
            rebuild();
    }

    public int getDamageMin() {
        return damageMin;
    }

    public int getDamageMax() {
        return damageMax;
    }

    public void setArmour(int a) {
        setArmour(a, true);
    }

    public void setArmour(int a, boolean update) {
        armour = a;
        damageMin = damageMax = 0;
        if (update)
            rebuild();
    }

    public int getArmour() {
        return armour;
    }

    public void setLore(String str) {
        setLore(str, true);
    }

    public void setLore(String str, boolean update) {
        loreText = ChatColor.translateAlternateColorCodes('&', str);
        if (update)
            rebuild();
    }

    public String getLore() {
        return loreText;
    }

    public void setQuality(Quality q) {
        setQuality(q, true);
    }

    public void setQuality(Quality q, boolean update) {
        quality = q;
        if (update)
            rebuild();
    }

    public Quality getQuality() {
        return quality;
    }

    public void setItem(Material mat) {
        setItem(mat, true);
    }

    public void setItem(Material mat, boolean update) {
        item.setType(mat);
        if (update)
            rebuild();
    }

    public void setDataValue(short value) {
        item.setDurability(value);
    }

    public Material getItem() {
        return item.getType();
    }

    public void give(Player player) {
        player.getInventory().addItem(item);
    }

    public void addPower(Power power) {
        addPower(power, true);
    }

    public void addPower(Power power, boolean update) {
        powers.add(power);
        Power.powerUsage.put(power.getName(), Power.powerUsage.get(power.getName()) + 1);
        if (update)
            rebuild();
    }

    public void addDescription(String str) {
        addDescription(str, true);
    }

    public void addDescription(String str, boolean update) {
        description.add(ChatColor.translateAlternateColorCodes('&', str));
        if (update)
            rebuild();
    }

    public void rebuild() {
        int width = 150;
        meta.setDisplayName(encodedID + quality.colour + ChatColor.BOLD + displayName);
        int dWidth = getStringWidthBold(ChatColor.stripColor(displayName));
        if (dWidth > width)
            width = dWidth;

        dWidth = getStringWidth(ChatColor.stripColor(hand + "     " + type));
        if (dWidth > width)
            width = dWidth;
        String damageStr = null;
        if (damageMin == 0 && damageMax == 0 && armour != 0) {
            damageStr = armour + "% " + Plugin.plugin.getConfig().getString("defaults.armour", "Armour");
        } else if (armour == 0 && damageMin == 0 && damageMax == 0) {
            damageStr = null;
        } else if (damageMin == damageMax) {
            damageStr = damageMin + " " + Plugin.plugin.getConfig().getString("defaults.damage", "Damage");
        } else {
            damageStr = damageMin + "-" + damageMax + " " + Plugin.plugin.getConfig().getString("defaults.damage", "Damage");
        }
        if (damageMin != 0 || damageMax != 0 || armour != 0) {
            dWidth = getStringWidth(damageStr);
            if (dWidth > width)
                width = dWidth;
        }

        for (Power p : powers) {
            dWidth = getStringWidth(ChatColor.stripColor(p.displayText()));
            if (dWidth > width)
                width = dWidth;
        }

        for (String s : description) {
            dWidth = getStringWidth(ChatColor.stripColor(s));
            if (dWidth > width)
                width = dWidth;
        }

        ArrayList<String> lore = new ArrayList<String>();

        lore.add(ChatColor.WHITE + hand + StringUtils.repeat(" ", (width - getStringWidth(ChatColor.stripColor(hand + type))) / 4) + type);
        if (damageStr != null) {
            lore.add(ChatColor.WHITE + damageStr);
        }

        for (Power p : powers) {
            lore.add(p.displayText());
        }
        if (loreText.length() != 0) {
            int cWidth = 0;
            int tWidth = 0;
            StringBuilder out = new StringBuilder();
            StringBuilder temp = new StringBuilder();
            out.append(ChatColor.YELLOW);
            out.append(ChatColor.ITALIC);
            String currentColour = ChatColor.YELLOW.toString();
            String dMsg = "\"" + loreText + "\"";
            for (int i = 0; i < dMsg.length(); i++) {
                char c = dMsg.charAt(i);
                temp.append(c);
                if (c == ChatColor.COLOR_CHAR || c == '&') {
                    i += 1;
                    temp.append(dMsg.charAt(i));
                    currentColour = ChatColor.COLOR_CHAR + "" + dMsg.charAt(i);
                    continue;
                }
                if (c == ' ')
                    tWidth += 4;
                else
                    tWidth += Font.widths[c] + 1;
                if (c == ' ' || i == dMsg.length() - 1) {
                    if (cWidth + tWidth > width) {
                        cWidth = 0;
                        cWidth += tWidth;
                        tWidth = 0;
                        lore.add(out.toString());
                        out = new StringBuilder();
                        out.append(currentColour);
                        out.append(ChatColor.ITALIC);
                        out.append(temp);
                        temp = new StringBuilder();
                    } else {
                        out.append(temp);
                        temp = new StringBuilder();
                        cWidth += tWidth;
                        tWidth = 0;
                    }
                }
            }
            out.append(temp);
            lore.add(out.toString());
        }

        for (String s : description) {
            lore.add(s);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Iterator<ItemStack> it = player.getInventory().iterator();
            while (it.hasNext()) {
                ItemStack item = it.next();
                if (item == null)
                    continue;
                if (!item.hasItemMeta())
                    continue;
                if (!item.getItemMeta().hasDisplayName())
                    continue;
                ItemMeta im = item.getItemMeta();
                try {
                    int id = ItemManager.decodeId(im.getDisplayName());
                    if (id != getID())
                        continue;
                    item.setType(this.item.getType());
                    if (!(meta instanceof LeatherArmorMeta))
                        item.setDurability(this.item.getDurability());
                    item.setItemMeta(meta);
                } catch (Exception e) {
                }
            }
        }

    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public String getMCEncodedID() {
        return encodedID;
    }

    String getMCEncodedID(int id) {
        String hex = String.format("%08x", id);
        StringBuilder out = new StringBuilder();
        for (char h : hex.toCharArray()) {
            out.append(ChatColor.COLOR_CHAR);
            out.append(h);
        }
        return out.toString();
    }

    static int getStringWidth(String str) {
        int width = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == ' ')
                width += 4;
            else
                width += Font.widths[c] + 1;
        }
        return width;
    }

    static int getStringWidthBold(String str) {
        int width = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == ' ')
                width += 4;
            else
                width += Font.widths[c] + 2;
        }
        return width;
    }

    public void save(ConfigurationSection s) {
        s.set("name", name);
        s.set("id", id);
        // s.set("display_bin", displayName.replaceAll("" + ChatColor.COLOR_CHAR,
        // "&").getBytes("UTF-8"));
        s.set("display", displayName.replaceAll("" + ChatColor.COLOR_CHAR, "&"));
        s.set("quality", quality.toString());
        s.set("damageMin", damageMin);
        s.set("damageMax", damageMax);
        s.set("armour", armour);
        // s.set("type_bin", type.replaceAll("" + ChatColor.COLOR_CHAR, "&").getBytes("UTF-8"));
        s.set("type", type.replaceAll("" + ChatColor.COLOR_CHAR, "&"));
        // s.set("hand_bin", hand.replaceAll("" + ChatColor.COLOR_CHAR, "&").getBytes("UTF-8"));
        s.set("hand", hand.replaceAll("" + ChatColor.COLOR_CHAR, "&"));
        // s.set("lore_bin", loreText.replaceAll("" + ChatColor.COLOR_CHAR, "&").getBytes("UTF-8"));
        s.set("lore", loreText.replaceAll("" + ChatColor.COLOR_CHAR, "&"));
        ArrayList<String> descriptionConv = new ArrayList<String>(description);
        for (int i = 0; i < descriptionConv.size(); i++) {
            descriptionConv.set(i, descriptionConv.get(i).replaceAll("" + ChatColor.COLOR_CHAR, "&"));
        }
        s.set("description", descriptionConv);
        s.set("item", item.getType().toString());
        s.set("ignoreWorldGuard", ignoreWorldGuard);
        if (meta instanceof LeatherArmorMeta) {
            s.set("item_colour", ((LeatherArmorMeta) meta).getColor().asRGB());
        } else {
            s.set("item_data", item.getDurability());
        }
        ConfigurationSection power = s.createSection("powers");
        for (Power p : powers) {
            p.save(power.createSection(p.getName()));
        }
    }

    public boolean removePower(String pow) {
        Iterator<Power> it = powers.iterator();
        while (it.hasNext()) {
            if (it.next().getName().equalsIgnoreCase(pow)) {
                it.remove();
                rebuild();
                return true;
            }
        }
        return false;
    }

    public void print(CommandSender sender) {
        int width = 150;
        sender.sendMessage(quality.colour + ChatColor.BOLD + displayName);
        int dWidth = getStringWidthBold(ChatColor.stripColor(displayName));
        if (dWidth > width)
            width = dWidth;

        dWidth = getStringWidth(ChatColor.stripColor(hand + "     " + type));
        if (dWidth > width)
            width = dWidth;
        String damageStr = null;
        if (damageMin == 0 && damageMax == 0 && armour != 0) {
            damageStr = armour + "% " + Plugin.plugin.getConfig().getString("defaults.armour", "Armour");
        } else if (armour == 0 && damageMin == 0 && damageMax == 0) {
            damageStr = null;
        } else if (damageMin == damageMax) {
            damageStr = damageMin + " " + Plugin.plugin.getConfig().getString("defaults.damage", "Damage");
        } else {
            damageStr = damageMin + "-" + damageMax + " " + Plugin.plugin.getConfig().getString("defaults.damage", "Damage");
        }
        if (damageMin != 0 || damageMax != 0 || armour != 0) {
            dWidth = getStringWidth(damageStr);
            if (dWidth > width)
                width = dWidth;
        }

        for (Power p : powers) {
            dWidth = getStringWidth(ChatColor.stripColor(p.displayText()));
            if (dWidth > width)
                width = dWidth;
        }

        for (String s : description) {
            dWidth = getStringWidth(ChatColor.stripColor(s));
            if (dWidth > width)
                width = dWidth;
        }

        ArrayList<String> lore = new ArrayList<String>();

        lore.add(ChatColor.WHITE + hand + StringUtils.repeat(" ", (width - getStringWidth(ChatColor.stripColor(hand + type))) / 4) + type);
        if (damageStr != null) {
            lore.add(ChatColor.WHITE + damageStr);
        }

        for (Power p : powers) {
            lore.add(p.displayText());
        }
        if (loreText.length() != 0) {
            int cWidth = 0;
            int tWidth = 0;
            StringBuilder out = new StringBuilder();
            StringBuilder temp = new StringBuilder();
            out.append(ChatColor.YELLOW);
            out.append(ChatColor.ITALIC);
            String currentColour = ChatColor.YELLOW.toString();
            String dMsg = "\"" + loreText + "\"";
            for (int i = 0; i < dMsg.length(); i++) {
                char c = dMsg.charAt(i);
                temp.append(c);
                if (c == ChatColor.COLOR_CHAR || c == '&') {
                    i += 1;
                    temp.append(dMsg.charAt(i));
                    currentColour = ChatColor.COLOR_CHAR + "" + dMsg.charAt(i);
                    continue;
                }
                if (c == ' ')
                    tWidth += 4;
                else
                    tWidth += Font.widths[c] + 1;
                if (c == ' ' || i == dMsg.length() - 1) {
                    if (cWidth + tWidth > width) {
                        cWidth = 0;
                        cWidth += tWidth;
                        tWidth = 0;
                        lore.add(out.toString());
                        out = new StringBuilder();
                        out.append(currentColour);
                        out.append(ChatColor.ITALIC);
                        out.append(temp);
                        temp = new StringBuilder();
                    } else {
                        out.append(temp);
                        temp = new StringBuilder();
                        cWidth += tWidth;
                        tWidth = 0;
                    }
                }
            }
            out.append(temp);
            lore.add(out.toString());
        }
        for (String s : description) {
            lore.add(s);
        }
        for (String s : lore) {
            sender.sendMessage(s);
        }
    }
}
