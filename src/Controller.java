package hw3;

import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;

import hw3.Product.ProductNutrient;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

//Vipin Gyanchandani	Andrew-id: vgyancha
public class Controller {

	//takes input from input fields, creates person object based on input, passes it to NutriProfiler.createNutriProfile() to populate recommendedNutrientsList
	//finally binds recommendedNutrientsList with recommendedNutrientsTableView
	class RecommendNutrientsButtonHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			try {
				if (NutriByte.view.genderComboBox.getSelectionModel().getSelectedIndex()<0) //if gender is not selected
					throw new InvalidProfileException("Missing gender information");
				
				if(NutriByte.view.ageTextField.getText().isEmpty())
					throw new InvalidProfileException("Missing age information");
				if(!NutriByte.view.ageTextField.getText().trim().matches("^([+-]?\\d+(\\.)?(\\d+)?)$|^([+-]?(\\d+)?(\\.)?\\d+)$")) //check if not a float
					throw new InvalidProfileException("Incorrect age input. Must be a number");
				float age = Float.parseFloat(NutriByte.view.ageTextField.getText().trim());
				if(age<0) throw new InvalidProfileException("Age must be a positive number");
				
				if(NutriByte.view.weightTextField.getText().isEmpty())
					throw new InvalidProfileException("Missing weight information");
				if(!NutriByte.view.weightTextField.getText().trim().matches("^([+-]?\\d+(\\.)?(\\d+)?)$|^([+-]?(\\d+)?(\\.)?\\d+)$")) //check if not a float
					throw new InvalidProfileException("Incorrect weight input. Must be a number");
				float weight = Float.parseFloat(NutriByte.view.weightTextField.getText().trim());
				if(weight < 0) throw new InvalidProfileException("Weight must be a positive number");
				
				if(NutriByte.view.heightTextField.getText().isEmpty())
					throw new InvalidProfileException("Missing height information");
				if(!NutriByte.view.heightTextField.getText().trim().matches("^([+-]?\\d+(\\.)?(\\d+)?)$|^([+-]?(\\d+)?(\\.)?\\d+)$")) //check if not a float
					throw new InvalidProfileException("Incorrect height input. Must be a number");
				float height = Float.parseFloat(NutriByte.view.heightTextField.getText().trim());
				if(height < 0) throw new InvalidProfileException("Height must be a positive number");
				
				String ingredientsToAvoid = NutriByte.view.ingredientsToWatchTextArea.getText().trim();
				
				int physicalActivityLevelIndex = NutriByte.view.physicalActivityComboBox.getSelectionModel().getSelectedIndex();
				float physicalActivityLevel = NutriProfiler.PhysicalActivityEnum.values()[physicalActivityLevelIndex].getPhysicalActivityLevel();
				
				if(NutriByte.view.genderComboBox.getSelectionModel().getSelectedIndex()==0) //if gender = female
					NutriProfiler.createNutriProfile(NutriByte.person = new Female(age, weight, height, physicalActivityLevel, ingredientsToAvoid));
				else 
					NutriProfiler.createNutriProfile(NutriByte.person = new Male(age, weight, height, physicalActivityLevel, ingredientsToAvoid));
				
				NutriByte.view.recommendedNutrientsTableView.setItems(NutriByte.person.recommendedNutrientsList);
			}
			catch (InvalidProfileException e) {}
		}			
	}
	
	//creates a fileChooser, passes selected filename to readProfile (which reads the file, populates profile data for valid data.
	//PersonBinding gets updated and PersonBinding Listener gets invoked automatically for valid profile data)
	class OpenMenuItemHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select file");
			fileChooser.setInitialDirectory(new File(NutriByte.NUTRIBYTE_PROFILE_PATH));
			fileChooser.getExtensionFilters().addAll(
					new ExtensionFilter("CSV Files", "*.csv"),
					new ExtensionFilter("XML Files", "*.xml"));
					File file = null;
					if((file = fileChooser.showOpenDialog(NutriByte.view.root.getScene().getWindow()))!=null) { 
						//if file is not null
						if(!NutriByte.model.readProfile(file.getAbsolutePath())) //stop if readProfile fails
							return;
						
						// execute below statements if readProfile returns true
						ObservableList<Product> tempList = FXCollections.observableArrayList();
						tempList.addAll(Person.dietProductsList);
						Person tempPerson = NutriByte.person;
						NutriByte.view.newNutriProfileMenuItem.fire(); //clears everything including Person.dietProductsList and NutriByte.person
						NutriByte.person = tempPerson;
						Person.dietProductsList = tempList;
						NutriByte.view.dietProductsTableView.setItems(Person.dietProductsList);
						
						//Note: any changes in profile data values updates PersonBinding and invokes listener attached to it
						NutriByte.view.ageTextField.setText(String.format("%.2f",NutriByte.person.age));
						NutriByte.view.weightTextField.setText(String.format("%.2f",NutriByte.person.weight));
						NutriByte.view.heightTextField.setText(String.format("%.2f",NutriByte.person.height));
						NutriByte.view.ingredientsToWatchTextArea.setText(NutriByte.person.ingredientsToWatch);
						
						for(NutriProfiler.PhysicalActivityEnum activity: NutriProfiler.PhysicalActivityEnum.values())
						{
							if(NutriByte.person.physicalActivityLevel==activity.getPhysicalActivityLevel())
							{
								NutriByte.view.physicalActivityComboBox.getSelectionModel().select(activity.ordinal());
							}
						}
						
						if(NutriByte.person instanceof Female)
						{	
							NutriByte.view.genderComboBox.getSelectionModel().select(0);
						}
						else
						{
							NutriByte.view.genderComboBox.getSelectionModel().select(1);
						}
						
						NutriByte.model.searchResultsList.clear(); //clears searchResultsList for valid data
						for(Product p: Person.dietProductsList)
						{
							NutriByte.model.searchResultsList.add(Model.productsMap.get(p.getNdbNumber()));
						}
						NutriByte.view.searchResultSizeLabel.setText(NutriByte.model.searchResultsList.size() + " product(s) found");
						
						
						NutriByte.view.productsComboBox.setItems(NutriByte.model.searchResultsList);
						NutriByte.view.productsComboBox.getSelectionModel().selectFirst();
						
						NutriByte.person.populateDietNutrientsMap();
						NutriByte.view.nutriChart.updateChart();
					}	
		}
	}

	// clears up all data from the previous user interaction, including nutriChart
	class NewMenuItemHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			NutriByte.view.root.setCenter(NutriByte.view.nutriProfilerGrid); //sets center of borderPane root as NutriByte.view.nutriProfilerGrid
			NutriByte.view.root.setBottom(NutriByte.view.nutriTrackerPane); //sets bottom of borderPane root as NutriByte.view.nutriTrackerPane
			NutriByte.view.initializePrompts();
			NutriByte.view.servingSizeLabel.setText("0.00");
			NutriByte.view.householdSizeLabel.setText("0.00");
			NutriByte.view.dietProductsTableView.getItems().clear();
			NutriByte.view.productNutrientsTableView.getItems().clear();
			NutriByte.view.recommendedNutrientsTableView.getItems().clear();
			NutriByte.model.searchResultsList.clear();
			Person.dietProductsList.clear();
			NutriByte.view.productSearchTextField.clear();
			NutriByte.view.nutrientSearchTextField.clear();
			NutriByte.view.ingredientSearchTextField.clear();
			NutriByte.view.dietHouseholdSizeTextField.clear();
			NutriByte.view.dietServingSizeTextField.clear();
			NutriByte.view.productIngredientsTextArea.clear();
			NutriByte.view.searchResultSizeLabel.setText("");
			NutriByte.view.dietServingUomLabel.setText("");
			NutriByte.view.dietHouseholdUomLabel.setText("");
			NutriByte.view.nutriChart.clearChart();		
		}
	}

	class AboutMenuItemHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("About");
			alert.setHeaderText("NutriByte");
			alert.setContentText("Version 2.0 \nRelease 1.0\nCopyleft Java Nerds\nThis software is designed purely for educational purposes.\nNo commercial use intended");
			Image image = new Image(getClass().getClassLoader().getResource(NutriByte.NUTRIBYTE_IMAGE_FILE).toString());
			ImageView imageView = new ImageView();
			imageView.setImage(image);
			imageView.setFitWidth(300);
			imageView.setPreserveRatio(true);
			imageView.setSmooth(true);
			alert.setGraphic(imageView);
			alert.showAndWait();
		}
	}
	
	//Handles all search functionality
	class SearchButtonHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			NutriByte.view.productsComboBox.setItems(NutriByte.model.searchResultsList); //bind combobox with list
			
			String productSearchTerm = NutriByte.view.productSearchTextField.getText().trim().toLowerCase();
			NutriByte.model.searchResultsList.clear();
			// for each Product in productMap, add that product to list if name contains searchTerm
			for(Entry<String, Product> searchProduct: Model.productsMap.entrySet())
			{
				if(searchProduct.getValue().getProductName().toLowerCase().contains(productSearchTerm))
				{
					NutriByte.model.searchResultsList.add(searchProduct.getValue());
				}
			}
			
			// filter searchResultsList based on nutrient search keyword
			String nutrientSearchTerm = NutriByte.view.nutrientSearchTextField.getText().trim().toLowerCase();
			if(!nutrientSearchTerm.isEmpty())
			{
				ArrayList<String> nutrientResultCodes = new ArrayList<>(); 
				for(Nutrient n: Model.nutrientsMap.values()) //gets all nutrient codes that contain nutrient search term in their name
				{
					if(n.getNutrientName().toLowerCase().contains(nutrientSearchTerm))
					{
						nutrientResultCodes.add(n.getNutrientCode());
					}
				}
				
				ArrayList<Product> searchResultsListTemp = new ArrayList<>(NutriByte.model.searchResultsList); //copy of searchResultsList
				NutriByte.model.searchResultsList.clear();
				for(Product p: searchResultsListTemp) // add product to searchResultsList if nutrient code found
				{
					for(String code: nutrientResultCodes)
					{
						if(p.getProductNutrients().containsKey(code))
						{
							NutriByte.model.searchResultsList.add(p);
							break;
						}
					}	
				}
			}
			
			//filter searchResultsList based on ingredient search
			String ingredientSearchTerm = NutriByte.view.ingredientSearchTextField.getText().trim().toLowerCase(); 
			if(!ingredientSearchTerm.isEmpty())
			{
				ArrayList<Product> searchResultsListTemp = new ArrayList<>(NutriByte.model.searchResultsList); //copy of searchResultsList
				NutriByte.model.searchResultsList.clear();
				for(Product p: searchResultsListTemp) // add product to searchResultsList if ingredient found
				{
					if (p.getIngredients().toLowerCase().contains(ingredientSearchTerm))
					{
						NutriByte.model.searchResultsList.add(p);
					}
				}
			}
			
			NutriByte.view.productsComboBox.getSelectionModel().selectFirst();
			NutriByte.view.searchResultSizeLabel.setText(NutriByte.model.searchResultsList.size() + " product(s) found");
		}
	}
	
	//Selecting any product in the combo-box invokes this listener and displays the product’s nutrients in the productNutrientsTableView and ingredients in the productIngredientsTextArea
	//Also updates serving size with serving Uom and household size with householdUom in region 3
	class ProductsComboBoxListener implements ChangeListener<Product>
	{
		@Override
		public void changed(ObservableValue<? extends Product> observable, Product oldValue, Product newValue) {
			if(NutriByte.view.productsComboBox.getItems().isEmpty())
				return;
			Product selectedProduct = NutriByte.view.productsComboBox.getSelectionModel().getSelectedItem();
			NutriByte.view.productNutrientsTableView.setItems(mapToList(selectedProduct.getProductNutrients()));
			NutriByte.view.productIngredientsTextArea.setText(selectedProduct.getIngredients());
			NutriByte.view.servingSizeLabel.setText(String.format("%.2f %s", selectedProduct.getServingSize(), selectedProduct.getServingUom()));
			NutriByte.view.householdSizeLabel.setText(String.format("%.2f %s", selectedProduct.getHouseholdSize(), selectedProduct.getHouseholdUom()));
			NutriByte.view.dietServingUomLabel.setText(selectedProduct.getServingUom());
			NutriByte.view.dietHouseholdUomLabel.setText(selectedProduct.getHouseholdUom());
		}
		
		ObservableList<ProductNutrient> mapToList(ObservableMap<String, ProductNutrient> pnObsMap) 
		 {
		        ObservableList<ProductNutrient> pnObsList = FXCollections.observableArrayList();
		        for (ProductNutrient pn: pnObsMap.values())
		        {
		            pnObsList.add(pn);
		        }
		        return pnObsList;
		 }
	
	}
	
	//Clears product, nutrient, and ingredient search boxes and all products from the productsComboBox
	class ClearButtonHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent arg0) {
			NutriByte.model.searchResultsList.clear();
			NutriByte.view.productNutrientsTableView.getItems().clear();
			NutriByte.view.productIngredientsTextArea.clear();
			NutriByte.view.productSearchTextField.clear();
			NutriByte.view.nutrientSearchTextField.clear();
			NutriByte.view.ingredientSearchTextField.clear();
			NutriByte.view.searchResultSizeLabel.setText("");
			NutriByte.view.dietHouseholdUomLabel.setText("");
			NutriByte.view.dietServingUomLabel.setText("");
			NutriByte.view.householdSizeLabel.setText("0.00");
			NutriByte.view.servingSizeLabel.setText("0.00");
		}	
	}
	
	//Adds product selected in the productsComboBox to the dietProductsList (dietProductsTableView is bound with dietProductsList)
	//also calls populateDietNutrientsMap and updates nutrichart
	class AddDietButtonHandler implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent event) {
			
			if(NutriByte.view.productsComboBox.getSelectionModel().getSelectedIndex() < 0)
				return;
			
			Product selectedProduct = NutriByte.view.productsComboBox.getSelectionModel().getSelectedItem();
			
			String input1 = NutriByte.view.dietServingSizeTextField.getText().trim();
			String input2 = NutriByte.view.dietHouseholdSizeTextField.getText().trim();
			
			if(input1.isEmpty() && input2.isEmpty())
			{
				Person.dietProductsList.add(selectedProduct);
			}
			//missing size value is calculated based on ratio of standard serving size vs. standard household size
			else if (input2.isEmpty())
			{
				Product p = new Product(selectedProduct);
				p.setServingSize(Float.parseFloat(input1));
				if(selectedProduct.getServingSize()!=0.0f)
				{
					float roundOff = Math.round(selectedProduct.getHouseholdSize()/selectedProduct.getServingSize()*Float.parseFloat(input1)*100.0)/100.0f;
					p.setHouseholdSize(roundOff);
				}
				Person.dietProductsList.add(p);
			}
			//missing size value is calculated based on ratio of standard serving size vs. standard household size
			else if (input1.isEmpty())
			{
				Product p = new Product(selectedProduct);
				p.setHouseholdSize(Float.parseFloat(input2));
				if(selectedProduct.getHouseholdSize()!=0.0f)
				{
					float roundOff = Math.round(selectedProduct.getServingSize()/selectedProduct.getHouseholdSize()*Float.parseFloat(input2)*100.0)/100.0f;
					p.setServingSize(roundOff);
				}
				Person.dietProductsList.add(p);
			}
			//entered household value is ignored and calculated based on ratio of standard serving size vs. standard household size
			else
			{
				Product p = new Product(selectedProduct);
				p.setServingSize(Float.parseFloat(input1));
				if(selectedProduct.getServingSize()!=0.0f)
				{
					float roundOff = Math.round(selectedProduct.getHouseholdSize()/selectedProduct.getServingSize()*Float.parseFloat(input1)*100.0)/100.0f;
					p.setHouseholdSize(roundOff);
				}
				Person.dietProductsList.add(p);
			}
			
			if(!(NutriByte.person==null))
			{
				NutriByte.person.populateDietNutrientsMap();
				NutriByte.view.nutriChart.updateChart();
			}
			
		}
	}
	
	//Clicking this button reverses the process done in AddDietButtonHandler
	class RemoveDietButtonHandler implements EventHandler<ActionEvent>
	{

		@Override
		public void handle(ActionEvent event) {
			if(NutriByte.view.dietProductsTableView.getItems().isEmpty())
				return;
				
			if(NutriByte.view.dietProductsTableView.getSelectionModel().getSelectedIndex()<0)
				return;
			
		 	int selectedIndex = NutriByte.view.dietProductsTableView.getSelectionModel().getSelectedIndex();
		 	Person.dietProductsList.remove(selectedIndex);
		 	
		 	if(!(NutriByte.person==null))
			{
				NutriByte.person.populateDietNutrientsMap();
				NutriByte.view.nutriChart.updateChart();
				if(NutriByte.view.dietProductsTableView.getItems().isEmpty())
					NutriByte.view.nutriChart.clearChart();
			}
		}
		
	}
	
	//Performs all required validation and exception handling for person data
	//Opens up File chooser for valid person data, and when a file name is entered by the user, takes the person-data (region 1) and diet data (region 3) and saves it into a new profile file.
	class SaveMenuItemHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent arg0) {
			
			try {
				if (NutriByte.view.genderComboBox.getSelectionModel().getSelectedIndex()<0) //if gender is not selected
					throw new InvalidProfileException("Missing gender information");
				
				if(NutriByte.view.ageTextField.getText().isEmpty())
					throw new InvalidProfileException("Missing age information");
				if(!NutriByte.view.ageTextField.getText().trim().matches("^([+-]?\\d+(\\.)?(\\d+)?)$|^([+-]?(\\d+)?(\\.)?\\d+)$")) //check if not a float
					throw new InvalidProfileException("Incorrect age input. Must be a number");
				float age = Float.parseFloat(NutriByte.view.ageTextField.getText().trim());
				if(age<0) throw new InvalidProfileException("Age must be a positive number");
				
				if(NutriByte.view.weightTextField.getText().isEmpty())
					throw new InvalidProfileException("Missing weight information");
				if(!NutriByte.view.weightTextField.getText().trim().matches("^([+-]?\\d+(\\.)?(\\d+)?)$|^([+-]?(\\d+)?(\\.)?\\d+)$")) //check if not a float
					throw new InvalidProfileException("Incorrect weight input. Must be a number");
				float weight = Float.parseFloat(NutriByte.view.weightTextField.getText().trim());
				if(weight < 0) throw new InvalidProfileException("Weight must be a positive number");
				
				if(NutriByte.view.heightTextField.getText().isEmpty())
					throw new InvalidProfileException("Missing height information");
				if(!NutriByte.view.heightTextField.getText().trim().matches("^([+-]?\\d+(\\.)?(\\d+)?)$|^([+-]?(\\d+)?(\\.)?\\d+)$")) //check if not a float
					throw new InvalidProfileException("Incorrect height input. Must be a number");
				float height = Float.parseFloat(NutriByte.view.heightTextField.getText().trim());
				if(height < 0) throw new InvalidProfileException("Height must be a positive number");
			}
			catch (InvalidProfileException e) {
				return;
			}
			
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select file");
			fileChooser.setInitialDirectory(new File(NutriByte.NUTRIBYTE_PROFILE_PATH));
			fileChooser.getExtensionFilters().addAll(
					new ExtensionFilter("CSV Files", "*.csv"));
					File file = null;
					if((file = fileChooser.showSaveDialog(NutriByte.view.root.getScene().getWindow()))!=null) { //if file is not null
						NutriByte.model.writeProfile(file.getAbsolutePath());
					}
		}
	}
}

