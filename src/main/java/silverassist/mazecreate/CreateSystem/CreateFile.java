package silverassist.mazecreate.CreateSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

public class CreateFile {
    static void createMazeFileByWall(int[][] data){
        Path pp = Paths.get("./plugins/MazeCreate/log");
        try{
            Files.createDirectories(pp);
        }catch(IOException e){
            return;
        }

        Date date = new Date();
        String s = date.getYear()+"_"+date.getMonth()+"_"+date.getDate()+"-"+date.getHours()+"_"+date.getMinutes()+"_"+date.getSeconds();
        Path pc = Paths.get("./plugins/MazeCreate/log/"+s+".txt");
        try{
            Files.createFile(pc);
        } catch (IOException e) {
            return;
        }
        File file = new File(String.valueOf(pc));
        if(!(file.isFile() && file.canWrite()))return;
        FileWriter fileWriter;
        try {
            fileWriter= new FileWriter(file);
            for(int i = data.length-1;i>=0;i--){
                fileWriter.write(Arrays.toString(data[i]).replace("[","").replace("]","").replace(",","").replace(" ","")+"\n");
            }
            fileWriter.close();
        } catch (IOException e) {return;}
    }
}
