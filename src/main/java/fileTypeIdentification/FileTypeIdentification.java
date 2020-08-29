package fileTypeIdentification;

/***
 * @author Siddharth
 * 
 *  Execution starts from FileTypeIdentification class
 *  HashMaps are created to load all known file types and their information from JSON files
 *  Input file provided by user is parsed and output is generated for each filename and written to a CSV file
 *  Additional information is provided in the form of frequency of each file extension type 
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class FileTypeIdentification {

	static HashMap<String, FileExtData> source1;
	static HashMap<String, FileExtData> source2;
	static HashMap<String, FileExtData> source3;

	/***
	 * This is the main method which creates multiple threads to store file extension information and then process input file.
	 * @param args - Not used
	 * @throws IOException on Input error
	 * @throws InterruptedException on execution being interrupted
	 */
	public static void main(String[] args) throws IOException, InterruptedException {

		source1 = new HashMap<String, FileExtData>();
		source2 = new HashMap<String, FileExtData>();
		source3 = new HashMap<String, FileExtData>();

		//create 3 threads to read JSON files and create 3 HashMaps for storing file information
		String sourceLocation = "src/data/";
		ReadThread t1 = new ReadThread(sourceLocation + "source1.json", source1);
		ReadThread t2 = new ReadThread(sourceLocation + "source2.json", source2);
		ReadThread t3 = new ReadThread(sourceLocation + "source3.json", source3);

		//start threads
		t1.start();
		t2.start();
		t3.start();

		//Get the input file from the user
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter the .txt file to be processed in src/input/");
		String inputFile = br.readLine();
		
		//If the file name provided is not valid then default file is used to generate output.
		if (inputFile.isEmpty() || !inputFile.endsWith(".txt")) 
		{
			System.out.println("\nInvalid file provided. Default input file taken.");
			inputFile = "input1.txt";
		}

		//Object to read file names from input file.
		BufferedReader inputFileNames;
		try 
		{
			inputFileNames = new BufferedReader(new FileReader("src/input/" + inputFile));
		} 
		//If file does not exist in the folder then default file is used.
		catch (FileNotFoundException e) 
		{
			System.out.println("\nFile Not Found! Default input File taken.");
			inputFile = "input1.txt";
			inputFileNames = new BufferedReader(new FileReader("src/input/" + inputFile));
		}
		
		System.out.println("\nInput File: " + inputFile + "\n");

		//List to store all file names from input file
		ArrayList<String> inputList = new ArrayList<String>();
		String fileNameNExtension;
		
		//read all file names from file and store in array list.
		while ((fileNameNExtension = inputFileNames.readLine()) != null)
			inputList.add(fileNameNExtension);
		inputFileNames.close();

		//Wait until all threads have finished execution. This ensures that the HashMaps are ready for computation
		t1.join();
		t2.join();
		t3.join();

		//Start processing each file name in list
		try 
		{
			//output.csv file stores a record of information (about the file extension) for each input file name
			//Create object to write into output.csv file. Enter the headers into the file
			Writer writer = Files.newBufferedWriter(Paths.get("src/output/output.csv"));
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("File Name", "File Extension",
					"Extension Name", "Category", "Applications", "Description"));

			
			//for each file name get the extension and retrieve all available information from HashMaps
			for (String fileName : inputList) 
			{
				int indexOfDot = fileName.lastIndexOf(".") + 1;
				String key = fileName.substring(indexOfDot).toLowerCase();
				String name, category, application, description;

				//Check if HashMap 1 contains the file extension
				if (source1.containsKey(key)) 
				{
					name = source1.get(key).name;
					category = source1.get(key).category;
					application = source1.get(key).application;
					description = source1.get(key).description;
				} 
				
				//Check if HashMap 2 contains file extension
				else if (source2.containsKey(key)) 
				{
					name = source2.get(key).name;
					category = source2.get(key).category;
					application = source2.get(key).application;
					description = source2.get(key).description;
				} 
				
				//Check if HashMap 3 contains file extension
				else if (source3.containsKey(key))
				{
					name = source3.get(key).name;
					category = source3.get(key).category;
					application = source3.get(key).application;
					description = source3.get(key).description;
				} 
				
				//If file extension does not exist in any HashMap
				else
				{
					name = "N/A";
					category = "N/A";
					application = "N/A";
					description = "N/A";
				}
				
				//Write all the retrieved data into the output.csv file
				csvPrinter.printRecord(fileName, key, name, category, application, description);
			}
			csvPrinter.flush();
			csvPrinter.close();
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("File Processed!");
	}

}
