import java.util.*;

public class main {
	public static void main(String[] args) {
		int[][] maze = new int[10][10];
		for(int i = 0; i < maze.length; i++) {
			for(int j = 0; j < maze[0].length; j++) {
				if(i == 0 || i == maze.length - 1)
					maze[i][j] =1;
				if(j == 0 || j == maze[0].length - 1)
					maze[i][j] =1;
			}
		}
		for(int[] i : maze) {
			System.out.println(Arrays.toString(i));
		}
	}
}
