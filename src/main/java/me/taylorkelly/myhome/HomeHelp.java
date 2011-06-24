/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.taylorkelly.myhome;

import me.taylorkelly.help.Help;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author taylor
 */
class HomeHelp {

    public static void initialize(Plugin plugin) {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Help");
        if (test != null) {
            Help helpPlugin = ((Help) test);
            helpPlugin.registerCommand("home", "Go home young chap!", plugin, true, "myhome.home.basic.home");
            helpPlugin.registerCommand("home set", "Set your home", plugin, true, "myhome.home.basic.set");
            helpPlugin.registerCommand("home [player]", "Go to [player]'s home", plugin, "myhome.home.soc.others");
            helpPlugin.registerCommand("home invite [player]", "Invite [player] to your home", plugin, "myhome.home.soc.invite");
            helpPlugin.registerCommand("home uninvite [player]", "Uninvite [player] to your home", plugin, "myhome.home.soc.uninvite");
            helpPlugin.registerCommand("home list", "List the homes you're invited to", plugin, "myhome.home.soc.list");
            helpPlugin.registerCommand("home ilist", "List the people invited to your home", plugin, "myhome.home.soc.list");
            helpPlugin.registerCommand("home public", "Makes your home public", plugin, "myhome.home.soc.public");
            helpPlugin.registerCommand("home private", "Makes your home private", plugin, "myhome.home.soc.private");
            HomeLogger.info("Help plugin support enabled.");
        } else {
        	HomeLogger.warning("Help plugin not detected. Only providing help via /home help.");
        }
    }
}
