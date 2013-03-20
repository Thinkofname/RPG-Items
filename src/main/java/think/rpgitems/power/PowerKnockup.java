package think.rpgitems.power;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerKnockup extends Power {

    int chance = 20;
    double power = 2;

    Random rand = new Random();

    @Override
    public void hit(Player player, LivingEntity e) {
        if (rand.nextInt(chance) == 0)
            e.setVelocity(player.getLocation().getDirection().setY(power));
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + String.format(Locale.get("POWER_KNOCKUP"), (int) ((1d / (double) chance) * 100d));
    }

    @Override
    public String getName() {
        return "knockup";
    }

    @Override
    public void init(ConfigurationSection s) {
        chance = s.getInt("chance");
        power = s.getDouble("power", 2);
    }

    @Override
    public void save(ConfigurationSection s) {
        s.set("chance", chance);
        s.set("power", power);
    }

    static {
        Commands.add("rpgitem $n[] power knockup", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_KNOCKUP");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerKnockup pow = new PowerKnockup();
                pow.item = item;
                pow.chance = 20;
                pow.power = 2;
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
        Commands.add("rpgitem $n[] power knockup $CHANCE:i[] $POWER:f[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_KNOCKUP_FULL");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerKnockup pow = new PowerKnockup();
                pow.item = item;
                pow.chance = (Integer) args[1];
                pow.power = (Double) args[2];
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }

}
