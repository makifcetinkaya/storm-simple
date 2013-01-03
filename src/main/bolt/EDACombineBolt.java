package main.bolt;



import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.utils.EDAFileWriter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;


public class EDACombineBolt extends BaseRichBolt {

	public static final String EDA_OUT_DIR = "/home/slices/output/";
	private OutputCollector _collector;
	Logger _logger = LoggerFactory.getLogger(EDACombineBolt.class);
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
		_logger.info("---------- WRITING CHUNK INDEX: "+chunkIndex+"-----------");
		
		byte[] content = input.getBinary(1);		
		int byteIndex = chunkIndex*chunkSize*4;
		int dataIndex = chunkIndex*chunkSize;
		
		//String filename = origFileName;
		
		EDAFileWriter.writeToFile(EDA_OUT_DIR+fileName, byteIndex, content);
		
		//String peakFile = fileName.split(".eda")[0];
		byte[] maxPeaks = input.getBinary(2);
		EDAFileWriter.writePeaksToFile(EDA_OUT_DIR+origFileName+"-mxpeaks", dataIndex, maxPeaks);
		byte[] minPeaks = input.getBinary(3);
		EDAFileWriter.writePeaksToFile(EDA_OUT_DIR+origFileName+"-mnpeaks", dataIndex, minPeaks);		
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

}
