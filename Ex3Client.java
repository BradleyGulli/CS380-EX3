import java.net.*;
import java.io.*;

/*
 * Authors: Bradley Gulli & Brian Sandoval
 * 
 * This program generates a checksum value from data sent from a server
 */
public class Ex3Client {
	
	/*
	 * main method handles simple I/O as well as sending the final checksum back to the server
	 */
	public static void main(String[] args) throws Exception{
		try(Socket socket = new Socket("codebank.xyz", 38103)) {
			System.out.println("Connected to server.");
			InputStream in = socket.getInputStream();
			int size = in.read();
			System.out.println("Reading " + size + " bytes.");
			
			byte[] received = receiveBytes(socket, size);
			short crc = checkSum(received);
			System.out.println("Checksum calculated: 0x" + Integer.toHexString(crc & 0xFFFF).toUpperCase());
			 OutputStream os = socket.getOutputStream();
	         byte[] asArray = new byte[2];
	         asArray[0] = (byte)((crc & 0xFF00) >>> 8);
	         asArray[1] = (byte)((crc & 0x00FF));
	         os.write(asArray);
	         String response = (in.read() == 1) ? "Response good." : "Bad Response.";
	         System.out.println(response);
			
		}
	}
	
	/*
	 * this method just recieves the bytes from the server
	 */
	private static byte[] receiveBytes(Socket socket, int size) throws Exception{
		System.out.println("Data received: ");
		InputStream in = socket.getInputStream();
		byte[] received = new byte[size];
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++){
					
			int byteRead = in.read();
			sb.append(Integer.toHexString(byteRead).toUpperCase());
			received[i] = (byte)byteRead;
	
		}
		printAsHex(sb.toString());
		System.out.println();
		
		return received;
	}
	
	public static void printAsHex(String hexString) {
		
		int count = 0;
		System.out.print("   ");
		for(int i = 0; i < hexString.length(); ++i) {
			
			if(count == 20) {
				System.out.println();
				System.out.print("   ");count = 0;
			}
			
			System.out.print(hexString.charAt(i));
			++count;
		}
	}
	
	
	/*
	 * this method uses the checksum algorithm to generate a check sum.
	 */
	public static short checkSum(byte[] b){
		long sum = 0;
		int length = b.length;
		int i = 0;
		long highVal;
		long lowVal;
		long value;
		
		while(length > 1){
			//gets the two halves of the whole byte and adds to the sum
			highVal = ((b[i] << 8) & 0xFF00); 
			lowVal = ((b[i + 1]) & 0x00FF);
			value = highVal | lowVal;
		    sum += value;
		    
		    //check for the overflow
		    if ((sum & 0xFFFF0000) > 0) {
		        sum = sum & 0xFFFF;
		        sum += 1;
		      }

		      //iterates
		      i += 2;
		      length -= 2;
		}
		//leftover bits
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
