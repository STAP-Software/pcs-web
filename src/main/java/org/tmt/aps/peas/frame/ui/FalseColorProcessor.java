package org.tmt.aps.peas.frame.ui;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.primefaces.model.DefaultStreamedContent;
 
public class FalseColorProcessor { 
	public static void main(String[] args) throws IOException { 
 
		BufferedReader reader; // This object is to read in grayscale data 
		String line; 
		String strings[]; 
		int width; 
		int height; 
 
		try { 
			reader = new BufferedReader(new FileReader("data.txt")); 
			line = reader.readLine(); // Read array size data 
										// Parse the first line and output size 
										// info to terminal ... 
			strings = line.split(" "); 
			height = Integer.parseInt(strings[0]); 
			width = Integer.parseInt(strings[1]); 
			System.out.println(" NR = " + height + ", NC = " + width); 
 
			// Read in grayscale information ... 
			int grayArray[][] = new int[height][width]; // 2D array form 
			int numPixels = width * height; 
			int gray[] = new int[numPixels]; // 1D array form 
			int entry = 0; 
			for (int i = 0; i < height; i++) { 
				line = reader.readLine(); 
				strings = line.split(" "); 
				for (int j = 0; j < width; j++) { 
					grayArray[i][j] = Integer.parseInt(strings[j]); 
					gray[entry++] = grayArray[i][j]; 
				} 
			} 
			reader.close(); 
 
			
		} catch (Exception e) {
		}
	}
	
	public DefaultStreamedContent createImage(short[][] grayArray) {
			
		int width = grayArray.length; 
		int height = grayArray[0].length; 
		
		
		int numPixels = width * height; 
		int gray[] = new int[numPixels]; // 1D array form 
		int entry = 0; 
		int imageMax = 0;
		int imageMin = 16000;
		for (int i = 0; i < height; i++) { 
			for (int j = 0; j < width; j++) { 
				imageMax = (imageMax > grayArray[i][j] && imageMax < 6000) ? imageMax : grayArray[i][j];
				imageMin = (imageMin < grayArray[i][j]) ? imageMin : grayArray[i][j];
				gray[entry++] = grayArray[i][j]; 
			} 
		} 

		int imageRange = imageMax - imageMin;
		System.out.println("max = " + imageMax + ", min = " + imageMin + ", range = " + imageRange);
		
		try { 
			// Create the R,G,B arrays for the false color image ... 
 
			// the 256 number is not variable.  We need to take the max and min and use that as the range.
			
			int red[][] = new int[width][height]; 
			int green[][] = new int[width][height]; 
			int blue[][] = new int[width][height]; 
			float midSlope, leftSlope, rightSlope; 

			midSlope = (float) (255.0 / (192.0 - 64.0)); 
			leftSlope = (float) (255.0 / 64.0); 
			rightSlope = (float) (-255.0 / 63.0); 

			
			int X; 
 
			entry = 0; 
			for (int h = 0; h < height; h++) { 
				for (int w = 0; w < width; w++) { 
					X = gray[entry++]; 
					
					// normalize X to 0 to 255
					float y = ((float)((X - imageMin) * 255)) / (float)imageRange;
 
					X = (int)y;
					
					// Now the false color assignment ... 
					if (X < 64) { 
						red[w][h] = 0; 
						green[w][h] = Math.round(leftSlope * X); 
						blue[w][h] = 255; 
						
					} else if ((X >= 64) && (X < 192)) { 
						red[w][h] = Math.round(255 + midSlope * (X - 192)); 
						green[w][h] = 255; 
						blue[w][h] = Math.round(255 - midSlope * (X - 64)); 
					} else { 
						red[w][h] = 255; 
						green[w][h] = Math.round(255 + rightSlope * (X - 192)); 
						blue[w][h] = 0; 
					} 
				} 
			} 
 
			// Now create the false color image ... 
			BufferedImage falseColor = new BufferedImage(width, height, 
					BufferedImage.TYPE_INT_RGB); 
			WritableRaster falseColorRaster = falseColor.getRaster(); 
			for (int y = 0; y < height; y++) { 
				for (int x = 0; x < width; x++) { 
					falseColorRaster.setSample(x, y, 0, red[x][y]); 
					falseColorRaster.setSample(x, y, 1, green[x][y]); 
					falseColorRaster.setSample(x, y, 2, blue[x][y]); 
 
				} 
			} 
			
            ByteArrayOutputStream os = new ByteArrayOutputStream();  
            ImageIO.write(falseColor, "png", os);  
            return new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/png");   

			
			
			// Save the false color image to a png file ... 
			//ImageIO.write(falseColor, "PNG", new File("C:\\Users\\Scott\\Desktop\\falseColorOut.png")); 
 
			/*
			// Set things up for saving the gray array to an output 
			// file as a grayscale image ... 
			BufferedImage grayImage = new BufferedImage(width, height, 
					BufferedImage.TYPE_BYTE_GRAY); 
			WritableRaster grayRaster = grayImage.getRaster(); 
			grayRaster.setPixels(0, 0, width, height, gray); 
 
			// Save the gray image to a png file ... 
 
			ImageIO.write(grayImage, "PNG", new File("C:\\Users\\Scott\\Desktop\\grayImageOut.png")); 
 			*/
 
		} catch (IOException ioe) { 
			System.out.println("Error: IO Exception."); 
			return null;
		} 
 
	} 
} 
