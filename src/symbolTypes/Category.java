package symbolTypes;

public enum Category 
{	
	CLASS("Class"),
	STATIC_METHOD("Static method"),
	VIRTUAL_METHOD("Virtual method"),
	VARIABLE("Local variable"),
	FORMAL("Parameter"),
	FIELD("Field"); 
	
	private final String category;       

	private Category(String cat) {
		category = cat;
	}
	
	public String toString(){
		    return category;
	}
}
