import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.utils.Utils;
import flanagan.analysis.CurveSmooth;

public class FileReaderBolt extends BaseRichBolt{
		private OutputCollector _collector;
		private static final String S1 = "Start Time:";
		private static final String S2 = "Sampling Rate:";
		private static final String S3 = "Offset:";
		private static final String DATA_LINE_SEP = "------------";
		private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss Z");
		private static final String DELIMITER = ",";
		private static final int HEADER_LENGTH = 10;
		
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			_collector = collector;
		}

		public void execute(Tuple input) {
			String file = input.getString(0);
			double[][] fileContent = getData(FileSpout.EDA_FOLDER+"/"+file);
			double[] edaData = getColumnData(fileContent, 0);
			
			CurveSmooth cs = new CurveSmooth(edaData);
			double[] smoothedEDA = cs.movingAverage(40);
			System.out.println("Length of smoothed eda is: "+smoothedEDA.length);
			cs.movingAveragePlot(40);		
		}
		
		private double[] getColumnData(double[][] fileContent, int c){
			int rows = fileContent.length;
			double[] colData = new double[rows];
			for(int i = 0; i < rows; i++){
				colData[i] = fileContent[i][c];
			}
			return colData;
		}
		private double[][] getData(String file){
			FileReader fReader;
			LineNumberReader lnr;
			BufferedReader bReader;
			int rows;
			double[][] fileContent;
			try {
				
				fReader = new FileReader(file);
				
				bReader = new BufferedReader(fReader);
				while(!bReader.ready()){
					System.out.println("not ready");
					Utils.sleep(50);
				}
				
				lnr = new LineNumberReader(fReader);
				lnr.skip(Integer.MAX_VALUE);
				rows = lnr.getLineNumber() - HEADER_LENGTH; 
				fileContent = new double[rows][6];
				lnr.close();
				
				System.out.println("Number of rows in file:"+rows);
				FileReader fReader1 = new FileReader(file);
				bReader = new BufferedReader(fReader1);
				
				String line; 
				boolean dataLine = false;
				int dataRow = 0;
				
				System.out.println("Reading file content"+bReader.readLine());
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
				return fileContent;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("could not find file:"+file);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			//declarer.declare(new Fields("file"));
		}
		
	}