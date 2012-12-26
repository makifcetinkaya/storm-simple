package main.bolt;



import java.util.Map;

import main.utils.Conversions;
import main.utils.EDAFileWriter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;


public class EDACombineBolt extends BaseRichBolt {

	//public static final String EDA_OUT_DIR = "/home/affective/Downloads/slices/";
	private OutputCollector _collector;
	// private EDAFileWriter edaFileWriter;
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// TODO Auto-generated method stub
		_collector = collector;
		//edaFileWriter = new EDAFileWriter();
	}

	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		String metadata = input.getString(0);
		String[] fileInfo = metadata.split(",");
		
		String fileName = fileInfo[0];
		String origFileName = fileName.split("_part")[0];
		int chunkSize = Integer.parseInt(fileInfo[1]);	
		int chunkIndex = Integer.parseInt(fileInfo[2]);
		System.out.println("---------- WRITING CHUNK INDEX: "+chunkIndex+"-----------");
		
		byte[] content = input.getBinary(1);		
		int index = chunkIndex*chunkSize;
		
		String filename = origFileName;
		EDAFileWriter.writeToFile(filename, index, content);
		
		byte[] maxPeaks = input.getBinary(2);
		EDAFileWriter.writePeaksToFile(filename+"-mxpeaks", index, maxPeaks);
		byte[] minPeaks = input.getBinary(3);
		EDAFileWriter.writePeaksToFile(filename+"-mnpeaks", index, minPeaks);		
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

}
