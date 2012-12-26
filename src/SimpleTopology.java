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
		EDAFileSpout fs = new EDAFileSpout();
		builder.setSpout("filespout", fs, 1);
		EDASmoothBolt frb = new EDASmoothBolt();
		builder.setBolt("smoother", frb ,5).shuffleGrouping("filespout");
		PeakFinderBolt pfb = new PeakFinderBolt();
		builder.setBolt("peakfinder", pfb, 5).shuffleGrouping("smoother");
		EDACombineBolt ecb = new EDACombineBolt();
		builder.setBolt("combiner", ecb, 5).shuffleGrouping("peakfinder");
		
		Config conf = new Config();
		conf.setDebug(false);
		
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());
		Utils.sleep(10000);
		cluster.killTopology("test");
		cluster.shutdown();
		
	}

}
