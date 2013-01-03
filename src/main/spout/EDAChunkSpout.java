package main.spout;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import main.utils.Conversions;
import main.utils.EDAFileReader;

import org.apache.thrift7.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.generated.NotAliveException;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.NimbusClient;

public class EDAChunkSpout extends BaseRichSpout{
		private SpoutOutputCollector _collector;
		private Map _conf;
		public static final String EDA_INP_DIR = "/home/slices/input/";
		private static final int CHUNK_SIZE = 2000;		
		private ArrayList<String> sentFiles = new ArrayList<String>();		
		Logger _logger = LoggerFactory.getLogger(EDAChunkSpout.class);
		
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			_collector = collector;
			_conf = conf;
			//_logger.info("CREATED EDA CHUNK SPOUT...");
			
		}

		public void nextTuple() {
			// TODO Auto-generated method stub
			String fileName = getUnprocessedFile(EDA_INP_DIR);	
			if(fileName != null){
				System.out.println("------------- READING FILE "+fileName+" ---------------");
				String part = fileName.split(".eda_part")[1];
				String chunkIndex = part.split("of")[0];				
				String metadata = fileName+","+CHUNK_SIZE+","+chunkIndex;
				File file = new File(EDA_INP_DIR+fileName);				
				EDAFileReader efr = new EDAFileReader(file);
				efr.readFileIntoArray();
				float[] fArr = efr.getColumnData(5);
				byte[] eda = Conversions.toBytaArr(fArr);
				_collector.emit(new Values(metadata, eda));
			}else{
				
				
				//System.out.println("FILESPOUT COULD NOT FIND UNPROCESSED EDA FILE");
				NimbusClient client = NimbusClient.getConfiguredClient(_conf);
				try {
					_logger.info("NO UNPROCESSED EDA CHUNK, killing topology...");
					client.getClient().killTopology("simple");
				} catch (NotAliveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//client.close();
//				try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			
		}
		
		private String getUnprocessedFile(String dir){
			File folder = new File(dir);
			if(folder.list() == null){
				_logger.info("-------Problem reading files in input dir...");
			}
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