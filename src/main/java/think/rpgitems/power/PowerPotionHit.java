/*
 *  This file is part of RPG Items.
 *
 *  RPG Items is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  RPG Items is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with RPG Items.  If not, see <http://www.gnu.org/licenses/>.
 */
package think.rpgitems.power;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerPotionHit extends Power {

    private int chance = 20;
    private Random random = new Random();
    private PotionEffectType type = PotionEffectType.HARM;
    private int duration = 20;
    private int amplifier = 1;

    @Override
    public void hit(Player player, LivingEntity e) {
        if (random.nextInt(chance) == 0)
            e.addPotionEffect(new PotionEffect(type, duration, amplifier));
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + String.format(Locale.get("POWER_POTIONHIT"), (int) ((1d / (double) chance) * 100d), type.getName().toLowerCase().replace('_', ' '));
    }

    @Override
    public String getName() {
        return "potionhit";
    }

    @Override
    public void init(ConfigurationSection s) {
        chance = s.getInt("chance", 20);
        duration = s.getInt("duration", 20);
        amplifier = s.getInt("amplifier", 1);
        type = PotionEffectType.getByName(s.getString("type", PotionEffectType.HARM.getName()));
    }

    @Override
    public void save(ConfigurationSection s) {
        s.set("chance", chance);
        s.set("duration", duration);
        s.set("amplifier", amplifier);
        s.set("type", type.getName());
    }

    static {
        Commands.add("rpgitem $n[] power potionhit $CHANCE:i[] $DURATION:i[] $AMPLIFIER:i[] $EFFECT:s[]", new Commands() {

            @Override
            public String getDocs() {
                StringBuilder out = new StringBuilder();
                for (PotionEffectType type : PotionEffectType.values()) {
                    if (type != null)
                        out.append(type.getName().toLowerCase()).append(", ");
                }
                return Locale.get("COMMAND_RPGITEM_POTIONHIT") + out.toString();
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerPotionHit pow = new PowerPotionHit();
                pow.item = item;
                pow.chance = (Integer) args[1];
                pow.duration = (Integer) args[2];
                pow.amplifier = (Integer) args[3];
                pow.type = PotionEffectType.getByName((String) args[4]);
                if (pow.type == null) {
                    sender.sendMessage(ChatColor.RED + String.format(Locale.get("MESSAGE_ERROR_EFFECT"), (String) args[4]));
                    return;
                }
                item.addPower(pow);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));

            }
        });
    }
}
