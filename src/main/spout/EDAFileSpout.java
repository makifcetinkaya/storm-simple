package main.spout;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class EDAFileSpout extends BaseRichSpout{
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
			Utils.sleep(5);
			String filename = getUnprocessedFile(EDA_FOLDER);	
			if(filename != null){
				//System.out.println("------------- EMITTING THE FILE "+filename+" ---------------");
				String part = filename.split(".eda_part")[1];
				String chunkIndex = part.split("of")[0];				
				String fileInfo = EDA_FOLDER+"/"+filename+","+CHUNK_SIZE+","+chunkIndex;
				_collector.emit(new Values(fileInfo));
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
			declarer.declare(new Fields("fileInfo"));
		}

		
	}