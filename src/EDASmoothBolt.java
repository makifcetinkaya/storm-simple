import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

import com.esotericsoftware.kryo.io.ByteBufferInputStream;

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
		 * Reads the EDA file, performs smoothing on EDA data, and emits it as double[]
		 * */
		public void execute(Tuple input) {
			String fileInfo = input.getString(0);
			String fileName = fileInfo.split(",")[0];
			
			efr = new EDAFileReader(fileName);
			efr.readFileIntoArray();
			double[] edaData = efr.getColumnData(0);
			
			CurveSmooth cs = new CurveSmooth(edaData);
			double[] smoothedEDA = cs.movingAverage(40);
			System.out.println("------------- EMITTING SMOOTHED VALS as BYTE ARRAY ---------------");
			
			// Convert data to byte array
			byte[] bArr = getByteArray(smoothedEDA);
			_collector.emit(new Values(fileInfo,bArr));
		}
		
		private byte[] getByteArray(double[] arr){
			float[] fArr = new float[arr.length];
			for(int i = 0; i<arr.length; i++){
				fArr[i] = (float) arr[i];
			}
			ByteBuffer buf = ByteBuffer.allocate(4*fArr.length);
			for(float f: fArr){
				buf.putFloat(f);
			}
			return buf.array();
		}
		
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("fileInfo","eda"));
		}
		
	}