package fileTypeIdentification;

/***
 * 
 * @author Siddharth
 * FileExtData class objects are used to store the information from JSON files to the HashMaps.
 * Parameterized constructor is used to initialize the data members. 
 *
 */

public class FileExtData {
	String name;
	String category;
	String application;
	String description;

	FileExtData(String name, String category, String application, String description) {
		this.name = name;
		this.category = category;
		this.application = application;
		this.description = description;
	}
}
