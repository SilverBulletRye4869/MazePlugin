package silverassist.mazecreate;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MazeCreate extends JavaPlugin {
    private static JavaPlugin plugin = null;
    private static CustomConfig data = null;
    private static CustomConfig time = null;
    @Override
    public void onEnable() {
        plugin = this;
        plugin.saveDefaultConfig();
        data = new CustomConfig(this,"data.yml");
        time = new CustomConfig(this,"time.yml");
        data.saveDefaultConfig();
        time.saveDefaultConfig();
        // Plugin startup logic
        PluginCommand command = getCommand("maze");
        command.setExecutor(new Command());
        command.setTabCompleter(new Tab());
        this.getServer().getPluginManager().registerEvents(new RangeSet(),this);
        this.getServer().getPluginManager().registerEvents(new TimerSystem(), this);



    }

    public static JavaPlugin getInstance(){return plugin;}
    public static CustomConfig getDataYml(){return data;}
    public static CustomConfig getTimeYml(){return time;}
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
