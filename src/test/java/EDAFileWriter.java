import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class EDAFileWriter {
	
	private File file;
	private FileChannel fc;
	private RandomAccessFile randomAccessFile;
	public EDAFileWriter(){
		
	}
	
	public void createEDAFile(String filename){
		this.file = new File(filename);
	}
	
	public void write(String content, int index){
		
		try {
			this.randomAccessFile = new RandomAccessFile(file, "rw");
			this.fc = randomAccessFile.getChannel();
			fc.position(index);
			byte[] bytes = content.getBytes();
			ByteBuffer buf = ByteBuffer.allocate(bytes.length);
			buf.put(bytes);
			buf.flip();
			while(buf.hasRemaining()){
				fc.write(buf);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
