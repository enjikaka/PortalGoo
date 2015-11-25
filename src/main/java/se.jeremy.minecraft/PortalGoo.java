package se.jeremy.minecraft;

import java.io.File;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class PortalGoo extends JavaPlugin implements Listener {
    FileConfiguration config;

    // The value for when the player is not jumping
    double stilla = -0.0784000015258789;

    // The calue for when the player is jumping
    double hoppa = -0.7170746714356033;

    private double getNode(String m) {
        return config.getDouble(m);
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        config = getConfig();

        addNode("heightPoof", 0.5);
        addNode("sidePoof", 0.5);
        addNode("portalGooBlock", "LAPIS_BLOCK");

        config.options().copyDefaults(true);
        saveConfig();
    }

    private void addNode(String o, Object p) {
        File configFile = new File("plugins" + File.separator + "Core" + File.separator + "config.yml");
        config.addDefault(o, p);

        if (!configFile.exists()) {
            config.set(o, p);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location loc = player.getLocation();

        int x = (int) loc.getX();
        int y = (int) loc.getY();
        int z = (int) loc.getZ();

        Material b = world.getBlockAt(x, y - 1, z).getType();
        Material c = world.getBlockAt(x, y - 2, z).getType();

        double xb = 0;
        double zb = 0;

        double pp = getNode("sidePoof") * 1.0D;
        double pn = getNode("sidePoof") * -1.0D;

        Vector v = player.getVelocity();

        double vy = v.getY();

        String playerDirection = getDirection(player);

        switch (playerDirection) {
            case "N":
                xb = pp;
                break;
            case "S":
                xb = pn;
                break;
            case "E":
                zb = pp;
                break;
            case "W":
                zb = pn;
                break;
            case "NE":
                xb = pp;
                zb = pp;
                break;
            case "NW":
                xb = pp;
                zb = pn;
                break;
            case "SE":
                xb = pn;
                zb = pp;
                break;
            case "SW":
                xb = pn;
                zb = pn;
                break;
        }

        Material portalGooBlock = Material.getMaterial(config.getString("portalGooBlock"));

        if ((b.equals(portalGooBlock) && c.equals(portalGooBlock) && vy != stilla) || b.equals(portalGooBlock) && vy != stilla) {
            player.setFallDistance(0);
            player.setVelocity(new Vector(xb, getNode("heightPoof") * 1.0D, zb));
            world.playEffect(loc, Effect.SMOKE, 50);
            world.playEffect(loc, Effect.EXTINGUISH, 50);
        }
        if (((b.equals(portalGooBlock) || c.equals(portalGooBlock)) && player.isSneaking()) || ((b.equals(portalGooBlock) && c.equals(portalGooBlock)) && player.isSneaking())) {
            player.setVelocity(new Vector(0,-0.0784000015258789,0));
            player.setFallDistance(0);
        }

        // Wall jumping
        Material blockXN = world.getBlockAt(x - 1, y, z).getType();
        Material blockXP = world.getBlockAt(x + 1, y, z).getType();
        Material blockZN = world.getBlockAt(x, y, z - 1).getType();
        Material blockZP = world.getBlockAt(x, y, z + 1).getType();

        xb = xb * 1.5D;
        zb = zb * 1.5D;

        if (blockXN.equals(portalGooBlock) && vy > hoppa) {
            player.setVelocity(new Vector(getNode("sidePoof") * 2.0D,0.5D,zb));
        }

        if (blockXP.equals(portalGooBlock) && vy > hoppa) {
            player.setVelocity(new Vector(getNode("sidePoof") * -2.0D,0.5D,zb));
        }

        if (blockZN.equals(portalGooBlock) && vy > hoppa) {
            player.setVelocity(new Vector(xb,0.5D,getNode("sidePoof") * 2.0D));
        }

        if (blockZP.equals(portalGooBlock) && vy > hoppa) {
            player.setVelocity(new Vector(xb,0.5D,getNode("sidePoof") * -2.0D));
        }
    }

    private String getDirection(Player player) {
        float yaw = player.getLocation().getYaw();
        String returnValue = "N";

        if (((yaw >= 22.5D) && (yaw < 67.5D)) || ((yaw <= -292.5D) && (yaw > -337.5D))) {
            returnValue = "SE";
        }

        if (((yaw >= 67.5D) && (yaw < 112.5D)) || ((yaw <= -247.5D) && (yaw > -292.5D))) {
            returnValue = "S";
        }

        if (((yaw >= 112.5D) && (yaw < 157.5D)) || ((yaw <= -202.5D) && (yaw > -247.5D))) {
            returnValue = "SW";
        }

        if (((yaw >= 157.5D) && (yaw < 202.5D)) || ((yaw <= -157.5D) && (yaw > -202.5D))) {
            returnValue = "W";
        }

        if (((yaw >= 202.5D) && (yaw < 247.5D)) || ((yaw <= -112.5D) && (yaw > -157.5D))) {
            returnValue = "NW";
        }

        if (((yaw >= 247.5D) && (yaw < 292.5D)) || ((yaw <= -67.5D) && (yaw > -112.5D))) {
            returnValue = "N";
        }

        if (((yaw >= 292.5D) && (yaw < 337.5D)) || ((yaw <= -22.5D) && (yaw > -67.5D))) {
            returnValue = "NE";
        }

        if ((yaw >= 337.5D) || (yaw < 22.5D) || (yaw <= -337.5D) || (yaw > -22.5D)) {
            returnValue = "E";
        }

        return returnValue;
    }
}