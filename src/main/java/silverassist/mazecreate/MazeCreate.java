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
        data = new CustomConfig(this,"data.yml");
        time = new CustomConfig(this,"time.yml");
        plugin.saveDefaultConfig();
        data.saveDefaultConfig();
        time.saveDefaultConfig();
        //コマンド登録
        PluginCommand command = getCommand("maze");
        if(command!=null){
            command.setExecutor(new Command());
            command.setTabCompleter(new Tab());
        }
        //イベント登録
        this.getServer().getPluginManager().registerEvents(new RangeSet(),this);  //範囲指定用イベント
        this.getServer().getPluginManager().registerEvents(new TimerSystem(this), this);  //スタート、ゴール検知用



    }

    public static JavaPlugin getInstance(){return plugin;}
    public static CustomConfig getDataYml(){return data;}
    public static CustomConfig getTimeYml(){return time;}
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
