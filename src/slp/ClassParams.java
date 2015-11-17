package slp;

import java.util.ArrayList;
import java.util.List;

public class ClassParams 
{
	public List<ClassField> fields;
	public List<ClassMethod> methods;
	
	public ClassParams()
	{
		fields = new ArrayList<ClassField>();
		methods = new ArrayList<ClassMethod>();
	}
}
