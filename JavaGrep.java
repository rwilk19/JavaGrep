package homework5;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.regex.Matcher;

//The JavaGrep class implements the Runnable interface
public class JavaGrep implements Runnable {

	private String regularExpression;
	private String fileName;
	private int firstLine;
	private int lastLine;
	public static String showLineNumber = null;
	
	public static void main(String[] args) throws IOException {
		//start variable is used to measure performance
		long start = System.nanoTime();
		
		String regularExpression;
		String fileName;
		//If the command line has three arguments, the first is '-n' to show line numbers
		if(args.length == 3) {
			showLineNumber = args[0];
			regularExpression = args[1];
			fileName = args[2];
		//If the command line has two arguments, print matches without line numbers
		} else {
			regularExpression = args[0];
			fileName = args[1];
		}
		//Creates file and determines number of lines in the input file and halfway point
		File file1 = new File(fileName);
		int lineCount = getLineCount(file1);
		lineCount--;
		int halfWay = lineCount / 2;
		
		String firstLineSecondRegion = Files.readAllLines(Paths.get(fileName)).get(halfWay+1);
		Pattern pattern =  Pattern.compile(regularExpression);
	    Matcher match = null;
	    match = pattern.matcher(firstLineSecondRegion);
	    //If first line in second region matches regular expression...
	    //then first thread outputs the line
	    if(match.find()){
	    		while(match.find()) {
	    			halfWay++;	
	    			firstLineSecondRegion = Files.readAllLines(Paths.get(fileName)).get(halfWay);
	    			match = pattern.matcher(firstLineSecondRegion);
	    		}
		}
		
		//Pass first half of file into first runnable
		//and second half into second runnable
		Runnable firstHalf = new JavaGrep(regularExpression, fileName, 0, halfWay);
		Runnable secondHalf = new JavaGrep(regularExpression, fileName, halfWay + 1, lineCount);
		//Create the two threads
		Thread thread1 = new Thread(firstHalf);
		Thread thread2 = new Thread(secondHalf);
		//Starts first thread
		thread1.start();
        try { 
        		//The join method forces the second thread to wait for the first thread to finish
            thread1.join(); 
        } 
        catch(Exception ex) 
        { 
            System.out.println("Exception: " + ex); 
        } 
        
        //Starts the second thread
  		thread2.start();
  		try { 
    		//The join method forces the "Performance" output line to wait until second thread has finished
        thread2.join(); 
  		} 
  		catch(Exception ex) 
  		{ 
        System.out.println("Exception: " + ex); 
  		}
	
  		//end variable is used to measure performance
		long end = System.nanoTime();
		// end minus start gives time elapsed
		System.out.println("Performance: " + (end - start) + " nanoseconds");
		//scan.close();
	}
	//This static method gets the number of lines of the original file, which is used to...
	//break the input file into two halves.
	public static int getLineCount(File file) throws IOException {
		try (Stream<String> lines = Files.lines(file.toPath())) {
	        return (int) lines.count();
	    }
	}
	//Constructor, which creates JavaGrep objects  
	//In Main method, instances of JavaGrep objects are stored in references to Runnable, 
	//which are then passed in to create threads. 
	public JavaGrep(String regularExpression, String fileName, int firstLine, int lastLine) {
		this.regularExpression = regularExpression;
		this.fileName = fileName;
		this.firstLine = firstLine;
		this.lastLine = lastLine;
	}
	//Gets start line number
	public int getFirst() {
		return this.firstLine;
	}
	//Gets end line number
	public int getLast() {
		return this.lastLine;
	}
	//Implements the run method of Runnable interface
	@Override
	public void run() {
	    Pattern pattern =  Pattern.compile(regularExpression);
	    Matcher match = null;
	    String line = "";
	    	int first = this.getFirst();
	    	int last = this.getLast();
	    	//Loops until all lines in region have been searched for match
	    	for(int i = first; i < last + 1; ++i){
	    		try {
	    			//Gets the line
				line = Files.readAllLines(Paths.get(fileName)).get(first);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		match = pattern.matcher(line);
	    		//If '-n' was command line argument, then print line numbers too
	    		if(showLineNumber != null){
	    			if(match.find()) {
	    				System.out.print(first + "  ");
	    				System.out.println(line);
	    			}
	    		//If only two command line arguments, only matching lines are printed. 
	    		}else if(match.find()) {
	    			System.out.println(line);
	    		}
	    		first++;
	    	}
	}
}