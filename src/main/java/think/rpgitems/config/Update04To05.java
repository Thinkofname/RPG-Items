package think.rpgitems.config;

import org.bukkit.configuration.ConfigurationSection;
import think.rpgitems.Plugin;

public class Update04To05 implements Updater {

    @Override
    public void update(ConfigurationSection section) {
        if (!section.contains("support.enchantments")) section.set("support.enchantments", false);
        section.set("version", "0.5");

        Plugin.plugin.saveConfig();
    }
}
