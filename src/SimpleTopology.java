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
		FileSpout fs = new FileSpout();
		builder.setSpout("filespout", fs, 2);
		EDASmoothBolt frb = new EDASmoothBolt();
		builder.setBolt("filereader", frb ,2).shuffleGrouping("filespout");
		PeakFinderBolt pfb = new PeakFinderBolt();
		builder.setBolt("peakfinder", pfb, 2).shuffleGrouping("filereader");
		
		Config conf = new Config();
		conf.setDebug(true);
		
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());
		Utils.sleep(10000);
		cluster.killTopology("test");
		cluster.shutdown();
		
	}

}
