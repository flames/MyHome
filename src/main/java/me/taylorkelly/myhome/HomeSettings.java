package me.taylorkelly.myhome;

import java.io.File;


public class HomeSettings {
    
    private static final String settingsFile = "MyHome.settings";
    public static File dataDir;
    public static boolean compassPointer;
    public static boolean respawnToHome;
    public static boolean allowSetHome;
    public static boolean homesArePublic;
    public static int bedsCanSethome;
    public static boolean oneHomeAllWorlds;
    public static boolean loadChunks;
        
    public static boolean usemySQL;
    public static String mySQLuname;
    public static String mySQLpass;
    public static String mySQLconn;
    
    public static void initialize(File dataFolder) {
        if(!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile  = new File(dataFolder, settingsFile);
        PropertiesFile file = new PropertiesFile(configFile);
        dataDir = dataFolder;
        
        compassPointer = file.getBoolean("compassPointer", true, "Whether or not users' compasses point to home");
        respawnToHome = file.getBoolean("respawnToHome", true, "Whether or not players will respawn to their homes (false means to global spawn)");
        allowSetHome = file.getBoolean("allowSetHome", false, "Whether MyHome should also watch for /sethome - This may cause conflicts with Essentials");
        homesArePublic = file.getBoolean("homesArePublic", false, "Should home warps be made public by default");
        bedsCanSethome = file.getInt("bedsCanSethome", 0, "0 = Disabled, 1 = Using a bed will /sethome automatically, 2 = /sethome is disabled and can only be set by using a bed ");
        oneHomeAllWorlds = file.getBoolean("oneHomeAllWorlds", true, "Only allow one home for all worlds on the server - False = one home per world");
        loadChunks = file.getBoolean("loadChunks", false, "Force sending of the chunk which people teleport to - default: false");
        
        // MySQL
        usemySQL = file.getBoolean("usemySQL", false, "MySQL usage --  true = use MySQL database / false = use SQLite");
		mySQLconn = file.getString("mySQLconn", "jdbc:mysql://localhost:3306/minecraft", "MySQL Connection (only if using MySQL)");
		mySQLuname = file.getString("mySQLuname", "root", "MySQL Username (only if using MySQL)");
		mySQLpass = file.getString("mySQLpass", "password", "MySQL Password (only if using MySQL)");
		
        file.save();
    }
}
