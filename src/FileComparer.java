import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import Settings.*;
import Utilities.StringComparisionAlgorithms;

/*
 * TODO:
 * Think about it like this: If there is a line below with a higher comparision point, but is dieing because of lineDecrement, why don't we switch to that
 * http://dozer.sourceforge.net/documentation/about.html
 */

public class FileComparer 
{	
	// Constructor Variables
	private File fileA;
	private File fileB;
	private Settings.FileComparer defaultSettings;
	private ExecutableMain parent;
	
	// Log variables:
	private boolean logRequested;
	
	// Initlization Variables
	private String identifier;
	private String shortName;
	private boolean warningFlagA;
	private boolean warningFlagB;
	private boolean failureFlagA;
	private boolean failureFlagB;
	
	// Result Variables
	private String[][] HTMLOutputArray;
	private boolean warningFlag;
	private boolean failureFlag;
	private boolean filesAreIdentical;
	
	private int numOfSingularLines;
	private int numOfDifferences;
	private int numIdenticalLines;
	private int numSingularLeft;
	private int numSingularRight;
	//TODO: We need to create a method that detects if b->a swap it'll swap #leftsing and #rightsing

	private String warningData;
	
	// Itteration Variables
	private boolean invert;
	
	// Statistic Variables
	private boolean invertSuccessful;
	private boolean BtoASuccessful;
	private boolean BToAInvertSuccssful;
	private String settingsToString;
	
	public FileComparer(File fileA, File fileB, Settings.FileComparer settings, ExecutableMain parent)
	{
		// Initilization
		this.fileA = fileA;
		this.fileB = fileB;
		this.defaultSettings = settings;
		this.parent = parent;
		
		// Should no settings be passed then we'll use the default settings
		if(settings == null)
		{
			defaultSettings = Settings.FileComparer.generateDefault();
			forceWriteToLog("Settings passed were null generating default", false);
		}
		
		// Create the log identifiers for reporting
		this.identifier = "File Comparision Object for:" + fileA.getName() +
				"\n within Directories: \n\t" + fileA.getParent() +
				"\n\t" + fileB.getParent();
		
		this.shortName = "File Comparision Object for:" + fileA.getName();

		writeToLog(identifier + "\n Has been created!", true, 1);
		
		// Determine that if in the settings it was required to report heavily on this specific file comparision object
		logRequested = false;
		
		for(int i = 0; i < SettingSRC.FileComparer_ReportRequest.size(); i++)
		{
			if(SettingSRC.FileComparer_ReportRequest.get(i).equals(fileA.getName()))
			{
				//SettingSRC.FileComparer_ReportRequest.remove(i);
				logRequested = true;
				break;
			}
		}
		
		invert = false;
		initilize();
		
		writeToLog(shortName + "\n Has been initilized", 1);
	}
	
	public void initilize()
	{
		// Sets all readable outputs to original values
		HTMLOutputArray = null;
		warningFlagA = false;
		warningFlagB = false;
		failureFlagA = false;
		failureFlagB = false;
		filesAreIdentical = true;
		numOfSingularLines = 0;
		numOfDifferences = 0;
		numIdenticalLines = 0;
		
		//writeToLog(shortName + "\n Has been initilized", 1);
	}
	
	/*
	 * Produces a 2d String array containing the data from the file converted into Strings
	 * the 0 index, refers to Fikle A
	 * the 1 index refers to File B
	 */
	public String[][] produceFileStringArrays(Settings.FileComparer settings, boolean invert, boolean bToA)
	{
		String[][] output = new String[2][];
		writeToLog("Converting files into Strings", 2);
		String[] fileBStringArray = null;
		String[] fileAStringArray = null;

		fileAStringArray = convertFileToString(fileA, settings);
		fileBStringArray = convertFileToString(fileB, settings);
		
		if(invert == true)
		{
			String[] tempA = new String[fileAStringArray.length];
			String[] tempB = new String[fileBStringArray.length];
			for(int i = 0; i < fileAStringArray.length; i ++)
			{
				tempA[i] = fileAStringArray[fileAStringArray.length - 1 -i];
			}
			for(int i = 0; i < fileBStringArray.length; i++)
			{
				tempB[i] = fileBStringArray[fileBStringArray.length - 1 -i];
			}
			fileAStringArray = tempA;
			fileBStringArray = tempB;
		}
		
		output[0] = fileAStringArray;
		output[1] = fileBStringArray;
		
		writeToLog("\tConversion Succsseful", 2);
		
		return output;
	}
	
	public void masterStart(Settings.FileComparer settings)
	{
		settingsToString = Utilities.Debug.getPublicFieldsFromClassToString(settings);
		if(settingsToString == null)
		{
			settingsToString = "null -- ERROR!";
			ExecutableMain.reportAWarning("File Comp Object", "We couldn't get a string representation of the settings Object", parent);
		}
		writeToLog(shortName + "\n Starting the FileComparision algorithm", 1);
	}
	
	public void start2(Settings.FileComparer settings)
	{
		settingsToString = Utilities.Debug.getPublicFieldsFromClassToString(settings);
		if(settingsToString == null)
		{
			settingsToString = "null -- ERROR!";
			ExecutableMain.reportAWarning("File Comp Object", "We couldn't get a string representation of the settings Object", parent);
		}
		writeToLog(shortName + "\n Starting the FileComparision algorithm", 1);
		String[][] fileStringArrays = produceFileStringArrays(settings, false, false);
		
		String[] fileAStringArray = fileStringArrays[0];
		String[] fileBStringArray = fileStringArrays[1];
		
		performAlgorithm(fileAStringArray, fileBStringArray, settings);
		if(settings.compareBothWays == true)
		{
			
		}
		if(settings.compareByInversion == true)
		{
			
		}
			
	}
	
	public void inversionComparision(Settings.FileComparer settings)
	{
		
	}
	
	public void bToAComparision(Settings.FileComparer settings)
	{
		
	}
	
	public void performAlgorithm(String[] fileAStringArray, String[] fileBStringArray, Settings.FileComparer settings)
	{
		writeToLog("Creating Index Matching Arrays: A -> B", 2);
		FileIndexMatch[] indexMatchArrayA = generateIndexMatchingArray(fileAStringArray, fileBStringArray, settings);
		writeToLog("\tIndex Matching Array Creation successfuly", 2);
		
		writeToLog("Generating HTML Output Array", 2);
		String[][] HTMLOutputArrayA = generateHTMLOutputArray(indexMatchArrayA, fileAStringArray, fileBStringArray, settings.htmlStringArrays);
		writeToLog("\tFinishedGenerating HTML Output Array", 2);
	}
	
	public void start(Settings.FileComparer settings)
	{
		writeToLog(shortName + "\n Starting the FileComparision algorithm", 1);
		if(settings == null)
		{
			if(defaultSettings == null)
			{
				settings = Settings.FileComparer.generateDefault();
			}
			else
			{
				settings = defaultSettings;
			}
		}
		
		writeToLog("Converting files into Strings", 2);
		String[] fileAStringArray = convertFileToString(fileA, settings);
		String[] fileBStringArray = convertFileToString(fileB, settings);
		
		// If inversion is true we'll flip the array, so that the bottom is now index 0.
		if(invert == true)
		{
			writeToLog("Performing Operations through file Inversion");
			String[] tempA = new String[fileAStringArray.length];
			String[] tempB = new String[fileBStringArray.length];
			for(int i = 0; i < fileAStringArray.length; i ++)
			{
				tempA[i] = fileAStringArray[fileAStringArray.length - 1 -i];
			}
			for(int i = 0; i < fileBStringArray.length; i++)
			{
				tempB[i] = fileBStringArray[fileBStringArray.length - 1 -i];
			}
			fileAStringArray = tempA;
			fileBStringArray = tempB;
		}
		writeToLog("\tConversion Succsseful", 2);
		
		// Will create an indexMatchArray which is a set of integers describing how a line in file A relates to a line in file B.
		writeToLog("Creating Index Matching Arrays: A -> B", 2);
		FileIndexMatch[] indexMatchArrayA = generateIndexMatchingArray(fileAStringArray, fileBStringArray, settings);
		writeToLog("\tIndex Matching Array Creation successfuly", 2);
		
		// Produce the String[][] array that will be seen as the output of this file and to later be parsed by the HTML writter
		writeToLog("Generating HTML Output Array", 2);
		String[][] HTMLOutputArrayA = generateHTMLOutputArray(indexMatchArrayA, fileAStringArray, fileBStringArray, settings.htmlStringArrays);
		writeToLog("\tFinishedGenerating HTML Output Array", 2);
		
		// Saving the state for documentation purposes of numOfSingualr Lines and numOfDifferences
		int numOfSingularTryOne = numOfSingularLines;
		int numOfDifferencesTryOne = numOfDifferences;
		
		//TODO: Set it up for the A->B and invert, for warnings and all cases
		if(filesAreIdentical == false)
		{
			writeToLog("Files Are NOT identical", 2);

			initilize();
			
			writeToLog("Checking to see if we were to compare B to A, if we would recieve a beter result", 2);
			writeToLog(" Generating indexMatchArray B -> A", 2);
			FileIndexMatch[] indexMatchArrayB = generateIndexMatchingArray(fileBStringArray, fileAStringArray, settings);
			writeToLog("  indexMatchArray B -> A: Generation Complete", 2);
			
			writeToLog(" Generating HTMLOutputArray B -> A: ", 2);
			String[][] HTMLOutputArrayB = generateHTMLOutputArray(indexMatchArrayB, fileBStringArray, fileAStringArray, settings.htmlStringArrays);
			writeToLog("  HTMLOutputArray B -> A: Complete", 2);
			swapFilesInHTMLOutputArray(HTMLOutputArrayB);
			
			if(HTMLOutputArrayA.length < HTMLOutputArrayB.length)
			{
				numOfSingularLines = numOfSingularTryOne;
				numOfDifferences = numOfDifferencesTryOne;
				HTMLOutputArray = HTMLOutputArrayA;
				HTMLOutputArrayB = null;
				failureFlag = failureFlagA;
			}	
			else
			{
				HTMLOutputArray = HTMLOutputArrayB;
				HTMLOutputArrayA = null;
				
				if(invert == true)
					BToAInvertSuccssful = true;
				else
					BtoASuccessful = true;
				
				failureFlag = failureFlagB;
			}
			
			if(numOfSingularLines > (int) (Math.max(fileAStringArray.length, fileBStringArray.length) * settings.warningFactor))
			{
				warningFlag = true;
				String warning = "Appending the entire HTMLOutput Array\n" + HTMLOutputToString(HTMLOutputArray);
				if(invert == false && (settings.compareByInversionUponWarning == true || settings.compareByInversion == true))
				{
					performInversionComparission(settings);
				}
				if(warningFlag == true)
				{
					//ExecutableMain.reportAWarning(identifier, warning);
					warningData = warning;
				}
			}
		}
		else
		{
			writeToLog("Files Are Identical", 2);
			HTMLOutputArray = HTMLOutputArrayA;
		}
		
		writeToLog(HTMLOutputToString(HTMLOutputArray), 4);
	}
	
	public void performInversionComparission(Settings.FileComparer settings)
	{
		writeToLog("We'll be inverting the file, and attempt a comparision from the bottom up", 2);
		invert = true;
		String[][] HTMLOutputArraySnapShot = new String[HTMLOutputArray.length][HTMLOutputArray[0].length];
		for(int i = 0; i < HTMLOutputArraySnapShot.length; i++)
		{
			for(int j = 0; j < HTMLOutputArraySnapShot[0].length; j++)
			{
				HTMLOutputArraySnapShot[i][j] = HTMLOutputArray[i][j];
			}
		}
		initilize();
		start(settings);
		// If the original is longer than the inversion
		if(HTMLOutputArraySnapShot.length > HTMLOutputArray.length)
		{
			writeToLog("Flipped Successful", 2);
			// We'll make a new array and store the original into it, and modify that
			HTMLOutputArraySnapShot = new String[HTMLOutputArray.length][HTMLOutputArray[0].length];
			// Switch all the pointers around
			for(int i = 0; i < HTMLOutputArraySnapShot.length; i++)
			{
				HTMLOutputArraySnapShot[i] = HTMLOutputArray[HTMLOutputArray.length - 1 -i];
			}
			
			int aCount = 0;
			int bCount = 0;
			writeToLog("\tFlipping the New Inversion Array", 2);
			for(int i = 0; i < HTMLOutputArraySnapShot.length; i++)
			{
				/*
				 * output[0] = fileAText formatted for HTML 
				 * output[1] = fileALineIndex
				 * output[2] = fileBText formated for HTML
				 * output[3] = fileBLineIndex
				 * output[4] = HTML Line Number (in NATURAL format, therefore Line #1 not Line #0, corresponds to Line #1 in the code
				 * output[5] = HTML Line Formatting (-1 => Left -2 => Right)
				 * output[6] = Match Score
				 */
				HTMLOutputArraySnapShot[i][4] = (i + 1) + "";
				if(HTMLOutputArraySnapShot[i][5].equals("-1") == false)
					aCount++;
				if(HTMLOutputArraySnapShot[i][5].equals("-2") == false)
					bCount++;
				HTMLOutputArraySnapShot[i][1] = (aCount) + "";
				HTMLOutputArraySnapShot[i][3] = (bCount) + "";
			}
			HTMLOutputArray = HTMLOutputArraySnapShot;
			LogBook.writeToDebug("We Needed to flip for: " + identifier, true);
			invertSuccessful = true;
			
		}
		else
		{
			//TODO: When you update the method, remember if it doesnt'w ork you have to reset all previous values such as # sing # dif etc.
			// Since the original is still shorter, restore from snapshot
			HTMLOutputArray = HTMLOutputArraySnapShot;
			invertSuccessful = false;
			BToAInvertSuccssful = false;
			writeToLog("Flipping proved unsuccseful", 2);
		}
	}
	
	public void compareBToA()
	{
		
	}
	
	public void produceStringArrays()
	{
		
	}
	
	public int getNumSingular()
	{
		return numOfSingularLines;
	}
	
	public int getNumDifferences()
	{
		return numOfDifferences;
	}
	
	public int getNumIdentical()
	{
		return numIdenticalLines;
	}
	
	public String[][] getOutput()
	{
		return HTMLOutputArray;
	}
	
	public boolean getWarningFlag()
	{
		return warningFlag;
	}
	
	public boolean getFailureFlag()
	{
		return failureFlag;
	}
	
	public boolean areFilesIdentical()
	{
		return filesAreIdentical;
	}
	
	/* *********************************
	 * Static methods start:
	 */
	
	// This is a separate method, in case we have Settings to dictate, how the file is converted.
	private static String[] convertFileToString(File file, Settings.FileComparer settings)
	{
		return Utilities.FileUtilities.fileToStringArrayNoNewLines(file);
	}
	
	/*
	 * This method removes unwanted characters from the String's to be examined
	 */
	public static String scrubString(String input, Settings.FileComparer.StringMatch settings)
	{
		if(settings.scrubString == false)
			return input;
		
		String output = input;
		
		if(settings.ignoreComments == true)
		{
			if(settings.ignoreSingleLineComments == true)
			{
				if(output.contains(settings.commentLineIndicator))
				{
					output = output.substring(0, output.indexOf(settings.commentLineIndicator) + settings.commentLineIndicator.length());
				}
			}
			if(settings.ignoreMultiLineComments == true)
			{
				if(output.contains(settings.multiLineCommentStartIndicator))
				{
					output = output.substring(0, output.indexOf(settings.commentLineIndicator) + settings.commentLineIndicator.length());
				}
			}
		}
		
		if(settings.ignoreNumbers == true)
			output = output.replaceAll("\\d", "");
		
		if(settings.ignoreWhiteSpace == true)
			output = output.replaceAll("\\s", "");
		
		return output;
	}
	
	/*
	 * Produces a FileIndexMatchArray[]
	 * A FileIndexMatch is an object describing the best line of File B that Corresponds to a given line of File A. (the given line of file A in question is given by the index of the array)
	 */
	private FileIndexMatch[] generateIndexMatchingArray(String[] fileAStringArray, String[] fileBStringArray, Settings.FileComparer settings)
	{
		writeToLog("Generating FileIndexMatch Array:", true);
		writeToLog("Size of A:\t" + fileAStringArray.length + "\tSize Of B:\t" + fileBStringArray.length);
		FileIndexMatch[] result = new FileIndexMatch[fileAStringArray.length];
		
		// These two variables indicate the index of Fiel A or File B to check
		int indexOfA = 0;
		int indexOfB = 0;
		boolean itteratedThroughA = false;
		
		ArrayList<Integer> confirmationIndexList = new ArrayList<Integer>();
			// The variable above is a flag, used to indicate whether we have encountered a line that needs to be verified if it's correct
			// Usually it's because these Strings are so common, that they could easily be confirmed incorrectly, therefore we will wait until a String that is not on the confirmationlist is verified, before checking if these are unique or not
			/*
			 * EXAMPLE:
			 * 	Consider the following files:
			 * 		A				B
			 *	1	\n		|		\n
			 * 	2	\n		|		Data
			 * 	3	Data	|		Chip
			 * 	4	Chip	|		\n
			 * 	5	\n		|		\n
			 * 
			 * 	Line 1a and Line 1b match
			 * 	Line 2a	and Line 4b would then proceed to match, and by extension 2b, and 3b would be considered singular
			 *  Line 3a and Line 4a would check and have no matches following 3b, and would therefore also be considered singular
			 *  Line 5a and Line 5b would match.
			 *  
			 *  In reality the only likely singular line is: 2a, and 4b (or 5b) therefore if we wait to verify if the 2a to 4b match is correct, we would arrive at a more accurate matching pair	
			 */		
		
		// Indicates the numberOfComments, also a good gauge of the strength of this FileIndexMatch.
		int numStringsWithNoCorrespondingIndex = 0;
		// TODO: Something is messing up with that numOfLiensRequiring
		int numOfLinesRequiringConfirmation = 0;
		
		while(indexOfB < fileBStringArray.length)
		{
			writeToLog("\nIndex of A:\t" + indexOfA + "\tIndexOfB:\t" + indexOfB + "\tSizeOfConfirmationList:\t" + confirmationIndexList.size() + "\tnum Lines requiring confirmation:\t" + numOfLinesRequiringConfirmation);
			// Indicates we have reached the endOf File A before the endOfFileB
			if(indexOfA >= fileAStringArray.length)
			{
				itteratedThroughA = true;
				break;
			}
			
			boolean lineRequiresConfirmation = false;
			
			int loopLength = settings.numberOfLinesToItterateThrough;
			// We check the settings file to see how many lines ahead we are to look, we check if this number > than the # lines we have left to check and if so set it to that
			if(loopLength > (fileBStringArray.length - indexOfB))
			{
				loopLength = fileBStringArray.length - indexOfB;
			}
			
			String a = scrubString(fileAStringArray[indexOfA], settings.stringMatch);
			writeToLog("\tScrubbing String A: " + a);
			
			// After scrubbing the string and realize that the string is empty, we'll need confirmation in regards to the match.
			if(a.equals(""))
			{
				lineRequiresConfirmation = true;
				writeToLog("\t String A requires Confirmation");
			}
			
			/*
			 * If we have lines that we need to confirm, we will start checking from that line's index down.
			 */
			if(confirmationIndexList.size() != 0 && lineRequiresConfirmation == true)
			{
//				loopLength = loopLength - numOfLinesRequiringConfirmation;
//				writeToLog("Comparing File A Index Line: " + indexOfA + "\ttowards the B Indexes from:\t" + (indexOfB + numOfLinesRequiringConfirmation) + " to " + (loopLength + indexOfB));
				loopLength = loopLength - ((result[confirmationIndexList.get(confirmationIndexList.size() - 1).intValue()].getCorrespondingIndex() + 1) - indexOfB);
				writeToLog("Comparing File A Index Line: " + indexOfA + "\ttowards the B Indexes from:\t" + (indexOfB + numOfLinesRequiringConfirmation) + " to " + (loopLength + indexOfB));
			}
			else
			{
				writeToLog("Comparing File A Index Line: " + indexOfA + "\ttowards the B Indexes from:\t" + indexOfB + " to " + (loopLength + indexOfB));
			}
			
			double[] scores = new double[loopLength];
			double[] rawScores = new double[loopLength];
			
			for(int i = 0; i < loopLength; i++)
			{
				String b = "";
				// Logic regarding when we ackonwledge the lineRequiersConfirmation flag
				if(confirmationIndexList.size() != 0 && lineRequiresConfirmation == true)
				{
					writeToLog("\t\tAcknowledge that this string requiresConfirmation, and that the flag was set, therefore fetching: ");
					// Start the index from the confirmationIndexList's last entry.
					int tempIndex = (result[confirmationIndexList.get(confirmationIndexList.size() - 1).intValue()].getCorrespondingIndex() + 1);
					b = scrubString(fileBStringArray[tempIndex + i], settings.stringMatch);
					writeToLog("\tScrubbing String B whose index is: " + (tempIndex + i) + "\tand is: " + b);
//					b = scrubString(fileBStringArray[indexOfB + i + numOfLinesRequiringConfirmation], settings.stringModifiers);
//					writeToLog("\tScrubbing String B whose index is: " + (indexOfB + i + numOfLinesRequiringConfirmation) + "\tand is: " + b);
				}
				else
				{
					b = scrubString(fileBStringArray[indexOfB + i], settings.stringMatch);
					writeToLog("\tScrubbing String B whose index is: " + (indexOfB + i) + "\tand is: " + b);
				}
				writeToLog("\t\tcomparing scrubbed string A and scrubb string B");
				
				// Perform the algorithms specified
				rawScores[i] = generateStringMatchScore(a, b, settings.stringMatch);
				// TODO: Consider if the check flag is set, we'll anti-penalize it.
				scores[i] = rawScores[i] * Math.pow(settings.lineDecrementFactor, i);
				writeToLog("\t\tString A compares to String B with a score of:\t " + scores[i]);
			}
			
			// Determine if there is a corresponding match, what is the strength of the match, and what is the corresponding index in B.
			double maxScore = 0;
			int scoreIndex = -1;
			for(int i = 0; i < loopLength; i++)
			{
				/*
				 *  We're going through looking at all the confidence scores, we'll say the best match is one that:
				 *  	The score > min confidence
				 *  	AND
				 *  	The score > then the current score 
				 *  		OR
				 *  	the raw score is high enough such that it'll be considered "white" and somehow it's been penalized enough that it's not counted	
				 *  	
				 */
				
				if((scores[i] > maxScore || (rawScores[i] >= settings.htmlStringArrays.thresholds[0] && scores[i] < maxScore))  && scores[i] > settings.minConfidence)
				{
					maxScore = scores[i];
					scoreIndex = i;
				}
			}
			
			if(maxScore > 1000)
			{
				ExecutableMain.reportAWarning(identifier, "Has reported a warning due to a score > 1000 between the strings: \n" + fileAStringArray[indexOfA] + "\n" + fileBStringArray[indexOfB + scoreIndex], parent);
			}
			
			writeToLog("\n\t The Max score is:\t" + maxScore + "\t whose Line B index is:\t" + (scoreIndex + indexOfB) + "\tThe min Score Necessary is: " + settings.minConfidence);
			
			// The line in reference to indexOfA may be scrubbed to null, but there is no corresponding match therefore irrelevent
			if(lineRequiresConfirmation == true && scoreIndex == -1)
			{
				writeToLog("\tEven though this line requires confirmation, we found no comprable comparisions therefore... scrubbed");
				lineRequiresConfirmation = false;
			}
			
			// A -1 score index means no match
			if(scoreIndex != -1)
			{
				// Perform all the log necessary for dealing with the line requires confirmation flag
				if(confirmationIndexList.size() == 0 && lineRequiresConfirmation == false)
				{
					indexOfB += scoreIndex;
					result[indexOfA] = new FileIndexMatch(indexOfB, (int) (maxScore * 1000));
				}
				else
				{
					// Refers to the fact that: we acknowledge the confirmation flag, but it wasn't generated from this line, therefore we must check 
					if(lineRequiresConfirmation == false)
					{
						indexOfB += scoreIndex;
						result[indexOfA] = new FileIndexMatch(indexOfB, (int) (maxScore * 1000));
						
						// We will now confirm each confirmation.
						while(confirmationIndexList.size() != 0)
						{
							if(result[confirmationIndexList.get(0).intValue()].getCorrespondingIndex() < result[indexOfA].getCorrespondingIndex())
							{
								// Confirmation returns true, therefore we do not have to change the variable
								writeToLog("\tWe have determined that:\t" + result[confirmationIndexList.get(0).intValue()].getCorrespondingIndex() + " < " + result[indexOfA].getCorrespondingIndex() + "\t Therefore line: " + confirmationIndexList.get(0).intValue() + " is fine");
								confirmationIndexList.remove(0);
							}
							else
							{
								// We must set the line to singular
								writeToLog("\tWe have determined that:\t" + result[confirmationIndexList.get(0).intValue()].getCorrespondingIndex() + " >= " + result[indexOfA].getCorrespondingIndex() + "\t Therefore, line:" + confirmationIndexList.get(0).intValue()  + "is singular");
								result[confirmationIndexList.get(0).intValue()].setIndex(-1);
								result[confirmationIndexList.get(0).intValue()].setScore(0);
								confirmationIndexList.remove(0);
								numStringsWithNoCorrespondingIndex++;
							}
							writeToLog("Decrementing number of liens that require confirmation to: " + numOfLinesRequiringConfirmation);
							numOfLinesRequiringConfirmation--;
						}
					}
				}
				
				// Flags the line for needing confirmation
				if(lineRequiresConfirmation == true)
				{
					int tempB = indexOfB + scoreIndex + numOfLinesRequiringConfirmation;
					if(confirmationIndexList.size() != 0)
						tempB = (result[confirmationIndexList.get(confirmationIndexList.size() - 1).intValue()].getCorrespondingIndex() + 1) + scoreIndex;
					result[indexOfA] = new FileIndexMatch(tempB, (int) (maxScore * 1000));
					confirmationIndexList.add(new Integer(indexOfA));
					writeToLog("adding the index: " + indexOfA + "\tto the list whose value is:\t" + tempB);
					numOfLinesRequiringConfirmation++;
					writeToLog("Incrementing number of liens that require confirmation to: " + numOfLinesRequiringConfirmation);
				}
				else
				{
					indexOfB++;
					writeToLog("\tIncrementing B Index to:" + indexOfB);
				}
			}
			else // The line is therefore singular
			{
				result[indexOfA] = new FileIndexMatch(-1, 0);
				numStringsWithNoCorrespondingIndex++;
				
//				// We must increase the confirmation list size
//				if(confirmationIndexList.size() != 0)
//					numOfLinesRequiringConfirmation++;
			}
			indexOfA++;
		}
		// Since we have reached the end of FileB before the end of FIle A, we will assume that all the rest of the remaining lines of A are singular
		if(itteratedThroughA == false)
		{
			for(int i = indexOfA ; i < fileAStringArray.length; i++ )
			{
				result[i] = new FileIndexMatch(-1, 0);
				numStringsWithNoCorrespondingIndex++;
			}
		}
		
		writeToLog("\n" + FileIndexMatchArrayToString(result));
		return result;
	}
	
	/*
	 * These are the character codes for these special reserved characters in HTML
	 */
	private static String removeHTMLLiterals(String x)
	{
		String result = x.replaceAll("<", "&lt;");
		result = result.replaceAll(">", "&gt;");
		result = result.replaceAll("/", "&#47;");
		// we need to double escape the [ key
		result = result.replaceAll("\\[", "&#91");
		result = result.replaceAll("]", "&#93;");
		result = result.replaceAll("\"", "&#34;");
		result = result.replaceAll("\'", "&#39;");
		return result;
	}
	
	/*
	 * Produces the comparison score between two strings
	 */
	private double generateStringMatchScore(String a, String b, Settings.FileComparer.StringMatch settings)
	{
		Settings.FileComparer.verifyStringMatchSettings(settings);
		
		if(settings.ignoreCase == false)
		{
			if(a.equals(b))
				return 1.0;
		}
		else
		{
			if(a.equalsIgnoreCase(b))
				return 1.0;	
		}
		
		double result = 0;
		
		int maxLength = Math.max(a.length(), b.length());
		int minLength = Math.min(a.length(), b.length());
		
		double confidenceValue = 0.0;
		
		for(int i = 0; i < settings.algorithmsToUse.length; i++)
		{
			switch(settings.algorithmsToUse[i])
			{
				case SimpleMatch:
					confidenceValue += StringComparisionAlgorithms.algorithm1(a,b,maxLength) * settings.weightingFactor[i];
					break;
				case ExponentialMatch:
					confidenceValue += StringComparisionAlgorithms.exponentialAlgorithm(maxLength, minLength, a, b) * settings.weightingFactor[i];
					break;
				case LinearMatch:
					confidenceValue += StringComparisionAlgorithms.linearAlgorithm(maxLength, minLength, a, b) * settings.weightingFactor[i];
					break;
				case SimpleContains:
					confidenceValue += StringComparisionAlgorithms.basicCharacterRemovalAlgorithm(a, b, settings) * settings.weightingFactor[i];
					break;
				case LinearContains:
					confidenceValue += StringComparisionAlgorithms.linearWeightedCharacterRemovalAlgorithm(a, b, settings) * settings.weightingFactor[i];
					break;
				case ExponentialContains:
					confidenceValue += StringComparisionAlgorithms.exponentiallyWeightedCharacterRemovalAlgorithm(a, b, settings) * settings.weightingFactor[i];
				case SimpleLongestCommonSubsequence:
					confidenceValue += StringComparisionAlgorithms.lcsAlgorithmSimple(a, b);
				case WeightedLongestCommonSubsequence:
					confidenceValue += StringComparisionAlgorithms.lcsAlgorithmWeighted(a, b);
			}
		}
		
		return confidenceValue;
	}
	
	private String[][] generateHTMLOutputArray(FileIndexMatch[] indexMatchArray, String[] fileAText, String[] fileBText, Settings.FileComparer.HTMLStringArray settings)
	{
		ArrayList<String[]> result = new ArrayList<String[]>();
		int indexOfLastPrintedB = 0;
		int indexOfLastPrintedA = 0;
		
		int singularFactor = 0;	// A variable that indicates which file has more singular lines, with negative being A leaning, and positive being B leaning
		
		int lastPrintedHTMLLine = 0;

		while(true)
		{
			// The output array is a data array that contains all the data for a given line in the HTML table.
			/*
			 * output[0] = fileAText formatted for HTML 
			 * output[1] = fileALineIndex
			 * output[2] = fileBText formated for HTML
			 * output[3] = fileBLineIndex
			 * output[4] = HTML Line Number (in NATURAL format, therefore Line #1 not Line #0, corresponds to Line #1 in the code
			 * output[5] = HTML Line Formatting
			 * output[6] = Match Score
			 */
			String[] output = new String[7];
			if(indexOfLastPrintedA >= fileAText.length || indexOfLastPrintedB >= fileBText.length)
			{
				writeToLog("We have determined that whilst compiling the HTMLOutput String Array, one of the files ended before the other, we will itterate to fill out the singular lines");
					if(indexOfLastPrintedA >= fileAText.length)
						writeToLog("The File to be filled is: File B (therefore a reached its limit)");
					if(indexOfLastPrintedB >= fileBText.length)
						writeToLog("The File to be filled is: File A (therefore b reached its limit)");
					
				boolean inB = false;
				boolean inA = false;
				while(indexOfLastPrintedB < fileBText.length)
				{
					output = new String[7];
					if(inB == false)
					{
						inB = true;
						writeToLog("Looping to add B");
					}
					output[0] = "";
					output[2] = removeHTMLLiterals(fileBText[indexOfLastPrintedB]);
					output[5] = "-2";
					output[6] = "---";
					indexOfLastPrintedB++;
					singularFactor++;
					this.filesAreIdentical = false;
					lastPrintedHTMLLine++;
					output[4] = lastPrintedHTMLLine + "";
					output[1] = indexOfLastPrintedA + "";
					output[3] = indexOfLastPrintedB + "";
					numOfSingularLines++;
					result.add(output);
				}
				while(indexOfLastPrintedA < fileAText.length)
				{
					output = new String[7];
					if(inA == false)
					{
						inA = true;
						writeToLog("Looping to add A");
					}
					output[0] = removeHTMLLiterals(fileAText[indexOfLastPrintedA]);
					output[2] = "";
					output[5] = "-1";
					output[6] = "---";
					indexOfLastPrintedA++;
					singularFactor--;
					this.filesAreIdentical = false;
					lastPrintedHTMLLine++;
					output[4] = lastPrintedHTMLLine + "";
					output[1] = indexOfLastPrintedA + "";
					output[3] = indexOfLastPrintedB + "";
					numOfSingularLines++;
					result.add(output);
				}
				writeToLog("exiting the end itteration loop");
				break;
			}
			
			int backgroundType = 0;
			if(indexMatchArray[indexOfLastPrintedA].getCorrespondingIndex() == -1) // Left Side Is Singular
			{
				output[0] = removeHTMLLiterals(fileAText[indexOfLastPrintedA]);
				output[2] = "";
				output[5] = "-1";
				output[6] = "---";
				indexOfLastPrintedA++;
				singularFactor--;
				numOfSingularLines++;
				this.filesAreIdentical = false;
			}
			else
			{
				if((indexOfLastPrintedA + singularFactor) < indexMatchArray[indexOfLastPrintedA].getCorrespondingIndex()) // We havn't matched up the lines properlly
				{
					output[0] = "";
					output[2] = removeHTMLLiterals(fileBText[indexOfLastPrintedB]);
					output[5] = "-2";
					output[6] = "---";
					indexOfLastPrintedB++;
					singularFactor++;
					numOfSingularLines++;
					this.filesAreIdentical = false;
				}
				else
				{
					output[5] = "0";
					for(int i = 0; i <settings.thresholds.length; i++)
					{
						if(indexMatchArray[indexOfLastPrintedA].getScore() < settings.thresholds[i])
							output[5] = (i + 1) + "";
					}
					output[0] = removeHTMLLiterals(fileAText[indexOfLastPrintedA]);
					output[2] = removeHTMLLiterals(fileBText[indexOfLastPrintedB]);
					output[6] = indexMatchArray[indexOfLastPrintedA].getScore() + "";
					
					indexOfLastPrintedA++;
					indexOfLastPrintedB++;
					if(output[5].equals("0") == false)
						numOfDifferences++;
					else
						numIdenticalLines++;
				}
			}
			lastPrintedHTMLLine++;
			output[4] = lastPrintedHTMLLine + "";
			output[1] = indexOfLastPrintedA + "";
			output[3] = indexOfLastPrintedB + "";
			
			result.add(output);
		}
		
		String[][] formatedResult = new String[result.size()][7];
		int counter = 0;
		
		while(result.size() != 0)
		{
			formatedResult[counter] = result.get(0);
			counter++;
			result.remove(0);
		}
		
		return formatedResult;
	}
	
	private String HTMLOutputToString(String[][] input)
	{
		String output = "Output for This Strign Array is:";
		for(int i = 0 ; i < input.length; i++)
		{
			output +="\n";
			for(int j = 0; j < input[i].length; j++)
			{
				if(j == 0)
				{
					if(input[i][j].replaceAll("\\s", "").equals("") == false && Integer.parseInt(input[i][5]) >= 0)
						output += "A";
					else
						output += "_";
				}
				else if (j == 2)
				{
					if(input[i][j].replaceAll("\\s", "").equals("") == false && Integer.parseInt(input[i][5]) >= 0)
						output += "B";
					else
						output += "_";
				}
				else
					output += input[i][j];
				
				output += "\t||\t";
			}
		}
		return output;
	}
	
	// This is for when the B->A comparison occurs
	private String[][] swapFilesInHTMLOutputArray(String[][] input)
	{
		/*
		 * output[0] = fileAText formatted for HTML 
		 * output[1] = fileALineIndex
		 * output[2] = fileBText formated for HTML
		 * output[3] = fileBLineIndex
		 * output[4] = HTML Line Number (in NATURAL format, therefore Line #1 not Line #0, corresponds to Line #1 in the code
		 * output[5] = HTML Line Formatting
		 * output[6] = Match Score
		 */
		
		writeToLog("Swapping Columns for the HTMLOutputArray");
		writeToLog("Prior:");
		writeToLog(HTMLOutputToString(input));
		
		String tmpString;
		String tmpLine;
		for(int i = 0; i < input.length; i++)
		{
			tmpString = new String(input[i][0]);
			tmpLine = new String(input[i][1]);
			input[i][0] = input[i][2];
			input[i][1] = input[i][3];
			input[i][2] = tmpString;
			input[i][3] = tmpLine;
		}
		return input;
	}
	
	public boolean[] getApproachBooleans()
	{
		boolean[] output = {BtoASuccessful, invertSuccessful, BToAInvertSuccssful};
		return output;
	}
	
	public String getSettings()
	{
		return settingsToString;
	}
	
	// Log Methods:
	
	private void writeToLog(String entry, boolean forceTimeStamp, int priority, Settings.FileComparer settings)
	{
		if(settings.logPriority >= priority)
			LogBook.getLog(Log.logType.File_Comparision).writeToLog(entry, forceTimeStamp);
		if(logRequested == true)
			LogBook.getLog(Log.logType.Requested_Report).writeToLog(entry, forceTimeStamp);
	}	
	
	private void writeToLog(String entry)
	{
		writeToLog(entry, false);
	}
	
	private void writeToLog(String entry, int priority)
	{
		writeToLog(entry, false, priority);
	}
	
	private void writeToLog(String entry, boolean forceTimeStamp)
	{
		if(forceTimeStamp == true)
			writeToLog(entry, forceTimeStamp, 1, defaultSettings);
		else
			writeToLog(entry, forceTimeStamp, 5, defaultSettings);
	}
	
	private void writeToLog(String entry, boolean forceTimeStamp, int priority)
	{
		writeToLog(entry, forceTimeStamp, priority, defaultSettings);
	}
	
	private void forceWriteToLog(String entry, boolean forceTimeStamp)
	{
		LogBook.getLog(Log.logType.File_Comparision).writeToLog(entry, forceTimeStamp);
	}
	
	// Diagnostic Output Methods:
	
	private String FileIndexMatchArrayToString(FileIndexMatch[] input)
	{
		String output = ("Displaying the output of the FileIndexMatch Algorithm:" +
				"\n\tNote Line Formating IS NATURAL (meaning 1 corresponds to first line of code not 0)" +
				"\nFileA\t--->\tFileB\tScore (/1000)");
		
		for(int i = 0; i < input.length; i++)
		{
			output += "\n";
			if(i < 9)
			{
				if(input[i].getCorrespondingIndex() < 99)
				{
					if(input[i].getCorrespondingIndex() >= 0)
						output += ("  " + (i + 1) + "\t\t--->\t " + (input[i].getCorrespondingIndex() + 1) + "\t\t\t" + input[i].getScore());
					else
						output += ("  " + (i + 1) + "\t\t--->\t " + (input[i].getCorrespondingIndex()) + "\t\t\t" + input[i].getScore());
				}
				else
				{
					if(input[i].getCorrespondingIndex() >= 0)
						output += ("  " + (i + 1) + "\t\t--->\t " + (input[i].getCorrespondingIndex() + 1) + "\t\t" + input[i].getScore());
					else
						output +=("  " + (i + 1) + "\t\t--->\t " + (input[i].getCorrespondingIndex()) + "\t\t" + input[i].getScore());
				}
			}
			else if(i < 99)
			{
				if(input[i].getCorrespondingIndex() < 99)
				{
					if(input[i].getCorrespondingIndex() >= 0)
						output += ("  " + (i + 1) + "\t--->\t " + (input[i].getCorrespondingIndex() + 1) + "\t\t\t" + input[i].getScore());
					else
						output += ("  " + (i + 1) + "\t--->\t " + (input[i].getCorrespondingIndex()) + "\t\t\t" + input[i].getScore());
				}
				else
				{
					if(input[i].getCorrespondingIndex() >= 0)
						output += ("  " + (i + 1) + "\t--->\t " + (input[i].getCorrespondingIndex() + 1) + "\t\t" + input[i].getScore());
					else
						output +=("  " + (i + 1) + "\t--->\t " + (input[i].getCorrespondingIndex()) + "\t\t" + input[i].getScore());
				}
			}
			else
			{
				if(input[i].getCorrespondingIndex() < 99)
				{
					if(input[i].getCorrespondingIndex() >= 0)
						output += ("  " + (i + 1) + "\t--->\t " + (input[i].getCorrespondingIndex() + 1) + "\t\t\t" + input[i].getScore());
					else
						output += ("  " + (i + 1) + "\t--->\t " + (input[i].getCorrespondingIndex()) + "\t\t\t" + input[i].getScore());
				}
				else
				{
					if(input[i].getCorrespondingIndex() >= 0)
						output += ("  " + (i + 1) + "\t--->\t " + (input[i].getCorrespondingIndex() + 1) + "\t\t\t" + input[i].getScore());
					else
						output +=("  " + (i + 1) + "\t--->\t " + (input[i].getCorrespondingIndex()) + "\t\t\t" + input[i].getScore());
				}	
			}	
		}
		return output;
	}
	
	public String getWarningData()
	{
		return warningData;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
}
