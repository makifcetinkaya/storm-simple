package main.plot;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.text.NumberFormat;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ShapeUtilities;

public class ChartPlot extends JFrame {
    
//	private double[][] data;
//	private double[][] mxSeries;
//	private double[][] mnSeries;

	private final BasicStroke stroke = new BasicStroke(.1f);
	//private final BasicStroke peakStroke = new BasicStroke(3f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
	private final Shape edaShape = ShapeUtilities.createDiagonalCross(.01f, .01f);
	private final Shape peakShape = ShapeUtilities.createDiamond(2f);
	/**
     * Construct a new frame 
     *
     * @param title the frame title
     */
    public ChartPlot(String title, double[][] data) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.data = data;
        final DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries("data", data);
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(1000, 480));
        this.add(chartPanel, BorderLayout.CENTER);
    }
    
    public ChartPlot(String title, double[][] data, double[][] data1, double[][] data2) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.data = data;
        final DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries("data", data);
        dataset.addSeries("data1", data1);
        dataset.addSeries("data2", data2);
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(2000, 480));
        this.add(chartPanel, BorderLayout.CENTER);
    }

    

    /**
     * Create a chart.
     *
     * @param dataset the dataset
     * @return the chart
     */
    private JFreeChart createChart(XYDataset dataset) {

        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Serial Data", // chart title
            "Domain", // domain axis label
            "Range", // range axis label
            dataset,  // initial series
            PlotOrientation.VERTICAL, // orientation
            true, // include legend
            true, // tooltips?
            false // URLs?
            );

        // set chart background
        chart.setBackgroundPaint(Color.white);

        // set a few custom plot features
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        // set the plot's axes to display integers
        TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
        NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        domain.setStandardTickUnits(ticks);
        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setStandardTickUnits(ticks);

        // render shapes and lines
        XYLineAndShapeRenderer renderer =
            new XYLineAndShapeRenderer(false, true);
        plot.setRenderer(renderer);
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);

        // set the renderer's stroke
        renderer.setBaseOutlineStroke(stroke);

        // Set data point size
        
        
        plot = (XYPlot) chart.getPlot();
        renderer.setSeriesShape(0, edaShape);
        renderer.setSeriesShape(1, peakShape);
        renderer.setSeriesShape(2, peakShape);
        
        
        
        // label the points
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(4);
//        XYItemLabelGenerator generator =
//            new StandardXYItemLabelGenerator(
//                StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT,
//                format, format);
//        renderer.setBaseItemLabelGenerator(generator);
        renderer.setBaseItemLabelsVisible(true);

        return chart;
    }
   
    
   public static class PlotThread extends Thread{
    	private ChartPlot plot;

		public PlotThread(ChartPlot plot){
    		this.plot = plot;
    	}
		
		public void run(){
			plot.pack();
			plot.setLocationRelativeTo(null);
			plot.setVisible(true);
		}
    }

}