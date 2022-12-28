package silverassist.mazecreate.CreateSystem;

import silverassist.mazecreate.Function;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

public class CreateFile {
    static void createMazeFileByWall(int[][] data){  //dataは迷路の壁データが格納されてる
        //ディレクトリ作成
        Path pp = Paths.get("./plugins/MazeCreate/log");
        try{
            Files.createDirectories(pp);
        }catch(IOException e){
            System.err.println("[MazeCreate]ディレクトリの作成に失敗しました: "+e);
            return;
        }

        //ファイル作成
        Date date = new Date();
        String fileName = date.getYear()+"_"+(date.getMonth() + 1)+"_"+date.getDate()+"-"+date.getHours()+"_"+date.getMinutes()+"_"+date.getSeconds();
        Path pc = Paths.get("./plugins/MazeCreate/log/"+fileName+".txt");
        try{
            Files.createFile(pc);
        } catch (IOException e) {
            System.err.println("[MazeCreate]ファイル作成に失敗しました: "+e);
            return;
        }

        //ファイル書き込み
        File file = new File(String.valueOf(pc));
        if(!(file.isFile() && file.canWrite()))return;
        FileWriter fileWriter;
        try {
            fileWriter= new FileWriter(file);
            for(int i = data.length-1;i>=0;i--){
                fileWriter.write(Arrays.toString(data[i]).replace("[","").replace("]","").replace(",","").replace(" ","")+"\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("[MazeCreate]ファイル書き込みに失敗しました: "+e);
            return;
        }
    }
}
