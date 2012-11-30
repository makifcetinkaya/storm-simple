import java.io.IOException;


public class PythonTrial {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		String file = "~/Downloads/eda/LOG01_00H3_2011_11_15.eda";
		String script_dir = "~/Downloads";
		Runtime r = Runtime.getRuntime();
		Process p;
		System.out.println("attempting to execute");
		p = r.exec("python "+script_dir+" example.py "+file);
		int exitValue = p.waitFor();
		System.out.println("exit value is: "+exitValue);		
		System.out.println("finished");		
	}

}
