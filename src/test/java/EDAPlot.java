import java.io.File;

import main.plot.EDAPlotFile;


public class EDAPlot {
	public static void main(String[] args){
		File origFile = new File( "/home/affective/Downloads/slices2/LOG01_00H3_2011_11_15.eda_part0of9");
		File byteFile = new File("/home/affective/Downloads/slices/LOG01_00H3_2011_11_15.eda");
		
		EDAPlotFile epf = new EDAPlotFile(origFile, false);
		epf.readDataIntoArr();
		epf.plotData();
		
		epf = new EDAPlotFile(byteFile, true);
		epf.readDataIntoArr();
		epf.plotData();
	}
}
