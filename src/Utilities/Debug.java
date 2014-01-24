package Utilities;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.awt.Point;
import java.awt.geom.Point2D;

public class Debug 
{
	// The below method will only work for fields that are public.
	// http://stackoverflow.com/questions/13008746/check-if-java-lang-reflect-field-type-is-a-byte-array
	// http://mathdotrandom.blogspot.ca/2011/08/recursive-reflective-hierarchical-deep.html
	public static String[] displayOutputOfClass(Object o)
	{
		Class c = o.getClass();
		Field[] theFields = c.getFields();
		String[] output = new String[theFields.length];
		for(int i = 0; i < theFields.length; i++)
		{
			try 
			{
				output[i] = "";
				if(theFields[i].getType().isPrimitive())
				{
					output[i] += "We got a primitive: " + theFields[i].getDeclaringClass() + "\t"; 
				}
				output[i] += (theFields[i].getName() + /*"of type: "+ theFields[i].getType().getCanonicalName() + */":\t" + theFields[i].get(o));
			} 
			catch (IllegalArgumentException e) // for .getName()
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IllegalAccessException e) // for .get(o)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return output;
	}
	
	public static String getPublicFieldsFromClassToString(Object o)
	{
		String[][] data = getPublicFieldsFromClass(o);
		String result = "";
		for(int i = 0; i < data.length; i++)
		{
			result += data[i][0];
			result += "\t||\t";
			result += data[i][1];
			result += "\n";
		}
		return result;
	}
	
	public static String[][] getPublicFieldsFromClass(Object o)
	{
		return (getPublicFieldsFromClass(o, null, 0));
	}
	
	public static String[][] getPublicFieldsFromClass(Object o, ArrayList<String[]> input, int tabCount)
	{
		Class c = o.getClass();
		Field[] theFields = c.getFields();
		ArrayList<String[]> output = null;
				
		if(input == null)
			output = new ArrayList<String[]>();
		else
			output = input;
		
		output.add(new String[] {"*" + c.getName(), "USER DEFNED CLASS"});
		
		for(int i = 0; i < theFields.length; i++)
		{
			String[] temp = new String[2];
			temp[0] = "";
			for(int j = 0; j < tabCount; j++)
			{
				temp[0] += "\t";
			}
			temp[0] += theFields[i].getName();
			try
			{
				if(theFields[i].getType().isPrimitive())
				{
					temp[1] = theFields[i].get(o) + "";
					
				}
				else
				{
					if(theFields[i].get(o) == null)
						temp[1] = "NULL";
					else if(theFields[i].getType().equals(java.lang.String.class))
						temp[1] = theFields[i].get(o) + "";
					else if(theFields[i].getType().isEnum() == true)
						temp[1] = "ENUM";
					else if(theFields[i].equals(java.lang.Class.class))
						temp[1] = "CLASS";
					else if(theFields[i].getType().isAnnotation() == true)
						temp[1] = "ANNOTATION";
					else if(theFields[i].getType().isArray())
					{
						String[] temp1 = new String[2];
						temp1[0] = temp[0];
						temp1[1] = "Array:" + theFields[i].getType().getComponentType();
						output.add(temp1);
						
						for(int j = 0; j < Array.getLength(theFields[i].get(o)); j++)
						{
							String[] temp2 = new String[2];
							temp2[0] = "";
							for(int k = 0; k < tabCount; k++)
							{
								temp2[0] += "\t";
							}
							temp2[1] = Array.get(theFields[i].get(o), j) + "";
							output.add(temp2);
						}

						temp[1] = null;
					}
					else
					{
						if(theFields[i].get(o) == null)
						{
							temp[1] = "NULL";
						}
						else
						{
							temp[1] = null;
							getPublicFieldsFromClass(theFields[i].get(o), output, tabCount + 1);
						}
					}
				}
				if(temp[1] != null)
					output.add(temp);
			}
			catch (IllegalArgumentException e) 
			{
				System.out.println(theFields[i].getName());
				e.printStackTrace();
			} 
			catch (IllegalAccessException e) 
			{
				e.printStackTrace();
			}
		}
		
//		return (String[][]) output.toArray();
		
		String[][] outputArray = new String[output.size()][2];
		for(int i = 0; i < output.size(); i++)
		{
			outputArray[i] = output.get(i);
		}
		return outputArray;
	}
	

}
