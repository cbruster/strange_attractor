package strange_attractor;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class image_set 
{
	public static void main(String[] args) throws IOException
	{
		/*
		 * I wanted to recreate this idea https://www.williamrchase.com/post/strange-attractors-12-months-of-art-february/
		 * 
		 * While doing so, I wondered what would happen if I slowly stepped the four 
		 * parameters around, and turn the resulting images into gifs. The result was
		 * actually rather interesting: a continuous transformation of the fractal
		 * properties of the strange attractor easily seen in an animation. 
		 * 
		 * The below code creates a series of images that I can later compile in a gif.
		 * That said, the methods that make a single image out of randomly generated 
		 * parameters still exist. 
		 * 
		 */
		
		double a, b, c, d;
		long start = System.nanoTime();
		a = 1.24;
		b = -1.25;
		c = -1.81;
		d = -1.91;
		System.out.print("Making frames...");
		
		for (int i = 0; i < 28; i++)
		{
			double[][] XY_data = buildData(a, b, c, d);
			BufferedImage bi = makeImage(XY_data, 500, 100);
			putImage_BI(bi, i);
			a += 0.01;
			d -= 0.02;
		}
		
		for (int i = 0; i < 28; i++)
		{
			b -= 0.01;
			c += 0.02;
			double[][] XY_data = buildData(a, b, c, d);
			BufferedImage bi = makeImage(XY_data, 500, 100);
			putImage_BI(bi, i + 28);
		}
		
		for (int i = 0; i < 28; i++)
		{
			a -= 0.01;
			d += 0.02;
			double[][] XY_data = buildData(a, b, c, d);
			BufferedImage bi = makeImage(XY_data, 500, 100);
			putImage_BI(bi, i + 56);
		}
		
		for (int i = 0; i < 28; i++)
		{
			b += 0.01;
			c -= 0.02;
			double[][] XY_data = buildData(a, b, c, d);
			BufferedImage bi = makeImage(XY_data, 500, 100);
			putImage_BI(bi, i + 84);
		}
		System.out.println("Done!");
		System.out.println("Took "  + (System.nanoTime() - start)/1e9 + " seconds to generate frames");
		
//		System.out.println("Making data...");
//		double[][] XY_Data = buildData(5); //building data using random weights up to 5 from 0.0 (can be hardcoded)
//		System.out.println("Took "  + (System.nanoTime() - start)/1e9 + " seconds to make data");
//		System.out.println("Making images...");
//		makeImages(XY_Data, 500, 100); //making 10,000 images in target dir
//		System.out.println("Done!");
//		System.out.println("Took "  + (System.nanoTime() - start)/1e9 + " seconds to make images");
	}
	
	//builds data for single set of constant params
	static double[][] buildData(int interval)
	{	
		double a, b, c, d;
		//generate random numbers on interval (-i, i) given by parameter
//		a = (Math.random() * 2 * interval) - interval;
//		b = (Math.random() * 2 * interval) - interval;
//		c = (Math.random() * 2 * interval) - interval;
//		d = (Math.random() * 2 * interval) - interval;	
		
		//hardcode params for specific shape
		a = -1.0038864261246183;
		b = -3.3105084021667794;
		c = 3.863141595810271;
		d = -1.2772692743618865;
		
		//show parameters in case the output is interesting
		System.out.println("a = " + a + ";");
		System.out.println("b = " + b + ";");
		System.out.println("c = " + c + ";");
		System.out.println("d = " + d + ";");
		
		
		double[] startPoint = {0.0, 0.0}; //x, y coordinate of start point for iteration
		double[][] points = new double[10000000][]; //ten million points will be stored
		
		for(int i = 0; i < points.length; i++) //build dataset. Looks like set 10 million x, y coords as arrays. 
		{
			double[] next = new double[2];
			if(i == 0) 
			{
				next[0] = Math.sin(a * startPoint[1]) + (c * Math.cos(a * startPoint[0]));
				next[1]	= Math.sin(b * startPoint[0]) + (d * Math.cos(b * startPoint[1]));
			}
			else 
			{
				next[0] = Math.sin(a * points[i-1][1]) + (c * Math.cos(a * points[i-1][0]));
				next[1]	= Math.sin(b * points[i-1][0]) + (d * Math.cos(b * points[i-1][1]));
			}
			points[i] = next;
		}
		return points;
	}
	
	//make x, y data using diffeq solutions, and four input paramters. 
	static double[][] buildData(double a, double b, double c, double d)
	{	
		double[] startPoint = {0.0, 0.0}; //x, y coordinate of start point for iteration
		double[][] points = new double[10000000][]; //ten million points will be stored
		
		for(int i = 0; i < points.length; i++) //build dataset. Looks like set 10 million x, y coords as arrays. 
		{
			double[] next = new double[2];
			if(i == 0) 
			{
				next[0] = Math.sin(a * startPoint[1]) + (c * Math.cos(a * startPoint[0]));
				next[1]	= Math.sin(b * startPoint[0]) + (d * Math.cos(b * startPoint[1]));
			}
			else 
			{
				next[0] = Math.sin(a * points[i-1][1]) + (c * Math.cos(a * points[i-1][0]));
				next[1]	= Math.sin(b * points[i-1][0]) + (d * Math.cos(b * points[i-1][1]));
			}
			points[i] = next;
		}
		return points;
	}	
	
	//processes raw x, y data into a buffered image for use by other methods 
	static BufferedImage makeImage(double[][] data, int size, int gamma) throws IOException
	{
		double x_lowest = 0, x_highest = 0, y_lowest = 0, y_highest = 0;
		int imgSize = size;
		int maxValue = gamma;
		int[][] count = new int[imgSize][imgSize]; //counting the number of "hits" on each pixel by data
		
		for(int i = 0; i < data.length; i++)
		{	
			//determine ranges in x and y directions
			if(data[i][0] < x_lowest) x_lowest = data[i][0];
			if(data[i][0] > x_highest) x_highest = data[i][0];
			if(data[i][1] < y_lowest) y_lowest = data[i][1];
			if(data[i][1] > y_highest) y_highest = data[i][1];
		}
		
		//based on ranges, take size to be the largest range
		double x_range = x_highest + Math.abs(x_lowest);
		double y_range = y_highest + Math.abs(y_lowest);
		
		for (int i = 0; i < imgSize; i++)
			Arrays.fill(count[i], 0);
		
		//calculating "hits" on each pixel
		for(int i = 0; i < data.length; i++)
		{
			int bound = imgSize - 1;
			int x = (int) Math.floor(((data[i][0] - x_lowest) / x_range) * bound);
			int y = (int) Math.floor(((data[i][1] - y_lowest) / y_range) * bound);		
			count[x][y]++;
		}
		
		//building raster
		BufferedImage bufferedImage = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = bufferedImage.getRaster();
		int[] bgColor = {255, 255, 255}; //white 
		
		//filling background of raster
		for(int i = 0; i < imgSize; i++)
		{
			for(int j = 0; j < imgSize; j++)
			{
				raster.setPixel(i, j, bgColor);
			}
		}
		
		//drawing path
		for(int i = 0; i < imgSize; i++) 
		{
			for(int j = 0; j < imgSize; j++) 
			{	
				int pixelValue = (255 - (int) (255 * count[i][j] / (float) maxValue));
				pixelValue = Math.max(0, Math.min(255, pixelValue));
				int[] pixelData = {pixelValue, pixelValue, pixelValue};
				raster.setPixel(i, j, pixelData);
			}
		}
		return bufferedImage;
	}
	
	//for placing the series of images to make a gif out of in the target directory
	static void putImage_BI(BufferedImage bi, int imgNum) throws IOException
	{
		String dirString = "D:\\eclipse\\strange_attractor\\images\\strange_attractor_" + imgNum + ".png";
		File outputFile = new File(dirString);
		
		//write image to file
		ImageIO.write(bi, "png", outputFile);
	}
}
