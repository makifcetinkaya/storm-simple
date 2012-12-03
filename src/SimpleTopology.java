import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import flanagan.analysis.CurveSmooth;


public class SimpleTopology {

	public static class FileReaderBolt extends BaseRichBolt{
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
			double[][] fileContent = getData(file);
			double[] edaData = getColumnData(fileContent, 0);
			
			CurveSmooth cs = new CurveSmooth(edaData);
			double[] smoothedEDA = cs.getSmoothedValues();			
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
				lnr = new LineNumberReader(fReader);
				lnr.skip(Integer.MAX_VALUE);
				rows = lnr.getLineNumber() - HEADER_LENGTH; 
				fileContent = new double[rows][6];
				lnr.close();
				
				bReader = new BufferedReader(fReader);
				String line; 
				boolean dataLine = false;
				int dataRow = 0;
							
				while((line = bReader.readLine()) != null){
					if(line.startsWith(S1)){
						// get date time
					}else if(line.startsWith(S2)){
						// get sampling rate
					}else if(line.startsWith(DATA_LINE_SEP)){
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
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class FileSpout extends BaseRichSpout{
		private SpoutOutputCollector _collector;
		private final File folder = new File("");
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			_collector = collector;
		}

		public void nextTuple() {
			// TODO Auto-generated method stub
			Utils.sleep(100);
			String filename = getUnprocessedFile(folder);
			_collector.emit(new Values(filename));
			
		}
		
		private String getUnprocessedFile(File folder){
			for(String file:folder.list()){
				if(! file.endsWith("PRO")){
					return file;
				}
			}
			return null;
		}
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			
		}

		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("filespout", new FileSpout(),2);
		builder.setBolt("filereader", new FileReaderBolt(),2).shuffleGrouping("filespout");
	}

}
