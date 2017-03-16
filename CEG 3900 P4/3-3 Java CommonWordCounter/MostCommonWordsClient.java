
import java.io.*;
import java.net.*;
import java.nio.file.*; 

public class MostCommonWordsClient{

	/**
	 * Queries an instance of MostCommonWordsServer with a url list and number n. Prints
	 * the n most common words among the urls in the list.
	 * 
	 * Takes four arguments:
	 * java MostCommonWordsClient <urlfilename> <outputfilename> <n> [hostname]
	 * urlfilename: local file containing a list of newline delimited urls
	 * outputfilename: local file to which results will be written
	 * n: the number of most frequent words to be saved
	 * hostname: the address of the server to be queried. If omitted, this defaults to 'localhost'
	 */
	public static void main(String[] args)
	{
		if(args.length < 3)
		{
			System.err.println("Usage: java MostCommonWordsClient <urlfilename> <outputfilename> <n> [hostname]");
		}

		int portNumber = 80;

		String hostName = "localhost";
		if(args.length >= 4)
			hostName = args[3];
		
		String urlfname = args[0]; //"urls.txt";
		String outfname = args[1];

		int numWords = Integer.parseInt(args[2]);// 25;

		try{

		String urls = new String(Files.readAllBytes(Paths.get(urlfname)));

		String result = queryCommonWords(hostName, portNumber, urls, numWords);
			
		Files.write(Paths.get(outfname), result.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	
		} catch(IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Interacts with the server at the given hostName and portNumber. Returns the server's
	 * response. This is a blocking method.
	 * 
	 * @param urls A newline delimited list of web urls whose words will be counted
	 * @param numWords The number of most common words to be returned
	 */
	public static String queryCommonWords(String hostName, int portNumber, String urls, int numWords)
	{
		try (
			Socket sock = new Socket(hostName, portNumber);
			ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
		) {
			String urlstr = urls;
			urlstr = Integer.toString(numWords) + '\n' + urlstr;

			System.out.println("Sending Request: \n"+urlstr);
			out.writeObject(urlstr);

			String response = (String) in.readObject();
			System.out.println("Recieved Response: \n"+response);

			return response;

		} catch(UnknownHostException e)
		{
			e.printStackTrace();
		} catch(IOException e)
		{
			e.printStackTrace();
		} catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return "";
	}


}
