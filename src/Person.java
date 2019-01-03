package hw3;

import hw3.NutriProfiler.AgeGroupEnum;
import hw3.NutriProfiler.NutriEnum;
import hw3.Product.ProductNutrient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

//Vipin Gyanchandani	Andrew-id: vgyancha
public abstract class Person {
	
	ObservableList<RecommendedNutrient> recommendedNutrientsList = FXCollections.observableArrayList();
	static ObservableList<Product> dietProductsList =  FXCollections.observableArrayList(); //used to store products added by the user in dietProductsTableView
	ObservableMap<String, RecommendedNutrient> dietNutrientsMap = FXCollections.observableHashMap(); //used to store the total of all nutrients based on products in dietProductsList. Used by NutriChart class to display four nutrient’s total actual values

	float age, weight, height, physicalActivityLevel; //age in years, weight in kg, height in cm
	String ingredientsToWatch;
	float[][] nutriConstantsTable = new float[NutriProfiler.RECOMMENDED_NUTRI_COUNT][NutriProfiler.AGE_GROUP_COUNT];

	NutriProfiler.AgeGroupEnum ageGroup;

	abstract void initializeNutriConstantsTable();
	abstract float calculateEnergyRequirement();

	//non- default constructor
	Person(float age, float weight, float height, float physicalActivityLevel, String ingredientsToWatch) {
		this.age = age;
		this.weight = weight;
		this.height = height;
		this.physicalActivityLevel = physicalActivityLevel;
		this.ingredientsToWatch = ingredientsToWatch;
		
		//set ageGroup based on age
		if(age <= AgeGroupEnum.MAX_AGE_3M.getAge()) ageGroup = AgeGroupEnum.MAX_AGE_3M;
		else if (age <= AgeGroupEnum.MAX_AGE_6M.getAge()) ageGroup = AgeGroupEnum.MAX_AGE_6M;
		else if (age <= AgeGroupEnum.MAX_AGE_1Y.getAge()) ageGroup = AgeGroupEnum.MAX_AGE_1Y;
		else if (age <= AgeGroupEnum.MAX_AGE_3Y.getAge()) ageGroup = AgeGroupEnum.MAX_AGE_3Y;
		else if (age <= AgeGroupEnum.MAX_AGE_8Y.getAge()) ageGroup = AgeGroupEnum.MAX_AGE_8Y;
		else if (age <= AgeGroupEnum.MAX_AGE_13Y.getAge()) ageGroup = AgeGroupEnum.MAX_AGE_13Y;
		else if (age <= AgeGroupEnum.MAX_AGE_18Y.getAge()) ageGroup = AgeGroupEnum.MAX_AGE_18Y;
		else if (age <= AgeGroupEnum.MAX_AGE_30Y.getAge()) ageGroup = AgeGroupEnum.MAX_AGE_30Y;
		else if (age <= AgeGroupEnum.MAX_AGE_50Y.getAge()) ageGroup = AgeGroupEnum.MAX_AGE_50Y;
		else ageGroup = AgeGroupEnum.MAX_AGE_ABOVE;
	}

	//returns an array of nutrient values of size NutriProfiler.RECOMMENDED_NUTRI_COUNT. 
	//Each value is calculated as follows:
	//For Protein, it multiples the constant with the person's weight.
	//For Carb and Fiber, it simply takes the constant from the 
	//nutriConstantsTable based on NutriEnums' nutriIndex and the person's ageGroup
	//For others, it multiples the constant with the person's weight and divides by 1000.
	float[] calculateNutriRequirement() {
		float[] nutriValues = new float[NutriProfiler.RECOMMENDED_NUTRI_COUNT];
		
		for(NutriEnum nutrient: NutriEnum.values())
		{
			if(nutrient.getNutriIndex()==NutriEnum.PROTEIN.getNutriIndex()) {
				nutriValues[nutrient.getNutriIndex()]=nutriConstantsTable[nutrient.getNutriIndex()][ageGroup.getAgeGroupIndex()]*weight;
			}
			else if(nutrient.getNutriIndex()==NutriEnum.CARBOHYDRATE.getNutriIndex() || nutrient.getNutriIndex()==NutriEnum.FIBER.getNutriIndex())
			{
				nutriValues[nutrient.getNutriIndex()]=nutriConstantsTable[nutrient.getNutriIndex()][ageGroup.getAgeGroupIndex()];
			}
			else
			{
				nutriValues[nutrient.getNutriIndex()]=nutriConstantsTable[nutrient.getNutriIndex()][ageGroup.getAgeGroupIndex()]*weight/1000;
			}
		}
		return nutriValues;
	}
	
	// takes the data from dietProductList populated in CSVFiler or AddDietButtonHandler and populates dietNutrientsMap. First clears dietNutrientsMap one each call
	public void populateDietNutrientsMap(){
		dietNutrientsMap.clear(); 
		
		for(Product p: dietProductsList)
		{
			for(ProductNutrient pn: p.getProductNutrients().values())
			{
				if(!dietNutrientsMap.containsKey(pn.getNutrientCode()))
				{
					float nutrientQuantity = pn.getNutrientQuantity()/100*p.getServingSize(); //pn.getNutrientQuantity() gives nutrient quantity per 100 grams
					dietNutrientsMap.put(pn.getNutrientCode(), new RecommendedNutrient(pn.getNutrientCode(), nutrientQuantity));
				}
				else
				{ //add nutrient quantity if nutrient already exists in dietNutrientsMap
					float nutrientQuantity = dietNutrientsMap.get(pn.getNutrientCode()).getNutrientQuantity() + pn.getNutrientQuantity()/100*p.getServingSize();
					dietNutrientsMap.get(pn.getNutrientCode()).setNutrientQuantity(nutrientQuantity);
				}
			}
		}
	}
	
}
