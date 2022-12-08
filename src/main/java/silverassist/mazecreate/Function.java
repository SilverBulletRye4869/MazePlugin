package silverassist.mazecreate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class Function {
    public static final String PREFIX = "§b§l[§e§lMazeCreate§b§l]";
    public static final String MAZEWAND = PREFIX +" §c§l領域指定斧";

    //Prefix付きのメッセージに変更
    public static void sendPrefixMessage(Player p, String text){
        p.sendMessage(PREFIX+text);
    }

    public static void broadCast(String s){
        MazeCreate.getInstance().getServer().broadcastMessage(PREFIX+s);
    }

    public static void setBlock(World w, float[] base, List<Integer> loc, int height, Material m){
        int x = loc.get(0);
        int z = loc.get(1);
        for(int k = 0;k<=height;k++){
            new Location(w,base[0]+x,base[1]+k,base[2]+z).getBlock().setType(m);
        }
    }

}
