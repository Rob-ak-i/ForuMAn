package common;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import unit.DataTable;

public class FileIO {
	public static void openFile(String fileName, boolean waitForProcess) {
		/**examples:
		 * 		powershell Start-Process <browser> <URL>
		 * 		powershell Start-Process chrome http://google.com/
		 * 		powershell Start-Process http://google.com/
		 * 		powershell start http://google.com
		 * 
		 * LINUX:
		 * 		open ./index.html
		 * 		man open
		 * 		open -a "Google Chrome" index.html
		 * 		open -a "$(/usr/local/bin/DefaultApplication -url 'http:')" "/path/to/your/document.html"
		 * */
		
		
		//String homeDirectory = System.getProperty("user.home");
		Process process=null;int exitCode=0;
		boolean isWindows = System.getProperty("os.name").contains("Windows");
		try {
			if (isWindows) {
				process = Runtime.getRuntime().exec(String.format("cmd.exe start \"%s\"", fileName));
			    //process = Runtime.getRuntime().exec(String.format("cmd.exe /c dir %s", homeDirectory));
			} else {
				ProcessBuilder pb = new ProcessBuilder(
						new String[] {
								"bash",
								"-c",
								String.format("open \"%s\"",fileName)
						}
				);
				pb.redirectErrorStream(true);
				process=pb.start();
			}
			if(!waitForProcess)return;
			long time0=System.nanoTime();
			process.waitFor();
			BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while((line = reader.readLine()) != null)System.out.println(line);
			long time1=System.nanoTime();
			long dt=time1-time0;dt=dt/1000000l;
			System.out.println("process finished, alive time is "+Integer.toString((int)dt)+" ms");
			
		}catch(Exception e) {System.out.println(e);}
	}
	public static String readTextFile(String fileName) {
		File file = new File(fileName);try {if(!file.exists())file.createNewFile();} catch(java.io.IOException e) {throw new RuntimeException(e);}
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
	}
	public static void drawBitMap(String fileName, int[][] data)
	{
		int width=data.length;
		int height = data[0].length;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int i=0;i<width;++i) {
			for(int j=0;j<height;++j)
				img.setRGB(i, j, data[i][j]);
				//out.print(data[i][j]);
			//out.println();
		}
		try {
			ImageIO.write(img, "BMP", new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		img.flush();
		img=null;
	}
	public static void drawImage(String fileName, BufferedImage img)
	{
		try {
			ImageIO.write(img, "BMP", new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static BufferedImage readImage(String fileName)
	{
		try {
			return ImageIO.read(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
