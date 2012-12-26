package main.plot;
import java.io.File;

import main.utils.EDAFileReader;
import main.utils.Conversions;


public class EDAPlotFile {

	private boolean isByteFile;
	private double[][] data;
	private File file;
	public EDAPlotFile(File file, boolean isByteFile){
		this.file = file;
		this.isByteFile = isByteFile;
	}
	public static double[][] create2DData(double[] data){
		double[][] res = new double[2][data.length];
		for(int i = 0 ; i < data.length; i ++){
			res[1][i] =  data[i];
			res[0][i] = i;
		}
		return res;
	}
	public void readDataIntoArr(){
		double[] eda;
		if(isByteFile){
			float[] f = EDAFileReader.readEDAByteFile(file);
			eda = Conversions.toDoubla(f);			
		}else{
			EDAFileReader efr = new EDAFileReader(file);
			efr.readFileIntoArray();
			eda = efr.getColumnData(5);			
		}
		this.data = create2DData(eda);
	}
	
	public void plotData(){
		ChartPlot cp = new ChartPlot("Title", data);
		ChartPlot.PlotThread pt = new ChartPlot.PlotThread(cp);
		pt.start();
	}
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		String filename = "/home/affective/Downloads/log.eda";
//		EDAFileReader efr = new EDAFileReader(filename);
//		efr.readFileIntoArray();
//		double[] eda = efr.getColumnData(5);
//		double[][] data = create2DData(eda);
//		ChartPlot cp = new ChartPlot("Title", data);
//		ChartPlot.PlotThread pt = new ChartPlot.PlotThread(cp);
//		pt.start();
//	}

}
