package think.rpgitems.power;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import think.rpgitems.item.RPGItem;

public abstract class Power {

    public static HashMap<String, Class<? extends Power>> powers = new HashMap<String, Class<? extends Power>>();
    public static TObjectIntHashMap<String> powerUsage = new TObjectIntHashMap<String>();

    public RPGItem item;

    public Power() {

    }

    public abstract void init(ConfigurationSection s);

    public abstract void save(ConfigurationSection s);

    public abstract String getName();

    public void rightClick(Player player) {

    }

    public void leftClick(Player player) {

    }

    public void hit(Player player, LivingEntity e) {

    }

    public void projectileHit(Player player, Projectile arrow) {

    }

    public abstract String displayText();

    public static Entity[] getNearbyEntities(Location l, double radius) {
        int iRadius = (int) radius;
        int chunkRadius = iRadius < 16 ? 1 : (iRadius - (iRadius % 16)) / 16;
        HashSet<Entity> radiusEntities = new HashSet<Entity>();
        for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
            for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
                int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
                for (Entity e : new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
                    if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
                        radiusEntities.add(e);
                }
            }
        }
        return radiusEntities.toArray(new Entity[radiusEntities.size()]);
    }
}
