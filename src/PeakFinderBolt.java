import java.util.ArrayList;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;


public class PeakFinderBolt extends BaseRichBolt{

	private OutputCollector _collector;
	private PeakDetector peakDetector;
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// TODO Auto-generated method stub
		_collector = collector;
	}

	/*
	 * Takes smoothed double array as input, and find peaks on the values
	 * */
	public void execute(Tuple input) {
		// TODO create peak detector according to sampling rate
		double[] edaData = (double[]) input.getValue(0);
		peakDetector = new PeakDetector(8, 1); 
		ArrayList<Integer> peakIndices = peakDetector.detectPeaks(edaData);

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

}
