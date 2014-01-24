import java.awt.Point;
import java.io.File;

import Settings.FileComparer;
import Utilities.Debug;


public class Test 
{
	public static void main (String args[])
	{
		System.out.println(">" + File.separator + "<");
//		oldFileComparer xkcd = new oldFileComparer(args[0], new File(args[1]), new File(args[2]));
//		xkcd.start();
		
//		String b = "waitfor 10, alarm_log_search,alarm_log_search2132212313156465645648973123123 / / header";
//		String a = "waitfor 10, alarm_log_search / / header";
//		
//		Settings.FileComparer settings = Settings.FileComparer.generateDefault();
//		
//		
//		System.out.println(linearWeightedCharacterRemovalAlgorithm(a, b, settings.stringMatch) + "");
		
//			Point point = new Point(1, 2);
//			File file = new File("C:\\Users\\ESTEYEE\\workspace\\Java_File_Comparer\\res\\Settings\\FileComp-Default.txt");
			
//			Settings.FileComparer x = new Settings.FileComparer(file);
//			String[] k = Utilities.Debug.displayOutputOfClass(x);
//			for(int i = 0; i < k.length; i++)
//			{
//				System.out.println(k[i]);
//			}
//			
//			System.out.println("**************************A");
//			
////			x = Settings.FileComparer.generateDefault();
////			k = Utilities.Debug.displayOutputOfClass(x);
////			for(int i = 0; i < k.length; i++)
////			{
////				System.out.println(k[i]);
////			}
//			
//			k = Utilities.Debug.displayOutputOfClass(x.stringMatch);
//			
//			for(int i = 0; i < k.length; i++)
//			{
//				System.out.println(k[i]);
//			}
//			
//			System.out.println("**************************B");
//			
//			k = Utilities.Debug.displayOutputOfClass(x.htmlStringArrays);
//			
//			for(int i = 0; i < k.length; i++)
//			{
//				System.out.println(k[i]);
//			}
//			
//			System.out.println("**************************C");
//			
//			k = Utilities.Debug.displayOutputOfClass(x.stringMatch.algorithmsToUse);
//			
//			for(int i = 0; i <x.stringMatch.algorithmsToUse.length; i++)
//			{
//				System.out.println(x.stringMatch.algorithmsToUse[i].toString());
//			}
//			
//			for(int i = 0; i < k.length; i++)
//			{
//				System.out.println(k[i]);
//			}
//			
//			Settings.FileComparer x = new Settings.FileComparer(file);
//			String[][] k = Utilities.Debug.getPublicFieldsFromClass(x);
//			System.out.println(k.length);
//			for(int i = 0 ; i < k.length; i++)
//			{
//				System.out.println(k[i][0] + "\t||\t" + k[i][1]);
//			}
			
//			String a = "SarahKittyRosyLauraBaker";
//			String b = "rahsyafasdBake";
//			String c = "RosyKitty";
//			
//			System.out.println(Utilities.StringComparisionAlgorithms.lcs(a, b));
//			System.out.println(Utilities.StringComparisionAlgorithms.lcs(a, c));
			
	}
	
	public static double linearWeightedCharacterRemovalAlgorithm(String A, String B, Settings.FileComparer.StringMatch settings)
	{	
		double result = 0.0;
		String shorter = "";
		String longer = "";
		if(A.length() < B.length())
		{
			shorter = A;
			longer = B;
		}
		else
		{
			shorter = B;
			longer = A;
		}
		
		int maxLength = Math.max(shorter.length(), longer.length());
		
		String temp = "";
		int confirmedIndex = 0;
		int index = 0;
		int score = 0;
		
		boolean check = false;
		int checkRequirement = settings.minimumConsecutiveMatchingCharacters;
		int checkIndex = 0;
		
		int reference = maxLength;
		int total = 0;
		
		while(index != shorter.length())
		{
			temp += shorter.charAt(index);
			if(check == true)
			{
				if(longer.indexOf(temp) == confirmedIndex)
				{
					score+= reference;
					index++;
				}
				else
				{
					longer = longer.substring(confirmedIndex + temp.length() - 1);
					check = false;
					temp = "";
				}
				total += reference;
			}
			else
			{
				int checkCount = 0;
				while(temp.length() != (checkRequirement - 1))
				{
					checkCount++;
					if(index + checkCount == shorter.length())
						break;
					temp += shorter.charAt(index + checkCount);
				}
				if(index + checkCount == shorter.length())
					break;
				
				checkIndex = longer.indexOf(temp);
				checkCount++;
				
				if(index + checkCount == shorter.length())
					break;
				
				temp += shorter.charAt(index + checkCount);
				if(longer.indexOf(temp) != checkIndex || checkIndex == -1)
				{
					index++;
					temp = "";
				}
				else
				{
					check = true;
					confirmedIndex = checkIndex;
					for(int i = 0; i < checkCount + 1; i++)
					{
						score += reference;
						total += reference;
						reference--;
					}
					index += (checkCount + 1);	
				}
			}
			reference--;
		}
		
		while(reference != 0)
		{
			total += reference;
			reference--;
		}
		
		result = ((double) score) / ((double)total);
		return result;
	}
	
}
