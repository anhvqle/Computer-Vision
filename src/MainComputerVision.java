import java.util.*;
import java.io.*;

public class MainComputerVision 
{
	private static int WIDTH = 128;			//Default width is 128 pixel
	private static int HEIGHT = 128;		//Default height is 128 pixel
	private static int MAX_PIXEL = 255;
	
	private static int[][] orig_pixels;
	private static int[][] pixels;
	
	public static void writeFile( String file ) throws Exception
	{
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
		
		writer.write( 'P' );
		writer.write( '5' );
		writer.write( ' ' );
		writer.write( '1' );
		writer.write( '2' );
		writer.write( '8' );
		writer.write( ' ' );
		writer.write( '1' );
		writer.write( '2' );
		writer.write( '8' );
		writer.write( ' ' );
		writer.write( '2' );
		writer.write( '5' );
		writer.write( '5' );
		writer.write( ' ' );

		for ( int i = 0; i < WIDTH; i++ ){
			for ( int j = 0; j < HEIGHT; j++ )
				writer.write(pixels[i][j]);
		}
		writer.close();
	}
	
	public static void main(String[] args) throws Exception{
		orig_pixels = new int[WIDTH][HEIGHT];
		pixels = new int[WIDTH][HEIGHT];
		
		System.out.println("Please enter a file name: ");
		Scanner input = new Scanner(System.in);
		String fileName = input.nextLine();
		input.close();
		FileReader f = null;
		String format;
		int width, height, maxIntensity;
		
		try{
			f = new FileReader(fileName);			
		}
		catch(FileNotFoundException e){
			System.out.println("Invalid file");
			System.exit(1);
		}
		Scanner reader = new Scanner(f);
		format = reader.next();
		width = reader.nextInt();
		height = reader.nextInt();
		maxIntensity = reader.nextInt();
		
		if((width != WIDTH) || (height != HEIGHT) || (maxIntensity != MAX_PIXEL)){
			System.out.println("Properties Error Detected. Wrong format, width, height, and intensity");
			System.exit(1);
		}
		
		if(format.equals("P2")){
			for(int i = 0; i < WIDTH; i++){
				for (int j = 0; j < HEIGHT; j++)
					orig_pixels[i][j] = reader.nextByte();
			}
		}
		
		if(format.equals("P5")){
			reader.close();
			@SuppressWarnings("resource")
			DataInputStream newInput = new DataInputStream(new FileInputStream(fileName));

			for (int i = 0; i < 15; i++)
				newInput.readUnsignedByte();
			
			for (int i = 0; i < WIDTH; i++){
				for (int j = 0; j < HEIGHT; j++)
					orig_pixels[i][j] = newInput.readUnsignedByte();
			}
			input.close();
		}

		//average filtering
		for (int i = 1; i < WIDTH-1; i++)
			for (int j = 1; j < HEIGHT-1; j++)
				pixels[i][j] = (orig_pixels[i-1][j-1] + orig_pixels[i-1][j] + orig_pixels[i-1][j+1]
								+ orig_pixels[i][j-1] + orig_pixels[i][j] + orig_pixels[i][j+1]
								+ orig_pixels[i+1][j-1] + orig_pixels[i+1][j] + orig_pixels[i+1][j+1])/9;

		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				if(i == 0 || i == pixels.length - 1)
					pixels[i][j] = 255;
				if(j == 0 || j == pixels[0].length - 1) {
					pixels[i][j] = 255;
				}
			}
		}
		writeFile("average.pgm");

		//median filtering
		int [] array = new int [9];
		for ( int i = 1; i < WIDTH-1; i++ )
			for ( int j = 1; j < HEIGHT-1; j++ ) {
				array[0] = orig_pixels[i-1][j-1];
				array[1] = orig_pixels[i-1][j];
				array[2] = orig_pixels[i-1][j+1];
				array[3] = orig_pixels[i][j-1];
				array[4] = orig_pixels[i][j];
				array[5] = orig_pixels[i][j+1];
				array[6] = orig_pixels[i+1][j-1]; 
				array[7] = orig_pixels[i+1][j];
				array[8] = orig_pixels[i+1][j+1];
				
				Arrays.sort(array);
				int median = (int) array[4];
				pixels[i][j] = median;
			}

		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				if(i == 0 || i == WIDTH - 1)
					pixels[i][j] = 255;
				if(j == 0 || j == HEIGHT - 1) {
					pixels[i][j] = 255;
				}
			}
		}
		
		
		System.out.println("Done");
	}
}
