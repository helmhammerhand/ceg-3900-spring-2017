
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.io.IOException;

/**
 * AuthLogChecker
 * by Paul Fuchs
 * 
 * This java program checks a Linux system's auth.log and writes the log entries
 * for any invalid log-in attempts to an output file.
 *
 * The input file and output file can be set from the command line according to
 * java AuthLogChecker <logfilename> <resultfilename>
 * The default values are
 *    logfilename = "/var/log/auth.log"
 *    resultsfilename = "/tmp/invalidUsers.txt"
 */
public class AuthLogChecker
{

  public static void main(String[] args)
  {
    //default ifnm changed to match where I downloaded the sample input file
    String ifnm = "/var/log/auth.log", ofnm = "/tmp/invalidUsers.txt";
    switch (args.length) {
    case 0: break;
    case 2: ofnm = args[ 1]; // fall through
    case 1: ifnm = args[ 0]; break;
    default:
       System.out.println
         ("Usage: At most two file names expected");
       System.exit(0);
    }

    try{
      //Regex will match any lines containing the phrase "Invalid user"
      final String INVALID_USER_REGEX = ".*Invalid\\suser.*";

      //Reads the input file by line, filters using the regex, and reduces 
      //to a single string with each string seperated by a new-line character
      String invalidUsers = Files.lines(Paths.get(ifnm))
                            .filter(str -> str.matches(INVALID_USER_REGEX))
                            .reduce("",(a, b) ->a + "\n" + b) + "\n";

      //writes the string of invalid users to the output file
      Files.write(Paths.get(ofnm), invalidUsers.getBytes());
 
    } catch(IOException e)
    { e.printStackTrace();}//handle exceptions

  } //end of main

} // end of AuthLogChecker
