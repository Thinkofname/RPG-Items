package think.rpgitems.power;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import think.rpgitems.data.RPGValue;

@Deprecated
public class PowerRush extends Power {

    long cd = 20;
    int speed = 3;
    int time = 20;

    @Override
    public void rightClick(Player player) {
        long cooldown;
        RPGValue value = RPGValue.get(player, item, "rush.cooldown");
        if (value == null) {
            cooldown = System.currentTimeMillis() / 50;
            value = new RPGValue(player, item, "rush.cooldown", cooldown);
        } else {
            cooldown = value.asLong();
        }
        if (cooldown <= System.currentTimeMillis() / 50) {
            value.set(System.currentTimeMillis() / 50 + cd);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, time, speed));
        }
    }

    @Override
    public void init(ConfigurationSection s) {
        cd = s.getLong("cooldown");
        speed = s.getInt("speed");
        time = s.getInt("time");
    }

    @Override
    public void save(ConfigurationSection s) {
        s.set("cooldown", cd);
        s.set("speed", speed);
        s.set("time", time);
    }

    @Override
    public String getName() {
        return "rush";
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + "Gives temporary speed boost";
    }

}
