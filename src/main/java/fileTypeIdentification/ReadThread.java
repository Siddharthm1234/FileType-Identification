package fileTypeIdentification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * 
 * @author Siddharth 
 * 
 * ReadThread class extends Thread class to implement multi-threading
 * run() function - uses a JSON parser to read a JSON file and store file extension and file information in a HashMap. 
 *
 */
public class ReadThread extends Thread {
	String sourceLocation;
	HashMap<String, FileExtData> source;

	public ReadThread(String sourceLocation, HashMap<String, FileExtData> source) {
		this.sourceLocation = sourceLocation;
		this.source = source;
	}

	@Override
	public void run() {
		readAndParse(sourceLocation, source);
	}

	/***
	 * function to read the given JSON file and store content in a JSON array
	 * @param sourceLocation - path to JSON file 
	 * @param source - HashMap to store file extension and file information 
	 * @return Nothing
	 */
	static void readAndParse(String sourceLocation, HashMap<String, FileExtData> source) {
		
		JSONParser jsonParser = new JSONParser();
		
		try {
			//object to read JSON file
			BufferedReader reader = new BufferedReader(new FileReader(sourceLocation));
			//convert the JSON file content to a JSON array
			JSONArray fileList = (JSONArray) jsonParser.parse(reader);
			parseSource(fileList, source);

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * function to store the JSON array in a HashMap
	 * @param fileList - JSON array that stores all file extension data
	 * @param source - HashMap to store file extension and file extension information
	 * @return Nothing
	 */
	public static void parseSource(JSONArray fileList, HashMap<String, FileExtData> source) {

		for (int i = 0; i < fileList.size(); i++) 
		{
			//extract each object from array by index
			JSONObject file = (JSONObject) fileList.get(i);
			
			//retrieve individual content from each object
			String ext = (String) (file.get("Extension")).toString().substring(1).toLowerCase();
			String name = (String) (file.get("Name"));
			String category = (String) (file.get("Category"));
			String application = (String) (file.get("Application"));
			String description = (String) (file.get("Description"));
			
			//create new object of FileExtData class and store it in HashMap
			FileExtData fileExtData = new FileExtData(name, category, application, description);
			source.put(ext, fileExtData);
		}
	}
}