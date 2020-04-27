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
		
		
		System.out.println("Done");
	}
}
