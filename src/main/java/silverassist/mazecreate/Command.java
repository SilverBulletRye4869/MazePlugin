package silverassist.mazecreate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

import static silverassist.mazecreate.Function.*;

public class Command implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(!p.isOp())return true;
        if(args.length<1){
            //help
            return true;
        }

        ItemStack item;
        ItemMeta meta;
        switch (args[0]){
            case "wand":
                item = new ItemStack(Material.DIAMOND_AXE);
                meta = item.getItemMeta();
                meta.setDisplayName(MAZEWAND);
                meta.setLore(List.of("§f§l開始位置: §6§l左クリックで指定","§f§l終了位置: §6§l右クリックで指定",p.getUniqueId().toString()));
                item.setItemMeta(meta);
                p.getInventory().addItem(item);
                break;
            case "create":
                if(args.length<2)return true;
                Material mate;
                try {
                    mate = Material.valueOf(args[1]);
                }catch (IllegalArgumentException e){
                    sendPrefixMessage(p,"§cアイテムが適切ではありません");
                    return true;
                }
                if(mate==null)return true;
                item = p.getInventory().getItemInMainHand();
                if (item == null) return true;
                if (item.getType() != Material.DIAMOND_AXE) return true;  //ダイヤの斧か
                meta = item.getItemMeta();
                if (meta == null) return true;
                if (!meta.getDisplayName().equals(MAZEWAND)) return true;  //名前が等しいか
                //=================================================================斧が正常なものか判定
                List<String> lore = meta.getLore();
                if (lore == null) return true;
                if (lore.size() != 3 || !lore.get(2).equals(p.getUniqueId().toString())) {
                    sendPrefixMessage(p, "§c§l斧が正常ではありません。再度斧を取得してください");
                    return true;
                }
                //指定されているか
                lore = meta.getLore();
                if(lore.get(1).contains("§6§l左クリックで指定")){
                    sendPrefixMessage(p,"§d§l開始位置が指定されていません");
                    return true;
                }
                if(lore.get(2).contains("§6§l右クリックで指定")){
                    sendPrefixMessage(p,"§d§l終了位置が指定されていません");
                    return true;
                }
                //各座標の最少位置、最大位置を登録
                String[] locS = lore.get(0).replace("§f§l開始位置: ","").split(",");  //最少位置
                String[] locE = lore.get(1).replace("§f§l終了位置: ","").split(",");  //最大位置
                float[][] locRegister = new float[2][3];
                for(int i = 0;i<3;i++){
                    float s = Float.parseFloat(locS[i+1]);
                    float e = Float.parseFloat(locE[i+1]);
                    if(s<e){
                        locRegister[0][i] =s;
                        locRegister[1][i] =e;
                    }else{
                        locRegister[0][i] = e;
                        locRegister[1][i] = s;
                    }
                }
                if((locRegister[1][0] - locRegister[0][0])%2==1){
                    locRegister[1][0]--;
                    sendPrefixMessage(p,"§cdxが奇数出なかったので調整を行いました");
                }
                if((locRegister[1][2] - locRegister[0][2])%2==1){
                    locRegister[1][2]--;
                    sendPrefixMessage(p,"§cdzが奇数出なかったので調整を行いました");
                }
                MainSystem.createMaze(locRegister, Bukkit.getWorld(locS[0]), mate);
                sendPrefixMessage(p,"§a§l迷路を生成しました");

        }
        return true;
    }
}
