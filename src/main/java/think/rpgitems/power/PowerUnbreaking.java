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

public class PowerUnbreaking extends Power {

    private int level = 1;
    private Random random = new Random();

    @SuppressWarnings("deprecation")
    @Override
    public void hit(Player player, LivingEntity e) {
        if (random.nextDouble() < ((double) level) / 100d) {
            System.out.println(player.getItemInHand().getDurability());
            player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() - 1));
            System.out.println(player.getItemInHand().getDurability());
            player.updateInventory();
        }
    }

    @Override
    public void init(ConfigurationSection s) {
        level = s.getInt("level", 1);
    }

    @Override
    public void save(ConfigurationSection s) {
        s.set("level", level);
    }

    @Override
    public String getName() {
        return "unbreaking";
    }

    @Override
    public String displayText() {
        return String.format(ChatColor.GREEN + Locale.get("POWER_UNBREAKING"), level);
    }

    static {
        Commands.add("rpgitem $n[] power unbreaking", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_UNBREAKING");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerUnbreaking pow = new PowerUnbreaking();
                pow.item = item;
                pow.level = 1;
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
        Commands.add("rpgitem $n[] power unbreaking $LEVEL:i[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_UNBREAKING_FULL");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerUnbreaking pow = new PowerUnbreaking();
                pow.item = item;
                pow.level = (Integer) args[1];
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }

}
