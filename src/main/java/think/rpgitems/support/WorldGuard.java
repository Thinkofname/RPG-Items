package think.rpgitems.support;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class WorldGuard {

    private static WorldGuardPlugin plugin;
    private static boolean hasSupport = false;
    public static boolean useWorldGuard = true;

    public static void init(think.rpgitems.Plugin plugin2) {
        Plugin plugin = plugin2.getServer().getPluginManager().getPlugin("WorldGuard");
        useWorldGuard = plugin2.getConfig().getBoolean("support.worldguard", false);
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return;
        }
        hasSupport = true;
        WorldGuard.plugin = (WorldGuardPlugin) plugin;
        think.rpgitems.Plugin.logger.info("[RPG Items] World Guard found");
    }

    public static boolean isEnabled() {
        return hasSupport;
    }

    public static boolean canBuild(Player player, Location location) {
        if (!hasSupport || !useWorldGuard)
            return true;
        return plugin.canBuild(player, location);
    }

    public static boolean canPvP(Location location) {
        if (!hasSupport || !useWorldGuard)
            return true;
        return plugin.getRegionManager(location.getWorld()).getApplicableRegions(location).allows(DefaultFlag.PVP);
    }
}
