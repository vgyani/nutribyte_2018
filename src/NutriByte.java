package hw3;

import hw3.Product.ProductNutrient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

//Vipin Gyanchandani	Andrew-id: vgyancha
public class NutriByte extends Application{
	static Model model = new Model();  	//made static to make accessible in the controller
	static View view = new View();		//made static to make accessible in the controller
	static Person person;			//made static to make accessible in the controller
	
	
	Controller controller = new Controller();	//all event handlers 

	/**Uncomment the following three lines if you want to try out the full-size data files */
	static final String PRODUCT_FILE = "data/Products.csv";
	static final String NUTRIENT_FILE = "data/Nutrients.csv";
	static final String SERVING_SIZE_FILE = "data/ServingSize.csv";
	
	/**The following constants refer to the data files to be used for this application */
//	static final String PRODUCT_FILE = "data/Nutri2Products.csv";
//	static final String NUTRIENT_FILE = "data/Nutri2Nutrients.csv";
//	static final String SERVING_SIZE_FILE = "data/Nutri2ServingSize.csv";
	
	static final String NUTRIBYTE_IMAGE_FILE = "NutriByteLogo.png"; //Refers to the file holding NutriByte logo image 

	static final String NUTRIBYTE_PROFILE_PATH = "profiles";  //folder that has profile data files

	static final int NUTRIBYTE_SCREEN_WIDTH = 1015;
	static final int NUTRIBYTE_SCREEN_HEIGHT = 675;
	
	// Person binding (Returns new person object as soon as profile data is changed. Invalid profile data returns a null object)
	ObjectBinding<Person> personBinding = new ObjectBinding<Person>() {
		
		{	// binding dependencies
			super.bind(NutriByte.view.ageTextField.textProperty(), NutriByte.view.weightTextField.textProperty(),
					NutriByte.view.heightTextField.textProperty(), NutriByte.view.physicalActivityComboBox.getSelectionModel().selectedIndexProperty(),
					NutriByte.view.genderComboBox.getSelectionModel().selectedIndexProperty(),
					NutriByte.view.ingredientsToWatchTextArea.textProperty());
		}
		
		@Override
		protected Person computeValue() {
			TextField textField = null;
			try {
				textField = NutriByte.view.ageTextField;
				textField.setStyle("-fx-text-inner-color: black;");
				float age = Float.parseFloat(textField.getText().trim());
				if(age < 0) throw new Exception();
				
				textField = NutriByte.view.weightTextField;
				textField.setStyle("-fx-text-inner-color: black;");
				float weight = Float.parseFloat(textField.getText().trim());
				if(weight < 0) throw new Exception();
				
				textField = NutriByte.view.heightTextField;
				textField.setStyle("-fx-text-inner-color: black;");
				float height = Float.parseFloat(textField.getText().trim());
				if(height < 0) throw new Exception();
				
				float physicalActivityLevel = NutriProfiler.PhysicalActivityEnum.values()[0].getPhysicalActivityLevel(); //set default physicalActivityLevel = Sedentary
				int physicalActivityLevelIndex = NutriByte.view.physicalActivityComboBox.getSelectionModel().getSelectedIndex();
				if(physicalActivityLevelIndex >= 0) //if physical activity level is selected
					physicalActivityLevel = NutriProfiler.PhysicalActivityEnum.values()[physicalActivityLevelIndex].getPhysicalActivityLevel();
				
				String ingredientsToAvoid = NutriByte.view.ingredientsToWatchTextArea.getText().trim();
				
				if (NutriByte.view.genderComboBox.getSelectionModel().getSelectedIndex()<0) //if gender is not selected
					return null;
				else if(NutriByte.view.genderComboBox.getSelectionModel().getSelectedIndex()==0)
					return new Female(age, weight, height, physicalActivityLevel, ingredientsToAvoid);
				else 
					return new Male(age, weight, height, physicalActivityLevel, ingredientsToAvoid);
			}catch (Exception e) {
				textField.setStyle("-fx-text-inner-color: red;");
				return null;
			}
		}
	};

	@Override
	public void start(Stage stage) throws Exception {
		model.readProducts(PRODUCT_FILE);
		model.readNutrients(NUTRIENT_FILE);
		model.readServingSizes(SERVING_SIZE_FILE );
		view.setupMenus();
		view.setupNutriTrackerGrid();
		view.root.setCenter(view.setupWelcomeScene());
		Background b = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
		view.root.setBackground(b);
		Scene scene = new Scene (view.root, NUTRIBYTE_SCREEN_WIDTH, NUTRIBYTE_SCREEN_HEIGHT);
		view.root.requestFocus();  //this keeps focus on entire window and allows the textfield-prompt to be visible
		setupBindings();
		stage.setTitle("NutriByte 3.0");
		stage.setScene(scene);
		
		// personBinding changeListener (updates NutriByte.person.recommendedNutrientsList as soon as personBinding changes)
		personBinding.addListener((observable, oldValue, newValue) -> {
			if(newValue != null)
			{
				NutriByte.person = newValue;
				NutriProfiler.createNutriProfile(NutriByte.person);
				NutriByte.view.recommendedNutrientsTableView.setItems(NutriByte.person.recommendedNutrientsList);
				NutriByte.view.createProfileButton.setDisable(true);
			}
			else
			{
				NutriByte.person.recommendedNutrientsList.clear();
				NutriByte.person = null;
				NutriByte.view.createProfileButton.setDisable(false);
			}
		});
		
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	void setupBindings() {
		view.newNutriProfileMenuItem.setOnAction(controller.new NewMenuItemHandler());
		view.openNutriProfileMenuItem.setOnAction(controller.new OpenMenuItemHandler());
		view.exitNutriProfileMenuItem.setOnAction(event -> Platform.exit());
		view.aboutMenuItem.setOnAction(controller.new AboutMenuItemHandler());
		
		view.recommendedNutrientNameColumn.setCellValueFactory(recommendedNutrientNameCallback);
		view.recommendedNutrientQuantityColumn.setCellValueFactory(recommendedNutrientQuantityCallback);
		view.recommendedNutrientUomColumn.setCellValueFactory(recommendedNutrientUomCallback);

		view.createProfileButton.setOnAction(controller.new RecommendNutrientsButtonHandler());
		
		// add event handlers and listeners
		view.searchButton.setOnAction(controller.new SearchButtonHandler());
		view.clearButton.setOnAction(controller.new ClearButtonHandler());
		view.addDietButton.setOnAction(controller.new AddDietButtonHandler());
		view.removeDietButton.setOnAction(controller.new RemoveDietButtonHandler());
		view.saveNutriProfileMenuItem.setOnAction(controller.new SaveMenuItemHandler());
		view.productsComboBox.valueProperty().addListener(controller.new ProductsComboBoxListener());
		
		//setCellValueFactory for productNutrientsTableView columns
		view.productNutrientNameColumn.setCellValueFactory(productNutrientNameCallback);
		view.productNutrientQuantityColumn.setCellValueFactory(productNutrientQuantityCallback);
		view.productNutrientUomColumn.setCellValueFactory(productNutrientUomCallback);
		
		//bind dietProductsTableView with dietProductsList
		NutriByte.view.dietProductsTableView.setItems(Person.dietProductsList);
		
		//closeNutriProfileMenuItem handler
		NutriByte.view.closeNutriProfileMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				view.newNutriProfileMenuItem.fire();
				view.root.setCenter(view.setupWelcomeScene());
				
			}
		});
		
	}
	
	Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>> recommendedNutrientNameCallback = new Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>>() {
		@Override
		public ObservableValue<String> call(CellDataFeatures<RecommendedNutrient, String> arg0) {
			Nutrient nutrient = Model.nutrientsMap.get(arg0.getValue().getNutrientCode());
			return nutrient.nutrientNameProperty();
		}
	};
	
	Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>> recommendedNutrientQuantityCallback = new Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>>() {
		@Override
		public ObservableValue<String> call(CellDataFeatures<RecommendedNutrient, String> arg0) {
			//returns the recommended nutrients' quantity
			return new SimpleStringProperty(String.format("%.2f", arg0.getValue().getNutrientQuantity()));
		}
	};
	
	Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>> recommendedNutrientUomCallback = new Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>>() {
		@Override
		public ObservableValue<String> call(CellDataFeatures<RecommendedNutrient, String> arg0) {
			//finds the nutrient’s unit of measure from nutrientMap and returns it.
			Nutrient nutrient = Model.nutrientsMap.get(arg0.getValue().getNutrientCode());
			return nutrient.nutrientUomProperty();
		}
	};
	
	Callback<CellDataFeatures<Product.ProductNutrient, String>, ObservableValue<String>> productNutrientNameCallback = new Callback<CellDataFeatures<ProductNutrient,String>, ObservableValue<String>>() {
		
		@Override
		public ObservableValue<String> call(CellDataFeatures<ProductNutrient, String> param) {
			// finds nutrient's name from NutrientsMap using nutrient code and returns it
			return Bindings.format("%s", Model.nutrientsMap.get(param.getValue().getNutrientCode()).getNutrientName());
		}
	};
	
	Callback<CellDataFeatures<Product.ProductNutrient, String>, ObservableValue<String>> productNutrientQuantityCallback = new Callback<CellDataFeatures<ProductNutrient,String>, ObservableValue<String>>() {
		
		@Override
		public ObservableValue<String> call(CellDataFeatures<ProductNutrient, String> param) {
			// returns nutrient quantity
			return Bindings.format("%.2f",param.getValue().getNutrientQuantity());
		}
	};
	
	Callback<CellDataFeatures<Product.ProductNutrient, String>, ObservableValue<String>> productNutrientUomCallback = new Callback<CellDataFeatures<ProductNutrient,String>, ObservableValue<String>>() {
		
		@Override
		public ObservableValue<String> call(CellDataFeatures<ProductNutrient, String> param) {
			// finds nutrient's UOM from NutrientsMap using nutrient code and returns it
			return Bindings.format("%s", Model.nutrientsMap.get(param.getValue().getNutrientCode()).getNutrientUom());
		}
	};
}
