import main.bolt.EDACombineBolt;
import main.bolt.EDAPeakFinderBolt;
import main.bolt.EDASmoothBolt;
import main.spout.EDAChunkSpout;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;


public class SimpleTopology {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int NUM_OF_WORKERS = Integer.parseInt(args[0]);
		int PARALLELLISM_HINT = Integer.parseInt(args[1]);
		int MAX_SPOUT_PENDING = Integer.parseInt(args[2]);
		
		TopologyBuilder builder = new TopologyBuilder();
		EDAChunkSpout fs = new EDAChunkSpout();
		builder.setSpout("filespout", fs, PARALLELLISM_HINT);
		EDASmoothBolt esb = new EDASmoothBolt();
		builder.setBolt("smoother", esb ,PARALLELLISM_HINT*4).shuffleGrouping("filespout");
		EDAPeakFinderBolt epfb = new EDAPeakFinderBolt();
		builder.setBolt("peakfinder", epfb, PARALLELLISM_HINT*4).shuffleGrouping("smoother");
		EDACombineBolt ecb = new EDACombineBolt();
		builder.setBolt("combiner", ecb, PARALLELLISM_HINT*2).shuffleGrouping("peakfinder");
		
		StormTopology topology = builder.createTopology();
		Config conf = new Config();
		conf.setNumWorkers(NUM_OF_WORKERS);
		conf.setMaxSpoutPending(MAX_SPOUT_PENDING);
		conf.setDebug(true);
		try {
			StormSubmitter.submitTopology("simple", conf, topology);
		} catch (AlreadyAliveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTopologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		
//		LocalCluster cluster = new LocalCluster();
//		cluster.submitTopology("test", conf, topology);
//		Utils.sleep(30000);
//		cluster.killTopology("test");
//		cluster.shutdown();
		
	}

}
