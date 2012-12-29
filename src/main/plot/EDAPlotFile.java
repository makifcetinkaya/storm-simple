package main.plot;
import java.io.File;

import main.utils.EDAFileReader;


public class EDAPlotFile {

	private boolean isByteFile;
	private double[][] data;
	private double[][] mxPeaks;
	private double[][] mnPeaks;
	private File file;
	public EDAPlotFile(File file, boolean isByteFile){
		this.file = file;
		this.isByteFile = isByteFile;
	}
	
	public void readDataIntoArr(){
		float[] eda;
		if(isByteFile){
			eda = EDAFileReader.readEDAByteFile(file);	
		}else{
			EDAFileReader efr = new EDAFileReader(file);
			efr.readFileIntoArray();
			eda = efr.getColumnData(5);			
		}
		this.data = create2DData(eda);
	}
	
	public static double[][] create2DData(float[] data){
		double[][] res = new double[2][data.length];
		for(int i = 0 ; i < data.length; i ++){
			res[1][i] =  data[i];
			res[0][i] = i;
		}
		return res;
	}
	
	public void readDataAndPeaksIntoArr(){
		readDataIntoArr();
		File mxPeaksFile = new File(file.getPath()+"-mxpeaks");
		File mnPeaksFile = new File(file.getPath()+"-mnpeaks");
		int[] mx= EDAFileReader.readEDAPeaksFile(mxPeaksFile);
		int[] mn = EDAFileReader.readEDAPeaksFile(mnPeaksFile);
		mxPeaks = createPeakSeries(mx);
		mnPeaks = createPeakSeries(mn);
	}
	
	public void plotData(){
		ChartPlot cp = new ChartPlot(file.getPath(), data);
		ChartPlot.PlotThread pt = new ChartPlot.PlotThread(cp);
		pt.start();
	}
	
	public void plotDataWithPeaks(){
		
		ChartPlot cp = new ChartPlot(file.getPath(), data, mxPeaks, mnPeaks);
		ChartPlot.PlotThread pt = new ChartPlot.PlotThread(cp);
		pt.start();
		
	}
	
//	public int getDataLength(){
//		return data[0].length;
//	}
	private double[][] createPeakSeries(int[] peaks){
    	double[][] pSeries = new double[2][peaks.length];
    	for(int i = 0; i < peaks.length; i++){
    		int pIndex = peaks[i];
    		pSeries[0][i] = pIndex;
    		pSeries[1][i] = data[1][pIndex];
    	}
    	return pSeries;
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
