import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class PeakDetector1 {
	private int k; // window size
	private double h; // calibration variable 1<= h <=3
	private ArrayList<Integer> peakIndices;
	private double[] potVals;
	
	public PeakDetector1(int k, double h){
		this.k = k;
		this.h = h;
		this.peakIndices = new ArrayList<Integer>();
	}
	
	public ArrayList<Integer> detectPeaks(double[] data){
		potVals = new double[data.length];
		calcPotentials(data);
		calcPeakIndices();
		removeKAdjacent(data);
		return peakIndices;

	}
	
	private void calcPeakIndices(){
		double avg = getAverage(potVals);
		double stdev = getStdDev(potVals);		
		for(int i = 0; i < potVals.length; i++){
			double diff = potVals[i] - avg;
			if(potVals[i] > 0 && diff > h*stdev ){
				peakIndices.add(i);
			}
		}
		Collections.sort(peakIndices);
	}
	
	private void removeKAdjacent(double[] data){
		System.out.println("removing adjacent peaks");
		int i = 0;
		while(i  < peakIndices.size() - 1){
			int curr = peakIndices.get(i);
			int next = peakIndices.get(i+1);
			int indexDiff = Math.abs(next- curr);
			
			if(indexDiff < k){
				if(data[curr] > data[next]){
					peakIndices.remove(i+1);
				}else{
					peakIndices.remove(i);
				}
			}else{
				i++;
			}
		}
	}
	
	/*
	 * Calculates the peak function of MAX at each point.
	 * */
	public void calcPotentials(double[] data){
		for(int i = k; i < data.length-k ; i++){
			potVals[i] = calculateS1(Arrays.copyOfRange(data, i-k, i+k+1)); 
		}
	}
 
	/*
	 * Calculates the peak function of MAX at one point
	 * */
	public double calculateS1(double[] data_i){
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
	public double getMax(double[] arr){
		double max = arr[0];
		for(int i = 0; i < arr.length; i++){
			if (arr[i] > max){
				max = arr[i];
			}
		}
		return max;
	}
	
	// TODO calculate sigma and mu for + values
	public double getAverage(double[] data){
		
		double sum = 0;
		for(int i = 0; i < data.length; i++){
			sum = sum + data[i];
		}
		return sum/data.length;
	}
	public double getStdDev(double[] data){
		double avg = getAverage(data);
		double sum = 0;
		for(int i = 0; i < data.length; i++){
			sum = sum + (data[i] - avg)*(data[i] - avg);
		}
		double stdev = Math.sqrt(sum/data.length);
		return stdev;
	}
	
}
