import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;


public class EDACombineBolt extends BaseRichBolt {

	public static final File EDA_OUT_DIR = new File("/home/affective/Downloads/slices");
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
		int chunkSize = Integer.parseInt(fileInfo[1]);	
		int chunkIndex = Integer.parseInt(fileInfo[2]);
		
		byte[] content = input.getBinary(1);
		byte[] peaks = input.getBinary(2);
		
		int index = chunkIndex*chunkSize - 1;
		Utils.writeToFile(EDA_OUT_DIR+fileName, index, content);
	}
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

}
