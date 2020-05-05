import java.util.*;
import java.io.*;

public class MainComputerVision 
{
	private static int WIDTH = 128;			//Default width is 128 pixel
	private static int HEIGHT = 128;		//Default height is 128 pixel
	
	private static int[][] original = new int[WIDTH][HEIGHT];
	private static int[][] filtered = new int[WIDTH][HEIGHT];
	
	//WriteFile method to output a PGM file
	public static void writeFile( String file ) throws Exception
	{
		DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
		output.write( 'P' );
		output.write( '5' );
		output.write( ' ' );
		output.write( '1' );
		output.write( '2' );
		output.write( '8' );
		output.write( ' ' );
		output.write( '1' );
		output.write( '2' );
		output.write( '8' );
		output.write( ' ' );
		output.write( '2' );
		output.write( '5' );
		output.write( '5' );
		output.write( ' ' );
		
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
		}
		catch(FileNotFoundException e){
			System.out.println("Invalid files");
			System.exit(1);
		}
		
		format = scan.next();
		width = scan.nextInt();
		height = scan.nextInt();
		maxIntensity = scan.nextInt();
		scan.close();

		//Read in unsigned bytes
		try{
			newInput = new DataInputStream(new FileInputStream(fileName));
		}
		catch(FileNotFoundException e){
			System.out.println("Invalid file");
			System.exit(1);
		}

		for (int i = 0; i < WIDTH; i++){
			for (int j = 0; j < HEIGHT; j++) {
				original[i][j] = newInput.readUnsignedByte();
			}
		}
		newInput.close();		
		
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
		
		/*-------------------------Edge Detection---------------------------*/
		
		int[][] deltaX = new int[WIDTH][HEIGHT];
		int[][] deltaY = new int[WIDTH][HEIGHT];
		int[][] magnitude = new int[WIDTH][HEIGHT];
		
		//Compute deltaX, deltaY, and Magnitude 2D Arrays
		for(int i = 0; i < WIDTH; i++){
			for(int j = 0; j < HEIGHT; j++) {
				if (i==0 || j==0 || i == WIDTH -1 || j == HEIGHT -1)
				{
					deltaX[i][j] = deltaY[i][j] = 0;
					magnitude[i][j] = 0;
				}
				else
				{
					deltaX[i][j] = original[i-1][j+1] + original[i][j+1] + original[i+1][j+1] - (original[i-1][j-1] + original[i][j-1] + original[i+1][j-1]);
					deltaY[i][j] = original[i-1][j-1] + original[i-1][j] + original[i-1][j+1] - (original[i+1][j-1] + original[i+1][j] + original[i+1][j+1]);
					magnitude[i][j] = (int)(Math.sqrt(deltaX[i][j] * deltaX[i][j] + deltaY[i][j] * deltaY[i][j]));
				}
			}
		}
		
		//Compute the Threshold
		int threshold = Integer.MIN_VALUE;
		
		//Fill the edge pixels with 255
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				if(magnitude[i][j] >= threshold)
					filtered[i][j] = 255;
				else
					filtered[i][j] = 0;
			}
		}
				
		writeFile("edge.pgm");
		
		/*------------------------Hough Transform---------------------------*/

		System.out.println("Done - Check within project folder for filtered images");
	}
}
