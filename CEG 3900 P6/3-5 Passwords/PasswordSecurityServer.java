
import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;
import java.nio.charset.StandardCharsets;

/**
* Created by Paul Fuchs
*
* Server program for checking the security of a prospective password by comparing
* it against the word lists specified in wordlists.txt. 
*
* The client sends a single serialized string containing the password. The server 
* sends back a string containing its evaluation of the password. The password is
* evaluated based on its similarity to passwords in the word lists. 
* 
* There are three levels of similarity:
*	Exact match: the prospective password is found in the list
*		Outputs "BAD! Password found in wordlist LIST_NAME"
*	Case insensitive match: the prospective password matches one in the list ignoring case
*		Outputs "WARN! Password found ignoring case FOUND_PASSWORD"
*	Partial match: the prospective password either begins or ends with a password in the list
*		Ouputs "NOTE: Password may be similar to FOUND_PASSWORD"
*		Partial matches may be relevant or not depending on how close the user password is
*		to the one found by the server. It is left up to the user to make this judgement
*
* Compile with the command:	javac PasswordSecurityServer.java
* Run with the command:		java PasswordSecurityServer
*
* Requires java 8 to compile and run
*/

public class PasswordSecurityServer
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
				String response = processRequest(request);
		
				System.out.println("\nSending Response: \n"+response);
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

	public static String processRequest(String password)
	{

		//check the password's security

		String output = "Internal I/O Error";
		String plow = password.toLowerCase(); //lowercase password for case insensitive comparisons
		try{
			//list of wordlists to use
			List<String> lists = Files.lines(Paths.get(WORD_LISTS_FILE)).collect(Collectors.toList());
			
			output = "";
			boolean found = false;

			//for each word list
			for(String list : lists)
			{
				//password was found, no need to continue
				if(found) break;
				
				print(list);
				//read word list
				if(!list.isEmpty())
				{
					try{
						Scanner scan = new Scanner(Files.newInputStream(Paths.get(list), StandardOpenOption.READ));
						String word;
						while(scan.hasNextLine())
						{
							//get the next word
							word = scan.nextLine();
							String wlow = word.toLowerCase();
							if(word.isEmpty())
								continue;

							if(password.equals(word))
							{
								output = "BAD! Password found in "+list+"\n";
								found=true;
								break;
							}else if(plow.equals(wlow))
							{
								//found ignoring case
								output = "WARN! Found ignoring case: "+word+"\n";
							}else if(plow.startsWith(wlow) || plow.endsWith(wlow))
							{
								//user password begins or ends with the next word
								output += "NOTE: Password may be similar to: "+word+"\n";
							}
						}

					} catch(IOException e)
					{ System.err.println("Word List: \'"+list+"\' not found!"); }
				}
			}


		}catch (IOException e)
		{e.printStackTrace();}

		if(output.isEmpty())
			output = "OK! Password not found\n";
	
		return output;
	}

}
