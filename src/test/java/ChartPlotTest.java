import main.plot.ChartPlot;


public class ChartPlotTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[][] data = {{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15.3,16,17,18},{2,4,5,6,7,8,2,4,5,6,7,8,2,4,5,6,7.6,8.5}};
		ChartPlot cp = new ChartPlot("Title", data);
		ChartPlot.PlotThread pt = new ChartPlot.PlotThread(cp);
		pt.start();
	}

}
