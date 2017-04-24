
import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;
import java.nio.charset.StandardCharsets;

/**
* Created by Paul Fuchs
*
* Requires java 8 to compile and run
*/

public class CloudCatServer
{
	public static final String WORD_LISTS_FILE = "wordlists.txt";

	/**
	* The main thread listens for connections to the server. When a connection is
	* recieved, a Thread is created to handle the connection. This allows multiple
	* clients to connect simultaneously.
	*/
	public static void main(String[] args)
	{
	//server code based off java tutorial https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html

	//port number the server will run on
        final int portNumber = 8080;
        try { 
 
    	ServerSocket serverSocket = new ServerSocket(portNumber);
		System.out.println("Started server on port "+portNumber);

	//run the server forever
        while (true)
		{
	 
		   final Socket clientSocket = serverSocket.accept();
		   //handle the connection
		   Runnable handler = () ->
			{
			try{	
				
				System.out.println("Accpted Connection\n");
				//get input and output streams		   	    
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
	    	  		ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
		    	 
				//read input from client
		   		String request = (String) in.readObject();
				System.out.println("Received Request: \n"+request);
				//evaluate the password	
				String[] urls = request.split("\n");			
				String response = processRequest(urls[0], urls[1]);
		
				System.out.println("\nSending Response: \n"+response);
				System.out.println("----------------------------------------------------------");
				//reply to client
				out.writeObject(response);
		        
			} catch (IOException e) {
			        System.out.println("Exception caught when trying to listen on port "
			            + portNumber + " or listening for a connection");
			        System.out.println(e.getMessage());
			} catch(ClassNotFoundException e)
			{ e.printStackTrace();}
			};
			new Thread(handler).start();

		}

	    } catch (IOException e) {
	        System.out.println("Exception caught when trying to listen on port "
	            + portNumber + " or listening for a connection");
	        System.out.println(e.getMessage());
 
		}
	}

	public static String processRequest(String hashurl, String wordlisturl)
	{
		String output = "Internal Error";
		try{
			//read files from urls and write to local storage
		
			//download hash file
			URL hashfileurl = new URL(hashurl);			
			String hashes = new Scanner(hashfileurl.openStream(), 
				"UTF-8").useDelimiter("\\A").next();
			Files.write(Paths.get("./hashes.txt"), hashes.getBytes());
		
			//download word list file
			URL wordlistfileurl = new URL(wordlisturl);
			String wordlist = new Scanner(wordlistfileurl.openStream(), 
				"UTF-8").useDelimiter("\\A").next();
			Files.write(Paths.get("./wordlist.txt"), wordlist.getBytes());			
			//invoke hashcat on the files

			//start the process
			Process hashcat = Runtime.getRuntime().exec("hashcat -a 0 "+
				"--show ./hashes.txt ./wordlist.txt");
			
			//read the output from hashcat
			Scanner s = new Scanner(hashcat.getInputStream());
			output = "";
			while(s.hasNextLine())
				output += s.nextLine() + "\n";

		}catch (IOException e)
		{e.printStackTrace();}

		return output;
	}

}
