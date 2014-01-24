package Settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.ietf.jgss.Oid;

import Utilities.Debug;
import Utilities.GlobalVariables;

/*
 * http://docs.oracle.com/javase/tutorial/java/javaOO/innerclasses.html
 */

public class FileComparer
{
	private enum Variables
	{
		ItterationCount,
		LineDecFac,
		LineDecFacStep,
		MaxLineDecStep,
		Warningfac,
		MinConfFac,
		MinConfStep,
		MinConfMaxStep,
		ItterationDepth,
		ItterationProcess,
		BothWaysComp,
		BottomUpComp,
		Scrub,
		ScrubWhite,
		ScrubComment,
		ScrubNum,
		Algorithm,
		Language,
		Validate
	}
	
	public String fileName;
	
	public int numberOfLinesToItterateThrough;
	
	public double lineDecrementFactor;
	public double lineDecrementFactorStep;
	public double maxLineDecrementSteps;
	private double originalLineDecrementFactor;
	
	public double minConfidence;
	public double minConfidenceStep;
	public double maxMinConfidenceStep;
	private double originalMinConfidence;
	
	public double warningFactor;
	private double originalWarningFactor;
	
	public StringMatch stringMatch;
	public HTMLStringArray htmlStringArrays;
	
	//TODO: We need to parse attemptToFixWarnings
	public boolean attemptToFixWarnings = true;
	
	public boolean compareBothWays;
	public boolean compareBothWaysUponWarning;
	public boolean compareByInversion;
	public boolean compareByInversionUponWarning;
	
	public int itterationDepthLevel;
	
	public int logPriority = 2; // Move this to the general settings file, or a log settings file. 
	
	private static StringMatch readFileScrubbingSettings = StringMatch.generateSettingFileCommentScruber();
	
	public FileComparer(String source)
	{
		File file = new File(source);
		readSource(file);
	}
	
	private FileComparer()
	{
		
	}
	
	public FileComparer(File source)
	{
		readSource(source);
		saveOriginal();
	}
	
	private void readSource(File source)
	{
		Scanner scanner = null;
		try 
		{
			scanner = new Scanner(source);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		fileName = source.getName();
		
		String nextLine = "";
		StringMatch settings = StringMatch.generateSettingFileCommentScruber();
		stringMatch = new StringMatch(this);
		htmlStringArrays = new HTMLStringArray(this);
		while(scanner.hasNext())
		{
			nextLine = scanner.nextLine();
			String modLine = Utilities.General.scrubString(nextLine, settings);
			modLine = modLine.replaceAll(settings.commentLineIndicator, "");
			if(modLine.contains("Itteration Count"))
				numberOfLinesToItterateThrough = Integer.parseInt(modLine.split("=")[1].replaceAll("\\s", ""));
			if(modLine.contains("Line Decrement Factor"))
				lineDecrementFactor = Double.parseDouble(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Line Decrement Step"))
				lineDecrementFactorStep = Double.parseDouble(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Line Decrement Max Steps"))
				maxLineDecrementSteps = Double.parseDouble(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Warning Factor"))
				warningFactor = Double.parseDouble(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Minimum Confidence Factor"))
				minConfidence = Double.parseDouble(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Minimum Confidence Step"))
				minConfidenceStep = Double.parseDouble(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Minimum Confidence Max Step"))
				maxMinConfidenceStep = Double.parseDouble(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Itteration Depth Level"))
				itterationDepthLevel = Integer.parseInt(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Itteration Process"))
				setItterationProcess(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Always Compare Both Ways"))
				compareBothWays = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Compare Both Ways On Warning"))
				compareBothWaysUponWarning = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Always Compare From Bottom of Page"))
				compareByInversion = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("Compare From Bottom On Warning"))
				compareByInversionUponWarning = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
			if(modLine.contains("String Matching Properties:"))
				stringMatch.setUpFromInputFile(scanner, settings);
			if(modLine.contains("HTML Output Properties:"))
				htmlStringArrays.setUpFromInputFile(scanner, settings);	
		}
	}
	
		private void saveOriginal()
		{
			originalLineDecrementFactor = lineDecrementFactor;
			originalMinConfidence = minConfidence;
			originalWarningFactor = warningFactor;
			stringMatch.saveOriginal();
			// TODO: Will later implement a deep copy object using what's in the Utilities.Debug
		}
		
		public double getOriginalLineDecFactor()
		{
			return originalLineDecrementFactor;
		}
		
		public double getOriginalMinConfidence()
		{
			return originalMinConfidence;
		}
		
		public double getOriginalWarningFactor()
		{
			return originalWarningFactor;
		}
		
		public void restoreOrignal()
		{
			lineDecrementFactor = originalLineDecrementFactor;
			minConfidence = originalMinConfidence;
			warningFactor = warningFactor;
			stringMatch.restoreOriginal();
		}
		
		private void setItterationProcess(String input)
		{
			
		}
		
	
		
		public String toString()
		{
			String output = "";
			return output;
		}
		
		public void itterateMinConfidence()
		{
			minConfidence += minConfidenceStep;
		}
		
		public void itterateLineDecFactor()
		{
			lineDecrementFactor -= lineDecrementFactorStep;
		}
		
		
		public static Settings.FileComparer generateDefault()
		{
			return new FileComparer(System.getProperty("user.dir") + "\\res\\Settings\\FileComp-Default.txt");	
		}
		
		public static ArrayList<Settings.FileComparer> generateDefaultList()
		{
			ArrayList<Settings.FileComparer> result = new ArrayList<Settings.FileComparer>();
			result.add(new FileComparer(GlobalVariables.SETTING_RESOURCE_FILEPATH + "\\FileComp-DefaultLevel1.txt"));
			result.add(new FileComparer(GlobalVariables.SETTING_RESOURCE_FILEPATH + "\\FileComp-DefaultLevel2.txt"));
			result.add(new FileComparer(GlobalVariables.SETTING_RESOURCE_FILEPATH + "\\FileComp-DefaultLevel3.txt"));
			result.add(new FileComparer(GlobalVariables.SETTING_RESOURCE_FILEPATH + "\\FileComp-DefaultLevel4.txt"));
			result.add(new FileComparer(GlobalVariables.SETTING_RESOURCE_FILEPATH + "\\FileComp-DefaultLevel5.txt"));
			return result;
		}
		
		public String isInitilized()
		{
			return null;
		}
	
	public static class StringMatch
	{
		public Utilities.StringComparisionAlgorithms.algorithmNames[] algorithmsToUse;
		
		public double[] weightingFactor;
		
		public boolean ignoreCase = true;
		
		private Settings.FileComparer parent;
		
		public int minimumConsecutiveMatchingCharacters;
		private int originalMinConsecutiveMatchingCharacters;
		
		public boolean ignoreComments = true;
		public boolean ignoreSingleLineComments = false;
		public boolean ignoreMultiLineComments = false;
		
		public String commentLineIndicator;
		public String multiLineCommentStartIndicator;
		public String multiLineCommentEndIndicator;
		
		public String[] listOfStringsRequiringValidation;
		
		public String[] listOfKeywordsA;
		public String[] listOfKeywordsB;
		public String[] listOfKeywordsC;
		
		public boolean ignoreNumbers = false;
		
		public boolean scrubString = true;
			public boolean ignoreWhiteSpace = true;
			
		public String languageType;
			
		private StringMatch()
		{
			
		}
		
		public static StringMatch generateSettingFileCommentScruber()
		{
			StringMatch output = new StringMatch();
			output.ignoreComments = true;
			output.ignoreSingleLineComments = true;
			output.ignoreMultiLineComments = false;
			output.commentLineIndicator = "//";
			output.ignoreNumbers = false;
			output.scrubString = true;
			output.ignoreWhiteSpace = false;
			return output;
		}
		
		public StringMatch(Settings.FileComparer parent)
		{
			this.parent = parent;
		}
		
		public void setUpFromInputFile(Scanner scanner, StringMatch settings)
		{
			String nextLine = "";
			while(nextLine.contains("END STRING MATCHING PROPERTIES") == false)
			{
				//TODO: STILL HAVE SOME WORK TO DO
				nextLine = scanner.nextLine();
				String modLine = Utilities.General.scrubString(nextLine, settings);
				modLine = modLine.replaceAll(settings.commentLineIndicator, "");
				if(modLine.contains("Scrub Comments"))
					ignoreWhiteSpace = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Ignore Case"))
					ignoreCase = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Ignore WhiteSpace"))
					ignoreWhiteSpace = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Ignore Comments"))
					ignoreComments = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Ignore Single Line Comments"))
					ignoreSingleLineComments = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Ignore Multi Line Comments"))
					ignoreMultiLineComments = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Ignore Numbers"))
					ignoreNumbers = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Minimum # Of Consecutive Characters"))
					minimumConsecutiveMatchingCharacters = Integer.parseInt(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Language Type"))
					importLanguageFile(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Validate Lines Containing Only Keywords"))
					if(Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s","")) == true)
					{
						//TODO GOTTA FIX;
						//importKeywordsIntoValidationArray();
					}
				if(modLine.contains("Algorithms to Use"))
					importAlgorithmsFromSettings(modLine.split("=")[1].replaceAll("\\s",""));
			}
		}
		
		private void importAlgorithmsFromSettings(String input)
		{
			input = input.replaceAll("\\s", "");
			String[] algorithms = input.split("&");
			ArrayList<Utilities.StringComparisionAlgorithms.algorithmNames> algorithmsList = new ArrayList<Utilities.StringComparisionAlgorithms.algorithmNames>();
			ArrayList<Double> algorithmWeightings = new ArrayList<Double>();
			for(int i = 0; i < algorithms.length; i++)
			{
				String[] split = algorithms[i].split(":");
				try
				{
					Utilities.StringComparisionAlgorithms.algorithmNames temp = Utilities.StringComparisionAlgorithms.algorithmNames.valueOf(split[0]);
					algorithmWeightings.add(Double.parseDouble(split[1]));
					algorithmsList.add(temp);
				}
				catch (IllegalArgumentException e)
				{
					//TODO: We'll log a warning
					
				}
			}
			
			weightingFactor = new double[algorithmWeightings.size()];
			
			for(int i = 0; i < algorithmWeightings.size(); i++)
			{
				weightingFactor[i] = algorithmWeightings.get(i).doubleValue();
			}
			
			algorithmsToUse = new Utilities.StringComparisionAlgorithms.algorithmNames[algorithmWeightings.size()];
			
			for(int i = 0; i < algorithmWeightings.size(); i++)
			{
				algorithmsToUse[i] = algorithmsList.get(i);
			}
			
		}
		
		private void importKeywordsIntoValidationArray()
		{
			String[] output = new String[listOfStringsRequiringValidation.length + listOfKeywordsA.length + listOfKeywordsB.length + listOfKeywordsC.length];
			int counter = 0;
			for (int i = 0; i < listOfStringsRequiringValidation.length; i++)
			{
				output[i] = listOfStringsRequiringValidation[i];
				counter++;
			}
			for(int i = 0; i < listOfKeywordsA.length; i++)
			{
				output[counter + i] = listOfKeywordsA[i];
				counter++;
			}
			for(int i = 0; i < listOfKeywordsB.length; i++)
			{
				output[counter + i] = listOfKeywordsB[i];
				counter++;
			}
			for(int i = 0; i < listOfKeywordsC.length; i++)
			{
				output[counter + i] = listOfKeywordsC[i];
				counter++;
			}
		}
		
		private boolean importLanguageFile(String name)
		{
			languageType = name;
			String filePath = null;
			
			if(Utilities.GlobalVariables.LanguageFilePath != null)
				filePath = Utilities.GlobalVariables.LanguageFilePath + "\\" + name + ".txt";
			else
				filePath = System.getProperty("user.dir") + "\\res\\Settings\\FileComp-Default.txt";
			
			File file = new File(filePath);
			if(file.exists() == false || file.isFile() == false)
			{
				// TODO: We need to then check the global settings, to see if we can import from default
				file = new File(Utilities.GlobalVariables.SETTING_RESOURCE_FILEPATH + "\\FileComp-LanguageDefault.txt");
				if(file.exists() == false)
				{
					//TODO: LOG
					System.out.println("STILL DOESN'T EXIST");
					System.out.println(file.toString());
					return false;
				}
				else
				{
					System.out.println("Using Default Language, couldn't find: " + filePath);
				}
			}
			else
			{
				System.out.println("Found the Language File");
			}
			
			Scanner scanner = null;
			try 
			{
				scanner = new Scanner(file);
			} 
			catch (FileNotFoundException e) 
			{
				//TODO: Soemthing...
				return false;
			}
			
			StringMatch settings = generateSettingFileCommentScruber();
			
			String nextLine = "";
			while(scanner.hasNext() == true)
			{
				nextLine = scanner.nextLine();
				String modLine = Utilities.General.scrubString(nextLine, settings);
				modLine.replaceAll(settings.commentLineIndicator, "");
				if(modLine.contains("Single Line Comment Indicator"))
					commentLineIndicator = modLine.split("=")[1];
				if(modLine.contains("Multi Line Comment Start Indicator"))
					multiLineCommentStartIndicator = modLine.split("=")[1];
				if(modLine.contains("Multi Line Comment End Indicator"))
					multiLineCommentEndIndicator = modLine.split("=")[1];
				if(modLine.contains("List of Strings To Validate"))
					listOfStringsRequiringValidation = generateListOfKeywords(scanner);
				if(modLine.contains("Keywords List A"))
					listOfKeywordsA = generateListOfKeywords(scanner);
				if(modLine.contains("Keywords List B"))
					listOfKeywordsB = generateListOfKeywords(scanner);
				if(modLine.contains("Keywords List C"))
					listOfKeywordsC = generateListOfKeywords(scanner);
			}
			return true;
		}
		
		private String[] generateListOfKeywords(Scanner scanner)
		{
			ArrayList<String> list = new ArrayList<String>();
			String nextLine = "";
			while(nextLine.contains("END LIST") == false)
			{
				if(scanner.hasNext() == false)
				{
					break;
				}
				
				nextLine = scanner.nextLine();
				
				if(nextLine.equals("END LIST"))
					break;
				
				if(nextLine.replaceAll("\\s", "").equals("") == false)
					list.add(nextLine);
			}
			String[] result = new String[list.size()];
			for(int i = 0; i < list.size(); i++)
			{
				result[i] = list.get(i);
			}
//			int counter = 0;
//			while(list.size() != 0)
//			{
//				result[counter] = list.get(0);
//				list.remove(0);
//			}
			return result;
		}
		
		public void restoreOriginal()
		{
			minimumConsecutiveMatchingCharacters = originalMinConsecutiveMatchingCharacters;
		}
		
		private void saveOriginal()
		{
			originalMinConsecutiveMatchingCharacters = minimumConsecutiveMatchingCharacters;
		}
		
		public int getOriginalMinConsecMatchingChars()
		{
			return originalMinConsecutiveMatchingCharacters;
		}
		
	}
	
	public static class HTMLStringArray
	{
		// Note that 0 by default means white. 
		public int[] thresholds;
		
		private Settings.FileComparer parent;
		
		public boolean highlightKeywordsA;
		public boolean highlightKeywordsB;
		public boolean highlightKeywordsC;
		
		public HTMLStringArray(Settings.FileComparer parent)
		{
			this.parent = parent;
		}
		
		public void setUpFromInputFile(Scanner scanner, StringMatch settings)
		{
			String nextLine = "";
			while(nextLine.contains("END HTML OUTPUT PROPERTIES") == false)
			{
				if(scanner.hasNext() == false)
				{
					//TODO: Get Log down here
					System.out.println("Badly formated file");
					break;
				}
				nextLine = scanner.nextLine();
				String modLine = Utilities.General.scrubString(nextLine, settings);
				modLine = modLine.replaceAll(settings.commentLineIndicator, "");
				if(modLine.contains("Highlight KeywordsA"))
					highlightKeywordsA = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Highlight KeywordsB"))
					highlightKeywordsB = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Highlight KeywordsC"))
					highlightKeywordsC = Boolean.parseBoolean(modLine.split("=")[1].replaceAll("\\s",""));
				if(modLine.contains("Thresholds"))
				{
					String[] thresholdStrings = modLine.split("=")[1].replaceAll("\\s","").split(",");
					thresholds = new int[thresholdStrings.length];
					for(int i = 0; i < thresholdStrings.length; i++)
						thresholds[i] = Integer.parseInt(thresholdStrings[i]);
				}
			}
		}
	}
	
	public static boolean verifyStringMatchSettings(StringMatch settings)
	{
		int numberOfAlgorithms = settings.algorithmsToUse.length;
		
		double weightingFactorRunningTotal = 0;
		for(int i = 0; i < settings.weightingFactor.length; i++)
		{
			weightingFactorRunningTotal += settings.weightingFactor[i];
			numberOfAlgorithms--;
		}
		
		if(weightingFactorRunningTotal != 1.0)	// the weights of all the algorithms msut add up to 1
			return false;
		
		if(numberOfAlgorithms != 0) // We dont' have the same nubmer of algorithms and/or weighting factors
			return false;
		
		return true;
	}
}
