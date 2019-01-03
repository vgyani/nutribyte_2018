package hw3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

//Vipin Gyanchandani	Andrew-id: vgyancha
public class CSVFiler extends DataFiler{

	@Override
	public void writeFile(String filename) {
		
		float age = Float.parseFloat(NutriByte.view.ageTextField.getText().trim());
		float weight = Float.parseFloat(NutriByte.view.weightTextField.getText().trim());
		float height = Float.parseFloat(NutriByte.view.heightTextField.getText().trim());
		int physicalActivityLevelIndex = NutriByte.view.physicalActivityComboBox.getSelectionModel().getSelectedIndex();
		float physicalActivityLevel = NutriProfiler.PhysicalActivityEnum.values()[physicalActivityLevelIndex].getPhysicalActivityLevel();
		String ingredientsToAvoid = NutriByte.view.ingredientsToWatchTextArea.getText().trim();
		
		String personData = null;
		if(NutriByte.view.genderComboBox.getSelectionModel().getSelectedIndex()==0) //if gender = female
			personData = "Female" + ", " + age + ", " + weight + ", " + height + ", " + physicalActivityLevel + ", " + ingredientsToAvoid;
		else 
			personData = "Male" + ", " + age + ", " + weight + ", " + height + ", " + physicalActivityLevel + ", " + ingredientsToAvoid;
		
		StringBuilder data = new StringBuilder();
		data.append(personData);
		
		for(Product p: Person.dietProductsList)
		{
			String s = p.getNdbNumber() + ", " + p.getServingSize() + ", " + p.getHouseholdSize();
			data.append("\n" + s);
		}
	
		try(FileWriter fw = new FileWriter(filename))
		{
			fw.write(data.toString());
		}
		catch (Exception e)
		{
			new InvalidProfileException("Could not save profile data");
		}
		
	}

//	Takes csv filename, reads the first line that has gender, age, weight, height, physical activity
//	level, and a series of comma separated ingredients to watch. Uses this data to create a Male or Female object and
//	assigns it to NutriByte.person. Returns true if file read successfully. Returns false otherwise.
// Also, populates Person.dietProductsList from CSV data if profile data is valid
	@Override
	public boolean readFile(String filename) {
		
			try (Scanner profileScanner = new Scanner(new FileReader(new File(filename))))
			{
				NutriByte.person = validatePersonData(profileScanner.nextLine());
				
				if (NutriByte.person!=null) // if person is not null, return true and populate product data in Person.dietProductsList
				{
					Person.dietProductsList.clear();
					while(profileScanner.hasNextLine())
					{
						Product p = validateProductData(profileScanner.nextLine());
						if(p!=null)
						{
							Person.dietProductsList.add(p);
						}
					}
					return true;
				}
				else
					return false;
			} 
			catch (FileNotFoundException e) 
			{
				new InvalidProfileException("File not found");
				return false;
			}
		
		}
		
	//Takes the first string with gender, age, weight...etc. and checks each value one by one.
	//if some data is missing or invalid, throws InvalidProfileException with appropriate message
	//returns Person object for valid data
	Person validatePersonData(String data)
	{
		String[] rowData = data.split(",");
		try
		{
			if(!(rowData[0].toLowerCase().trim().equals("male") || rowData[0].toLowerCase().trim().equals("female")))
				throw new InvalidProfileException("The profile must have gender: Female or Male as first word");
			
			float age = Float.parseFloat(rowData[1].trim());
			if (age < 0) throw new InvalidProfileException("Invalid data for Age: " + age + "\nAge must be a positive number");
			
			float weight =  Float.parseFloat(rowData[2].trim());
			if (weight < 0) throw new InvalidProfileException("Invalid data for Weight: " + weight + "\nWeight must be a positive number");
			
			float height =  Float.parseFloat(rowData[3].trim());
			if (height < 0) throw new InvalidProfileException("Invalid data for Height: " + height + "\nHeight must be a positive number");
			
			float physicalActivityLevel = Float.parseFloat(rowData[4].trim());
			if (!(physicalActivityLevel == 1.00f || physicalActivityLevel == 1.1f || physicalActivityLevel == 1.25f || physicalActivityLevel == 1.48f))
				throw new InvalidProfileException("Invalid Physical Activity Level: " + physicalActivityLevel + "\nphysical Activity Level must be: 1.0, 1.1, 1.25 or 1.48");
			
			StringBuilder ingredientsToWatch = new StringBuilder();
			for(int i=5; i<rowData.length;i++)
			{
				if(i!=rowData.length-1)
				{
					ingredientsToWatch.append(rowData[i].trim() + ", ");
				}
				else {
					ingredientsToWatch.append(rowData[i].trim());
				}
			}
			
			if(rowData[0].toLowerCase().trim().equals("male")) 
			{
				return new Male(Float.parseFloat(rowData[1].trim()), Float.parseFloat(rowData[2].trim()), Float.parseFloat(rowData[3].trim()), Float.parseFloat(rowData[4].trim()), ingredientsToWatch.toString());
			}
			else
			{
				return new Female(Float.parseFloat(rowData[1].trim()), Float.parseFloat(rowData[2].trim()), Float.parseFloat(rowData[3].trim()), Float.parseFloat(rowData[4].trim()), ingredientsToWatch.toString());
			}
			
		}
		catch (InvalidProfileException e)
		{
			new InvalidProfileException("Could not read profile data");
			return null;
		}
		catch (NumberFormatException e) {
			try {Float.parseFloat(rowData[1].trim());}
				catch(NumberFormatException e1){new InvalidProfileException("Invalid data for Age: " + rowData[1] + "\nAge must be a positive number");}
			try {Float.parseFloat(rowData[2].trim());}
				catch(NumberFormatException e1){new InvalidProfileException("Invalid data for Weight: " + rowData[2] + "\nWeight must be a positive number");}
			try {Float.parseFloat(rowData[3].trim());}
				catch(NumberFormatException e1){new InvalidProfileException("Invalid data for Height: " + rowData[3] + "\nHeight must be a positive number");}
			try {Float.parseFloat(rowData[4].trim());}
				catch(NumberFormatException e1){new InvalidProfileException("Invalid Physical Activity Level: " + rowData[4] + "\nphysical Activity Level must be: 1.0, 1.1, 1.25 or 1.48");}
			new InvalidProfileException("Could not read profile data");
			return null;
		}
		catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			if(rowData.length==0) new InvalidProfileException("The profile must have gender: Female or Male as first word");
			else if (rowData.length==1) new InvalidProfileException("Missing age. Second value must be a number");
			else if (rowData.length==2) new InvalidProfileException("Missing weight. Third value must be a number");
			else if (rowData.length==3) new InvalidProfileException("Missing height. Fourth value must be a number");
			else if (rowData.length==4) new InvalidProfileException("Missing physical activity level. Fifth value must be 1.0, 1.1, 1.25 or 1.48");
			new InvalidProfileException("Could not read profile data");
			return null;
		}
		
	}
	
	//takes one product data and returns product object
	//if invalid or missing data, then throws InvalidProfileException and returns null object
	Product validateProductData(String data)
	{
		String[] productRow = data.split(",");
		Product p = null;
		try {
				if(Model.productsMap.get(productRow[0].trim())==null)
					if(p==null) throw new InvalidProfileException("No product found with this code: " + productRow[0]);
				p= new Product(Model.productsMap.get(productRow[0].trim()));
				
		}
		catch (NullPointerException | ArrayIndexOutOfBoundsException | InvalidProfileException e)
		{
			if(e instanceof NullPointerException || e instanceof ArrayIndexOutOfBoundsException)
				new InvalidProfileException("Missing product code\nThe data must be - String, number, number - for ndb number, serving size, household size");
			return null;
		}
	
		
		try
		{
			float servingSize = Float.parseFloat(productRow[1].trim());
			if(servingSize < 0) throw new InvalidProfileException("Cannot read: " + data + "\nServing size cannot be negative");
			p.setServingSize(servingSize);
		}
		catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException | InvalidProfileException e)
		{
			if(e instanceof NullPointerException || e instanceof ArrayIndexOutOfBoundsException)
				new InvalidProfileException("Cannot read: " + data + "\nThe data must be - String, number, number - for ndb number, serving size, household size");
			if(e instanceof NumberFormatException)
				new InvalidProfileException("Cannot read: " + data +  "\nThe data must be - String, number, number - for ndb number, serving size, household size");
			return null;
		}
		
		
		try
		{
			float houseHoldSize = Float.parseFloat(productRow[2].trim());
			if(houseHoldSize < 0) throw new InvalidProfileException("Cannot read: " + data +  "\nHousehold size cannot be negative");
			p.setHouseholdSize(houseHoldSize);
		}
		catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException | InvalidProfileException e)
		{
			if(e instanceof NullPointerException || e instanceof ArrayIndexOutOfBoundsException)
				new InvalidProfileException("Cannot read: " + data + "\nThe data must be - String, number, number - for ndb number, serving size, household size");
			if(e instanceof NumberFormatException)
				new InvalidProfileException("Cannot read: " + data + "\nThe data must be - String, number, number - for ndb number, serving size, household size");
			return null;
		}

		return p;
	}
}
