package silverassist.mazecreate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
            return List.of("wand","create");
        }
        else if(args.length==2){
            if(!args[0].equals("create"))return null;
            List<String> material = new ArrayList<>();
            Arrays.asList(Material.values()).forEach(m -> {
                if(!m.isBlock())return;
                String name = m.name();
                if(name.indexOf(args[1])!=0)return;
                material.add(name);
            });
            return material;
        }else if(args.length==3)return List.of("0","1","1r","2");
        return null;
    }
}
