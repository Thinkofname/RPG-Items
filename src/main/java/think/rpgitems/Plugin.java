package think.rpgitems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

import think.rpgitems.commands.Commands;
import think.rpgitems.config.ConfigUpdater;
import think.rpgitems.data.Font;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.power.Power;
import think.rpgitems.power.PowerArrow;
import think.rpgitems.power.PowerCommand;
import think.rpgitems.power.PowerConsume;
import think.rpgitems.power.PowerFireball;
import think.rpgitems.power.PowerFlame;
import think.rpgitems.power.PowerIce;
import think.rpgitems.power.PowerKnockup;
import think.rpgitems.power.PowerLightning;
import think.rpgitems.power.PowerPotionHit;
import think.rpgitems.power.PowerPotionSelf;
import think.rpgitems.power.PowerRainbow;
import think.rpgitems.power.PowerRumble;
import think.rpgitems.power.PowerRush;
import think.rpgitems.power.PowerTNTCannon;
import think.rpgitems.power.PowerTeleport;
import think.rpgitems.power.PowerUnbreakable;
import think.rpgitems.power.PowerUnbreaking;
import think.rpgitems.support.WorldGuard;

public class Plugin extends JavaPlugin {

    public static Logger logger = Logger.getLogger("RPGItems");

    public static Plugin plugin;

    @Override
    public void onLoad() {
        plugin = this;
        reloadConfig();
        Font.load();
        Power.powers.put("arrow", PowerArrow.class);
        Power.powers.put("tntcannon", PowerTNTCannon.class);
        Power.powers.put("rainbow", PowerRainbow.class);
        Power.powers.put("flame", PowerFlame.class);
        Power.powers.put("lightning", PowerLightning.class);
        Power.powers.put("ice", PowerIce.class);
        Power.powers.put("command", PowerCommand.class);
        Power.powers.put("potionhit", PowerPotionHit.class);
        Power.powers.put("teleport", PowerTeleport.class);
        Power.powers.put("fireball", PowerFireball.class);
        Power.powers.put("knockup", PowerKnockup.class);
        Power.powers.put("rush", PowerRush.class);
        Power.powers.put("potionself", PowerPotionSelf.class);
        Power.powers.put("consume", PowerConsume.class);
        Power.powers.put("unbreakable", PowerUnbreakable.class);
        Power.powers.put("unbreaking", PowerUnbreaking.class);
        Power.powers.put("rumble", PowerRumble.class);
        Locale.init(this);
    }

    @Override
    public void onEnable() {
        updateConfig();
        WorldGuard.init(this);
        ConfigurationSection conf = getConfig();
        if (conf.getBoolean("autoupdate", true)) {
            new Updater(this, "rpg-items", this.getFile(), Updater.UpdateType.DEFAULT, false);
        }
        getServer().getPluginManager().registerEvents(new Events(), this);
        ItemManager.load(this);

        try {
            Metrics metrics = new Metrics(this);
            metrics.addCustomData(new Metrics.Plotter("Total Items") {

                @Override
                public int getValue() {
                    return ItemManager.itemByName.size();
                }
            });
            Graph graph = metrics.createGraph("Power usage");
            for (String powerName : Power.powers.keySet()) {
                graph.addPlotter(new Metrics.Plotter(powerName) {

                    @Override
                    public int getValue() {
                        return Power.powerUsage.get(getColumnName());
                    }
                });
            }
            metrics.addGraph(graph);
            metrics.start();
        } catch (Exception e) {
        }

        // Commands.generateHelp();
    }

    @Override
    public void saveConfig() {
        FileConfiguration config = getConfig();
        FileOutputStream out = null;
        try {
            File f = new File(getDataFolder(), "config.yml");
            if (!f.exists())
                f.createNewFile();
            out = new FileOutputStream(f);
            out.write(config.saveToString().getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    FileConfiguration config;

    @Override
    public void reloadConfig() {
        FileInputStream in = null;
        config = new YamlConfiguration();
        try {
            File f = new File(getDataFolder(), "config.yml");
            in = new FileInputStream(f);
            byte[] data = new byte[(int) f.length()];
            in.read(data);
            String str = new String(data, "UTF-8");
            config.loadFromString(str);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (InvalidConfigurationException e) {
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    public void updateConfig() {
        ConfigUpdater.updateConfig(getConfig());
        saveConfig();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelAllTasks();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Player player = (Player) sender;
        if (sender instanceof Player ? !((Player) sender).hasPermission("rpgitem") : false) {
            sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_ERROR_PERMISSION"));
            return true;
        }
        StringBuilder out = new StringBuilder();
        out.append(label).append(' ');
        for (String arg : args)
            out.append(arg).append(' ');
        Commands.exec(sender, out.toString());
        return true;
    }

    public static String joinArgs(String[] args, int start, int length) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i == length - 1) {
                out.append(args[start + i]);
            } else {
                out.append(args[start + i]).append(' ');
            }
        }
        return out.toString();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        StringBuilder out = new StringBuilder();
        out.append(alias).append(' ');
        for (String arg : args)
            out.append(arg).append(' ');
        return Commands.complete(out.toString());
    }
}
