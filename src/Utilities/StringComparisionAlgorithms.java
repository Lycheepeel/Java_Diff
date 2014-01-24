package Utilities;
public class StringComparisionAlgorithms 
{
	public enum algorithmNames
	{
		SimpleMatch,
		LinearMatch,
		ExponentialMatch,
		SimpleContains,
		LinearContains,
		ExponentialContains,
		SimpleLongestCommonSubsequence,
		WeightedLongestCommonSubsequence,
	}
	
	public static double algorithm1(String tempA, String tempB, int maxLength)
	{	
		int matches = 0;
		double confidenceLevel;
		for(int i = 0; i < Math.min(tempA.length(), tempB.length()); i++)
		{
			if(tempA.charAt(i) == tempB.charAt(i))
				matches++;
			
		}

		if(maxLength != 0)
			confidenceLevel = ((double)matches)/((double)maxLength);
		
		else
		{	
			if(tempA.length() == tempB.length() && tempA.length() == 0)
				return 1.0;
			else
				confidenceLevel = -1.0;
		}
		return confidenceLevel;
	}
	
	public static double exponentialAlgorithm(int maxLength, int minLength, String a, String b)
	{
		return Math.max(exponentialAlgorithm_Front(maxLength, minLength, a,b), exponentialAlgorithm_Back(maxLength, minLength, a,b));
	}
	
	public static double exponentialAlgorithm_Front(int maxLength, int minLength, String tempA, String tempB)
	{
		double score = 0;
		double runningTotal = 0;
		double factor = 1.15;
		double startingTotal = Math.pow(factor, maxLength);
		double confidenceLevel;
		
		for(int i = 0; i < minLength; i++)
		{
			if(tempA.charAt(i) == tempB.charAt(i))
				score += startingTotal;
			runningTotal += startingTotal;
			startingTotal = startingTotal/factor;
		}
		for(int i = minLength; i < maxLength; i++)
		{
			runningTotal += startingTotal;
			startingTotal = startingTotal/factor;
		}
		
		if(runningTotal != 0)
		{
			confidenceLevel = score/runningTotal;
		}
		else
		{	
			if(tempA.length() == tempB.length() && tempA.length() == 0)
				confidenceLevel = 1.0;
			else
				confidenceLevel = -1.0;
		}
		return confidenceLevel;
	}
	
	public static double exponentialAlgorithm_Back(int maxLength, int minLength, String tempA, String tempB)
	{
		double score = 0;
		double runningTotal = 0;
		double factor = 1.15;
		double startingTotal = Math.pow(factor, maxLength);
		double confidenceLevel;
		
		for(int i = 0; i < minLength; i++)
		{
			if(tempA.charAt(tempA.length() - i - 1) == tempB.charAt(tempB.length() - i - 1))
				score += startingTotal;
			runningTotal += startingTotal;
			startingTotal = startingTotal/factor;
		}
		for(int i = minLength; i < maxLength; i++)
		{
			runningTotal += startingTotal;
			startingTotal = startingTotal/factor;
		}
		
		if(runningTotal != 0)
		{
			confidenceLevel = score/runningTotal;
		}
		else
		{	
			if(tempA.length() == tempB.length() && tempA.length() == 0)
				return 1.0;
			else
				confidenceLevel = -1.0;
		}
		return confidenceLevel;
	}
	
	public static double linearAlgorithm(int maxLength, int minLength, String a, String b)
	{
		return Math.max(linearAlgorithm_Front(maxLength, minLength, a,b), linearAlgorithm_Back(maxLength, minLength, a,b));
	}
	
	public static double linearAlgorithm_Back(int maxLength, int minLength, String tempA, String tempB)
	{
		double score = 0;
		double startingTotal = maxLength;
		double runningTotal = 0;
		double confidenceLevel;
		
		for(int i = 0; i < minLength; i++)
		{
			if(tempA.charAt(tempA.length() - i - 1) == tempB.charAt(tempB.length() - i - 1))
				score += startingTotal;
			runningTotal += startingTotal;
			startingTotal--;
		}
		for(int i = minLength; i < maxLength; i++)
		{
			runningTotal += startingTotal;
			startingTotal--;
		}
		
		if(runningTotal != 0)
		{
			confidenceLevel = score/runningTotal;
		}
		else
		{	
			if(tempA.length() == tempB.length() && tempA.length() == 0)
				return 1.0;
			else
				confidenceLevel = -1.0;
		}
		return confidenceLevel;	
	}
	
	public static double linearAlgorithm_Front(int maxLength, int minLength, String tempA, String tempB)
	{
		double score = 0;
		double startingTotal = maxLength;
		double runningTotal = 0;
		double confidenceLevel;
		
		for(int i = 0; i < minLength; i++)
		{
			if(tempA.charAt(i) == tempB.charAt(i))
				score += startingTotal;
			runningTotal += startingTotal;
			startingTotal--;
		}
		for(int i = minLength; i < maxLength; i++)
		{
			runningTotal += startingTotal;
			startingTotal--;
		}
		
		if(runningTotal != 0)
		{
			confidenceLevel = score/runningTotal;
		}
		else
		{	
			if(tempA.length() == tempB.length() && tempA.length() == 0)
				return 1.0;
			else
				confidenceLevel = -1.0;
		}
		return confidenceLevel;	
	}
	
	public static double basicCharacterRemovalAlgorithm(String A, String B, Settings.FileComparer.StringMatch settings)
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
		
		while(index != shorter.length())
		{
			temp += shorter.charAt(index);
			if(check == true)
			{
				if(longer.indexOf(temp) == confirmedIndex)
				{
					score++;
					index++;
				}
				else
				{
					longer = longer.substring(confirmedIndex + temp.length() - 1);
					check = false;
					temp = "";
				}
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
					index += (checkCount + 1);
					score += (checkCount + 1);	
				}
			}
		}
		result = ((double) score) / ((double)maxLength);
		return result;
	}
	
	// Can produce > 1000 why?
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
		
		int counter = 100;
		
		while(index != shorter.length())
		{
			temp += shorter.charAt(index);
			if(check == true)
			{
				if(longer.indexOf(temp) == confirmedIndex)
				{
					score+= reference;
					index++;
					total += reference;
					reference--;
				}
				else
				{
					longer = longer.substring(confirmedIndex + temp.length() - 1);
					check = false;
					temp = "";
					total += reference;
				}
			}
			else
			{
				int checkCount = 0;
				int smallC = 100;
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
					reference--;
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
		}
		
		
		while(reference > 0)
		{
			total += reference;
			reference--;
		}
		
		result = ((double) score) / ((double)total);
		return result;
	}
	
	public static double exponentiallyWeightedCharacterRemovalAlgorithm(String A, String B, Settings.FileComparer.StringMatch settings)
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
		
		double factor = 1.15;
		
		boolean check = false;
		int checkRequirement = settings.minimumConsecutiveMatchingCharacters;
		int checkIndex = 0;
		
		double reference = Math.pow(factor, maxLength);
		int total = 0;
		
		int counter = 100;
		
		while(index != shorter.length())
		{
			temp += shorter.charAt(index);
			if(check == true)
			{
				if(longer.indexOf(temp) == confirmedIndex)
				{
					score+= reference;
					index++;
					total += reference;
					reference = reference/factor;
				}
				else
				{
					longer = longer.substring(confirmedIndex + temp.length() - 1);
					check = false;
					temp = "";
					total += reference;
				}
			}
			else
			{
				int checkCount = 0;
				int smallC = 100;
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
					reference = reference/factor;;
				}
				else
				{
					check = true;
					confirmedIndex = checkIndex;
					for(int i = 0; i < checkCount + 1; i++)
					{
						score += reference;
						total += reference;
						reference = reference/factor;
					}
					index += (checkCount + 1);	
				}
			}
		}
		
		
		while(reference > 0)
		{
			total += reference;
			reference--;
		}
		
		result = ((double) score) / ((double)total);
		return result;
	}
	
	public static double lcsAlgorithmSimple(String a, String b)
	{
		String lcs = lcs(a,b);
		int maxLength = Math.max(a.length(), b.length());
		return ((double) lcs.length())/((double) maxLength);
	}
	
	public static double lcsAlgorithmExponential(String a, String b)
	{
		String lcs = lcs(a,b);
		return Math.max(lcsAlgorithmExponentialFront(a,b,lcs), lcsAlgorithmExponentialBack(a, b, lcs));
	}
	
	public static double lcsAlgorithmExponentialFront(String a, String b, String lcs)
	{
		double result = 0;
		double factor = 1.15;
		double score = Math.pow(factor, Math.max(a.length(), b.length()));
		double total = 0;
		int index = 0;
		for(int i = 0; i < b.length(); i++)
		{
			if(lcs.charAt(index) == b.charAt(i))
			{
				result += score;
				index++;
			}
			total += score;
			score = score/factor;
		}
		return result/total;
	}
	
	public static double lcsAlgorithmExponentialBack(String a, String b, String lcs)
	{
		double result = 0;
		double factor = 1.15;
		double score = Math.pow(factor, Math.max(a.length(), b.length()));
		double total = 0;
		int index = 0;
		for(int i = 0; i < b.length(); i++)
		{
			if(lcs.charAt(index) == b.charAt(i))
			{
				result += score;
				index++;
			}
			total += score;
			score = score/factor;
		}
		return result/total;
	}
	
	public static double lcsAlgorithmWeighted(String a, String b)
	{
		return Math.max(lcsAlgorithmWeightFront(a,b),lcAlgorithmWeightBack(a,b));
	}
	
	private static double lcsAlgorithmWeightFront(String a, String b)
	{
		String lcs = lcs(a,b);
		double result = 0;
		double score = Math.max(a.length(), b.length());
		double total = 0;
		int index = 0;
		for(int i = 0; i < b.length(); i++)
		{
			if(lcs.charAt(index) == b.charAt(i))
			{
				result += score;
				index++;
			}
			total += score;
			score--;
		}
		return result/total;
	}
	
	private static double lcAlgorithmWeightBack(String a, String b)
	{
		String lcs = lcs(a,b);
		double result = 0;
		double score = Math.max(a.length(), b.length());
		double total = 0;
		int index = b.length() - 1;
		for(int i = b.length(); i >= 0; i++)
		{
			if(lcs.charAt(index) == b.charAt(i))
			{
				result += score;
				index--;
			}
			total += score;
			score--;
		}
		return result/total;
	}
	
	public static String lcsRecursive(String a, String b){
	    int aLen = a.length();
	    int bLen = b.length();
	    if(aLen == 0 || bLen == 0)
	    {
	        return "";
	    }
	    else if(a.charAt(aLen-1) == b.charAt(bLen-1))
	    {
	        return lcsRecursive(a.substring(0,aLen-1),b.substring(0,bLen-1))
	        		+ a.charAt(aLen-1);
	    }
	    else
	    {
	        String x = lcsRecursive(a, b.substring(0,bLen-1));
	        String y = lcsRecursive(a.substring(0,aLen-1), b);
	        return (x.length() > y.length()) ? x : y;
	    }
	}
	
	public static String lcs(String a, String b) {
	    int[][] lengths = new int[a.length()+1][b.length()+1];
	 
	    // row 0 and column 0 are initialized to 0 already
	 
	    for (int i = 0; i < a.length(); i++)
	        for (int j = 0; j < b.length(); j++)
	            if (a.charAt(i) == b.charAt(j))
	                lengths[i+1][j+1] = lengths[i][j] + 1;
	            else
	                lengths[i+1][j+1] =
	                    Math.max(lengths[i+1][j], lengths[i][j+1]);
	 
	    // read the substring out from the matrix
	    StringBuffer sb = new StringBuffer();
	    for (int x = a.length(), y = b.length();
	         x != 0 && y != 0; ) {
	        if (lengths[x][y] == lengths[x-1][y])
	            x--;
	        else if (lengths[x][y] == lengths[x][y-1])
	            y--;
	        else {
	            assert a.charAt(x-1) == b.charAt(y-1);
	            sb.append(a.charAt(x-1));
	            x--;
	            y--;
	        }
	    }
	 
	    return sb.reverse().toString();
	}
}

