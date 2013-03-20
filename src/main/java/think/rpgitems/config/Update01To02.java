package think.rpgitems.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import think.rpgitems.Plugin;

public class Update01To02 implements Updater {

    public void update(ConfigurationSection section) {

        File iFile = new File(Plugin.plugin.getDataFolder(), "items.yml");
        YamlConfiguration itemStorage = YamlConfiguration.loadConfiguration(iFile);
        ConfigurationSection iSection = itemStorage.getConfigurationSection("items");

        if (iSection != null) {
            for (String key : iSection.getKeys(false)) {
                ConfigurationSection item = iSection.getConfigurationSection(key);
                if (item.contains("armour")) {
                    int dam = item.getInt("armour");
                    item.set("armour", (int) ((((double) dam) / 20d) * 100d));
                }
            }
        }

        section.set("version", "0.2");

        try {
            itemStorage.save(iFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Plugin.plugin.saveConfig();
    }

}
