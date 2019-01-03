package hw3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import hw3.Product.ProductNutrient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

//Vipin Gyanchandani	Andrew-id: vgyancha
public class Model {
	
	static ObservableMap<String, Product> productsMap = FXCollections.observableHashMap();
	static ObservableMap<String, Nutrient> nutrientsMap = FXCollections.observableHashMap();
	ObservableList<Product> searchResultsList = FXCollections.observableArrayList(); //used to store search results and populate productsComboBox
	
	//reads NutriByte.PRODUCT_FILE file to load product objects in the productsMap
	void readProducts(String filename) {
		CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
		try 
		{
			CSVParser csvParser = CSVParser.parse(new FileReader(filename), csvFormat);
			for (CSVRecord csvRecord : csvParser) //for each record in the .csv file
			{
				
				Product product = new Product(csvRecord.get(0), csvRecord.get(1),
												csvRecord.get(4), csvRecord.get(7));
				productsMap.put(csvRecord.get(0), product);
			}
		}
		catch (FileNotFoundException e1) { e1.printStackTrace(); }
		catch (IOException e1) { e1.printStackTrace(); }
	}
	
	//reads NutriByte.NUTRIENT_FILE to load objects in the nutrientsMap. Also loads ProductNutrient objects in each Product object in the productsMaps.
	// nutrientsMap will only hold unique nutrient objects.
	void readNutrients(String filename) {
		CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
		try 
		{
			CSVParser csvParser = CSVParser.parse(new FileReader(filename), csvFormat);
			for (CSVRecord csvRecord : csvParser) //for each record in the .csv file
			{
				Nutrient nutrient= new Nutrient(csvRecord.get(1), csvRecord.get(2), csvRecord.get(5));
				nutrientsMap.put(csvRecord.get(1), nutrient);
				if(productsMap.containsKey(csvRecord.get(0)))
				{
					if(Float.parseFloat(csvRecord.get(4))!=0) {
						ProductNutrient p = productsMap.get(csvRecord.get(0)).new ProductNutrient(csvRecord.get(1), Float.parseFloat(csvRecord.get(4)));
						productsMap.get(csvRecord.get(0)).getProductNutrients().put(csvRecord.get(1), p);
					}
				}
			}
		}
		catch (FileNotFoundException e1) { e1.printStackTrace(); }
		catch (IOException e1) { e1.printStackTrace(); }
	}
	
	//Reads NutriByte.SERVING_SIZE_FILE to populate four fields – servingSize, servingUom,
	//householdSize, householdUom - in each product object in the productsMaps
	void readServingSizes(String filename) {
		
		CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
		try 
		{
			CSVParser csvParser = CSVParser.parse(new FileReader(filename), csvFormat);
			for (CSVRecord csvRecord : csvParser) //for each record in the .csv file
			{		if(productsMap.containsKey(csvRecord.get(0))) {
						if(!csvRecord.get(1).isEmpty()) productsMap.get(csvRecord.get(0)).setServingSize(Float.parseFloat(csvRecord.get(1)));
						productsMap.get(csvRecord.get(0)).setServingUom(csvRecord.get(2));
						if(!csvRecord.get(3).isEmpty()) productsMap.get(csvRecord.get(0)).setHouseholdSize(Float.parseFloat(csvRecord.get(3)));
						productsMap.get(csvRecord.get(0)).setHouseholdUom(csvRecord.get(4));
					}
			}
		}
		catch (FileNotFoundException e1) { e1.printStackTrace(); }
		catch (IOException e1) { e1.printStackTrace(); }
		
	}
	
	//calls XMLFiler.readFile()  or CSVFiler.readFile() depending on the input filename
	boolean readProfile(String filename) {
		
		if(filename.contains(".csv")) {
			return new CSVFiler().readFile(filename);
		}
		else {
			return new XMLFiler().readFile(filename);
		}		
	}
	
	//calls writeFile() of CSVFiler class
	//allows user to save profile data and diet products in a new profile file as csv
	void writeProfile(String filename) {
			
			new CSVFiler().writeFile(filename);
		}
		
	
}