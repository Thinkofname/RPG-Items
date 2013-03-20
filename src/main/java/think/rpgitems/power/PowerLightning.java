package think.rpgitems.power;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerLightning extends Power {

    private int chance = 20;
    private Random random = new Random();

    @Override
    public void hit(Player player, LivingEntity e) {
        if (random.nextInt(chance) == 0)
            e.getWorld().strikeLightning(e.getLocation());
    }

    @Override
    public void projectileHit(Player player, Projectile p) {
        if (random.nextInt(chance) == 0)
            p.getWorld().strikeLightning(p.getLocation());
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + String.format(Locale.get("POWER_LIGHTNING"), (int) ((1d / (double) chance) * 100d));
    }

    @Override
    public String getName() {
        return "lightning";
    }

    @Override
    public void init(ConfigurationSection s) {
        chance = s.getInt("chance");
    }

    @Override
    public void save(ConfigurationSection s) {
        s.set("chance", chance);
    }

    static {
        Commands.add("rpgitem $n[] power lightning", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_LIGHTNING");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerLightning pow = new PowerLightning();
                pow.item = item;
                pow.chance = 20;
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
        Commands.add("rpgitem $n[] power lightning $CHANCE:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_LIGHTNING_FULL");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerLightning pow = new PowerLightning();
                pow.item = item;
                pow.chance = (Integer) args[1];
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }
}
