package silverassist.mazecreate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import silverassist.mazecreate.CreateSystem.ExtendWall;
import silverassist.mazecreate.CreateSystem.StickKnockDown;

import java.util.*;

import static silverassist.mazecreate.Function.*;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        try {

            ItemStack item;
            ItemMeta meta;
            FileConfiguration data = MazeCreate.getDataYml().getConfig();
            World world;

            switch (args[0]) {
                //===============================================================================領域指定斧取得
                case "wand":
                    if (!p.isOp()) return true;
                    item = new ItemStack(Material.DIAMOND_AXE);
                    meta = item.getItemMeta();
                    meta.setDisplayName(MAZEWAND);
                    meta.setLore(List.of("§f§l開始位置: §6§l左クリックで指定", "§f§l終了位置: §6§l右クリックで指定", p.getUniqueId().toString()));
                    item.setItemMeta(meta);
                    p.getInventory().addItem(item);
                    return true;
                //================================================================================迷路生成
                case "define":
                case "create":
                    if (!p.isOp()) return true;
                    if (data.get(args[1]) != null) {
                        sendPrefixMessage(p, "§c§lそのidの迷路は存在します");
                        return true;
                    }
                    boolean isCreate = args[0].equals("create");

                    //壁にするブロックを見る (定義時を除く)
                    Material mate = null;
                    if (isCreate) {
                        try {
                            mate = Material.valueOf(args[3]);
                        } catch (IllegalArgumentException e) {
                            sendPrefixMessage(p, "§cアイテムが適切ではありません");
                            return true;
                        }
                        if (!mate.isBlock()) {
                            sendPrefixMessage(p, "§cそれはブロックではありません");
                            return true;
                        }
                    }

                    //斧判定
                    item = p.getInventory().getItemInMainHand();
                    if (item.getType() != Material.DIAMOND_AXE) return true;  //ダイヤの斧か
                    meta = item.getItemMeta();
                    if (meta == null) return true;
                    if (!meta.getDisplayName().equals(MAZEWAND)) return true;  //名前が等しいか
                    //斧が正常なものか判定
                    List<String> lore = meta.getLore();
                    if (lore == null) return true;
                    if (lore.size() != 3 || !lore.get(2).equals(p.getUniqueId().toString())) {
                        sendPrefixMessage(p, "§c§l斧が正常ではありません。再度斧を取得してください");
                        return true;
                    }

                    //指定されているか
                    lore = meta.getLore();
                    if (lore.get(1).contains("§6§l左クリックで指定")) {
                        sendPrefixMessage(p, "§d§l開始位置が指定されていません");
                        return true;
                    }
                    if (lore.get(2).contains("§6§l右クリックで指定")) {
                        sendPrefixMessage(p, "§d§l終了位置が指定されていません");
                        return true;
                    }

                    //各座標の最少位置、最大位置を登録
                    String[] locS = lore.get(0).replace("§f§l開始位置: ", "").split(",");  //最少位置
                    world = Bukkit.getWorld(locS[0]);  //ワールドを記憶
                    String[] locE = lore.get(1).replace("§f§l終了位置: ", "").split(",");  //最大位置
                    float[][] locRegister = new float[2][3];
                    for (int i = 0; i < 3; i++) {
                        float s = Float.parseFloat(locS[i + 1]);
                        float e = Float.parseFloat(locE[i + 1]);
                        if (s < e) {
                            locRegister[0][i] = s;
                            locRegister[1][i] = e;
                        } else {
                            locRegister[0][i] = e;
                            locRegister[1][i] = s;
                        }
                    }


                    //迷路の情報を作成
                    int width = isCreate ? (args.length > 4 && args[4].matches("\\d+") ? Integer.parseInt(args[4]) : 1 ) : 0;  //幅を生成 (定義時は0マスとする)
                    if ((locRegister[1][0] - locRegister[0][0]) % (width + 1) != 0) {  //道幅による横幅調整
                        locRegister[1][0] -= (locRegister[1][0] - locRegister[0][0]) % (width + 1);
                        if (locRegister[1][0] == 0) {
                            sendPrefixMessage(p, "§cdxが小さすぎます");
                            return true;
                        }
                        sendPrefixMessage(p, "§c§nn(間隔+1)+1=Δx§r§cを満たす自然数nが存在しなかったのでxが若干縮小されました");
                    }
                    if ((locRegister[1][2] - locRegister[0][2]) % (width + 1) != 0) {  //道幅による縦幅調整
                        locRegister[1][2] -= (locRegister[1][2] - locRegister[0][2]) % (width + 1);
                        if (locRegister[1][2] == 0) {
                            sendPrefixMessage(p, "§cdxが小さすぎます");
                            return true;
                        }
                        sendPrefixMessage(p, "§c§nn(間隔+1)+1=Δz§r§cを満たす自然数nが存在しなかったのでzが若干縮小されました");
                    }

                    //迷路を生成 (定義時を除く)
                    if(isCreate) {
                        if (args[2].equals("0")) new ExtendWall(width).createMaze(locRegister, world, mate);  //壁伸ばし法
                        else if (args[2].charAt(0) == '1')
                            new StickKnockDown(width, args[2].contains("r")).createMaze(locRegister, world, mate);  //棒倒し法
                        else {
                            sendPrefixMessage(p, "§c§l不明な生成タイプ番号です");
                            return true;
                        }
                    }
                    //ymlに記録 (迷路だけを生成する場合を除く)
                    if (!args[1].equals("null")) {
                        data.set(args[1] + ".world", world.getUID().toString());
                        data.set(args[1] + ".start", List.of(locRegister[0][0], locRegister[0][1], locRegister[0][2]));
                        data.set(args[1] + ".end", List.of(locRegister[1][0], locRegister[1][1], locRegister[1][2]));
                    }
                    sendPrefixMessage(p, "§a§l迷路を"+ (isCreate ? "生成" : "定義")+"しました");
                    break;

                case "sethome":
                    if (!p.isOp() || args.length < 2) return true;
                    if (data.get(args[1]) == null) {
                        sendPrefixMessage(p, "§c§lそのidの迷路が見つかりません");
                        return true;
                    }
                    data.set(args[1] + ".home", p.getLocation());
                    sendPrefixMessage(p, "§a現在地を§d§lid:" + args[1] + "§a§lのホームに設定しました。");
                    break;

                case "giveup":
                    UUID u = p.getUniqueId();
                    if (!TimerSystem.timeMemo.containsKey(u)) {
                        sendPrefixMessage(p, "§c§l現在挑戦中の迷路がありません");
                        return true;
                    }
                    String id = TimerSystem.timeMemo.get(u).get(0);
                    Location home = data.getLocation(id + ".home");
                    p.teleport(home == null ? p.getWorld().getSpawnLocation() : home);
                    sendPrefixMessage(p, "§a§l迷路をギブアップしました");
                    return true;

                case "delete":
                    if (!p.isOp() || args.length < 2) return true;
                    if (data.get(args[1]) == null) {
                        sendPrefixMessage(p, "§c§lそのidの迷路が見つかりません");
                        return true;
                    }
                    world = Bukkit.getWorld(UUID.fromString(data.getString(args[1] + ".world")));
                    List<Float> s = data.getFloatList(args[1] + ".start");
                    List<Float> e = data.getFloatList(args[1] + ".end");
                    //O(N^3)だからなんとかしたい
                    for (int i = s.get(0).intValue(); i <= e.get(0).intValue(); i++) {
                        for (int j = s.get(1).intValue(); j <= e.get(1).intValue(); j++) {
                            for (int k = s.get(2).intValue(); k < e.get(2).intValue(); k++)
                                world.getBlockAt(i, j, k).setType(Material.AIR);
                        }
                    }
                    data.set(args[1], null);
                    sendPrefixMessage(p, "§d§lid:" + args[1] + "§a§lの迷路を正常に削除しました");

                    break;
                case "list":
                    Set<String> keys = data.getKeys(false);
                    sendPrefixMessage(p,"§e§l現存する迷路のidは以下の通りです");
                    for(String key : keys)sendPrefixMessage(p,"§6"+key);
                    sendPrefixMessage(p,"§e§l--- 以上 ---");
                    return true;
            }
            MazeCreate.getDataYml().saveConfig();


        }catch (ArrayIndexOutOfBoundsException e){
            if(p.isOp()) {
                sendPrefixMessage(p,"§a/maze wand §: 領域斧を取得");
                sendPrefixMessage(p,"§a/maze define <id> §d: 指定範囲を<id>の迷路として定義");
                sendPrefixMessage(p, "§a/maze create <id> <生成アルゴリズム> <ブロック> [<道幅>] §d: 迷路を生成");
                sendPrefixMessage(p,"§6┗idを§n\"null\"§r§6とした場合、迷路のみを生成");
                sendPrefixMessage(p,"§a/maze sethome <id> §d: 指定したidの迷路のホームを現在地に設定");
                sendPrefixMessage(p,"§a/maze delete <id> §d: 指定したidの迷路を削除");
                sendPrefixMessage(p,"§a/maze list §d: 現存する迷路のlistを取得");
            }
            sendPrefixMessage(p,"§a/maze giveup §d: 現在挑戦中の迷路を諦める");
        }
        return true;
    }


}
