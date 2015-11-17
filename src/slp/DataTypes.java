package slp;

public enum DataTypes 
{

	INT(0, "int"), 
	BOOLEAN(false, "boolean"), 
	STRING(null, "string"), 
	VOID(null, "void");
	
	private Object value;
	private String description;

	private DataTypes(Object value, String description) 
	{
		this.value = value;
		this.description = description;
	}

	public Object getDefaultValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

}
