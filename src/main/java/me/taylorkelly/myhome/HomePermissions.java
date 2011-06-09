package me.taylorkelly.myhome;

import ru.tehkode.permissions.bukkit.*;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.anjocaido.groupmanager.GroupManager;

import me.taylorkelly.myhome.HomeLogger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HomePermissions {

    private enum PermissionsHandler {
        PERMISSIONSEX, PERMISSIONS3, PERMISSIONS, GROUPMANAGER, NONE
    }
    private static PermissionsHandler handler;
    private static Plugin permissionPlugin;

    public static void initialize(Server server) {
    	Plugin permissionsEx = server.getPluginManager().getPlugin("PermissionsEx");
    	Plugin groupManager = server.getPluginManager().getPlugin("GroupManager");
        Plugin permissions = server.getPluginManager().getPlugin("Permissions");

        if (permissionsEx != null) {
            permissionPlugin = permissionsEx;
            handler = PermissionsHandler.PERMISSIONSEX;
            String version = permissionsEx.getDescription().getVersion();
            HomeLogger.info("Permissions enabled using: PermissionsEx v" + version);
        } else if (groupManager != null) {
            permissionPlugin = groupManager;
            handler = PermissionsHandler.GROUPMANAGER;
            String version = groupManager.getDescription().getVersion();
            HomeLogger.info("Permissions enabled using: GroupManager v" + version);
        } else if (permissions != null) {
            permissionPlugin = permissions;
            String version = permissions.getDescription().getVersion();
            if(version.contains("3.")) {
            	// This shouldn't make any difference according to the Permissions API
            	handler = PermissionsHandler.PERMISSIONS3;
            } else {
            	handler = PermissionsHandler.PERMISSIONS;
            }
            HomeLogger.info("Permissions enabled using: Permissions v" + version);
        } else {
            handler = PermissionsHandler.NONE;
            HomeLogger.warning("A permission plugin isn't loaded.");
        }
    }

    public static boolean permission(Player player, String permission, boolean defaultPerm) {
        switch (handler) {
            case PERMISSIONSEX:
    		    return ((PermissionsEx) permissionPlugin).getPermissionManager().has(player, permission);
            case PERMISSIONS3:
                return ((Permissions) permissionPlugin).getHandler().has(player, permission);
            case PERMISSIONS:
                return ((Permissions) permissionPlugin).getHandler().has(player, permission);
            case GROUPMANAGER:
                return ((GroupManager) permissionPlugin).getWorldsHolder().getWorldPermissions(player).has(player, permission);
            case NONE:
                return defaultPerm;
            default:
                return defaultPerm;
        }
    }

    public static boolean isAdmin(Player player) {
        return permission(player, "myhome.admin", player.isOp());
    }

    public static boolean home(Player player) {
        return permission(player, "myhome.home.basic.home", true);
    }

    public static boolean set(Player player) {
        return permission(player, "myhome.home.basic.set", true);
    }

    public static boolean delete(Player player) {
        return permission(player, "myhome.home.basic.delete", true);
    }

    public static boolean list(Player player) {
        return permission(player, "myhome.home.soc.list", true);
    }

    public static boolean homeOthers(Player player) {
        return permission(player, "myhome.home.soc.others", true);
    }

    public static boolean invite(Player player) {
        return permission(player, "myhome.home.soc.invite", true);
    }

    public static boolean uninvite(Player player) {
        return permission(player, "myhome.home.soc.uninvite", true);
    }

    public static boolean canPublic(Player player) {
        return permission(player, "myhome.home.soc.public", true);
    }

    public static boolean canPrivate(Player player) {
        return permission(player, "myhome.home.soc.private", true);
    }
    public static boolean homeFree(Player player) {
        return permission(player, "myhome.home.free", true);
    }
    public static boolean bedBypass(Player player) {
        return permission(player, "myhome.home.bypass.bedsethome", true);
    }
}
