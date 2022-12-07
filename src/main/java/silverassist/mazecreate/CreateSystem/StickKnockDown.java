package silverassist.mazecreate.CreateSystem;

import org.bukkit.Material;
import org.bukkit.World;
import silverassist.mazecreate.Function;

import java.util.*;


public class StickKnockDown {
    static List<int[]> DIR = List.of(new int[]{0, -1},new int[]{0,1},new int[]{1,0},new int[]{-1,0});

    int[][] data = null;
    boolean isReverse = false;
    public void createMaze(float[][] Age, World world, Material mate, int width, boolean toReverse){
        int lx = (int)(Age[1][0] - Age[0][0]);
        int lz = (int)(Age[1][2] - Age[0][2]);
        int h = (int)(Age[1][1]-Age[0][1]);
        data = new int[lx+1][lz+1];
        isReverse = toReverse;

        Map<Integer, List<Integer>> start = new TreeMap<>();
        for(int i = 0;i<=lx;i++){
            for(int j=0;j<=lz;j++){
                if(i==0 || j==0 || i==lx || j==lz){
                    data[i][j] = 1;
                    Function.setBlock(world,Age[0],List.of(i,j),h,mate);
                    continue;
                }
                if(i%(width+1)==0 && j%(width+1)==0){
                    data[i][j] = 1;
                    if(!start.containsKey(i))start.put(i,new LinkedList<>());
                    Function.setBlock(world,Age[0],List.of(i,j),h,mate);
                    start.get(i).add(j);
                }
            }
        }
        for (int i : start.keySet()){
            boolean isFirstLine = i == (width+1);
            for(int j : start.get(i)){
                int[] thisDir = search(new int[]{i, j}, isFirstLine);
                int x = Math.abs((isReverse ? lx : 0) - (i+thisDir[0]));
                data[x][j + thisDir[1]] = 1;
                Function.setBlock(world,Age[0],List.of(x, j + thisDir[1]), h ,mate);
            }
        }
        CreateFile.createMazeFileByWall(data);
    }


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
