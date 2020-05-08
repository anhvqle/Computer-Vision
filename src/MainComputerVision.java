import java.util.*;
import java.io.*;

public class MainComputerVision 
{
	private static int WIDTH = 128;			//Default width is 128 pixel
	private static int HEIGHT = 128;		//Default height is 128 pixel
	
	private static int[][] original = new int[WIDTH][HEIGHT];
	private static int[][] filtered = new int[WIDTH][HEIGHT];
	private static int[][] verticalAccumulator = new int[WIDTH][HEIGHT];
	private static int[][] horizontalAccumulator = new int[WIDTH][HEIGHT];
	
	//WriteFile method to output a PGM file
	public static void writeFile( String file ) throws Exception
	{
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
		output.write('P');
		output.write('5');
		output.write(' ');
		output.write('1');
		output.write('2');
		output.write('8');
		output.write(' ');
		output.write('1');
		output.write('2');
		output.write('8');
		output.write(' ');
		output.write('2');
		output.write('5');
		output.write('5');
		output.write(' ');
		
		for ( int i = 0; i < WIDTH; i++ ){
			for ( int j = 0; j < HEIGHT; j++ )
				output.write(filtered[i][j]);
		}
		output.close();
	}
	
	public static void main(String[] args) throws Exception{	
		System.out.println("Please enter a file name: ");
		Scanner input = new Scanner(System.in);
		String fileName = input.nextLine();
		input.close();
		DataInputStream newInput = null;
		String format;
		int width, height, maxIntensity;
		//Get the file format, dimensions, and max intensity
		Scanner scan = null;
		try {
			scan = new Scanner(new FileReader(fileName));
			format = scan.next();
			width = scan.nextInt();
			height = scan.nextInt();
			maxIntensity = scan.nextInt();
			if(format.equals("P5")) {
				scan.close();
				newInput = new DataInputStream(new FileInputStream(fileName));
				for (int i = 0; i < 15; i++)
					newInput.readUnsignedByte();
				for (int i = 0; i < WIDTH; i++){
					for (int j = 0; j < HEIGHT; j++) {
						original[i][j] = newInput.readUnsignedByte();
					}
				}
			}
			newInput.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Invalid files");
			System.exit(1);
		}
		
		scan.close();		
		
		/*-------------------------Average Filtering-------------------------*/
		for (int i = 1; i < WIDTH-1; i++) {
			for (int j = 1; j < HEIGHT-1; j++) {
				filtered[i][j] = (original[i-1][j-1] + original[i-1][j] + original[i-1][j+1] + original[i][j-1] + original[i][j] 
						+ original[i][j+1] + original[i+1][j-1] + original[i+1][j] + original[i+1][j+1])/9;
			}
		}

		writeFile("average.pgm");
		
		/*-------------------------Median Filtering--------------------------*/
		int [] array = new int [9];
		for ( int i = 1; i < WIDTH-1; i++ ) {
			for ( int j = 1; j < HEIGHT-1; j++ ) {
				array[0] = original[i-1][j-1];
				array[1] = original[i-1][j];
				array[2] = original[i-1][j+1];
				array[3] = original[i][j-1];
				array[4] = original[i][j];
				array[5] = original[i][j+1];
				array[6] = original[i+1][j-1]; 
				array[7] = original[i+1][j];
				array[8] = original[i+1][j+1];
				Arrays.sort(array);
				int median = array[4];
				filtered[i][j] = median;
			}
		}

		writeFile("median.pgm");
		
		/*--------------------------Edge Detection----------------------------*/
		int[][] deltaX = new int[WIDTH][HEIGHT];
		int[][] deltaY = new int[WIDTH][HEIGHT];
		double[][] magnitude = new double[WIDTH][HEIGHT];
		
		//Compute deltaX, deltaY, and Magnitude 2D Arrays
		for(int i = 0; i < WIDTH; i++){
			for(int j = 0; j < HEIGHT; j++) {
				if (i==0 || j==0 || i == WIDTH -1 || j == HEIGHT -1)
				{
					filtered[i][j] = 0;
					magnitude[i][j] = 0;
				}
				else
				{
					deltaX[i][j] = original[i-1][j+1] + original[i][j+1] + original[i+1][j+1] - (original[i-1][j-1] + original[i][j-1] + original[i+1][j-1]);
					deltaY[i][j] = original[i-1][j-1] + original[i-1][j] + original[i-1][j+1] - (original[i+1][j-1] + original[i+1][j] + original[i+1][j+1]);
					magnitude[i][j] = (Math.sqrt(deltaX[i][j] * deltaX[i][j] + deltaY[i][j] * deltaY[i][j]));
				}
			}
		}
		
		//Compute the Threshold, which is 3 times the average value of all magnitudes
		double sum = 0;
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				sum += magnitude[i][j];
			}
		}
		double threshold = sum / (WIDTH * HEIGHT);
		
		//Fill the edge pixels with 255
		for(int i = 1; i < WIDTH-1; i++) {
			for(int j = 1; j < HEIGHT-1; j++) {
				filtered[i][j] = (magnitude[i][j] >= 3*threshold) ? 255 : 0;
			}
		}
				
		writeFile("edge.pgm");
		
		/*-------------------------Hough Transform----------------------------*/
		//Vertical Lines
		for(int x = 0; x < WIDTH; x++) {
			for(int y = 0; y < HEIGHT; y++) {
				if(filtered[x][y] != 0) {
					for(int m = 0; m < WIDTH; m++) {
						for(int b = 0; b < HEIGHT; b++) {
							if(b == y - m*x)
								verticalAccumulator[m][b] += 1;
						}
					}
				}
			}
		}
		int maxB = 0;
		int maxM = 0;
		int max = verticalAccumulator[0][0];
		for(int i = 0; i < 6; i++) {
			for(int m = 0; m < WIDTH; m++) {
				for(int b = 0; b < HEIGHT; b++) {
					if(verticalAccumulator[m][b] > max) {
						max = verticalAccumulator[m][b];
						maxM = m;
						maxB = b;
					}
				}
			}
			for(int x = 0; x < WIDTH; x++) {
				for(int y = 0; y < HEIGHT; y++) {
					if(y == maxM*x + maxB)
						filtered[x][y] = 255;
				}
			}
			max = verticalAccumulator[maxM][maxB] = 0;
		}		
		
		//Horizontal Lines
		for(int x = 0; x < WIDTH; x++) {
			for(int y = 0; y < HEIGHT; y++) {
				if(filtered[x][y] != 0) {
					for(int m = 0; m < WIDTH; m++) {
						for(int b = 0; b < HEIGHT; b++) {
							if(b == x - m*y)
								horizontalAccumulator[m][b] += 1;
						}
					}
				}
			}
		}
		maxB = 0;
		maxM = 0;
		max = horizontalAccumulator[0][0];
		for(int i = 0; i < 6; i++) {
			for(int m = 0; m < WIDTH; m++) {
				for(int b = 0; b < HEIGHT; b++) {
					if(horizontalAccumulator[m][b] > max) {
						max = horizontalAccumulator[m][b];
						maxM = m;
						maxB = b;
					}
				}
			}
			for(int x = 0; x < WIDTH; x++) {
				for(int y = 0; y < HEIGHT; y++) {
					if(x == y * maxM + maxB)
						filtered[x][y] = 255;
				}
			}
			max = horizontalAccumulator[maxM][maxB] = 0;
		}		

		writeFile("lines.pgm");

		System.out.println("Done - Check within project folder for filtered images");
	}
}
