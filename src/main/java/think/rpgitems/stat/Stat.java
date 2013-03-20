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
