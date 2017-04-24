import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;

public class GradeAveragerServer
{


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
		   Runnable handler = new Runnable()
			{
			public void run()
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
				System.out.println("----------------------------------------------------------");
				//reply to client
				out.writeObject(response);
		        
			} catch (IOException e) {
			        System.out.println("Exception caught when trying to listen on port "
			            + portNumber + " or listening for a connection");
			        System.out.println(e.getMessage());
			} catch(ClassNotFoundException e)
			{ e.printStackTrace();}
			}
			};
			new Thread(handler).start();

		}

	    } catch (IOException e) {
	        System.out.println("Exception caught when trying to listen on port "
	            + portNumber + " or listening for a connection");
	        System.out.println(e.getMessage());
 
		}
	}

public static String processRequest(String googledriveurl)
{

try{	
	//download word list file
	URL zipfile = new URL(googledriveurl);
	System.out.println("Downloading");			
	//start the process
	String cmd = "wget "+googledriveurl+ " -O gradezip.tar.bzip2";
	cmd = cmd.replaceAll("&", "\\&");
	System.out.println(cmd);
	Process download = Runtime.getRuntime().exec(cmd);
			
	//read the output from process to block it
	Scanner s = new Scanner(download.getErrorStream());
	String output = "";
	while(s.hasNextLine())
		output += s.nextLine() + "\n";
	System.out.println(output);
	System.out.println("Download Complete");

	System.out.println("Unzipping");
	//unzip the file
	Process unzip = Runtime.getRuntime().exec("tar -vxjf gradezip.tar.bzip2");
	//read the output from process to block it
	s = new Scanner(unzip.getErrorStream());
	output = "";
	while(s.hasNextLine())
		output += s.nextLine() + "\n";
	System.out.println(output);
	System.out.println("Extraction complete");
	System.out.println("Averaging grades");
	File gradesfile = new File("./WorldWideGrades");
	output = computeAverageByCountry(gradesfile);
	return output;	
}catch(Exception e)
{
e.printStackTrace();
return "ERROR";
}
}
public static String computeAverageByCountry(File directory)
{

if(directory.exists())
{
	//get an array of all files in the directory
	File[] files = directory.listFiles();
	Stream<File> fileStream = Arrays.stream(files);
	//remove any directories from the file list
	fileStream = fileStream.filter(x -> !x.isDirectory());
	
	//for each file,
		//compute average grade
		//merge with any other average grade data for the country

	HashMap<String, AverageData> averages = new HashMap<>();
	System.out.println(files.length);
	fileStream.forEach(file -> {
		
		//get country of the file
		String country = file.getName().substring(0,4);
		System.out.println("Averaging: "+file);
		AverageData courseAvg = computeCourseAverage(file);
		if(averages.containsKey(country))
			averages.get(country).averageWith(courseAvg);
		else averages.put(country, courseAvg);
	});

	System.out.println("Found "+averages.size()+" countries");
	List<String> countryId = new ArrayList<>(averages.keySet());
	System.out.println(countryId.size());
	Collections.sort(countryId);
	
	StringBuilder sb = new StringBuilder();
	for(String country : countryId)
	{
	
		sb.append(country+"\t"+averages.get(country)+"\n");
	}
	String output = sb.toString();
	return output;
}
else
{
	return "Error: directory does not exist "+directory;
}



}
//end of main

public static AverageData computeCourseAverage(File file)
{
	try{
	AverageData ad = new AverageData();
	Stream<String> lineStream = Files.lines(Paths.get(file.getPath()));
	String[] lines = lineStream.toArray(size -> new String[size]);
	for(String line : lines)
	{
		ad.averageWith(Double.parseDouble(line.split("\\s+")[1]));
	}

	lineStream.close();
	return ad;

	}catch(Exception e)
	{
		e.printStackTrace();
		return new AverageData();
	}
}

}
//end of GradeAverager


class AverageData{

	int count=0;
	double average=0;

	public void averageWith(AverageData a)
	{
		double total = a.count * a.average + count * average;
		average = total / (a.count + count);
		count += a.count;
	}
	public void averageWith(double datapoint)
	{
		double total = average * count + datapoint;
		count ++;
		average = total / count;
	}
	public String toString()
	{return Double.toString(average);}
}

