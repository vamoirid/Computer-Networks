package userApplication;

import ithakimodem.*;

import java.util.Scanner; //Reading the input from user
import java.util.ArrayList; //Handling ArrayLists
import java.io.File; //File Handling
import java.io.FileOutputStream;
//import java.io.FileNotFoundException; //Handle not Found Exceptions
//import java.io.FileOutputStream; //Writing to file.
import java.io.IOException; //Handle File Errors
//import java.util.Random;

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
			int  k, countPixels = 0;
			ArrayList<Integer> pixelsValue = new ArrayList<Integer>();  
			
			File imageFile = new File("imageWithoutErrors.jpeg");
			FileOutputStream imageStream = new FileOutputStream(imageFile);
			
			modem.write(("MXXXX" + "CAM=PTZ" + "DIR=U" + "\r").getBytes());
			for(;;) {
				try {
					k = modem.read();
					if (k == -1) break;
					pixelsValue.add(k);
					countPixels++;
				}
				catch (Exception x) {
					break;
				}
			}
			
			for (int i = 0; i < countPixels; i++) {
				imageStream.write(pixelsValue.get(i));
			}
			
			imageStream.close();	
		} //End of Image without Errors method
		
		//Method for Image with Errors method
		public void imageWithErrors(Modem modem) throws IOException {
			int  k, countPixels = 0;
			ArrayList<Integer> pixelsValue = new ArrayList<Integer>();  
			
			File imageFile = new File("imageWithErrors.jpeg");
			FileOutputStream imageStream = new FileOutputStream(imageFile);

			modem.write(("GXXXX" + "CAM=PTZ" + "DIR=U" + "\r").getBytes());
			for(;;) {
				try {
					k = modem.read();
					if (k == -1) break;
					pixelsValue.add(k);
					countPixels++;
				}
				catch (Exception x) {
					break;
				}
			}

			for (int i = 0; i < countPixels; i++) {
				imageStream.write(pixelsValue.get(i));
			}
			
			imageStream.close();
		} //End of Image with Errors method
		
		//Method for GPS data
		public void GPS(Modem modem) throws IOException, InterruptedException {
			
		} //End of GPS data
		
		//Method for ACK/NACK 
		public void ackNack(Modem modem) throws IOException {
			
		}
		
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
}
