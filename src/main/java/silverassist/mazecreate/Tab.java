package silverassist.mazecreate;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length==1){
            if(sender.isOp())return List.of("wand","define","create","sethome","giveup","define");
            else return List.of("giveup");
        }
        if(!sender.isOp())return null;
        if(args.length==4){
            if(!args[0].equals("create"))return null;
            List<String> material = new ArrayList<>();
            Arrays.asList(Material.values()).forEach(m -> {
                if(!m.isBlock())return;
                String name = m.name();
                if(name.indexOf(args[3])!=0)return;
                material.add(name);
            });
            return material;
        }else if(args.length==3)return List.of("0","1","1r");
        return null;
    }
}
