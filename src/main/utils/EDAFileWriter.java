package main.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class EDAFileWriter {
	public static void writeToFile(String fileName, int index, byte[] content){
		assert content.length%4 == 0;
		assert index >= 0;
		File file = new File(fileName); // Creates the file if not exists
		//System.out.println("-----------CREATED FILE:"+fileName+"----------");
		try {
			RandomAccessFile RAF = new RandomAccessFile(file, "rw");
			RAF.seek(index);
			//if(index>800000){ System.out.println("-------WRITING AT INDEX:"+index); }
			RAF.write(content);
			RAF.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writePeaksToFile(String fileName, int peaksOffset, byte[] peaks){
		assert peaks.length%4 == 0;
		assert peaksOffset >= 0;
		int numOfPeaks = peaks.length/4;
		
//		String peakType;
//		if(fileName.contains("mx")){
//			peakType = "max";
//		}else{
//			peakType = "min";
//		}
		//System.out.println("-----------NUM OF PEAKS:"+numOfPeaks+"---------");
		File file = new File(fileName);
		try {
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter out = new BufferedWriter(fw);
			for(int i = 0; i < numOfPeaks; i++){
				byte[] b = Arrays.copyOfRange(peaks, 4*i, 4*i+4); 
				int globalIndex = Conversions.bytaToInt(b) + peaksOffset;
				//System.out.println("-----------WRITING "+peakType+" PEAK:"+globalIndex+"----------");
				out.append(Integer.toString(globalIndex));
				out.append(',');
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
