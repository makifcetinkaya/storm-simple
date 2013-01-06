package main.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;

import main.spout.EDAChunkSpout;

import org.apache.commons.io.FileUtils;


public class EDAFileReader {
	private float[][] fileContent;
	private File file;
	
	private static final String S1 = "Start Time:";
	private static final String S2 = "Sampling Rate:";
	private static final String S3 = "Offset:";
	private static final String DATA_LINE_SEP = "------------";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss Z");
	private static final String DELIMITER = ",";
	private static final int HEADER_LENGTH = 9;
	
	public EDAFileReader(File file){
		this.file = file;
	}
	
	public float[] getColumnData(int c){
		int rows = fileContent.length;
		float[] colData = new float[rows];
		for(int i = 0; i < rows; i++){
			colData[i] = fileContent[i][c];
		}
		return colData;
	}
	
	public void readFileIntoArray(){
		FileReader fReader;
		BufferedReader bReader;
		LineNumberReader lnr;
		//String file = EDAFileSpout.EDA_FOLDER+"/"+filename;
		//System.out.println("---------READING FILE INTO ARRAY:"+file);
		try {
			
			fReader = new FileReader(file);			
			bReader = new BufferedReader(fReader);					
			lnr = new LineNumberReader(fReader);
			//System.out.println("------FOUND FILE:"+file);
			
			lnr.skip(Integer.MAX_VALUE);
			int rows = lnr.getLineNumber() - HEADER_LENGTH; 
			
			fileContent = new float[rows][6];
			lnr.close();
			
			//System.out.println("Number of rows in file:"+rows);
			FileReader fReader1 = new FileReader(file);
			bReader = new BufferedReader(fReader1);
			
			String line; 
			boolean dataLine = false;
			int dataRow = 0;
			//System.out.println("Reading file content "+bReader.readLine());
			
			while((line = bReader.readLine()) != null){
				if(line.startsWith(S1)){
					// get date time
					//System.out.println("time line");
				}else if(line.startsWith(S2)){
					// get sampling rate
					//System.out.println("sampling rate line");
				}else if(line.contains(DATA_LINE_SEP)){
					//System.out.println("--------------Data lines reached--------------");
					dataLine = true;
				}else if(dataLine){
					int i = 0;
					for(String s:line.split(DELIMITER)){
						fileContent[dataRow][i] = Float.parseFloat(s); 
						i++;
					}
					dataRow++;						
				}
			}
			try {
				bReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("------FILE NOT FOUND:"+file);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static int[] readEDAPeaksFile(File file){
		try {
			String content = FileUtils.readFileToString(file);
			String[] vals = content.split(",");
			int[] peaks = Conversions.strArrToIntArr(vals);
			return peaks;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static float[] readEDAByteFile(File file){	
		byte[] bArr;
		try {
			bArr = FileUtils.readFileToByteArray(file);
			float[] fArr = Conversions.toFloatArr(bArr);
			return fArr;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
