package silverassist.mazecreate.CreateSystem;

import org.bukkit.Material;
import org.bukkit.World;
import silverassist.mazecreate.Function;

import java.util.*;

public class ExtendWall {
    public static void createMaze(float[][] Age, World world, Material mate,  int width){
        //float[][] Age = new float[][]{{2,3},{502,503}};
        int lx = (int)(Age[1][0] - Age[0][0]);
        int lz = (int)(Age[1][2] - Age[0][2]);
        int h = (int)(Age[1][1]-Age[0][1]);
        int[][] data = new int[lx+1][lz+1];

        Map<Integer, List<Integer>> start = new TreeMap<>();
        int cnt = 0;
        for(int i = 0;i<=lx;i++){
            for(int j=0;j<=lz;j++){
                if(i==0 || j==0 || i==lx || j==lz){
                    data[i][j] = 1;
                    Function.setBlock(world,Age[0],List.of(i,j),h,mate);
                    continue;
                }
                if(i%(width+1)==0 && j%(width+1)==0){
                    if(!start.containsKey(i))start.put(i,new LinkedList<>());
                    start.get(i).add(j);
                    cnt++;
                }
            }
        }
        while (cnt>0){
            Object[] xl = start.keySet().toArray();
            int l0 = (int) xl[(int) (Math.random()*xl.length)];
            Object[] zl = start.get(l0).toArray();
            int l1 = (int) zl[(int) (Math.random() * zl.length)];
            List<Integer> loc = new ArrayList<>(List.of(l0, l1));
            if(start.get(l0).size()==1)start.remove(l0);
            else start.get(l0).remove(start.get(l0).indexOf(loc.get(1)));
            data[l0][l1] = 1;
            Function.setBlock(world,Age[0],loc,h,mate);
            List<List<Integer>> fin = new LinkedList<>();
            cnt--;
            int back = 0;
            while (true){
                if(!fin.contains(loc))fin.add(new ArrayList<>(loc));
                int[] next = search(fin.get(fin.size()-back-1), fin, width);
                if(next==null){
                    back++;
                    if(fin.size() == back)break;
                    continue;
                }
                boolean breakFlag = data[loc.get(0) + next[0]][loc.get(1) + next[1]] == 1;

                for(double j=1;j<=width+1;j++){
                    data[loc.get(0) + (int)Math.round(next[0] *j/(width+1))][loc.get(1) + (int)Math.round(next[1]* j/(width+1))] = 1;
                    Function.setBlock(world,Age[0],List.of(loc.get(0) + (int)(next[0] *j/(width+1)), loc.get(1) + (int)(next[1]* j/(width+1))),h,mate);
                }

                for(int i = 0;i<2;i++)loc.set(i,loc.get(i) + next[i]);

                if(breakFlag)break;
                int l02 = loc.get(0);
                if(start.get(l02).size()==1)start.remove(l02);
                else start.get(l02).remove(start.get(l02).indexOf(loc.get(1)));
                cnt--;

            }
        }
        CreateFile.createMazeFileByWall(data);
    }

    private static int[] search(List<Integer> l, List<List<Integer>> fin, int width){
        List<int[]> d = new LinkedList<>(){{add(new int[]{-(width+1),0});add(new int[]{(width+1),0});add(new int[]{0,-(width+1)});add(new int[]{0,(width+1)});}};
        new ArrayList<>(d).forEach( dd ->{
            if(fin.contains(List.of(l.get(0)+dd[0],l.get(1)+dd[1])))d.remove(dd);
        });
        if(d.size()==0)return null;
        return d.get((int) (Math.random() * d.size()));
    }



}