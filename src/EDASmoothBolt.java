import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import flanagan.analysis.CurveSmooth;

public class EDASmoothBolt extends BaseRichBolt{
		private OutputCollector _collector;
		private EDAFileReader efr;
		
		public EDASmoothBolt(){
			System.out.println("New FileReaderBolt is created");
		}
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			_collector = collector;
		}

		/*
		 * Reads the EDA file, performs smoothing on EDA data, and emits it as byte[]
		 * */
		public void execute(Tuple input) {
			String metadata = input.getString(0);
			String fileName = metadata.split(",")[0];
						
			efr = new EDAFileReader(fileName);
			efr.readFileIntoArray();
			double[] edaData = efr.getColumnData(0);
			
			CurveSmooth cs = new CurveSmooth(edaData);
			double[] smoothedEDA = cs.movingAverage(40);
			
			byte[] bArr = Utils.toBytaArr(smoothedEDA);
			_collector.emit(new Values(metadata, bArr));
		}

		
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("metadata","eda"));
		}
		
	}