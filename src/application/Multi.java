package application;
	
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.application.Application;
import javafx.stage.Stage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;


public class Multi extends Application implements Runnable {
	
	Tesseract ts;
	
	int start_index , end_index;

	public static int NUM_THREADS = 50;
	public static ArrayList<Integer> ThreadTime = new ArrayList<Integer>();
	
	public Multi() {
		
	}
	
	public Multi(int start, int end){
		this.start_index = start;
		this.end_index = end;
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		//Defining the x axis             
	      NumberAxis xAxis = new NumberAxis(0, NUM_THREADS + 3, 1); 
	      xAxis.setLabel("No. of Threads"); 
	        
	      //Defining the y axis   
	      NumberAxis yAxis = new NumberAxis   (0, 800, 25); 
	      yAxis.setLabel("Time in seconds"); 
	        
	      //Creating the line chart 
	      LineChart linechart = new LineChart(xAxis, yAxis);  
	        
	      //Prepare XYChart.Series objects by setting data 
	      XYChart.Series series = new XYChart.Series(); 
	      series.setName("Time taken with respect to threads"); 
	        
	      
	      for(int i=0; i<ThreadTime.size(); i++) {
		      series.getData().add(new XYChart.Data(i+1, ThreadTime.get(i)));
			} 
	            
	      //Setting the data to Line chart    
	      linechart.getData().add(series);        
	        
	      //Creating a Group object  
	      Group root = new Group(linechart); 
	         
	      //Creating a scene object 
	      Scene scene = new Scene(root, 1000, 600);  
	      
	      //Setting title to the Stage 
	      primaryStage.setTitle("Line Chart"); 
	         
	      //Adding scene to the stage 
	      primaryStage.setScene(scene);
		   
	      //Displaying the contents of the stage 
	      primaryStage.show();         
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		int n;
		System.out.println("Application Started");
		for(int j=1; j<=NUM_THREADS; j++) {
			n = j;
			int base = 0;
			int end = 800/n;
			int interval = 800/n;
			long startTime = System.nanoTime();
			Thread [] t = new Thread[n];
			for(int i=0; i<n; i++){
				t[i] = new Thread(new Multi(base, end));
				t[i].start();
				base = end;
				end += interval;
			}
			for(int i=0; i<n; i++) {
				t[i].join();
			}
			long endTime = System.nanoTime();
			Integer threadTime = (int)(((endTime-startTime)/1000000000));
			ThreadTime.add(threadTime);
			System.out.println("Number of threads is "+j);			
			System.out.println("Time taken in seconds is "+ threadTime);
		}
		launch(args);
	}

	@Override
	public void run() {
		ts = new Tesseract();
		ts.setDatapath("");
		ts.setLanguage("eng");
		try {
			 File dir = new File("imagess");
			  File[] directoryListing = dir.listFiles();
			  if (directoryListing != null) {
			    for (int i=start_index; i<end_index; i++) {
			    	File child = directoryListing[i];
			    	String path = child.getAbsolutePath();
			    	//read image
					Mat mat=Imgcodecs.imread(path);
					
					//change image to gray scale
					Mat gray = new Mat();
					Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
					
					//resize image
					Mat resized = new Mat();
					Size size = new Size(mat.width() * 1.9f, mat.height() * 1.9f);
					Imgproc.resize(gray, resized, size);
					
					//convert to buffered image
					MatOfByte mof = new MatOfByte();
					byte imageByte[];
					Imgcodecs.imencode(".png", resized, mof);
					imageByte = mof.toArray();
					BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageByte));
			    	String text = ts.doOCR(img);
					System.out.println(text + "\n" + child.getAbsolutePath()+ "\n\n\n");
			    }
			  } else {
				  System.out.println("Directory not recognized");
			  }
		
		}
		catch(TesseractException e){
		} catch (IOException e) {
		}
		
	}
}
