package hw3;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//Vipin Gyanchandani	Andrew-id: vgyancha
public class RecommendedNutrient {
	
	private StringProperty nutrientCode = new SimpleStringProperty();
	private FloatProperty nutrientQuantity = new SimpleFloatProperty();
	
	public RecommendedNutrient() {
		this.nutrientCode.set("");
		this.nutrientQuantity.set(0);
	}

	public RecommendedNutrient(String nutrientCode, Float nutrientQuantity) {
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
		this.nutrientQuantity.set(nutrientQuantity);
	}
	
	public final StringProperty nutrientCodeProperty() {
		return nutrientCode;
	}
	
	public final FloatProperty nutrientQuantityProperty() {
		return nutrientQuantity;
	}
}
