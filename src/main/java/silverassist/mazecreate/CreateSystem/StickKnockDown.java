package silverassist.mazecreate.CreateSystem;

import org.bukkit.Material;
import org.bukkit.World;
import silverassist.mazecreate.Function;

import java.util.*;


public class StickKnockDown {  //棒倒し法
    final int width;  //迷路の幅
    List<int[]> DIR;  //移動ベクトル
    int[][] data = null;  //壁データを格納しておく奴
    boolean isReverse = false;  //xに対する上下反転か？　(棒倒し法は片方が凄い進みやすくなりやすい)

    public StickKnockDown(int width, boolean toReverse){
        this.width = width;
        DIR = List.of(new int[]{0, -width},new int[]{0,width},new int[]{width,0},new int[]{-width,0});
        isReverse = toReverse;
    }
    public void createMaze(float[][] Age, World world, Material mate){
        //横幅、縦幅、高さを取得
        int lx = (int)(Age[1][0] - Age[0][0]);
        int lz = (int)(Age[1][2] - Age[0][2]);
        int h = (int)(Age[1][1]-Age[0][1]);
        data = new int[lx+1][lz+1];

        System.err.println(width);
        for(int i = 0;i<=lx;i++){
            for(int j=0;j<=lz;j++){
                if(i==0 || j==0 || i==lx || j==lz){  //外壁は壁にする
                    data[i][j] = 1;
                    Function.setBlock(world,Age[0],List.of(i,j),h,mate);  //ブロックを設置
                    continue;
                }
                if(i%(width+1)==0 && j%(width+1)==0){  //始点との差と(道幅+1)の剰余が0のとき実施
                    data[i][j] = 1;  //壁にもしておく
                    Function.setBlock(world,Age[0],List.of(i,j),h,mate); //現在地にブロック設置
                    int[] thisDir = search(new int[]{i, j}, i == (width+1));  //倒す方向を選択。第二引数は1番上の段(上に倒せるか)かどうか
                    for(double k = 1.0/width;k<=1;k+=1.0/width){  //倒すところを全部回す
                        int x = Math.abs((isReverse ? lx : 0) - (int)(i+thisDir[0]*k));  //x座標を確定（反転の場合は反対側からいくつかか）
                        int z = (int) (j + thisDir[1] * k);  //z座標を確定
                        data[x][z] = 1;  //倒すところを壁に
                        Function.setBlock(world,Age[0],List.of(x,z), h ,mate);  //倒すところにブロック設置
                    }
                }
            }
        }
        CreateFile.createMazeFileByWall(data);  //迷路データ生成
    }

    //倒せる場所を判定するメソッド
    private int[] search(int[] loc, boolean isFirstLine){
        int n = isFirstLine ? 4 : 3;
        List<int[]> dir_c = new LinkedList<>(DIR);
        for(int i = 0;i<n;i++){
            if(data[loc[0] + DIR.get(i)[0]][loc[1]+DIR.get(i)[1]] == 0)continue;
            dir_c.remove(i);
            n--;
        }
        return dir_c.get((int)(Math.random() * n));
    }
}
