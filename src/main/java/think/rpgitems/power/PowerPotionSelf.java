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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.data.RPGValue;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerPotionSelf extends Power {

    private long cd = 20;
    private int amp = 3;
    private int time = 20;
    private PotionEffectType type = PotionEffectType.HEAL;

    @Override
    public void rightClick(Player player) {
        long cooldown;
        RPGValue value = RPGValue.get(player, item, "potionself.cooldown");
        if (value == null) {
            cooldown = System.currentTimeMillis() / 50;
            value = new RPGValue(player, item, "potionself.cooldown", cooldown);
        } else {
            cooldown = value.asLong();
        }
        if (cooldown <= System.currentTimeMillis() / 50) {
            value.set(System.currentTimeMillis() / 50 + cd);
            player.addPotionEffect(new PotionEffect(type, time, amp));
        } else {
            player.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_COOLDOWN"), ((double) (cooldown - System.currentTimeMillis() / 50)) / 20d));
        }
    }

    @Override
    public void init(ConfigurationSection s) {
        cd = s.getLong("cooldown");
        amp = s.getInt("amp");
        time = s.getInt("time");
        type = PotionEffectType.getByName(s.getString("type", "heal"));
    }

    @Override
    public void save(ConfigurationSection s) {
        s.set("cooldown", cd);
        s.set("amp", amp);
        s.set("time", time);
        s.set("type", type.getName());
    }

    @Override
    public String getName() {
        return "potionself";
    }

    @Override
    public String displayText() {
        return ChatColor.GREEN + String.format(Locale.get("POWER_POTIONSELF"), type.getName().toLowerCase().replaceAll("_", " "), amp + 1, ((double) time) / 20d);
    }

    static {
        Commands.add("rpgitem $n[] power potionself $COOLDOWN:i[] $DURATION:i[] $AMPLIFIER:i[] $EFFECT:s[]", new Commands() {

            @Override
            public String getDocs() {
                StringBuilder out = new StringBuilder();
                for (PotionEffectType type : PotionEffectType.values()) {
                    if (type != null)
                        out.append(type.getName().toLowerCase()).append(", ");
                }
                return Locale.get("COMMAND_RPGITEM_POTIONSELF") + out.toString();
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerPotionSelf pow = new PowerPotionSelf();
                pow.item = item;
                pow.cd = (Integer) args[1];
                pow.time = (Integer) args[2];
                pow.amp = (Integer) args[3];
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
