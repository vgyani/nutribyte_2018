package hw3;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

//Vipin Gyanchandani	Andrew-id: vgyancha
public class Product {

	private StringProperty ndbNumber = new SimpleStringProperty();
	private StringProperty productName = new SimpleStringProperty();
	private StringProperty manufacturer = new SimpleStringProperty();
	private StringProperty ingredients = new SimpleStringProperty();
	private FloatProperty servingSize = new SimpleFloatProperty();
	private StringProperty servingUom = new SimpleStringProperty();
	private FloatProperty householdSize = new SimpleFloatProperty();
	private StringProperty householdUom = new SimpleStringProperty();
	
	private ObservableMap<String, ProductNutrient> productNutrients = FXCollections.observableHashMap();
	
	public Product() {
		this.ndbNumber.set("");
		this.productName.set("");
		this.manufacturer.set("");
		this.ingredients.set("");
		this.servingSize.set(0);
		this.servingUom.set("");
		this.householdSize.set(0);
		this.householdUom.set("");
	}

	public Product(String ndbNumber, String productName, String manufacturer,
			String ingredients) {
		this.ndbNumber.set(ndbNumber);
		this.productName.set(productName);
		this.manufacturer.set(manufacturer);
		this.ingredients.set(ingredients);
		this.servingSize.set(0);
		this.servingUom.set("");
		this.householdSize.set(0);
		this.householdUom.set("");
	}
	
	//copy constructor
	public Product(Product p)
	{
		this.ndbNumber.set(p.getNdbNumber());
		this.productName.set(p.getProductName());
		this.manufacturer.set(p.getManufacturer());
		this.ingredients.set(p.getIngredients());
		this.servingSize.set(p.getServingSize());
		this.servingUom.set(p.getServingUom());
		this.householdSize.set(p.getHouseholdSize());
		this.householdUom.set(p.getHouseholdUom());
		this.productNutrients = p.productNutrients;
	}
	
	// ComboBox uses String value returned by toString method for display of item
	@Override
	public String toString() {
		return (getProductName() + " by " + getManufacturer());
	}

	public final String getNdbNumber() {
		return ndbNumber.get();
	}

	public final void setNdbNumber(String ndbNumber) {
		this.ndbNumber.set(ndbNumber);
	}

	public final String getProductName() {
		return productName.get();
	}

	public final void setProductName(String productName) {
		this.productName.set(productName);;
	}

	public final String getManufacturer() {
		return manufacturer.get();
	}

	public final void setManufacturer(String manufacturer) {
		this.manufacturer.set(manufacturer);;
	}

	public final String getIngredients() {
		return ingredients.get();
	}

	public final void setIngredients(String ingredients) {
		this.ingredients.set(ingredients);;
	}

	public final Float getServingSize() {
		return servingSize.get();
	}

	public final void setServingSize(Float servingSize) {
		this.servingSize.set(servingSize);;
	}

	public final String getServingUom() {
		return servingUom.get();
	}

	public final void setServingUom(String servingUom) {
		this.servingUom.set(servingUom);;
	}

	public final Float getHouseholdSize() {
		return householdSize.get();
	}

	public final void setHouseholdSize(Float householdSize) {
		this.householdSize.set(householdSize);;
	}

	public final String getHouseholdUom() {
		return householdUom.get();
	}

	public final void setHouseholdUom(String householdUom) {
		this.householdUom.set(householdUom);;
	}
	
	public final StringProperty ndbNumberProperty() {
		return ndbNumber;
	}
	
	public final StringProperty productNameProperty() {
		return productName;
	}
	
	public final StringProperty manufacturerProperty() {
		return manufacturer;
	}
	
	public final StringProperty ingredientsProperty() {
		return ingredients;
	}
	
	public final FloatProperty servingSizeProperty() {
		return servingSize;
	}
	
	public final StringProperty servingUomProperty() {
		return servingUom;
	}
	
	public final FloatProperty householdSizeProperty() {
		return householdSize;
	}
	
	public final StringProperty householdUomProperty() {
		return householdUom;
	}
	
	public ObservableMap<String, ProductNutrient> getProductNutrients() {
		return productNutrients;
	}

	public void setProductNutrients(ObservableMap<String, ProductNutrient> productNutrients) {
		this.productNutrients = productNutrients;
	}

	
	
	class ProductNutrient{
		private StringProperty nutrientCode = new SimpleStringProperty();
		private FloatProperty nutrientQuantity = new SimpleFloatProperty();
		
		public ProductNutrient() {
			this.nutrientCode.set("");
			this.nutrientQuantity.set(0);;
		}

		public ProductNutrient(String nutrientCode, Float nutrientQuantity) {
			this.nutrientCode.set(nutrientCode);
			this.nutrientQuantity.set(nutrientQuantity);
		}

		public final String getNutrientCode() {
			return nutrientCode.get();
		}

		public final void setNutrientCode(String nutrientCode) {
			this.nutrientCode.set(nutrientCode);
		}

		public final Float getNutrientQuantity() {
			return nutrientQuantity.get();
		}

		public final void setNutrientQuantity(Float nutrientQuantity) {
			this.nutrientQuantity.set(nutrientQuantity);;
		}
		
		public final StringProperty nutrientCodeProperty() {
			return nutrientCode;
		}

		public final FloatProperty nutrientQuantityProperty() {
			return nutrientQuantity;
		}
		
	}

}
