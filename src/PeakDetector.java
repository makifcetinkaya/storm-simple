import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class PeakDetector {
	private int k; // window size
	private double h; // calibration variable 1<= h <=3
//	public final static int MAX=1;
//	public final static int AVR=2;
//	public final  static int DIFF=3;
	
	private ArrayList<Integer> peakIndices; // indices of the final peak values
	private double[] peakValues; // value of peak function at each point
	private double[] data;
	
	public PeakDetector(int k, double h){
		this.k = k;
		this.h = h;
		this.peakIndices = new ArrayList<Integer>();
	}
	
	public ArrayList<Integer> detectPeaks(double[] data){
		this.data = data;
		peakValues = new double[data.length];
		calculatePeakVals();
		calculatePeakIndices();
		return peakIndices;
	}
	
	private void calculatePeakIndices(){
		double avg = getAverage(peakValues);
		double stdev = getStdDev(peakValues);
		System.out.println("Diff must be greater than:"+h*stdev);
		for(int i = 0; i < peakValues.length; i++){
			double diff = peakValues[i] - avg;
			//System.out.println("diff is:"+diff);
			if(peakValues[i] > 0 && diff > h*stdev ){
				peakIndices.add(i);
			}
		}
		Collections.sort(peakIndices);
		removeAdjacentPeaks(peakIndices);
	}
	
	private void removeAdjacentPeaks(ArrayList<Integer> peakIndices){
		
		for(int i = 0; i < peakIndices.size() - 1; i++){
			int p_i = peakIndices.get(i);
			int p_pi = peakIndices.get(i+1);
			if (Math.abs(p_i - p_pi) <= this.k){
				if(p_i > p_pi){
					peakIndices.remove(i);
				}else{
					peakIndices.remove(i+1);
				}
			}					
		}
	}
	
	/*
	 * Calculates the peak function of MAX at each point.
	 * */
	private void calculatePeakVals(){
		for(int i = k; i < data.length-k ; i++){
			peakValues[i] = calculateS1(Arrays.copyOfRange(data, i-k, i+k+1)); 
		}
	}
 
	/*
	 * Calculates the peak function of MAX at one point
	 * */
	private double calculateS1(double[] data_i){
		double[] leftDiff = new double[k];
		double[] rightDiff = new double[k];
		assert data_i.length == 2*k+1;
		for(int j = 0; j < k ; j++){
			leftDiff[j] = data_i[k] - data_i[j];
		}
		for(int j = k+1; j < 2*k+1 ; j++){
			rightDiff[j-k-1] = data_i[k] - data_i[j];
		}
		
		double leftMax = getMax(leftDiff);
		double rightMax = getMax(rightDiff);
		
		return (leftMax + rightMax)/2;		
	}
	
	private double getMax(double[] arr){
		double max = Double.MIN_VALUE;
		for(int i = 0; i < arr.length; i++){
			if (arr[i] > max){
				max = arr[i];
			}
		}
		return max;
	}
	
	// TODO calculate sigma and mu for + values
	private double getAverage(double[] data){
		
		double sum = 0;
		for(int i = 0; i < data.length; i++){
			sum = sum + data[i];
		}
		return sum/data.length;
	}
	private double getStdDev(double[] data){
		double avg = getAverage(data);
		double sum = 0;
		for(int i = 0; i < data.length; i++){
			sum = sum + (data[i] - avg)*(data[i] - avg);
		}
		double stdev = Math.sqrt(sum/data.length);
		return stdev;
	}
	
}
