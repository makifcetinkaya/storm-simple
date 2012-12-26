import java.util.ArrayList;

import main.utils.PeakDetector;


public class PeakDetectorTest {
	public static void main(String[] args){
		float[] vals = new float[]{1,2,3,4,5,4,5,6,7,7,8,9,10,2,3,4,5,2,3,4,5,3,6,12,4,5,6,23,4};
		ArrayList[] res = PeakDetector.peakDetect(vals, 2, 1);
		System.out.println(res[0]);
		System.out.println(res[1]);
	}
}
