package main.spout;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Map;

import main.bolt.EDACombineBolt;
import main.utils.Conversions;

import org.apache.log4j.Logger;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class EDAChunkSpout extends BaseRichSpout{
		
		public static final int PORT = 6789;
		public static final int TIMEOUT = 100;
		private static final int MAGIC_NUM = 239155;
		public static final int NAME_LEN = 50;
		private static final int HEADER_SIZE = 4;
		private static final int META_SIZE = 2*NAME_LEN + 12;
		private static final int DATA_SIZE = 8000;
		private static final int CHUNK_SIZE = DATA_SIZE + META_SIZE + HEADER_SIZE;
		
		private boolean _transmitting;
		private DataInputStream _dataInputStream;	
		private SpoutOutputCollector _collector;
		private Map _conf;
		private Socket _connSocket;
		private ServerSocket _serverSocket;
		private File _logFile;
		private String _logString;
		private FileWriter _fileWriter;
		private int _i;
		private InputStream _connInputStream;
		private int _lastLogTime;
		public static Logger _logger = Logger.getLogger(EDAChunkSpout.class);
//		Logger _logger = LoggerFactory.getLogger(EDAChunkSpout.class);
		
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			_i = 1;
			_collector = collector;
			_conf = conf;
			_logFile = new File(EDACombineBolt.EDA_OUT_DIR+"log-combine");
			try {
				_fileWriter = new FileWriter(_logFile, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			_transmitting = false;
			log("=======opening spout========");
			try {
				_serverSocket = new ServerSocket(PORT);
				_serverSocket.setSoTimeout(TIMEOUT);
				//_serverSocket.setReceiveBufferSize(1000*DATA_SIZE);
				log("EDAChunkSpout CREATED server socket...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log("EDAChunkSpout could not create server socket...");
				e.printStackTrace();
				//Thread.currentThread().interrupt();
			}
			
						
		}

		public void nextTuple() {
			// TODO Auto-generated method stub			
	        int bRead;
			if (!_transmitting){
				try {
					//log("before client socket acquired");
					_connSocket = _serverSocket.accept();
					//log("after client socket acquired");
					_connInputStream = _connSocket.getInputStream();
					_dataInputStream = new DataInputStream(_connInputStream);
					_transmitting = true;
					log("now transmitting...setting _i to 1..");
					_i = 1;
				} catch (SocketTimeoutException e){
					//TODO 
					//e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}else{
			   try{
				   
				   byte[] header = new byte[HEADER_SIZE];
				   //log("before header");
				   _dataInputStream.readFully(header);
//				   if(bRead != HEADER_SIZE || !(isCorrHeader(header) )){
//					   throw new IOException();
//				   }
				   
				   if(_i == 1){
					   log("===BEGAN PROCESSING AT:"+System.currentTimeMillis());
				   }
				   
				   byte[] metadata = new byte[META_SIZE];
				   //log("before meta");
				   _dataInputStream.readFully(metadata);
//				   if(bRead != META_SIZE){
//					   throw new IOException();
//				   }
				   String metastr = metaArrToStr(metadata);			        	  
				   byte[] data = new byte[DATA_SIZE];
				   //log("before data, available data is:"+_dataInputStream.available());
				   _dataInputStream.readFully(data);
				   //log("after data");
//				   if(bRead != DATA_SIZE){
//					   throw new IOException();
//				   }else{
//					   if(_i==1000){
//						   log("===RECEIVED ALL CHUNKS AT:"+System.currentTimeMillis());
//					   }
					  _i++;
				   //}//
				   _collector.emit(new Values(metastr, data));
			   }catch(IOException e){
				   //RESET
				   log("SERVER RESETTING CONNECTION");
				   _transmitting = false;
				   try{
					   _connInputStream.close();
					   _dataInputStream.close();
	        		   _connSocket.close();
				   }catch(IOException e1){
					   e.printStackTrace();
				   }
				   
			   }
			}
			
		}

		private  void log(String msg){
			try {
				_fileWriter.append(msg+'\n');
				_fileWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private static boolean isCorrHeader(byte[] byta){
			int num = Conversions.bytaToInt(byta);
			if (num == MAGIC_NUM){
				return true;
			}else{
				return false;
			}
		}
		public static String metaArrToStr(byte[] bArr){
			byte[] fName = Arrays.copyOfRange(bArr, 0, NAME_LEN*2);
			String fileName = Conversions.bytaToUTF(fName);
			byte[] cSize = Arrays.copyOfRange(bArr, NAME_LEN*2, NAME_LEN*2+4);
			int chunkSize = Conversions.bytaToInt(cSize);
			byte[] cIndex = Arrays.copyOfRange(bArr, NAME_LEN*2+4, NAME_LEN*2+8);
			int chunkIndex = Conversions.bytaToInt(cIndex);
			byte[] nOfChunks = Arrays.copyOfRange(bArr, NAME_LEN*2+8, NAME_LEN*2+12);
			int numOfChunks = Conversions.bytaToInt(nOfChunks);
			
			return fileName+","+chunkSize+","+chunkIndex+","+numOfChunks;
		}
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("metadata", "eda"));
		}		
	}
