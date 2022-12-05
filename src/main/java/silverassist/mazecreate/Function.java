package silverassist.mazecreate;

import org.bukkit.entity.Player;

public class Function {
    public static final String PREFIX = "§b§l[§e§lMazeCreate§b§l]";
    public static final String MAZEWAND = PREFIX +" §c§l領域指定斧";

    //Prefix付きのメッセージに変更
    public static void sendPrefixMessage(Player p, String text){
        p.sendMessage(PREFIX+text);
    }

    public static void broadCast(String s){
        MazeCreate.plugin.getServer().broadcastMessage(PREFIX+s);
    }

}
