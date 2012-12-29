package main.spout;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import main.utils.Conversions;
import main.utils.EDAFileReader;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class EDAChunkSpout extends BaseRichSpout{
		private SpoutOutputCollector _collector;
		public static final File EDA_FOLDER = new File("/home/affective/Downloads/slices");
		private static final int CHUNK_SIZE = 2000;
		
		private ArrayList<String> sentFiles = new ArrayList<String>();
		
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			_collector = collector;
		}

		public void nextTuple() {
			// TODO Auto-generated method stub
			String fileName = getUnprocessedFile(EDA_FOLDER);	
			if(fileName != null){
				//System.out.println("------------- EMITTING THE FILE "+filename+" ---------------");
				String part = fileName.split(".eda_part")[1];
				String chunkIndex = part.split("of")[0];				
				String metadata = EDA_FOLDER+"/"+fileName+","+CHUNK_SIZE+","+chunkIndex;
				File file = new File(EDA_FOLDER+"/"+fileName);				
				EDAFileReader efr = new EDAFileReader(file);
				efr.readFileIntoArray();
				float[] fArr = efr.getColumnData(5);
				byte[] eda = Conversions.toBytaArr(fArr);
				_collector.emit(new Values(metadata, eda));
			}else{
				System.out.println("FILESPOUT COULD NOT FIND UNPROCESSED EDA FILE");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		private String getUnprocessedFile(File folder){
			for(String file:folder.list()){
				if (file.contains("eda_part") && ! sentFiles.contains(file)){
					sentFiles.add(file);
					return file;
				}
			}
			return null;
		}
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("metadata", "eda"));
		}

		
	}