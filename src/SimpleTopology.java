import main.bolt.EDACombineBolt;
import main.bolt.EDASmoothBolt;
import main.bolt.EDAPeakFinderBolt;
import main.spout.EDAChunkSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;


public class SimpleTopology {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TopologyBuilder builder = new TopologyBuilder();
		EDAChunkSpout fs = new EDAChunkSpout();
		builder.setSpout("filespout", fs, 1);
		EDASmoothBolt esb = new EDASmoothBolt();
		builder.setBolt("smoother", esb ,5).shuffleGrouping("filespout");
		EDAPeakFinderBolt epfb = new EDAPeakFinderBolt();
		builder.setBolt("peakfinder", epfb, 5).shuffleGrouping("smoother");
		EDACombineBolt ecb = new EDACombineBolt();
		builder.setBolt("combiner", ecb, 5).shuffleGrouping("peakfinder");
		
		Config conf = new Config();
		conf.setDebug(false);
		
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());
		Utils.sleep(12000);
		cluster.killTopology("test");
		cluster.shutdown();
		
	}

}
