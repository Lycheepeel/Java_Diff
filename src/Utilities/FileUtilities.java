package Utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileUtilities 
{
	public static int determineNumberOfLinesInFile(File file)
	{
		if(file.exists() == false)
		{
			return -1;
		}
		if(file.exists() == true && file.isDirectory())
		{
			return -2;
		}
		
		int count = 0;
		
		Scanner scanner = null;
		try 
		{
			scanner = new Scanner(file);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return -3;
		}
		
		while(scanner.hasNext())
		{
			count++;
		}
		
		return count;
	}
	
	public static String[] fileToStringArray(File file)
	{
		if(file.exists() == false)
		{
			System.out.println("ERROR FILE DOES NOT EXIST" );
			return null;
		}
		if(file.exists() == true && file.isDirectory())
		{
			System.out.println("ERROR FILE IS DIRECTORY" );
			return null;
		}
		
		String[] result = null;
		ArrayList<String> temp = new ArrayList<String>();
		
		Scanner scanner = null;
		try 
		{
			scanner = new Scanner(file);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		}
		
		while(scanner.hasNext())
		{
			temp.add(scanner.nextLine());
		}
		
		result = new String[temp.size()];
		
		for(int i = 0; i < temp.size(); i++)
		{
			
		}
		
		int i = 0;
		while(temp.isEmpty() == false)
		{
			result[i] = temp.get(0);
			temp.remove(0);
			i++;
		}
		
		return result;
	}
	
	public static String[] fileToStringArrayNoNewLines(File file)
	{
		if(file.exists() == false)
		{
			System.out.println("ERROR FILE DOES NOT EXIST" );
			return null;
		}
		if(file.exists() == true && file.isDirectory())
		{
			System.out.println("ERROR FILE IS DIRECTORY" );
			return null;
		}
		
		String[] result = null;
		ArrayList<String> temp = new ArrayList<String>();
		
		Scanner scanner = null;
		try 
		{
			scanner = new Scanner(file);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		}
		
		while(scanner.hasNext())
		{
			String tempString = scanner.nextLine();
			if(tempString.equals("") == false)
				temp.add(tempString);
		}
		
		result = new String[temp.size()];
		
		for(int i = 0; i < temp.size(); i++)
		{
			
		}
		
		int i = 0;
		while(temp.isEmpty() == false)
		{
			result[i] = temp.get(0);
			temp.remove(0);
			i++;
		}
		
		return result;
	}
}
