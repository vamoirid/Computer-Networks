package userApplication;

import ithakimodem.*;				//Modem library
import java.util.Scanner; 			//Reading the input from user
import java.util.ArrayList; 		//Handling ArrayLists
import java.io.File; 				//File Handling
import java.io.FileOutputStream;	//Writing to file
import java.io.IOException; 		//Handle File Errors

public class virtualModem {
	
	public static void main(String[] args) {
		(new virtualModem()).startUp();
	}
	
	//Start Up method for choosing application
		public void startUp() {
			Modem modem = new Modem();
			for(;;) {		
				int choice;
				Scanner in = new Scanner(System.in);
				
				System.out.println("Hello! I am Vasileios Amoiridis 8772 and this is my Computer Networks 1 user Application:");
				System.out.println("	1. Echo Request Code                - EXXXX");
				System.out.println("	2. Image Request Code (error free)  - MXXXX");
				System.out.println("	3. Image Request Code (with errors) - GXXXX");
				System.out.println("	4. GPS Request Code                 - PXXXX");
				System.out.println("	5. ARQ Request Code ACK/NACK        - (Q/R)XXXX");
				System.out.println("	6. Exit...");
				System.out.print("Choose an Application: ");
				
				try {
					choice = in.nextInt();
					switch (choice) {
					case 1:
						System.out.println();
						wakeUpModem(modem);
						echoRequestCode(modem);
						goToSleepModem(modem);
						break;
					case 2:
						System.out.println();
						wakeUpModem(modem);
						imageWithoutErrors(modem);
						goToSleepModem(modem);
						break;
					case 3:
						System.out.println();
						wakeUpModem(modem);
						imageWithErrors(modem);
						goToSleepModem(modem);
						break;
					case 4:
						System.out.println();
						wakeUpModem(modem);
						GPS(modem);
						goToSleepModem(modem);
						break;
					case 5:
						System.out.println();
						wakeUpModem(modem);
						ackNack(modem);
						goToSleepModem(modem);
						break;
					case 6:
						System.out.println();
						System.out.println("Auf Wiedersehen! Exiting...");
						break;
					default:
						System.out.println("WARNING: Unacceptable Application. Try again.");
					}
				} 
				catch (Exception ex) {
					System.out.println("WARNING: An exception was occured in startUp(). Try again." + ex);
				}
				
				System.out.println("Boom boom Ciao!");
				in.close();
				return;
			}
		} //End of Start Up method
		
		//Method for Echo Request Code	
		public void echoRequestCode(Modem modem) throws IOException {
			String receivedMessage = ""; 										//Stores the raw data received from the server
			int k = 0, totalMessages = 0;										//Received char and total Number of messages received
			long timeBegin = System.currentTimeMillis();						//Time at the beginning of the method
			long timeDelta = 0, timeStart = 0;									//Count time for each message to be transmitted
			long totalRuntime = 5*60*1000; 										//5 minutes loop in milliseconds
			ArrayList<String> sampleMessages = new ArrayList<String>(); 		//Stores the messages
			ArrayList<String> sampleTimes = new ArrayList<String>();    		//Stores time needed for each message to be transmitted
			
			File echo = new File("echo.txt");									//File to store server messages.
			File times = new File("timesEcho.txt");								//File to store transaction time for server messages
			FileOutputStream echoStream = new FileOutputStream(echo);  		 	//Output Stream for echo file
			FileOutputStream timesStream = new FileOutputStream(times);		 	//Output Stream for times file

			while((System.currentTimeMillis() - timeBegin) < totalRuntime) {
				modem.write("EXXXX\r".getBytes());								// **********ATTENTION********** ALWAYS CHECK THE CURRENT SERVER SETTINGS
				timeStart = System.currentTimeMillis();							//Start counting
				
				for (;;) {
					try {
						k = modem.read();
						if (k == -1) break;
						receivedMessage += (char)k;
						if(receivedMessage.indexOf("PSTOP") > -1) {				//End of in-line message
							timeDelta = System.currentTimeMillis() - timeStart;	//Elapsed time
							System.out.println(receivedMessage);
							totalMessages++;
						}
					} 
					catch (Exception x) {
						System.out.println("WARNING: An exception has occured in echoRequestCode()->modem.read(). " + x);
						break;
					}
				}
				
				System.out.println(timeDelta);
				sampleTimes.add(String.valueOf(timeDelta));						//Add time to ArrayList as string for compatibility with stream.write()
				sampleMessages.add(receivedMessage);							//Add message to ArrayList
				receivedMessage = "";
			}
			
			for(int i = 0; i < totalMessages; i++)
			{
				echoStream.write((sampleMessages.get(i) + "\r\n").getBytes());
				timesStream.write((sampleTimes.get(i) + "\r\n").getBytes());
			}
			
			echoStream.close();
			timesStream.close();
		} //End of Echo Request Code method
		
		//Method for Image without Errors method
		public void imageWithoutErrors(Modem modem) throws IOException {
			int  k, countJPEGdata = 0;											//Counter for the number of messages received
			ArrayList<Integer> jpegDataValue = new ArrayList<Integer>();  		//ArrayList for storing JPEG data
			
			File imageFile = new File("imageWithoutErrors.jpeg");
			FileOutputStream imageStream = new FileOutputStream(imageFile);		//File for saving the image data
			
			modem.write(("MXXXX" + "CAM=PTZ" + "DIR=U" + "\r").getBytes());
			for(;;) {
				try {
					k = modem.read();
					if (k == -1) break;
					jpegDataValue.add(k);										//Storing the info
					countJPEGdata++;
				}
				catch (Exception x) {
					break;
				}
			}
			
			for (int i = 0; i < countJPEGdata; i++) {
				imageStream.write(jpegDataValue.get(i));						//Saving the image
			}
			
			imageStream.close();	
		} //End of Image without Errors method
		
		//Method for Image with Errors method
		public void imageWithErrors(Modem modem) throws IOException {
			int  k, countJPEGdata = 0;											//Counter for the number of messages received
			ArrayList<Integer> jpegDataValue = new ArrayList<Integer>();  		//ArrayList for storing JPEG data
			
			File imageFile = new File("imageWithErrors.jpeg");
			FileOutputStream imageStream = new FileOutputStream(imageFile);		//File for saving the image data

			modem.write(("GXXXX" + "CAM=PTZ" + "DIR=U" + "\r").getBytes());
			for(;;) {
				try {
					k = modem.read();
					if (k == -1) break;
					jpegDataValue.add(k);										//Storing the info
					countJPEGdata++;
				}
				catch (Exception x) {
					break;
				}
			}

			for (int i = 0; i < countJPEGdata; i++) {
				imageStream.write(jpegDataValue.get(i));
			}
			
			imageStream.close();
		} //End of Image with Errors method
		
		//Method for GPS data
		public void GPS(Modem modem) throws IOException, InterruptedException { 
			int  k, gpsDataCnt = 0, gpsDataGPGGACnt = 0, gpsImageCnt = 0;			
			String receivedMessage = "";
			ArrayList<String> gpsData = new ArrayList<String>();
			ArrayList<String> gpsDataGPGGA = new ArrayList<String>();
			ArrayList<String> gpsDataGPGGARandom = new ArrayList<String>();
			
			File gpsText = new File("GPS.txt");
			File gpsImage = new File("GPS.jpeg");
			FileOutputStream gpsTextStream = new FileOutputStream(gpsText);
			FileOutputStream gpsImageStream = new FileOutputStream(gpsImage);
			
			//Receive Text from GPS
			modem.write(("PΧΧΧΧ" + "R=1000197" +"\r").getBytes());
			for(;;) {
				try {
					k = modem.read();
					if (k == -1) break;
					receivedMessage += (char)k;
					System.out.print((char)k);
				}
				catch (Exception x) {
					break;
				}
				if (receivedMessage.indexOf("\r\n") > -1) {
					gpsData.add(receivedMessage);
					gpsDataCnt++;
					
					if (receivedMessage.indexOf("GPGGA") > -1) {
						//System.out.print(receivedMessage);
						gpsDataGPGGA.add(receivedMessage);
						gpsDataGPGGACnt++;
					}
					receivedMessage = "";
				}
			}
			//System.out.println("Total received GPGGA: " + gpsDataGPGGACnt);
			for (int i = 0; i < gpsDataCnt; i++) {
				gpsTextStream.write(gpsData.get(i).getBytes());
			}
			
			gpsTextStream.close();
			//All required messages from GPS are received.
			
			//Modify the data, calculate 9 random points with 4sec distance and receive Image from server.			
		    int min = 1, max = 11;
			String geographicLongitude = "";
			String geographicLatitude = "";
			String degreesLongitude = "";
			String degreesLatitude = "";
			String minutesLongitude = "";
			String minutesLatitude = "";
			String minutesDecimalLongitude = "";
			String minutesDecimalLatitude = "";
			String secondsLongitude = "";
			String secondsLatitude = "";
			int indexOfDecimal; //index of decimal number for decimal conversion
			
			ArrayList<String> tValues = new ArrayList<String>();
			//Start picking 9 random GPGGA messages
			for (int i = 0; i < 9; i++) { // 9 loops
				int rand_int = (int)(Math.random()*(max - min + 1) + min);							//Random Number in [1 + i*9, 11 + i*9]
				min += 9;																			//If we add number 9 in each loop we are sure that every
				max += 9;																			//GPGGA message will have at least 9 sec time distance with the previous
				gpsDataGPGGARandom.add(gpsDataGPGGA.get(rand_int - 1)); //rand_int - 1 or i			//Store the random GPGGA messages
				//System.out.println(rand_int);
				//System.out.print(gpsDataGPGGARandom.get(i));
				//System.out.println("Length of String = " + gpsDataGPGGARandom.get(i).length());
				
				//18 19 20 21 23 24 are the character indexes for Latitude coords in a GPGGA message
				degreesLatitude += (char)gpsDataGPGGARandom.get(i).charAt(18);
				degreesLatitude += (char)gpsDataGPGGARandom.get(i).charAt(19);
				minutesLatitude += (char)gpsDataGPGGARandom.get(i).charAt(20);
				minutesLatitude += (char)gpsDataGPGGARandom.get(i).charAt(21);
				minutesDecimalLatitude += (char)gpsDataGPGGARandom.get(i).charAt(23);
				minutesDecimalLatitude += (char)gpsDataGPGGARandom.get(i).charAt(24);
				minutesDecimalLatitude += (char)gpsDataGPGGARandom.get(i).charAt(25);
				minutesDecimalLatitude += (char)gpsDataGPGGARandom.get(i).charAt(26);
				
				//Convert minutesDecimal to seconds
				double tempSecondsLatitude = (double)Integer.parseInt(minutesDecimalLatitude)*60/10000;
				//System.out.println("Double value is: " + tempSecondsLatitude);
				secondsLatitude = String.valueOf(tempSecondsLatitude);
				indexOfDecimal = secondsLatitude.indexOf(".");
				secondsLatitude = secondsLatitude.substring(0, indexOfDecimal);
				//System.out.println("Integer value is: " + secondsLatitude);
				geographicLatitude = degreesLatitude + minutesLatitude + secondsLatitude;
				//System.out.println("Geographic Latitude = " + geographicLatitude);
				//End of converting minutesDecimal to seconds
				
				degreesLatitude = "";
				minutesLatitude = "";
				secondsLatitude = "";
				minutesDecimalLatitude = "";
				
				//31 32 33 34 36 37 38 39 are the character indexes for Longitude coords in a GPGGA message
				degreesLongitude += (char)gpsDataGPGGARandom.get(i).charAt(31);
				degreesLongitude += (char)gpsDataGPGGARandom.get(i).charAt(32);
				minutesLongitude += (char)gpsDataGPGGARandom.get(i).charAt(33);
				minutesLongitude += (char)gpsDataGPGGARandom.get(i).charAt(34);
				minutesDecimalLongitude += (char)gpsDataGPGGARandom.get(i).charAt(36);
				minutesDecimalLongitude += (char)gpsDataGPGGARandom.get(i).charAt(37);
				minutesDecimalLongitude += (char)gpsDataGPGGARandom.get(i).charAt(38);
				minutesDecimalLongitude += (char)gpsDataGPGGARandom.get(i).charAt(39);
				
				double tempSecondsLongitude = (double)Integer.parseInt(minutesDecimalLongitude)*60/10000;
				//System.out.println("Double value is: " + tempSecondsLongitude);
				secondsLongitude = String.valueOf(tempSecondsLongitude);
				indexOfDecimal = secondsLongitude.indexOf(".");
				secondsLongitude = secondsLongitude.substring(0, indexOfDecimal);
				//System.out.println("Integer value is: " + secondsLongitude);
				geographicLongitude = degreesLongitude + minutesLongitude + secondsLongitude;
				//System.out.println("Geographic Longitude = " + geographicLongitude);
				
				degreesLongitude = "";
				minutesLongitude = "";
				secondsLongitude = "";
				minutesDecimalLongitude = "";
				
				tValues.add("T=" + geographicLongitude + geographicLatitude);
				//System.out.println("All together = " + tValues.get(i));
				geographicLongitude = "";
				geographicLatitude = "";
			}
			
			ArrayList<Integer> gpsImageDataValue = new ArrayList<Integer>();
			
			String pCodeToSend = "PΧΧΧΧ";
			for (int i = 0; i < 9; i++) {
				pCodeToSend += tValues.get(i);
			}
			pCodeToSend += "\r\n";
			//System.out.println(pCodeToSend);
			modem.write(pCodeToSend.getBytes());
			for(;;) {
				try {
					k = modem.read();
					if (k == -1) break;
					gpsImageDataValue.add(k);
					gpsImageCnt++;
				}
				catch (Exception x) {
					System.out.println(x);
				}
			}
			
			for(int i = 0; i < gpsImageCnt; i++) {
				gpsImageStream.write(gpsImageDataValue.get(i));
			}
			
			gpsImageStream.close();
			
		} //End of GPS data
		
		//Method for ACK/NACK 
		public void ackNack(Modem modem) throws IOException {
			int k, msgCnt = 0, ackCnt = 0, nackCnt = 0, retransmitTimes = 0, retransmitCnt = 0;
			int L = 16 * 8; //16 characters * 8 bits per character. Server sends 8-bit characters even though Java uses 16 to store them
			byte[] Ack = "Q6162\r".getBytes();
			byte[] Nack = "R0576\r".getBytes();
			byte[] txCode = Ack;
			String receivedMessage = "";
			long timeBegin = System.currentTimeMillis();
			long timeDelta = 0, timeStart = 0;
			long totalRuntime = 5*60*1000; //5 minutes loop in milliseconds
			
			ArrayList<String> sampleTimes = new ArrayList<String>();
			ArrayList<String> sampleMessages = new ArrayList<String>();
			ArrayList<String> sampleARQs = new ArrayList<String>();
			
			File ackNackMsg = new File("AckNackMessages.txt");
			File ackNackTime = new File("AckNackTimes.txt");
			File ackNackARQ = new File("AckNackRetransmissions.txt");
			
			FileOutputStream ackNackMsgStream = new FileOutputStream(ackNackMsg);
			FileOutputStream ackNackTimeStream = new FileOutputStream(ackNackTime);
			FileOutputStream ackNackARQStream = new FileOutputStream(ackNackARQ);
			
			while((System.currentTimeMillis() - timeBegin) < totalRuntime) {
				modem.write(txCode);
				timeStart = System.currentTimeMillis();
				for(;;) {
					try {
						k = modem.read();
						if (k == -1) break;
						receivedMessage += (char)k;
						if(receivedMessage.indexOf("PSTOP") > -1) {
							timeDelta = System.currentTimeMillis() - timeStart;
							System.out.println(receivedMessage);
							msgCnt++;
							
							if(checksum(receivedMessage)) {
								System.out.println("ACK!");
								txCode = Ack;
								ackCnt++;
								sampleARQs.add(String.valueOf(retransmitTimes));
								retransmitTimes = 0;
								retransmitCnt++;
							}
							else {
								System.out.println("NACK!");
								txCode = Nack;
								nackCnt++;
								retransmitTimes++;
							}
						}
					}
					catch (Exception x) {
						System.out.println(x);
						break;
					}
				}
				System.out.println("Time needed: " + timeDelta);
				sampleTimes.add(String.valueOf(timeDelta));	
				sampleMessages.add(receivedMessage);
				receivedMessage = "";
			}
			
			System.out.println("Total ack times = " + ackCnt);
			System.out.println("Total nack times = " + nackCnt);
			System.out.println("Total times = " + msgCnt);
			double successProb = (double)ackCnt/msgCnt;
			double ber = (1 - Math.pow(successProb,1.0/L));
		    System.out.println("BER = " + ber);
			
			for (int i = 0; i < msgCnt; i++) {
				ackNackMsgStream.write((sampleMessages.get(i) + "\r\n").getBytes());
				ackNackTimeStream.write((sampleTimes.get(i) + "\r\n").getBytes());
			}
			
			for (int i = 0; i < retransmitCnt; i++) {
				ackNackARQStream.write((sampleARQs.get(i) + "\r\n").getBytes());
			}
			ackNackARQStream.write(("BER: " + ber + "\r\n").getBytes());
			
			ackNackMsgStream.close();
			ackNackTimeStream.close();
			ackNackARQStream.close();
		} //End of ACK/NACK
		
		//Method for waking up the modem
		public void wakeUpModem(Modem modem) {
			int k;
			String initialMessage = "";
			
			modem.setSpeed(80000);
			modem.setTimeout(2000);
			modem.open("ithaki"); //atd2310ithaki
				
			for (;;) {
				try {
					k = modem.read();
					if (k == -1) break;
					initialMessage += (char)k;
					System.out.print((char)k);
				} 
				catch (Exception x) {
					//System.out.println("Encountered problem. Ciao...");
					break;
				}
				
				if(initialMessage.indexOf("\r\n\n\n") > -1) break; //End of message delimiter
				
			}
		} //End of Method for waking up the modem
		
		//Method to close the communication with the modem
		public void goToSleepModem(Modem modem) {
			modem.close();
		} //End of Method to close the communication with the modem
		
		//Method for checking the fcs with the encrypted message 
		public boolean checksum(String packet) {
			int leftOperand = packet.indexOf("<");
			int rightOperand = packet.indexOf(">");
			int fcsCode = Integer.parseInt(packet.substring(rightOperand+2, rightOperand+5));
			int xorSeq = (int) packet.charAt(leftOperand + 1);
			
			for (int i = leftOperand + 2; i < rightOperand; i++) {
				xorSeq ^= (int)packet.charAt(i);
			}
			
			if (xorSeq == fcsCode) return true;
			
			return false;
		}
}
