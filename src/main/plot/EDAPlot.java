package main.plot;
import java.io.File;



public class EDAPlot {
	public static void main(String[] args){
		File origFile = new File( "/home/affective/Downloads/log.eda");
		File byteFile = new File("/home/affective/Downloads/slices/LOG01_00H3_2011_11_15.eda");
		
		EDAPlotFile epf = new EDAPlotFile(origFile, false);
		epf.readDataIntoArr();
		epf.plotData();
		
		epf = new EDAPlotFile(byteFile, true);
		epf.readDataIntoArr();
		epf.plotData();
	}
}
