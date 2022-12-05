package silverassist.mazecreate;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MazeCreate extends JavaPlugin {
    public static JavaPlugin plugin = null;
    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        PluginCommand command = getCommand("maze");
        command.setExecutor(new Command());
        command.setTabCompleter(new Tab());
        this.getServer().getPluginManager().registerEvents(new RangeSet(),this);



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
