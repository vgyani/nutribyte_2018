package hw3;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//Vipin Gyanchandani	Andrew-id: vgyancha
public class Nutrient {
	
	private StringProperty nutrientCode = new SimpleStringProperty();
	private StringProperty nutrientName = new SimpleStringProperty();
	private StringProperty nutrientUom = new SimpleStringProperty();
	
	public Nutrient() {
		nutrientCode.set("");
		nutrientName.set("");
		nutrientUom.set("");
	}

	public Nutrient(String nutrientCode, String nutrientName, String nutrientUom) {
		this.nutrientCode.set(nutrientCode);
		this.nutrientName.set(nutrientName);
		this.nutrientUom.set(nutrientUom);
	}

	public final String getNutrientCode() {
		return nutrientCode.get();
	}

	public final void setNutrientCode(String nutrientCode) {
		this.nutrientCode.set(nutrientCode);
	}

	public final String getNutrientName() {
		return nutrientName.get();
	}

	public final void setNutrientName(String nutrientName) {
		this.nutrientName.set(nutrientName);
	}

	public final String getNutrientUom() {
		return nutrientUom.get();
	}

	public final void setNutrientUom(String nutrientUom) {
		this.nutrientUom.set(nutrientUom);
	}
	
	public final StringProperty nutrientNameProperty() {
		return nutrientName;
	}
	
	public final StringProperty nutrientCodeProperty() {
		return nutrientCode;
	}
	
	public final StringProperty nutrientUomProperty() {
		return nutrientUom;
	}
	
}
