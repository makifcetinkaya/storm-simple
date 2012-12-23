import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
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
	 * Takes a float of byte array as input, and find peaks on the values
	 * */
	public void execute(Tuple input) {
		// TODO create peak detector according to sampling rate
		String metadata = input.getString(0);
		byte[] data = input.getBinary(1);
		float[] fArr = Utils.toFloatArr(data);
		
//		peakDetector = new PeakDetector(200, 3); 
//		ArrayList<Integer> peakIndices = peakDetector.detectPeaks(edaArray);
//		System.out.println("------PEAK INDICES ARE: "+peakIndices.toString());
//		
//		_collector.emit(new Values(metadata, bArr));
	}
	
//	private double[] getEDAArray(String edaString){
//		String[] strVals = edaString.substring(1, edaString.length()-1).split(",");
//		double[] edaArray = new double[strVals.length];
//		for(int i=0; i<strVals.length; i++){
//			edaArray[i] = Double.parseDouble(strVals[i]);
//			i++;
//		}
//		return edaArray;
//	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("metadata","eda"));
	}

}
