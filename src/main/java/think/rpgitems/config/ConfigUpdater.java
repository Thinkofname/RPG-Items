package think.rpgitems.config;

import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;

import think.rpgitems.Plugin;

public class ConfigUpdater {

    final static String CONFIG_VERSION = "0.2";

    static HashMap<String, Updater> updates;
    static {
        updates = new HashMap<String, Updater>();
        updates.put("0.1", new Update01To02());
    }

    public static void updateConfig(ConfigurationSection conf) {
        while (!conf.getString("version", "0.0").equals(CONFIG_VERSION)) {
            if (!conf.contains("version")) {
                if (!conf.contains("autoupdate")) {
                    conf.set("autoupdate", true);
                }
                if (!conf.contains("defaults.hand")) {
                    conf.set("defaults.hand", "One handed");
                }
                if (!conf.contains("defaults.sword")) {
                    conf.set("defaults.sword", "Sword");
                }
                if (!conf.contains("defaults.damage")) {
                    conf.set("defaults.damage", "Damage");
                }
                if (!conf.contains("defaults.armour")) {
                    conf.set("defaults.armour", "Armour");
                }
                if (!conf.contains("support.worldguard")) {
                    conf.set("support.worldguard", false);
                }
                conf.set("version", "0.1");
                Plugin.plugin.saveConfig();
            } else {
                if (updates.containsKey(conf.get("version"))) {
                    updates.get(conf.get("version")).update(conf);
                } else {
                    break;
                }
            }
        }
        updates.clear();
    }
}
