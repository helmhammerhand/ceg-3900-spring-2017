
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.net.URL;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.Collections;
import java.io.IOException;

public class MostCommonWords
{

	public static void main(String[] args)
	{

		String ifnm = "urls.txt", ofnm = "/tmp/invalidUsers.txt";
		switch (args.length) {
			case 0: break;
			case 2: ofnm = args[ 1]; // fall through
			case 1: ifnm = args[ 0]; break;
			default:
				System.out.println("Usage: At most two file names expected");
				System.exit(0);
  		}
		long processtimems = 0;	
		long filtertimems = 0;
		long readtimems = 0;
		int numWords = 0;

		// data structures
		HashMap<String, MutableInt> wordfreq = new HashMap<>();
		ArrayList<WordData> wordList = new ArrayList<>();

		String[] urls = new String[0];
		try{

		urls = Files.lines(Paths.get(ifnm)).toArray(size -> new String[size]); // read urls from file
		} catch (IOException e)
		{ e.printStackTrace();}

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
		System.out.println("Read time: "+(readtimems));
		System.out.println("Filter/Parse time: "+(filtertimems));
		System.out.println("Build Data Struct time: "+(processtimems));
		System.out.println("Sort time: "+(System.currentTimeMillis() - start));
		System.out.println("Processed "+numWords+" words");
		System.out.println("Processed "+wordList.size()+" unique words");
		
		wordList.stream().forEach(x -> System.out.println(x));
	}
	
	public static Stream<String> getLines()
	{
		return Arrays.stream(new String[]{"This <b> b this is epic <p> epic epic fun <b> This"});
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

		public void setValue(int value) {this.value=value;} // setter
		public int intValue() {return value;} // getter
		public void increment(){value++;} // equivelent to setValue(intValue() + 1)
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
			return "WordData: "+word+", "+value.intValue();
		}		
	}


