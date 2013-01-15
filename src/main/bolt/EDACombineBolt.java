package main.bolt;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import main.utils.EDAFileWriter;

import org.apache.log4j.Logger;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;


public class EDACombineBolt extends BaseRichBolt {

	public static final String EDA_OUT_DIR = "/home/slices/output/";
	private OutputCollector _collector;
	private File _logFile;
	private FileWriter _fileWriter;
	public static Logger _logger = Logger.getLogger(EDACombineBolt.class);
	// private EDAFileWriter edaFileWriter;
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// TODO Auto-generated method stub
		_collector = collector;
		_logFile = new File(EDA_OUT_DIR+"log-combine");
		try {
			_fileWriter = new FileWriter(_logFile, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		//_logger.info("====WRITING CHUNK INDEX: "+chunkIndex+" AT TIME:"+System.currentTimeMillis());
		
		byte[] content = input.getBinary(1);		
		int byteIndex = chunkIndex*chunkSize*4;
		int dataIndex = chunkIndex*chunkSize;
		
		//String filename = origFileName;
		
		EDAFileWriter.writeToFile(EDA_OUT_DIR+fileName, content);
		
		//String peakFile = fileName.split(".eda")[0];
		byte[] maxPeaks = input.getBinary(2);
		EDAFileWriter.writePeaksToFile(EDA_OUT_DIR+origFileName+"-mxpeaks", dataIndex, maxPeaks);
		byte[] minPeaks = input.getBinary(3);
		EDAFileWriter.writePeaksToFile(EDA_OUT_DIR+origFileName+"-mnpeaks", dataIndex, minPeaks);		
		//_logger.info("=====EXECUTION END TIME at COMBINE BOLT:"+System.currentTimeMillis());
		log("CHUNK INDEX: "+chunkIndex+" AT TIME:"+System.currentTimeMillis());

	}
	private  void log(String msg){
		try {
			_fileWriter.append(msg+'\n');
			_fileWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

}
