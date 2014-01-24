import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;

import Settings.FileComparer.StringMatch;
import Utilities.GlobalVariables;

/*
 * C:\Users\ESTEYEE\Desktop\V2-differnce-files\Core_Common_V2\12.2 
C:\Users\ESTEYEE\Desktop\V2-differnce-files\Core_Common_V2\13a
C:\Users\ESTEYEE\Desktop\Testing_Main
 */

/*
 * TODO:
 * http://forums.winmerge.org/viewtopic.php?f=4&t=53 
 * http://wiki.winmerge.org/wiki/InLineDifferencingThoughts
 * http://stackoverflow.com/questions/943927/embed-a-web-browser-within-a-java-application
 * 
 * http://www.tutorialspoint.com/python/python_cgi_programming.htm -- Create a form, it'll execute a python command. 
 */

public class ExecutableMain 
{
	// an enum describing the optional commands that may be listed in the command line file, we only care about the names therefore we will not attach additional methods to this subclass
	public enum OptionalCommand
	{
		name,
		settingFileComp,
		settingFileCompReference,
		PROGRAM_SETTINGS_ENUM
	}
	
	// General Operational Statistics
	public int failureCount = 0;
	public int warningCount = 0;
	public int warningsFixedCount = 0;
	private int numIdenticalFiles = 0;
	private int numDifferentFiles = 0;
	
	// Array Lists of filePaths
	private ArrayList<String> left = null;
	private ArrayList<String> right = null;
	private ArrayList<String> both = null;
	
	private ArrayList<String> identicalFiles = null;
	private ArrayList<String> differentFiles = null;
	
	// Settings
	public static ArrayList<Settings.FileComparer> fileCompSettingsList = null;
	public static File PROGRAM_SETTINGS = null;
	
	// List of General HTML Report Writers
	// TODO: Replace the geneartion of 5 separate HTML Main reports, with an active Javascript that builds the table based on your search query.
	private HTMLWriter htmlMainReportWriter = null;
	private HTMLWriter htmlMainReportWriter_DifferenceOnly = null;
	private HTMLWriter htmlMainReportWriter_IdenticalOnly = null;
	private HTMLWriter htmlMainReportWriter_AOnly = null;
	private HTMLWriter htmlMainReportWriter_BOnly = null;
	
	private static boolean ignoreWarnings;
	
	// Variables to be stashed in a setting file later:
	
	private static final String nameOfDifferenceReportDirectory = "Difference_files";
	private static final String nameOfIdenticalReportDirectory = "File_Reports";
	
	private String leftFolderPath = null;
	private String rightFolderPath = null;
	private String outputReportPath = null;
	
	private boolean initilized;
	private boolean singleFileComparission;
	
	private ExecutableMain parent;
	
	public String leftInputName = null;
	public String rightInputName = null;
	private String mainReportName = null;
	
	public static void main(String args[])
	{
		ExecutableMain main = new ExecutableMain(args, null);
		if(main.initilized == true)
			main.runProgram();
		else
			terminateProgram("Main Program never initilzied properlly");
	}
	
	public ExecutableMain(String args[], ExecutableMain parent)
	{
		this.parent = parent;
		
		singleFileComparission = false;
		initilized = false;
		
		// Initilize the File comparison settings list
		fileCompSettingsList = new ArrayList<Settings.FileComparer>();
		boolean initilizationPass = true;
		
		try
		{
			leftFolderPath = args[0];
			rightFolderPath = args[1];
			outputReportPath = args[2];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			/*
			 * This is some dirty code, but it's a fail safe to ensure that the program can still run, if we get a bad subfolder comp
			 */
			if(parent == null)
			{
				displayHelp();
				System.exit(0);
			}
			else
			{
				initilizationPass = false;
				reportAFailure("We failed to generate a new Executable Main");
			}
		}
		
		// We are provided more arguments than just the 3 required, therefore they're optional
		if(args.length > 3)
		{
			int argOptionalCmds = args.length - 3;
			if( argOptionalCmds % 2 != 0)
			{
				if(parent == null)
				{
					displayHelp();
					System.exit(0);
				}
				else
				{
					initilizationPass = false;
					reportAFailure("We failed to generate a new Executable Main");
					terminateProgram("We have an unpaire optional argument");
				}
			}
			String[] optionalCmds = new String[args.length - 3];
			for(int i = 0; i < optionalCmds.length; i++)
				optionalCmds[i] = args[i + 3];
			
			parseAdditionalArguments(optionalCmds);
		}
		
		// checking if the left and right arguments passed are satisfactory
		File left = new File(leftFolderPath);
		File right = new File(rightFolderPath);
		
		if(left.isDirectory() == true && right.isDirectory() == false)
		{
			initilizationPass = false;
			reportAFailure("Was passed both a directory and file");
			terminateProgram("Program was passed a directory and a file");
		}
		
		if(left.isFile() == true && right.isFile() == true)
		{
			singleFileComparission = true;
		}
		
		if(initilizationPass == true)
			initilized = true;
	}
	
	public void runProgram()
	{
		long programStartTime = System.currentTimeMillis();
		
		// The parent being null means that this is the root file, therefore we will be taking down things such as the proram's start time and other initial environmental variables.
		if(parent == null)
			GlobalVariables.programStartTime = programStartTime;
		
		//Creating the appropriate directories for the log files
		String logFolderPath = (outputReportPath + "//" + "Generation_logs");
		createAppropriateDirectories(logFolderPath);
		
		if(parent == null)
			LogBook.initalizeLogBook(logFolderPath, programStartTime);
		
		leftInputName = new File(leftFolderPath).getName();
		rightInputName = new File(rightFolderPath).getName();
		
		String outputReportName = "Difference Report- " + leftInputName + " vs. " + rightInputName;
		
		if(mainReportName == null)
			mainReportName = outputReportName;
		
		// since the global variables for leftFolder and rightFolder, are only relevant should we be doing a folde rcomp we'll leave the name as such
		if(parent == null)
		{
			GlobalVariables.leftFolderName = leftInputName;
			GlobalVariables.rightFolderName = rightInputName;
			GlobalVariables.mainReportName = mainReportName;
		}
		
		/*
		 * Checking all the required files are there
		 * Setting up the environment in memory
		 */
		String executableDirectory = System.getProperty("user.dir");
			GlobalVariables.executableDirectroy = executableDirectory;
			
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy 'at' hh:mm:ss");
			GlobalVariables.executionDate = dateFormat.format(date);
		
		// Check for all required Resources
		if(Resources.checkForRequiredResources(Log.logType.Critical_Output) == false)
			terminateProgramFromMain("Missing Resources in Resource Folder");
		
		// Setting up Operational Settings
		//TODO: Set up the Operational Settings to be read from file
		SettingSRC.initalizeDefaultSettings();
		if(PROGRAM_SETTINGS != null)
			SettingSRC.loadSettings(PROGRAM_SETTINGS);
		else
			
		
		// If the user did not import any fileComp settings, use the default set.
		if(fileCompSettingsList.isEmpty() == true)
		{
			fileCompSettingsList = Settings.FileComparer.generateDefaultList();
		}
		
		// END Necessary INITILIZATION
		
		// Should we have parsed that it was a singleFileComparission run.
		if(singleFileComparission == true)
		{
			File left = new File(leftFolderPath);
			File right = new File(rightFolderPath);
			writeToLog("Parsing the inputs this is a single file comparission request");
			compareSingleFile(left, right);
			terminateProgram("Program Complete - Single File Comparission");
		}
		
		/*
		 * Setting up the environment for a folder-folder comparission request
		 */
		LogBook.getLog(Log.logType.Critical_Output).writeToLog("Reading from console arguments: \nProgram will compare: \n\t" + leftFolderPath + "\n\t" + rightFolderPath + "\nAnd write the report contents into: " + outputReportPath, true);
		
		String differenceFileOutputPath = outputReportPath + File.separator + nameOfDifferenceReportDirectory + File.separator;
		String leftOutputPath = outputReportPath + File.separator + leftInputName + File.separator;
		String leftOriginalPath = outputReportPath + File.separator + "Original_" + leftInputName + File.separator;
		String rightOriginalPath = outputReportPath + File.separator + "Original_" + rightInputName + File.separator;
		String rightOutputPath = outputReportPath + File.separator + rightInputName + File.separator;
		String identicalFileOutputPath = outputReportPath + File.separator + nameOfIdenticalReportDirectory + File.separator;
		String subReportPath = outputReportPath + File.separator + "subReports" + File.separator;
		String tempPath = outputReportPath + File.separator + "tmp" + File.separator;
		String commentPath = outputReportPath + File.separator + "comments" + File.separator;
		String subFolderComparisionPath = outputReportPath + File.separator + "subFolderComparision" + File.separator;
		
		generateDestiantionEnvironment(new String[] {differenceFileOutputPath, identicalFileOutputPath, leftOriginalPath, rightOriginalPath, subReportPath, tempPath, commentPath});
		//generateDestinationEnvironment(differenceFileOutputPath, leftOutputPath, rightOutputPath);
		
		// Begin the generation of all the HTML Master Reports
		htmlMainReportWriter = new HTMLWriter(outputReportPath, HTMLWriter.HTMLFileType.MasterDocument, executableDirectory, "", this);
		htmlMainReportWriter_DifferenceOnly = new HTMLWriter(subReportPath, HTMLWriter.HTMLFileType.MasterDocument, executableDirectory, "../" , this);
		htmlMainReportWriter_IdenticalOnly = new HTMLWriter(subReportPath, HTMLWriter.HTMLFileType.MasterDocument, executableDirectory, "../" , this);
		htmlMainReportWriter_AOnly = new HTMLWriter(subReportPath, HTMLWriter.HTMLFileType.MasterDocument, executableDirectory, "../" , this);
		htmlMainReportWriter_BOnly = new HTMLWriter(subReportPath, HTMLWriter.HTMLFileType.MasterDocument, executableDirectory, "../" , this);
		
		htmlMainReportWriter.generateMasterHTMLDocumentStart(mainReportName);
		htmlMainReportWriter_DifferenceOnly.generateMasterHTMLDocumentStart(mainReportName + "-differenceOnly");
		htmlMainReportWriter_IdenticalOnly.generateMasterHTMLDocumentStart(mainReportName + "-identicalOnly");
		htmlMainReportWriter_AOnly.generateMasterHTMLDocumentStart(mainReportName + "-" + leftInputName + "Only");
		htmlMainReportWriter_BOnly.generateMasterHTMLDocumentStart(mainReportName + "-" + rightInputName + "Only");
		
		//fileCompSettingsList.add(Settings.FileComparer.generateDefault());
	
		// Write to log the list of files to be used
		writeToLog("Settings to be used:", false);
		for(int i = 0; i < fileCompSettingsList.size(); i++)
		{
			String[][] temp = Utilities.Debug.getPublicFieldsFromClass(fileCompSettingsList.get(i));
			for(int j = 0; j < temp.length; j++)
			{
				writeToLog(temp[j][0] +"\t||\t" + temp[j][1], false);
			}
		}
		
		// Perform Top Level Comparision
		ArrayList<String> subFoldersToCompare = start(leftFolderPath, rightFolderPath, differenceFileOutputPath, leftOutputPath, rightOutputPath, fileCompSettingsList);
		
		HTMLWriter htmlIdenticalWriter = new HTMLWriter(identicalFileOutputPath, HTMLWriter.HTMLFileType.identicalFile, System.getProperty("user.dir"), "../", this);
		HTMLWriter commentFileGenerator = new HTMLWriter(outputReportPath + File.separator + "comments" + File.separator, HTMLWriter.HTMLFileType.CommentFile, System.getProperty("user.dir"), "../", this);
		
		/*
		 * We're prematurely writting the data that is to be shown as the statistics for the main, since ideally we should free some memory up by removing all the files in the left, and right and identical directories before proceeding through an unknown ammount of subfolders
		 */
		ArrayList<String[][]> data = new ArrayList<String[][]>();
		String[][] temp = 
			{
				// 
				{
					(differentFiles.size() + identicalFiles.size() + left.size() + right.size()) + "",
					identicalFiles.size() + "",
					differentFiles.size() + "",
					left.size() + "",
					right.size() + "",
					warningCount + "",
					warningsFixedCount + "",
				}
			};
		
		data.add(temp);
		
		/*
		 * Generation of all the relevant file reports
		 */
		writeToLog("Creating File Reports for all Identical Files");
		while(identicalFiles.size() != 0)
		{
			writeToLog("\tCreating Report for: " + identicalFiles.get(0));
			htmlIdenticalWriter.generateHTMLFileReport(leftFolderPath + File.separator + identicalFiles.get(0));
			commentFileGenerator.generateCommentFile("comments-" + identicalFiles.get(0));
			identicalFiles.remove(0);
		}
		writeToLog("Creating File Reports for all Files unique to: " + leftInputName);
		while(left.size() != 0)
		{
			writeToLog("\tCreating Report for: " + left.get(0));
			if(new File(right.get(0)).isDirectory() == true)
			{
				writeToLog("Found a directory unique to: " + leftInputName);
			}
			else
				htmlIdenticalWriter.generateHTMLFileReport(leftFolderPath + File.separator + left.get(0));
			
			commentFileGenerator.generateCommentFile("comments-" + left.get(0));
			left.remove(0);
		}
		writeToLog("Creating File Reports for all Files unique to: " + rightInputName);
		while(right.size() != 0)
		{
			writeToLog("\tCreating Report for: " + right.get(0));
			if(new File(rightFolderPath + File.separator + right.get(0)).isDirectory() == true)
			{
				writeToLog("Found a directory unique to: " + rightInputName);
			}
			else
				htmlIdenticalWriter.generateHTMLFileReport(rightFolderPath + File.separator + right.get(0));
			
			commentFileGenerator.generateCommentFile("comments-" + right.get(0));
			right.remove(0);
		}
		
		moveOriginalFiles(leftFolderPath, leftOriginalPath);
		moveOriginalFiles(rightFolderPath, rightOriginalPath);
		
		/*
		 * Performign subfolder comparissions
		 */
		if(subFoldersToCompare.size() != 0)
		{
			while(subFoldersToCompare.size() != 0)
			{
				String[] args = new String[5];
				String name = subFoldersToCompare.get(0);
				args[0] = leftFolderPath + File.separator + name + File.separator;
				args[1] = rightFolderPath + File.separator + name + File.separator;
				args[2] = outputReportPath + File.separator + "subFolderComparision" + File.separator + name;
				args[3] = "-name";
				args[4] = name;
				ExecutableMain subFolderMain = new ExecutableMain(args, this);
				subFolderMain.runProgram();
				
				String[] masterHTMLInput = new String[6];
				
				masterHTMLInput[0] = "subFolderComparision" + File.separator + subFoldersToCompare.get(0) + File.separator + name + ".html";
				masterHTMLInput[1] = "Folder Existing in both Directories";
				masterHTMLInput[2] = "---";
				masterHTMLInput[3] = "---";
				masterHTMLInput[4] = "&nbsp;";
				masterHTMLInput[5] = "&nbsp;";
				htmlMainReportWriter.generateMasterHTMLDocumentTableRow(masterHTMLInput, 0);
				
				subFoldersToCompare.remove(0);
			}
		}
		
		// finish off this itterations main reports
		
		writeToLog("Finishing off main report subFolders");
		
		// Generate the sidebars and end of the methods for the MASTER HTML Documents
		htmlMainReportWriter.generateMasterHTMLDocumentFinishReport(data);
		htmlMainReportWriter_DifferenceOnly.generateMasterHTMLDocumentFinishReport(data);
		htmlMainReportWriter_IdenticalOnly.generateMasterHTMLDocumentFinishReport(data);
		htmlMainReportWriter_AOnly.generateMasterHTMLDocumentFinishReport(data);
		htmlMainReportWriter_BOnly.generateMasterHTMLDocumentFinishReport(data);
		
		if(parent == null)
			terminateProgramFromMain("Program Succsesfully Completed");
	}

	private static void displayHelp()
	{
		String display = 
				"***************************************" +
				"\n Welcome to the File Comparer Program:" +
				"\n***************************************" +
				"\nThis Program allows a user to compare two folders together representing different versions of a program or script and produce An HTML document that will display the differences in a concise and clear manner" +
				"\n" +
				"\nTo use the program via the command-line interface, please provide it at minimum 3 arguments" +
				"\n\tArgument 1: Path to Folder/File A" +
				"\n\tArgument 2: Path to Folder/File B" +
				"\n\tArgument 3: Path to the Output Directory" +
				"\n" +
				"\nWe may also provide a further set of optional inputs:" +
				"\n\t-name [input]\t: Names the Output Directory and Main HTML document based on the provided argument" +
				"\n\t-settingFileComp [path1,path2,path3...]\t: Provides a file path where the program will load an optional setting file, and overide the default settings" +
				"\n\t-settingFileCompReference [path]\t: Provides a text file that contains a list of all the file paths for the File Comparer settings to be used" +
				"";
		System.out.println(display);
	}
	
	private void compareSingleFile(File left, File right)
	{
		Settings.FileComparer settings = fileCompSettingsList.get(0);
		HTMLWriter output = new HTMLWriter(outputReportPath, HTMLWriter.HTMLFileType.differenceFile, System.getProperty("user.dir"), "", this);
		FileComparer fileComp = new FileComparer(left, right, settings, this);
		fileComp.start(settings);
		
		//
		if(fileComp.areFilesIdentical() == false)
		{	
			if(fileComp.getWarningFlag() == true)
			{
				reportAWarning(fileComp.getIdentifier(), fileComp.getWarningData(), this);
			}
			int listCounter = 0;
			while(listCounter < fileCompSettingsList.size())
			{
				// Sets up the new environment on the assumption we'll perform a new file comparission
				settings = fileCompSettingsList.get(listCounter);
				writeToLog("Using Settings: " + settings.fileName);
				String settingsUsed = Utilities.Debug.getPublicFieldsFromClassToString(settings);
					//TODO: this is a crude 1 dimensional way, we'll probably create a method to handle the multi dimensional functionalities
				// Performs a file comparission on the assumption that it's 
				if(settings.attemptToFixWarnings == true && fileComp.getWarningFlag() == true)
				{
					writeToLog("Attempting to fix Warnings By:");
					int counter = 0;
					writeToLog("\t increasing minimumConfidence");
					while(counter < settings.maxMinConfidenceStep)
					{
						FileComparer fileCompFix = new FileComparer(left, right, settings, this);
						settings.itterateMinConfidence();
						//writeToLog("\t\tMinConfidence now " + settings.minConfidence);
						fileCompFix.start(settings);
						if(fileCompFix.getOutput().length < fileComp.getOutput().length)
						{
							writeToLog("\t\t Improvment seen by setting Minconfidence to: " + settings.minConfidence + " By reducing the OUTPUT Array length from: " + fileComp.getOutput().length + " to: " + fileCompFix.getOutput().length );
							fileComp = fileCompFix;
							settingsUsed = Utilities.Debug.getPublicFieldsFromClassToString(settings);
						}
						if(fileComp.getWarningFlag() == false)
							break;
						counter++;
					}
					if(fileComp.getWarningFlag() == false)
					{
						writeToLog("Warning fixed, via minConfidence modifications");
						warningsFixedCount++;
					}
					else
					{
						writeToLog("\t increasing the lineDecrementFactor");
						settings.minConfidence = settings.minConfidence - settings.minConfidenceStep * counter;
						counter = 0;
						while(counter < settings.maxLineDecrementSteps)
						{
							FileComparer fileCompFix = new FileComparer(left, right, settings, this);
							settings.itterateLineDecFactor();
							//writeToLog("\t\tLine Dec Factor now " + settings.lineDecrementFactor);
							fileCompFix.start(settings);
							if(fileCompFix.getOutput().length < fileComp.getOutput().length)
							{
								writeToLog("\t\t Improvment seen by setting Line Dec Factor to: " + settings.lineDecrementFactor + " By reducing the OUTPUT Array length from: " + fileComp.getOutput().length + " to: " + fileCompFix.getOutput().length );
								fileComp = fileCompFix;
								settingsUsed = Utilities.Debug.getPublicFieldsFromClassToString(settings);
							}
							if(fileComp.getWarningFlag() == false)
								break;
							counter++;
						}
						if(fileComp.getWarningFlag() == false)
						{
							writeToLog("Warning fixed, via line Decrement modifications");
							warningsFixedCount++;
						}
						else
						{
							settings.lineDecrementFactor = settings.lineDecrementFactor + settings.lineDecrementFactorStep * counter;
						}
					}
				}
				else
				{
					break;
				}
				
				listCounter++;
			}
			
			
			ArrayList<String[][]> data = new ArrayList<String[][]>();
			String[][] differenceStats = 
			{
					{"Number of Difference", fileComp.getNumDifferences() + ""},
					{"Number of Singular Lines", fileComp.getNumSingular() + ""},
					{"Number of Identical Lines", fileComp.getNumIdentical() + ""},
					{"Number of Lines in File A", fileComp.getOutput()[fileComp.getOutput().length - 1][1] + ""},
					{"Number of Lines in File B", fileComp.getOutput()[fileComp.getOutput().length - 1][3] + ""},
			};
			data.add(differenceStats);
			
			String approachType = "";
			if(fileComp.getApproachBooleans()[2] == true)
				approachType = "Bottom to Top / File B -> File A";
			else if(fileComp.getApproachBooleans()[1] == true)
				approachType = "Bottom to Top / File A -> File B";
			else if(fileComp.getApproachBooleans()[0] == true)
				approachType = "Top to Bottom / File B -> File A";
			else
				approachType = "Top to Bottom / File A -> File B";
			
			String[][] compareStats = 
			{
					{"Approach Type", approachType},
					{"Settings:", fileComp.getSettings()}
			};
			data.add(compareStats);
			
			output.generateDifferenceReport(mainReportName, fileComp, data);
			
		}
		else
		{
			writeToLog("Files Are Identical");
		}
		fileComp = null;
		//ExecutableMain.terminateProgram("PREDEFINED IN CODE");
		if(SettingSRC.consoleDisplay_CriticalInformation_FileComparision == true)
			writeToLog("File Comparision object has returned without alarm");
	}

	
	private void parseAdditionalArguments(String[] input)
	{
		int counter = 0;
		while(counter < input.length)
		{
			String command = input[counter];
			String argument = input[counter + 1];
			OptionalCommand parsedCommand = OptionalCommand.valueOf(command.substring(1));
			System.out.println(parsedCommand + "********");
			switch(parsedCommand)
			{
				case name:
					mainReportName = argument;
					System.out.println("Argument Parsed");
					break;
				case settingFileComp:
					String[] files = argument.split(",");
					for(int i = 0; i < files.length; i++)
					{
						Settings.FileComparer settings = new Settings.FileComparer(files[i]); 
						fileCompSettingsList.add(settings);
					}
					break;
				case settingFileCompReference:
					Scanner scanner = null;
					File file = new File(argument);
					try 
					{
						scanner = new Scanner(file);
					} 
					catch (FileNotFoundException e) 
					{
						System.out.println("Program Failed, due to beign unable to find the setting File Comp Reference File");
						System.exit(0);
					}
					
					while(scanner.hasNext())
					{
						Settings.FileComparer settings = new Settings.FileComparer(scanner.nextLine());
						fileCompSettingsList.add(settings);
					}
					break;
				case PROGRAM_SETTINGS_ENUM:
						PROGRAM_SETTINGS = new File(argument);
						if(PROGRAM_SETTINGS.exists() == false)
							PROGRAM_SETTINGS = null;
					break;
				default:
					break;
			}
			counter += 2;
		}
	}
	
	private ArrayList<String> start(String leftFolderPath, String rightFolderPath, String differenceFileOutputPath, String leftOutputPath, String rightOutputPath, ArrayList<Settings.FileComparer> settingsList)
	{
		Settings.FileComparer settings = null;
		ArrayList<String> result = new ArrayList<String>();
		
		writeToLog("Attempting to create the appropriate Destination Folders", true);
		
		createAppropriateDirectories(differenceFileOutputPath);
		createAppropriateDirectories(leftOutputPath);
		createAppropriateDirectories(rightOutputPath);
		
		File leftFolder = new File(leftFolderPath);
		File rightFolder = new File(rightFolderPath);
		
		writeToLog("Creating a Folder Comparer Object between the folders:\n" + leftFolder + "\n" + rightFolder, true);
		
		FolderComparer folderComp = new FolderComparer(leftFolder, rightFolder, SettingSRC.extensionList);
		folderComp.start();
		
		writeToLog("Folder Comparer Object has returned without alarm", true);
		
		left = folderComp.getAOnlyList();
		right = folderComp.getBOnlyList();
		both = folderComp.getContainedInBothList();
		
		writeToLog("Number of files in left Only: \t" + left.size() + "\nNumber of files in right Only: \t" + right.size() + "\n Number of files in both: \t" + both.size());
		
		if(SettingSRC.consoleDisplay_CriticalInformation_FileComparision == true)
			writeToLog("Beggining File Comparision Operations for the files found in both \n" + leftFolder + "\n" + rightFolder +"\n", true);
		
		identicalFiles = new ArrayList<String>();
		differentFiles = new ArrayList<String>();
		
		int differenceCounter = 0;
		
		HTMLWriter commentFileGenerator = new HTMLWriter(outputReportPath + File.separator + "comments" + File.separator, HTMLWriter.HTMLFileType.CommentFile, System.getProperty("user.dir"), "../", this);
		
		// Creating difference Reports
		for(int i = 0; i < both.size(); i++)
		{
			settings = settingsList.get(0);
			if(SettingSRC.consoleDisplay_CriticalInformation_FileComparision == true)
				writeToLog("Setting up the Environment to create a File Comparer Object for: \"" + both.get(i) + "\" between the folders: \n\t" + leftFolderPath + "\n\t" + rightFolderPath + "\n Providing a destination path for their comparision as\n\t" + differenceFileOutputPath + "\n", true);
			String destinationName = both.get(i);
			File a = new File(leftFolderPath + File.separator + destinationName);
			File b = new File(rightFolderPath + File.separator + destinationName);
			
			commentFileGenerator.generateCommentFile("comments-" + destinationName);
			
			HTMLWriter htmlDifferenceWriter = new HTMLWriter(differenceFileOutputPath, HTMLWriter.HTMLFileType.differenceFile, System.getProperty("user.dir"), "../", this);
			if(a.isDirectory() == false && b.isDirectory() == false)
			{
				//TODO: This should really run generateCommentFile
				
				String destination = (differenceFileOutputPath + destinationName);
				String[] masterHTMLInput = new String[6];
				//FileComparer fileComp = new FileComparer(destination, a, b);
				
				FileComparer fileComp = new FileComparer(a, b, settings, this);
				fileComp.start(settings);
				
				int masterHTMLRowColour = 0;
				
				if(fileComp.areFilesIdentical() == true)
				{
					identicalFiles.add(destinationName);
					masterHTMLInput[0] = nameOfIdenticalReportDirectory + "/" + both.get(i) + ".html";
					masterHTMLInput[1] = "Files Are Identical";
					masterHTMLInput[2] = "---";
					masterHTMLInput[3] = "---";
					masterHTMLInput[4] = "&nbsp;";
					masterHTMLInput[5] = "&nbsp;";
					htmlMainReportWriter_IdenticalOnly.generateMasterHTMLDocumentTableRow(masterHTMLInput, masterHTMLRowColour);
				}
				else
				{	
					if(fileComp.getWarningFlag() == true)
					{
						masterHTMLRowColour = 2;
						reportAWarning(fileComp.getIdentifier(), fileComp.getWarningData(), this);
					}
					int listCounter = 0;
					while(listCounter < settingsList.size())
					{
						settings = settingsList.get(listCounter);
						writeToLog("Using Settings: " + settings.fileName);
						String settingsUsed = Utilities.Debug.getPublicFieldsFromClassToString(settings);
						//TODO: this is a crude 1 dimensional way, we'll probably create a method to handle the multi dimensional functionalities
						if(settings.attemptToFixWarnings == true && fileComp.getWarningFlag() == true)
						{
							writeToLog("Attempting to fix Warnings By:");
							int counter = 0;
							writeToLog("\t increasing minimumConfidence");
							while(counter < settings.maxMinConfidenceStep)
							{
								FileComparer fileCompFix = new FileComparer(a, b, settings, this);
								settings.itterateMinConfidence();
								//writeToLog("\t\tMinConfidence now " + settings.minConfidence);
								fileCompFix.start(settings);
								if(fileCompFix.getOutput().length < fileComp.getOutput().length)
								{
									writeToLog("\t\t Improvment seen by setting Minconfidence to: " + settings.minConfidence + " By reducing the OUTPUT Array length from: " + fileComp.getOutput().length + " to: " + fileCompFix.getOutput().length );
									fileComp = fileCompFix;
									settingsUsed = Utilities.Debug.getPublicFieldsFromClassToString(settings);
								}
								if(fileComp.getWarningFlag() == false)
									break;
								counter++;
							}
							if(fileComp.getWarningFlag() == false)
							{
								writeToLog("Warning fixed, via minConfidence modifications");
								masterHTMLRowColour = 1;
								warningsFixedCount++;
							}
							else
							{
								writeToLog("\t increasing the lineDecrementFactor");
								settings.minConfidence = settings.minConfidence - settings.minConfidenceStep * counter;
								counter = 0;
								while(counter < settings.maxLineDecrementSteps)
								{
									FileComparer fileCompFix = new FileComparer(a, b, settings, this);
									settings.itterateLineDecFactor();
									//writeToLog("\t\tLine Dec Factor now " + settings.lineDecrementFactor);
									fileCompFix.start(settings);
									if(fileCompFix.getOutput().length < fileComp.getOutput().length)
									{
										writeToLog("\t\t Improvment seen by setting Line Dec Factor to: " + settings.lineDecrementFactor + " By reducing the OUTPUT Array length from: " + fileComp.getOutput().length + " to: " + fileCompFix.getOutput().length );
										fileComp = fileCompFix;
										settingsUsed = Utilities.Debug.getPublicFieldsFromClassToString(settings);
									}
									if(fileComp.getWarningFlag() == false)
										break;
									counter++;
								}
								if(fileComp.getWarningFlag() == false)
								{
									writeToLog("Warning fixed, via line Decrement modifications");
									masterHTMLRowColour = 1;
									warningsFixedCount++;
								}
								else
								{
									settings.lineDecrementFactor = settings.lineDecrementFactor + settings.lineDecrementFactorStep * counter;
								}
							}
						}
						else
						{
							break;
						}
						
						if(fileComp.getNumSingular() == 0)
						{
							masterHTMLRowColour = 0;
						}
						listCounter++;
					}
					differenceCounter++;
					
					ArrayList<String[][]> data = new ArrayList<String[][]>();
					String[][] differenceStats = 
					{
							{"Number of Difference", fileComp.getNumDifferences() + ""},
							{"Number of Singular Lines", fileComp.getNumSingular() + ""},
							{"Number of Identical Lines", fileComp.getNumIdentical() + ""},
							{"Number of Lines in File A", fileComp.getOutput()[fileComp.getOutput().length - 1][1] + ""},
							{"Number of Lines in File B", fileComp.getOutput()[fileComp.getOutput().length - 1][3] + ""},
					};
					data.add(differenceStats);
					
					String approachType = "";
					if(fileComp.getApproachBooleans()[2] == true)
						approachType = "Bottom to Top / File B -> File A";
					else if(fileComp.getApproachBooleans()[1] == true)
						approachType = "Bottom to Top / File A -> File B";
					else if(fileComp.getApproachBooleans()[0] == true)
						approachType = "Top to Bottom / File B -> File A";
					else
						approachType = "Top to Bottom / File A -> File B";
					
					String[][] compareStats = 
					{
							{"Approach Type", approachType},
							{"Settings:", fileComp.getSettings()}
					};
					data.add(compareStats);
					
					htmlDifferenceWriter.generateDifferenceReport(destinationName, fileComp, data);
					
					masterHTMLInput[0] = nameOfDifferenceReportDirectory + "/" + destinationName + ".html";
					masterHTMLInput[1] = "Files Are Different";
					masterHTMLInput[2] = fileComp.getNumDifferences() + "";
					masterHTMLInput[3] = fileComp.getNumSingular() + "";
					masterHTMLInput[4] = "&nbsp;";
					masterHTMLInput[5] = differenceCounter + "";
					differentFiles.add(destinationName);
					htmlMainReportWriter_DifferenceOnly.generateMasterHTMLDocumentTableRow(masterHTMLInput, masterHTMLRowColour);
				}
				htmlMainReportWriter.generateMasterHTMLDocumentTableRow(masterHTMLInput, masterHTMLRowColour);
				
				fileComp = null;
				System.gc();
				//ExecutableMain.terminateProgram("PREDEFINED IN CODE");
				if(SettingSRC.consoleDisplay_CriticalInformation_FileComparision == true)
					writeToLog("File Comparision object has returned without alarm");
			}
			else
			{
				writeToLog(destinationName + " is a directory in both directories therefore adding it to the subDirectory List", false);

				result.add(both.get(i));
			}
			
		}
		
		numIdenticalFiles = identicalFiles.size();
		numDifferentFiles = differentFiles.size();
		
		HTMLWriter fileMover = new HTMLWriter(leftOutputPath, HTMLWriter.HTMLFileType.copyFile, null, null, this);
		
		//Create file reports for Files existing only in Left/A
		createMasterHTMLTableRowForUniqueFiles(left, leftFolderPath, htmlMainReportWriter_AOnly);
		
		//Create file reports for Files existing only in Right/B
		createMasterHTMLTableRowForUniqueFiles(right, rightFolderPath, htmlMainReportWriter_BOnly);
		
		// Need to do subfolder comparision itterations
//		if(SettingSRC.performSubFolderComparisons == true)
//		{
//			while(result.isEmpty() == false)
//			{
//				String newLeftFolderPath = "";
//				String newRightFolderPath = "";
//				String newDestinationOutputPath = "";
//				String newLeftFolderOutputPath = "";
//				String newRightFolderOutputPath = "";
//				
//				start(newLeftFolderPath, newRightFolderPath, newDestinationOutputPath, newLeftFolderOutputPath, newRightFolderOutputPath, settings, settingsList);
//			}
//		}
		return result;
	}
	
	private static void moveOriginalFiles(String source, String destination)
	{
		File originalFolder = new File(source);
		File[] originalFiles = originalFolder.listFiles();
		HTMLWriter fileMover = new HTMLWriter(destination, HTMLWriter.HTMLFileType.copyFile, null, null, null);
		for(int i = 0; i < originalFiles.length; i++)
		{
			fileMover.copyPaste(originalFiles[i]);
			writeToLog("Moving File: " + originalFiles[i].getName() + " to the original Files File Path");
			
		}
	}
	
	private static void createMasterHTMLTableRowForUniqueFiles(ArrayList<String> list, String sourceFolderPath, HTMLWriter writeTo)
	{
		for(int i = 0; i < list.size(); i++)
		{
			String sourceName = list.get(i);
			File source = new File(sourceFolderPath + File.separator + sourceName);
			writeToLog("Attempting to create a FileReport: Searching for file: " + sourceName + "\t within \t" + sourceFolderPath);
			if(source.isDirectory() == false)
			{
				String[] masterHTMLInput = new String[6];
				masterHTMLInput[0] = masterHTMLInput[0] = nameOfIdenticalReportDirectory + "/" + list.get(i) + ".html";
				masterHTMLInput[1] = new File(sourceFolderPath).getName() + " only";
				masterHTMLInput[2] = "---";
				masterHTMLInput[3] = "---";
				masterHTMLInput[4] = "&nbsp;";
				masterHTMLInput[5] = "&nbsp;";
		
				writeTo.generateMasterHTMLDocumentTableRow(masterHTMLInput,0);
			}
			else
			{
				
			}
		}
	}
	
	private static boolean generateDestiantionEnvironment(String[] paths)
	{
		writeToLog("Creating Destination Envrionment:", true);
		createAppropriateDirectories(new File(paths[0]).getParent());
		
		for(int i = 0; i < paths.length; i++)
		{
			createAppropriateDirectories(paths[i]);
		}
		
		return true;
	}
	
	private static boolean createAppropriateDirectories(String name)
	{
		File file = new File(name);
		boolean result = false;
		if(file.exists() && file.isDirectory())
		{
			if(LogBook.isInitialized() == false)
			{
				if(SettingSRC.consoleDisplay_CriticalInformation == true)
				{
					System.out.println("We have already found the directory " + name);
				}
			}
			else
			{
				writeToLog("We have already found the directory " + name);
			}
		}
		else
		{
			if(LogBook.isInitialized() == false)
			{
				System.out.println("Making Directory: " + name + " \t Result: " + file.mkdirs());
			}
			else
			{
				writeToLog("Making Directory: " + name + " \t Result: " + file.mkdirs());
			}
		}
		return result;
	}
	
	public static void terminateProgram(String rationale)
	{
		writeToLog("TERMINATING PROGRAM \nRationale:\t" + rationale, true);
		System.exit(0);
	}
	
	public void terminateProgramFromMain(String rationale)
	{
		writeToLog("TERMINATING PROGRAM \nRationale:\t" + rationale, true);
		writeToLog(" Number of Failures: " + failureCount);
		writeToLog(" Number of warnings: " + warningCount);
		writeToLog("  Number of warnings fixed: " + warningsFixedCount);
		System.out.println("\n\nProgram Started On: " + GlobalVariables.executionDate);
		System.out.println("Program Time: " + Utilities.General.formatMilSec((System.currentTimeMillis() - GlobalVariables.programStartTime)));
		writeToLog(" Number of Files that are identical: " + numIdenticalFiles);
		writeToLog(" Number of Files that are different: " + numDifferentFiles);
		if(SettingSRC.FileComparer_ReportRequest.size() != 0)
		{
			writeToLog("User has requested to generate reports for specific scripts that the program did not find, please check for typos:", false);
			while(SettingSRC.FileComparer_ReportRequest.size() != 0)
			{
				writeToLog(" " + SettingSRC.FileComparer_ReportRequest.get(0), false);
				SettingSRC.FileComparer_ReportRequest.remove(0);
			}
		}
		System.exit(0);
	}
	
	private static boolean generateMasterHTMLReport(String outputReportPath, String reportName)
	{
		boolean result = false;
		return result;
	}
	
	public static void reportAFailure(String entry)
	{
		GlobalVariables.failureCount++;
		writeToLog(entry + " reports a failure\n Current Failure Count:" + GlobalVariables.failureCount + "\n", true);
		LogBook.getLog(Log.logType.Failure_Summary).createLogEntry(entry, true);
	}
	
	public static void reportAFailure(String name, String entry)
	{
		GlobalVariables.failureCount++;
		writeToLog(name + " has reported a failure\n Current Failure Count: " + GlobalVariables.failureCount + "\n", true);
		LogBook.getLog(Log.logType.Failure_Summary).createLogEntry(entry, true);
	}
	
	public static void reportAWarning(String name, String entry, ExecutableMain source)
	{
		if(ignoreWarnings == false)
		{
			source.warningCount++;
			String intro = "\n" + name + "\n\t Has Reported a warning\n";
			writeToLog(intro +" Current Warning Count:" + source.warningCount + "\n", true);
			LogBook.getLog(Log.logType.Warning_Summary).createLogEntry(intro, true);
			LogBook.logWarning(intro + entry, true);
		}
	}
	
	private static void writeToLog(String entry)
	{
		writeToLog(entry, false);
	}
	
	private static void writeToLog(String entry, boolean forceTimeStamp)
	{
		LogBook.getLog(Log.logType.Critical_Output).writeToLog(entry, forceTimeStamp);
	}
	
	/*
	 * 
	 */
	
	public ExecutableMain getParent()
	{
		return parent;
	}
	
	public String getReportName()
	{
		return mainReportName;
	}
}

