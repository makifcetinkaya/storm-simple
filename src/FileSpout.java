import java.io.File;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class FileSpout extends BaseRichSpout{
		private SpoutOutputCollector _collector;
		public static final File EDA_FOLDER = new File("/home/affective/Downloads");
		
		public FileSpout(){
			System.out.println("New FileSpout is created");
		}
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			_collector = collector;
		}

		public void nextTuple() {
			// TODO Auto-generated method stub
			Utils.sleep(1000);
			String filename = getUnprocessedFile(EDA_FOLDER);
			if(filename != null){
				System.out.println("------------- EMITTING THE FILE "+filename+" ---------------");
				_collector.emit(new Values(filename));
			}else{
				System.out.println("FILESPOUT COULD NOT FIND UNPROCESSED EDA FILE");
			}
			
		}
		
		private String getUnprocessedFile(File folder){
			for(String file:folder.list()){
				if (file.contains("eda_part")){
					return file;
				}
//				if(! file.endsWith("PRO")){
//					return file;
//				}
			}
			return null;
		}
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("file"));
		}

		
	}