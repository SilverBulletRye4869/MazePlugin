package silverassist.mazecreate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static silverassist.mazecreate.Function.sendPrefixMessage;

public class TimerSystem implements Listener {
    static Map<UUID, List<String>> timeMemo = new HashMap<>();
    private Map<Player, Location> coolDown = new HashMap<>();
    @EventHandler
    public void playerMoveEvent(PlayerInteractEvent e){
        if(e.getAction() != Action.PHYSICAL)return;
        Player p = e.getPlayer();
        Location loc = e.getClickedBlock().getLocation().add(0,-1,0);
        if(coolDown.containsKey(p) && coolDown.get(p).equals(loc)){
            e.setCancelled(true);
            return;
        }
        Material block = loc.getBlock().getType();
        if(block!= Material.DIAMOND_BLOCK && block!=Material.GOLD_BLOCK)return;
        FileConfiguration data = MazeCreate.getDataYml().getConfig();
        AtomicReference<String> AtoID = new AtomicReference<>("");
        data.getKeys(false).forEach( id -> {
            if (!p.getWorld().getUID().equals(UUID.fromString(data.getString(id + ".world"))))return;
            List<Float> ls = data.getFloatList(id + ".start");
            List<Float> le = data.getFloatList(id + ".end");
            if(ls.get(0) > loc.getX() || le.get(0) < loc.getX())return;
            if(ls.get(1) > loc.getY() + 1 || le.get(1) < loc.getY()+1)return;
            if(ls.get(2) > loc.getZ() || le.get(2) < loc.getZ())return;
            AtoID.set(id);
        });
        if(AtoID.get().equals(""))return;
        e.setCancelled(true);
        UUID u = p.getUniqueId();
        coolDown.put(p,loc);
        Bukkit.getScheduler().runTaskLater(MazeCreate.getInstance(), new Runnable(){
            @Override
            public void run() {
                coolDown.remove(p);
            }
        },20*5);

        String id = AtoID.get();
        if(block == Material.DIAMOND_BLOCK) {
            timeMemo.remove(u);
            timeMemo.put(u,List.of(id,String.valueOf(System.currentTimeMillis())));
            sendPrefixMessage(p,"§a§lid:§d§l"+id+"§a§lの迷路を開始ました");
        }else{
            if(!timeMemo.containsKey(u))return;
            List<String> timeMemo_c = timeMemo.get(u);
            timeMemo.remove(u);
            if(!timeMemo_c.get(0).equals(id))return;
            long timeE = System.currentTimeMillis();
            long timeS = Long.valueOf(timeMemo_c.get(1));
            sendPrefixMessage(p,"§e§lid:§d§l"+id+"§e§lの迷路をクリアました！");
            long clearTime= timeE-timeS;
            sendPrefixMessage(p,"§6§lクリア時間: §n"+(clearTime / 60000) +"分"+(clearTime % 60000 /1000.0)+"秒");

            Location home = data.getLocation(id+".home");
            p.teleport( home == null ? p.getWorld().getSpawnLocation() : home);

            FileConfiguration time = MazeCreate.getTimeYml().getConfig();
            long min = time.getLong(id+"."+u) == 0 ?  9223372036854775807l : time.getLong(id+"."+u);
            time.set(id+"."+u, Math.min(clearTime, min));
            MazeCreate.getTimeYml().saveConfig();
        }

    }
}
