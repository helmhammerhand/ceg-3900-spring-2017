
import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;
public class MostCommonWordsServer
{

	public static void main(String[] args)
	{
	//server code based off java tutorial https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html

        int portNumber = 80;
        try { 
 
    	 ServerSocket serverSocket = new ServerSocket(portNumber);
	System.out.println("Started server on port 80");

        while (true)
	{
 
          final Socket clientSocket = serverSocket.accept();

	  //create a thread for each connection
	   Runnable handler = () ->
		{
		try{	
			System.out.println("Accpted Connection\n");
        	    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        	    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        	 
        	    String request = (String) in.readObject();
			System.out.println("Received Request: \n"+request);
		    String response = processUrlFile(request.substring(request.indexOf('\n')+1), Integer.parseInt(request.substring(0,request.indexOf('\n'))));
		
			System.out.println("\nSending Response: \n"+response);

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
	

	/**
	 * Takes a String containing a list of newline delimited urls and an integer
	 * containing the number of words to return.
	 *
	 * Returns the numCommonWords most frequently occuring words in all of the web
	 * pages combined. Output has the format word1, count1 '\n' word2, count2 '\n' ...
	 */
	public static String processUrlFile(String urlstring, int numCommonWords)
	{
		// stores estimates of execution time
		long processtimems = 0;	
		long filtertimems = 0;
		long readtimems = 0;
		int numWords = 0;

		// data structures for processing word frequency
		HashMap<String, MutableInt> wordfreq = new HashMap<>();
		ArrayList<WordData> wordList = new ArrayList<>();

		String[] urls = urlstring.split("\\s+");

		// add words from each url to the data structure
		for(String nexturl : urls)
		{

			try{
				long start = System.currentTimeMillis();
				//load url
				URL url = new URL(nexturl);
					
				String line = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
				readtimems += System.currentTimeMillis() - start;
				start = System.currentTimeMillis();

				line = line.replaceAll("<[^>]*>", " ");// remove html tags
				line = line.toLowerCase();
				//System.out.println(line);
				String[] tokens = line.split("\\s+");// split on whitespace
				numWords += tokens.length;
				Stream<String> words  = Arrays.stream(tokens); 
				//remove all punctuation from words
				words = words.map(word -> word.replaceAll("\\p{Punct}",""));
				//filter out non alphabetic words
				words = words.filter(word -> word.chars().allMatch(Character::isLetter));
				//filter out all empty words
				words = words.filter(word -> !word.isEmpty());
				filtertimems += System.currentTimeMillis() - start;
				start = System.currentTimeMillis();
				//add words to data structure
				words.forEach(word ->
				{	
					//System.out.println(word);
					if(wordfreq.containsKey(word)) 
						wordfreq.get(word).increment(); 
					else 
					{
						MutableInt m = new MutableInt(); 
						m.increment(); 
						wordList.add(new WordData(word, m)); 
						wordfreq.put(word,m);
					} 
				});
				processtimems+= System.currentTimeMillis()-start;
			}catch(Exception e)
			{ e.printStackTrace();}
			
		}
		long start = System.currentTimeMillis();
		//sort words by frequency
		Collections.sort(wordList);

		//print statistics
		System.out.println("Read time: "+(readtimems));
		System.out.println("Filter/Parse time: "+(filtertimems));
		System.out.println("Build Data Struct time: "+(processtimems));
		System.out.println("Sort time: "+(System.currentTimeMillis() - start));
		System.out.println("Processed "+numWords+" words");
		System.out.println("Processed "+wordList.size()+" unique words");

		//build and return output string
		String output = "";
		for(int i=0;i<numCommonWords;i++)
		{
			output = output + wordList.get(i).toString() + '\n';
		}
		return output;
	}

}	

	/** 
	 * MutableInt
	 * 
	 * This is just a wrapper around an int allowing it to be passed by reference
	 */	
	class MutableInt implements Comparable<MutableInt>
	{

		private int value;

		public void setValue(int value) {this.value=value;}		
		public int intValue() {return value;}
		public void increment(){value++;}
		public int compareTo(MutableInt other)
		{
			return other.value - value;
		}
	}

	/**
	 * WordData
	 * 
	 * Stores a String, MutableInt pair. Used to associate a word with a MutableInt
	 * that points to the number of times the word occurred.
	 */
       	class WordData implements Comparable<WordData>
	{
		private MutableInt value;
		private String word;

		public WordData(String word, MutableInt value)
		{
			this.word = word;
			this.value = value;
		}

		public int compareTo(WordData other)
		{
			return value.compareTo(other.value);
		}
		
		public String getWord() {return word;}
		
		public String toString()
		{
			return word+": "+value.intValue();
		}
		public int getValue()
		{
			return value.intValue();
		}		
	}


