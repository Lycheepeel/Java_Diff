

import java.io.File;

import Utilities.GlobalVariables;

public class Resources 
{
	private static final String[] HTML_RESOURCE_FILES =
	{
		"comment.css",
		"comment_template.html",
		"difference_file.css",
		"difference_file_template.html",
		"file_report.css",
		"file_report_template.html",
		"master_report.css",
		"master_report_template.html",
		"global.css"
	};
	
	private static final String[] RESOURCE_SUBFOLDERS = 
	{
		"HTML",
		"Settings"
	};
	
	private static final String[] SETTINGS_SUBFOLDERS =
	{
		"Languages"
	};
	
	private static final String[] SETTINGS_RESOURCE_FILES =
	{
		"FileComp-Default.txt",
		"FileComp-DefaultLevel1.txt",
		"FileComp-DefaultLevel2.txt",
		"FileComp-DefaultLevel3.txt",
		"FileComp-LanguageDefault.txt"
	};
	
	private static final String RESOURCE_FILE_NAME = "res";
	
	public static String HTML_RESOURCE_FILEPATH;
	public static String SETTINGS_FILEPATH;

	private static Log.logType staticType;
	
	public static boolean checkForRequiredResources(Log.logType type)
	{
		staticType = type;
		if(checkForDirectoryInEnvironment(RESOURCE_FILE_NAME, GlobalVariables.executableDirectroy) == false)
			return false;
		GlobalVariables.RESOURCE_FILEPATH = GlobalVariables.executableDirectroy + "\\" + RESOURCE_FILE_NAME;
		
		for(int i = 0; i < RESOURCE_SUBFOLDERS.length; i++)
		{
			if(checkForDirectoryInEnvironment(RESOURCE_SUBFOLDERS[i], GlobalVariables.RESOURCE_FILEPATH) == false)
				return false;
		}
		
		GlobalVariables.HTML_RESOURCE_FILEPATH = GlobalVariables.RESOURCE_FILEPATH + "\\" + "HTML";
		GlobalVariables.SETTING_RESOURCE_FILEPATH = GlobalVariables.RESOURCE_FILEPATH + "\\" + "Settings";
		
		for(int i = 0; i < HTML_RESOURCE_FILES.length; i++)
		{
			if(checkForFileWithinDirectory(HTML_RESOURCE_FILES[i], GlobalVariables.HTML_RESOURCE_FILEPATH) == false)
				return false;
		}
		
		for(int i = 0; i < SETTINGS_SUBFOLDERS.length; i++)
		{
			if(checkForDirectoryInEnvironment(SETTINGS_SUBFOLDERS[i], GlobalVariables.SETTING_RESOURCE_FILEPATH) == false)
				return false;
		}
		
		GlobalVariables.LanguageFilePath = GlobalVariables.SETTING_RESOURCE_FILEPATH + "\\Languages";
		
		for(int i = 0; i < SETTINGS_RESOURCE_FILES.length; i++)
		{
			if(checkForFileWithinDirectory(SETTINGS_RESOURCE_FILES[i], GlobalVariables.SETTING_RESOURCE_FILEPATH) == false)
				return false;
		}
		
		return true;
	}
	
	private static boolean checkForDirectoryInEnvironment(String folderName, String environment)
	{
		writeToLog("Looking for " + folderName + " Directory within: " + environment + "\t...");
		String searchPath = environment + "\\" + folderName;
		File searchFile = new File(searchPath);
		if(searchFile.exists() == false)
		{
			ExecutableMain.reportAFailure("Could Not Find: " + folderName + " within " + environment);
			return false;
		}
		if (searchFile.isDirectory() == false)
		{
			ExecutableMain.reportAFailure(searchPath + " is valid but it does not lead to a directory");
			return false;
		}
		writeToLog("\tFound: " + folderName);
		return true;
	}
	
	public static boolean checkForFileWithinDirectory(String fileName, String environment)
	{
		writeToLog("Looking for " + fileName + " within the directory: " + environment);
		String searchPath = environment + "\\" + fileName;
		File searchFile = new File(searchPath);
		if(searchFile.exists() == false)
		{
			ExecutableMain.reportAFailure("Could Not Find: " + fileName + " within " + environment);
			return false;
		}
		if (searchFile.isFile() == false)
		{
			ExecutableMain.reportAFailure(searchPath + " is valid but it does not lead to a file");
			return false;
		}
		writeToLog("\tFile Found");
		return true;
	}
	
	private static void writeToLog(String entry)
	{
		LogBook.getLog(staticType).writeToLog(entry, false);
	}
}
