import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;

import backtype.storm.utils.Utils;


public class EDAFileReader {
	private String filename;
	private double[][] fileContent;
	
	private static final String S1 = "Start Time:";
	private static final String S2 = "Sampling Rate:";
	private static final String S3 = "Offset:";
	private static final String DATA_LINE_SEP = "------------";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss Z");
	private static final String DELIMITER = ",";
	private static final int HEADER_LENGTH = 10;
	
	public EDAFileReader(String filename){
		this.filename = filename;
	}
	
	public double[] getColumnData(int c){
		int rows = fileContent.length;
		double[] colData = new double[rows];
		for(int i = 0; i < rows; i++){
			colData[i] = fileContent[i][c];
		}
		return colData;
	}
	
	public void readFileIntoArray(){
		String file = FileSpout.EDA_FOLDER+"/"+filename;
		System.out.println("READING FILE INTO ARRAY:"+FileSpout.EDA_FOLDER+"/"+filename);
		try {
			
			FileReader fReader = new FileReader(file);
			
			BufferedReader bReader = new BufferedReader(fReader);
			while(!bReader.ready()){
				System.out.println("not ready");
				Utils.sleep(50);
			}
			
			LineNumberReader lnr = new LineNumberReader(fReader);
			lnr.skip(Integer.MAX_VALUE);
			int rows = lnr.getLineNumber() - HEADER_LENGTH; 
			fileContent = new double[rows][6];
			lnr.close();
			
			System.out.println("Number of rows in file:"+rows);
			FileReader fReader1 = new FileReader(file);
			bReader = new BufferedReader(fReader1);
			
			String line; 
			boolean dataLine = false;
			int dataRow = 0;
			//System.out.println("Reading file content "+bReader.readLine());
			
			while((line = bReader.readLine()) != null){
				if(line.startsWith(S1)){
					// get date time
					System.out.println("time line");
				}else if(line.startsWith(S2)){
					// get sampling rate
					System.out.println("sampling rate line");
				}else if(line.matches(DATA_LINE_SEP)){
					System.out.println("Data lines reached");
					dataLine = true;
				}else if(dataLine){
					int i = 0;
					for(String s:line.split(DELIMITER)){
						fileContent[dataRow][i] = Double.parseDouble(s); 
						i++;
					}
					dataRow++;						
				}
			}
			
			System.out.println("FILE "+FileSpout.EDA_FOLDER+"/"+filename+" READ INTO ARRAY...");
			System.out.println("ARRAY SIZE is: "+fileContent.length);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("could not find file:"+filename);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
