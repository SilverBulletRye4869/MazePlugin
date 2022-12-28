package silverassist.mazecreate.CreateSystem;

import org.bukkit.Material;
import org.bukkit.World;
import silverassist.mazecreate.Function;

import java.util.*;

public class ExtendWall {  //壁伸ばし法
    final int width;  //迷路の幅
    public ExtendWall(int width){
        this.width = width;
    }
    public void createMaze(float[][] Age, World world, Material mate){
        //float[][] Age = new float[][]{{2,3},{502,503}};

        //横幅、縦幅、高さを取得
        int lx = (int)(Age[1][0] - Age[0][0]);
        int lz = (int)(Age[1][2] - Age[0][2]);
        int h = (int)(Age[1][1]-Age[0][1]);
        int[][] data = new int[lx+1][lz+1];  //迷路の既存壁データを作成しておく奴

        Map<Integer, List<Integer>> start = new TreeMap<>();  //スタート地点の候補を格納しておくMap
        int cnt = 0;  //スタート地点の候補数
        for(int i = 0;i<=lx;i++){
            for(int j=0;j<=lz;j++){
                if(i==0 || j==0 || i==lx || j==lz){  //端は必ず壁
                    data[i][j] = 1;
                    Function.setBlock(world,Age[0],List.of(i,j),h,mate);
                    continue;
                }
                if(i%(width+1)==0 && j%(width+1)==0){  //始点との差と(道幅+1)の剰余が0のときそこを候補に入れる
                    if(!start.containsKey(i))start.put(i,new LinkedList<>());
                    start.get(i).add(j);
                    cnt++;  //候補+1
                }
            }
        }

        //いよいよ作成
        while (cnt>0){
            Object[] xl = start.keySet().toArray();  //xの候補を取得(Setはindexアクセスが不可なので配列化)
            int l0 = (int) xl[(int) (Math.random()*xl.length)];  //ランダムに取得
            Object[] zl = start.get(l0).toArray();  //zの候補を取得
            int l1 = (int) zl[(int) (Math.random() * zl.length)];  //ランダムに取得
            List<Integer> loc = new ArrayList<>(List.of(l0, l1));  //現在地をlocListで取得　配列だと途中でクラッシュする
            if(start.get(l0).size()==1)start.remove(l0);  //残り1個の時は、keyごと削除
            else start.get(l0).remove(start.get(l0).indexOf(loc.get(1)));  //2個以上残ってるときは、対象の物を削除
            data[l0][l1] = 1;  //候補地を壁にする
            Function.setBlock(world,Age[0],loc,h,mate);  //ブロック設置
            List<List<Integer>> fin = new LinkedList<>();  //今回の設置で壁を置いたところを記憶 (遡るためSet<>はできない)
            cnt--;  //残りの候補地を-1
            int back = 0;  //遡った数
            while (true){
                if(!fin.contains(loc))fin.add(new ArrayList<>(loc));  //現在地を見た地点に追加
                int[] next = search(fin.get(fin.size()-back-1), fin);  //移動先を選択
                if(next==null){  //移動不可の時
                    back++;  //1個遡る
                    if(fin.size() == back)break;  //壁を置いた数＝遡った数　となったらbreak
                    continue;  //そうでなければSkip
                }
                boolean breakFlag = data[loc.get(0) + next[0]][loc.get(1) + next[1]] == 1;  //移動先が既に設置されている壁ならbreak準備 (breakFlag = true)

                for(double j=1;j<=width+1;j++){  //次の地点までを壁で埋める
                    data[loc.get(0) + (int)Math.round(next[0] *j/(width+1))][loc.get(1) + (int)Math.round(next[1]* j/(width+1))] = 1;  //壁データに置換
                    Function.setBlock(world,Age[0],List.of(loc.get(0) + (int)(next[0] *j/(width+1)), loc.get(1) + (int)(next[1]* j/(width+1))),h,mate);  //ブロック設置
                }

                for(int i = 0;i<2;i++)loc.set(i,loc.get(i) + next[i]);  //現在地を書き換え

                if(breakFlag)break;

                int l02 = loc.get(0);  //新地点のx座標を変数に
                if(start.get(l02).size()==1)start.remove(l02);  //そのx地点のz候補が一つならkeyごと削除
                else start.get(l02).remove(start.get(l02).indexOf(loc.get(1)));  //そうでなければ、移動先のzを削除
                cnt--;  //残りの候補地を-1

            }
        }
        CreateFile.createMazeFileByWall(data);
    }

    //現在の設置でまだ訪れてない四方のいずれかをランダムに取得するメソッド
    private int[] search(List<Integer> l, List<List<Integer>> fin){
        List<int[]> d = new LinkedList<>(){{add(new int[]{-(width+1),0});add(new int[]{(width+1),0});add(new int[]{0,-(width+1)});add(new int[]{0,(width+1)});}};
        new ArrayList<>(d).forEach( dd ->{
            if(fin.contains(List.of(l.get(0)+dd[0],l.get(1)+dd[1])))d.remove(dd);
        });
        if(d.size()==0)return null;
        return d.get((int) (Math.random() * d.size()));
    }



}
