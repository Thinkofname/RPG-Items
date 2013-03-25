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
package think.rpgitems.stat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class StatPotion extends Stat {

    private int amplifier;
    private PotionEffectType type;

    @Override
    public String getDisplayText() {
        return ChatColor.BLUE + String.format("+ %s %d while equiped", type.getName(), amplifier);
    }

    @Override
    public void tick(Player player) {
        player.addPotionEffect(new PotionEffect(type, 3, amplifier, true), true);
    }

    static {
        Commands.add("rpgitem $n[] stat potion $AMPLIFIER:i[] $EFFECT:s[]", new Commands() {

            @Override
            public String getDocs() {
                return "(WIP)";
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                StatPotion stat = new StatPotion();
                stat.item = item;
                stat.amplifier = (Integer) args[1];
                stat.type = PotionEffectType.getByName((String) args[2]);
                if (stat.type == null) {
                    sender.sendMessage(ChatColor.RED + String.format(Locale.get("MESSAGE_ERROR_EFFECT"), (String) args[2]));
                    return;
                }
                item.addStat(stat);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + "Stat added");
            }
        });
    }

    @Override
    public String getName() {
        return "potion";
    }
}
