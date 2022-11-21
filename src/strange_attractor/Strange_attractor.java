package strange_attractor;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Strange_attractor 
{
	public static void main(String[] args) throws IOException
	{
		long start = System.nanoTime();
		System.out.println("Making data...");
		double[][] XY_Data = buildData(5);
		System.out.println("Making image...");
		makeImage(XY_Data, 1000, 30);
		System.out.println("Done!");
		System.out.println("Took "  + (System.nanoTime() - start)/1e9 + " seconds to generate image");
	}
	
	//note: data is raw function output. No transformation has been done to it.
	public static double[][] buildData(int interval)
	{
		//four params for strange attractor 
		double a, b, c, d;
		
		//generate random numbers on interval (-i, i) given by parameter
		a = (Math.random() * 2 * interval) - interval;
		b = (Math.random() * 2 * interval) - interval;
		c = (Math.random() * 2 * interval) - interval;
		d = (Math.random() * 2 * interval) - interval;	
		
//		a = -1.0038864261246183;
//		b = -3.3105084021667794;
//		c = 3.863141595810271;
//		d = -1.2772692743618865;
		
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
	
	public static void makeImage(double[][] data, int imgSize, int gamma) throws IOException
	{
		double x_lowest = 0, x_highest = 0, y_lowest = 0, y_highest = 0;
		int maxValue = gamma;
		int[][] count = new int[imgSize][imgSize]; //counting the number of hits on each pixel by data
		String dirString = "D:\\eclipse\\strange_attractor\\images\\strange_attractor.png";
		File outputFile = new File(dirString);
		
		for(int i = 0; i < data.length; i++)
		{	
			//find extrema on each axis
			if(data[i][0] < x_lowest) x_lowest = data[i][0];
			if(data[i][0] > x_highest) x_highest = data[i][0];
			if(data[i][1] < y_lowest) y_lowest = data[i][1];
			if(data[i][1] > y_highest) y_highest = data[i][1];
		}
		
		//determine ranges for drawing image 
		double x_range = x_highest - x_lowest;
		double y_range = y_highest - y_lowest;
		
		for (int i = 0; i < imgSize; i++)
			Arrays.fill(count[i], 0);
		
		//calculating hits on each pixel
		for(int i = 0; i < data.length; i++)
		{
			int x = (int) Math.floor((((data[i][0] - x_lowest) / x_range) * (imgSize - 0.0001)));
			int y = (int) Math.floor((((data[i][1] - y_lowest) / y_range) * (imgSize - 0.0001)));		
			
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
		
		//write image to file
		ImageIO.write(bufferedImage, "png", outputFile);
	}
}