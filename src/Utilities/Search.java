package Utilities;

import java.util.ArrayList;

public class Search 
{
	public static int forStringInSortedArray(String x, String[] array, boolean caseSensitive)
	{
		int length = Math.round(array.length/2);
		boolean failed = false;
		while(failed == false)
		{
			if(caseSensitive == false)
			{
				if(x.equalsIgnoreCase(array[length]) == true)
				{
					break;
				}
				else
				{
					if(length == array.length || length == 0)
					{
						failed = true;
					}
					else
					{
						if(isStringXLargerThanY(x.toLowerCase(), array[length].toLowerCase()) == true)
						{
							length = Math.round((array.length - length)/2) + length;
						}
						else
						{
							length = Math.round(array.length/2);
						}
					}
				}
			}
			else
			{
				if(x.equals(array[length]) == true)
				{
					failed = false;
					break;
				}
				else
				{
					if(length == array.length || length == 0)
					{
						failed = true;
					}
					else
					{
						if(isStringXLargerThanY(x.toLowerCase(), array[length].toLowerCase()) == true)
						{
							length = Math.round((array.length - length)/2) + length;
						}
						else
						{
							length = Math.round(array.length/2);
						}
					}
				}
			}
		}
		
		if(failed == true)
			return -1;
		else
			return length;
	}
	
	public static int forStringInSortedArrayList(String x, ArrayList<String> list, boolean caseSensitive)
	{
		int index = Math.round(list.size()/2);
		boolean failed = true;
		while(true)
		{
			if(caseSensitive == false)
			{
				if(x.equalsIgnoreCase(list.get(index)) == true)
				{
					failed = false;
					break;
				}
				else
				{
					if(isStringXLargerThanY(x.toLowerCase(), list.get(index).toLowerCase()) == true)
					{
						index = Math.round((list.size() - index)/2) + index;
						
					}
					else
					{
						index = Math.round(list.size()/2);
					}
				}
			}
			else
			{
				if(x.equalsIgnoreCase(list.get(index)) == true)
				{
					failed = false;
					break;
				}
				else
				{
					if(isStringXLargerThanY(x.toLowerCase(), list.get(index).toLowerCase()) == true)
					{
						index = Math.round((list.size() - index)/2) + index;
						
					}
					else
					{
						index = Math.round(list.size()/2);
					}
				}
			}
		}
		
		if(failed == false)
		{
			return index;
		}
		else return -1;
		
	}
	
	private static boolean isStringXLargerThanY(String x, String y)
	{
		boolean result = false;
		boolean xIsShorter = true;
		if(x.length() > y.length())
		{
			xIsShorter = false;
		}
		
		int shortestLength;
		
		if(xIsShorter == true)
		{
			shortestLength = x.length();
		}
		else
		{
			shortestLength = y.length();
		}
		
		boolean itteratedToEnd = true;
		for(int i = 0 ; i < shortestLength; i++)
		{
			if(x.charAt(i) != y.charAt(i))
			{
				itteratedToEnd = false;
				if(x.charAt(i) > y.charAt(i))
				{
					result = true;
				}
				else
				{
					result = false;
					
				}
				break;
			}
		}
		if(itteratedToEnd == true)
		{
			if(xIsShorter == true)
			{
				result = false;
			}
			else
			{
				result = true;
			}
		}
		
		return result;
	}
}
