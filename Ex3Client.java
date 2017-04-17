import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;
public class Ex3Client {
	
	public static void main(String[] args) throws Exception{
		try(Socket socket = new Socket("codebank.xyz", 38103)){
			InputStream in = socket.getInputStream();
			int size = in.read();
			System.out.println(size);
			
			byte[] received = receiveBytes(socket, size);
			short crc = checkSum(received);
			System.out.println("Checksum calculated: 0x" + Integer.toHexString(crc & 0xFFFF).toUpperCase());
			 OutputStream os = socket.getOutputStream();
	         byte[] asArray = new byte[2];
	         asArray[0] = (byte)((crc & 0xFF00) >>> 8);
	         asArray[1] = (byte)((crc & 0x00FF));
	         os.write(asArray);
	         System.out.println(in.read());
			
		}
	}
	
	private static byte[] receiveBytes(Socket socket, int size) throws Exception{
		InputStream in = socket.getInputStream();
		byte[] received = new byte[size];
		
		for(int i = 0; i < size; i++){
			received[i] = (byte)in.read();
		}
		
		return received;
	}
	
	public static short checkSum(byte[] b){
		long sum = 0;
		int length = b.length;
		int i = 0;
		long highVal;
		long lowVal;
		long value;
		
		while(length > 1){
			highVal = ((b[i] << 8) & 0xFF00); 
			lowVal = ((b[i + 1]) & 0x00FF);
			value = highVal | lowVal;
		    sum += value;
		    
		    if ((sum & 0xFFFF0000) > 0) {
		        sum = sum & 0xFFFF;
		        sum += 1;
		      }

		      i += 2;
		      length -= 2;
		}
		
		if(length > 0){
			sum += (b[i] << 8 & 0xFF00);
		      if ((sum & 0xFFFF0000) > 0) {
		        sum = sum & 0xFFFF;
		        sum += 1;
		      }
		}
		sum = ~sum;
		sum = sum & 0xFFFF;
		return (short)sum;
	}
	
}
