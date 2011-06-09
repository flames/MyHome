package me.taylorkelly.myhome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import me.taylorkelly.myhome.griefcraft.Updater;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

public class MyHome extends JavaPlugin {

    private MHPlayerListener playerListener;
    private HomeList homeList;
    private boolean warning = false;
    public String name;
    public String version;
    private Updater updater;
    
    
    @Override
    public void onDisable() {
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();

    	ConnectionManager.closeConnection();
        HomeLogger.info(name + " " + version + " disabled");
    }

    @Override
    public void onEnable() {
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();

        HomeSettings.initialize(getDataFolder());

        libCheck();
        if(!sqlCheck()) { return; }
        
        homeList = new HomeList(getServer());
        playerListener = new MHPlayerListener(homeList, getServer(), this);
        
        HomePermissions.initialize(getServer());
        HomeHelp.initialize(this);
        
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Monitor, this);
        if(HomeSettings.bedsCanSethome != 0) {
        	// We don't need this if the beds dont autosethome
        	getServer().getPluginManager().registerEvent(Event.Type.PLAYER_BED_LEAVE, playerListener, Priority.Monitor, this);
        }
        if(HomeSettings.loadChunks) {
         	// We dont need to register for teleporting if we dont want to load chunks.
         	getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
        }
       
        HomeLogger.info(name + " " + version + " enabled");
    }


    private void libCheck(){
        updater = new Updater();
        try {
            updater.check();
            updater.update();
        } catch (Exception e) {
        }
    }
    
    private boolean sqlCheck() {
        Connection conn = ConnectionManager.initialize();
        if (conn == null) {
            HomeLogger.severe("Could not establish SQL connection. Disabling MyHome");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        } 
        return true;
    }
    
    private void updateFiles(File oldDatabase, File newDatabase) {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        if (newDatabase.exists()) {
            newDatabase.delete();
        }
        try {
            newDatabase.createNewFile();
        } catch (IOException ex) {
            HomeLogger.severe("Could not create new database file", ex);
        }
        copyFile(oldDatabase, newDatabase);
    }

    /**
     * File copier from xZise
     * @param fromFile
     * @param toFile
     */
    private static void copyFile(File fromFile, File toFile) {
        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytesRead);
            }
        } catch (IOException ex) {
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                }
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (commandName.equals("sethome") && HomePermissions.set(player)) {
            	if(HomeSettings.bedsCanSethome == 2 && !HomePermissions.bedBypass(player) ) { 
            		player.sendMessage(ChatColor.RED + "You can only set a home by sleeping in a bed");
            		return true;
            	}
            	// Check for /sethome if enabled in the configuration
           		if (HomeSettings.allowSetHome) { 
           		    homeList.addHome(player, this);
           		} else {
           			player.sendMessage("Use: " + ChatColor.RED + "/home set" + ChatColor.WHITE + " to set a home");
           		}
           		return true;
            } else if (commandName.equals("home") || commandName.equals("myhome") || commandName.equals("mh")) {
                /**
                 * /home
                 */
                if (split.length == 0 && HomePermissions.home(player)) {
                    if (homeList.playerHasHome(player)) {
                        homeList.sendPlayerHome(player, this);
                    } else {
                        player.sendMessage(ChatColor.RED + "You have no home :(");
                        if(HomeSettings.bedsCanSethome == 2) { 
                    		player.sendMessage("You need to sleep in a bed to set a home");
                    	} else {       
                    		player.sendMessage("Use: " + ChatColor.RED + "/home set" + ChatColor.WHITE + " to set a home");
                    	}
                    }
                    /**
                     *  /home reload
                     */
                } else if(split.length == 1 && split[0].equalsIgnoreCase("reload") && HomePermissions.isAdmin(player)) {
                	HomeSettings.initialize(getDataFolder());
                	player.sendMessage("[MyHome] Reloading config");
                    /**
                     * /home set
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("set") && HomePermissions.set(player)) {
                	if(HomeSettings.bedsCanSethome == 2 && !HomePermissions.bedBypass(player)) { 
                		player.sendMessage("You can only set a home by sleeping in a bed");
                		return true;
                	} else {
                		homeList.addHome(player, this);
                	}
                    /**
                     * /home delete
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("delete") && HomePermissions.delete(player)) {
                    homeList.deleteHome(player);
                    /**
                     * /home list
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("list") && HomePermissions.list(player)) {
                    homeList.list(player);
                    /**
                     * /home ilist
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("ilist") && HomePermissions.list(player)) {
                    homeList.ilist(player);
                    /**
                     * /home private
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("private") && HomePermissions.canPrivate(player)) {
                    homeList.privatize(player);
                    /**
                     * /home public
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("public") && HomePermissions.canPublic(player)) {
                    homeList.publicize(player);
                    /**
                     * /home point
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("point") && HomeSettings.compassPointer) {
                    homeList.orientPlayer(player);
                    /**
                     * /home invite <player>
                     */
                } else if (split.length == 2 && split[0].equalsIgnoreCase("invite") && HomePermissions.invite(player)) {
                    Player invitee = getServer().getPlayer(split[1]);
                    String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    homeList.invite(player, inviteeName);
                    /**
                     * /home uninvite <player>
                     */
                } else if (split.length == 2 && split[0].equalsIgnoreCase("uninvite") && HomePermissions.uninvite(player)) {
                    Player invitee = getServer().getPlayer(split[1]);
                    String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    homeList.uninvite(player, inviteeName);
                    /**
                     * /home <name>
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("help")) {
                    ArrayList<String> messages = new ArrayList<String>();
                    messages.add(ChatColor.RED + "-------------------- " + ChatColor.WHITE + "/HOME HELP" + ChatColor.RED + " --------------------");
                    if (HomePermissions.home(player)) {
                        messages.add(ChatColor.RED + "/home" + ChatColor.WHITE + "  -  Go home young chap!");
                    }
                    if (HomePermissions.set(player)) {
                        messages.add(ChatColor.RED + "/home set" + ChatColor.WHITE + "  -  Sets your home to your current position");
                    }
                    if (HomePermissions.delete(player)) {
                        messages.add(ChatColor.RED + "/home delete" + ChatColor.WHITE + "  -  Deletes your current home");
                    }
                    if (HomePermissions.homeOthers(player)) {
                        messages.add(ChatColor.RED + "/home [player]" + ChatColor.WHITE + "  -  Go to " + ChatColor.GRAY + "[player]" + ChatColor.WHITE
                                + "'s house (if allowed)");
                    }
                    if (HomePermissions.list(player)) {
                        messages.add(ChatColor.RED + "/home list" + ChatColor.WHITE + "  -  List the homes that you are invited to");
                        messages.add(ChatColor.RED + "/home ilist" + ChatColor.WHITE + "  -  List the people invited to your home");
                    }
                    if (HomePermissions.invite(player)) {
                        messages.add(ChatColor.RED + "/home invite [player]" + ChatColor.WHITE + "  -  Invite " + ChatColor.GRAY + "[player]" + ChatColor.WHITE
                                + " to your house");
                    }
                    if (HomePermissions.uninvite(player)) {
                        messages.add(ChatColor.RED + "/home uninvite [player]" + ChatColor.WHITE + "  -  Uninvite " + ChatColor.GRAY + "[player]"
                                + ChatColor.WHITE + " to your house");
                    }
                    if (HomePermissions.canPublic(player)) {
                        messages.add(ChatColor.RED + "/home public" + ChatColor.WHITE + "  -  Makes your house public");
                    }
                    if (HomePermissions.canPrivate(player)) {
                        messages.add(ChatColor.RED + "/home private" + ChatColor.WHITE + "  -  Makes your house private");
                    }
                    if (HomeSettings.compassPointer) {
                        messages.add(ChatColor.RED + "/home point" + ChatColor.WHITE + "  -  Points your compass home");
                    }
                    for (String message : messages) {
                        player.sendMessage(message);
                    }
                } else if (split.length == 1 && HomePermissions.homeOthers(player)) {
                    // TODO ChunkLoading
                    
                    String playerName = split[0];
                    homeList.warpTo(playerName, player, this);
                } else {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static void setCompass(Player player, Location location) {
        if (HomeSettings.compassPointer) {
            player.setCompassTarget(location);
        }
    }
}
