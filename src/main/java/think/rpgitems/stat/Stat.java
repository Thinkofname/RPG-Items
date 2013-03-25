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

import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.HashMap;

import org.bukkit.entity.Player;

import think.rpgitems.item.RPGItem;

public abstract class Stat {

    public static HashMap<String, Class<? extends Stat>> stats = new HashMap<String, Class<? extends Stat>>();
    public static TObjectIntHashMap<String> statUsage = new TObjectIntHashMap<String>();
    protected RPGItem item;

    public abstract String getDisplayText();

    public abstract void tick(Player player);

    public abstract String getName();
}
