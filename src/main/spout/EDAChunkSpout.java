package main.spout;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import main.utils.Conversions;


import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class EDAChunkSpout extends BaseRichSpout{
		
		public static final int PORT = 6789;
		public static final int TIMEOUT = 20;
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

		public static Logger _logger = Logger.getLogger(EDAChunkSpout.class);
//		Logger _logger = LoggerFactory.getLogger(EDAChunkSpout.class);
		
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			_collector = collector;
			_conf = conf;
			_transmitting = false;
			try {
				_serverSocket = new ServerSocket(PORT);
				_serverSocket.setSoTimeout(TIMEOUT);
				System.out.println("EDAChunkSpout CREATED server socket...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("EDAChunkSpout could not create server socket...");
				e.printStackTrace();
				//Thread.currentThread().interrupt();
			}
						
		}

		public void nextTuple() {
			// TODO Auto-generated method stub
			if (!_transmitting){
				try {
					_connSocket = _serverSocket.accept();
					_dataInputStream = new DataInputStream(_connSocket.getInputStream());
					_transmitting = true;
					if((System.currentTimeMillis()/1000) % 10 == 0 ){
						_logger.info("EDAChunkSpout at "+ InetAddress.getLocalHost().getHostName()+
								" waiting for incoming TCP connections...");
						// _logger.info("EDAChunkSpout at "+InetAddress.getLocalHost().getHostName()+
		        		//	   "received metadata: "+metastr);
					}
				} catch (SocketTimeoutException e){
					//TODO e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}else{
	           try{
		           if(_dataInputStream.available() >= CHUNK_SIZE){
		        	   byte[] header = new byte[HEADER_SIZE];
		        	   _dataInputStream.read(header);
		        	   
		        	   if (isCorrHeader(header)){
			        	   byte[] metadata = new byte[META_SIZE];
			        	   _dataInputStream.read(metadata);
			        	   String metastr = metaArrToStr(metadata);			        	  
			        	   byte[] data = new byte[DATA_SIZE];
			        	   _dataInputStream.read(data);
			        	   _collector.emit(new Values(metastr, data));
		        	   }
		           }
	           }catch(IOException e){
	        	   _transmitting = false;
	        	   try {
	        		   _dataInputStream.close();
				   } catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
				   }
	        	   e.printStackTrace();
	           }
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
