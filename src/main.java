import java.util.*;

public class main {
	public static void main(String[] args) {
		int[][] maze = new int[10][10];
		for(int i = 0; i < maze.length; i++) {
			for(int j = 0; j < maze[0].length; j++) {
				if(i == 0 || i == maze.length - 1)
					maze[i][j] = 1;
				else if(j == 0 || j == maze[0].length - 1)
					maze[i][j] = 1;
				else 
					maze[i][j] = 2;
			}
		}
		for(int i = 0; i < maze.length; i++) {
			for(int j = 0; j < maze[0].length; j++) {
				if(i == 0 && j == 0 || i == 0 && j == maze[0].length-1 || i == maze.length-1 && j == 0 || i == maze.length-1 && j == maze[0].length-1)
					maze[i][j] = 5;
				if(i == 0 && j != maze[0].length - 1 && j != 0) {
					maze[i][j] = maze[i+1][j-1] + maze[i+1][j] + maze[i+1][j+1];
				}
				if(i == maze.length - 1 && j != maze[0].length - 1 && j != 0) {
					maze[i][j] = maze[i-1][j-1] + maze[i-1][j] + maze[i-1][j+1];
				}
			}
		}
		for(int[] i : maze) {
			System.out.println(Arrays.toString(i));
		}
	}
}
