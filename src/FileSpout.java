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
		
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			_collector = collector;
		}

		public void nextTuple() {
			// TODO Auto-generated method stub
			Utils.sleep(100);
			String filename = getUnprocessedFile(EDA_FOLDER);
			_collector.emit(new Values(filename));
			
		}
		
		private String getUnprocessedFile(File folder){
			for(String file:folder.list()){
				if (! file.matches(".eda_part")){
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