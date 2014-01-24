import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class SettingSRC 
{
	// Performance Settings 
	public static boolean performSubFolderComparisons = false;
	public static ArrayList<String> extensionList = null;
	//public static boolean redoAllWarningsWithoutNewLines = false;
	
	// Log Settings
	public static boolean generateLogFiles = true;
	public static boolean displayTimeStampInLogs = false;
	
	
	// Console Display Settings
	public static boolean consoleDisplay_CriticalInformation = true;
		public static boolean consoleDisplay_CriticalInformation_FileComparision = true;
	public static boolean consoleDisplay_FolderManipulation = false;
	public static boolean consoleDisplay_FileManipulation = false;
		public static boolean consoleDisplay_FileManipulation_File = false;
	public static boolean consoleDisplay_FolderComparision = false;
	public static boolean consoleDisplay_FileComparision = false;
		public static boolean consoleDisplay_FileComparision_Detailed = false;
	public static boolean consoleDispaly_HTMLReport = false;
	public static boolean consoleDisplay_FailureReport = false;
	public static boolean consoleDisplay_RequestedReport = false;
	public static boolean consoleDisplay_WarningReport = false;
	public static boolean consoleDisplay_timeStamp = false;
	
	// File Compare Settings
	public static boolean FileComparision_Log_DisplayAllStringMatching = true;
	public static int FileComparision_StringMatchAlgorithm = -1337;
	public static boolean itterateThroughSettingsArrayList = true;
	
	// Report Request Settings
	public static ArrayList<String> FileComparer_ReportRequest = null;
	public static final String[] FileComparer_ReportRequest_HARDCODE =
	{
//		"Master_FM_ack_op_01_02_05_06_07_08.script",
//		"ASM_tfm.script",
		"cex_change_anr_parameter.script" 
	};
	public static int FileComparer_ReportRequest_StringComparision_DetailLevel = 1; // 0 => No details, 1 => Show only the final total reports
	
	private enum Setting_Strings
	{
		performsubfoldercomparisions,
		includeonlyspecificfileextensions,
		fileextensionslist,
		itteratethroguhfilecomparisionsettings,
		generatelogfiles,
		displaytimestampforallevents,
		filecomparisionlogpriority,
		logallhtmllinesthatarewritten,
		displayinitlizationifnormation,
	}
	
	//FileWriting
	public static boolean FileManipulation_EnableDetailedLog = true;
	
	public static void initalizeDefaultSettings()
	{
		LogBook.getLog(Log.logType.Critical_Output).writeToLog("Initalizing Settings", true);
		FileComparer_ReportRequest = new ArrayList<String>();
		for(int i = 0; i < FileComparer_ReportRequest_HARDCODE.length; i++)
		{
			FileComparer_ReportRequest.add(FileComparer_ReportRequest_HARDCODE[i]);
		}
	}
	
	public static void loadSettings(File file)
	{
		Scanner scanner = null;
		try 
		{
			scanner = new Scanner(file);
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		String nextLine = "";
		while(scanner.hasNext() == true)
		{
			nextLine = scanner.nextLine();
			String data = nextLine.split("//")[0];
			if(data.replaceAll("\\s", "").equals("") == false)
			{
				String[] split = data.split("=");
				String header = split[0].replaceAll("\\s", "").toLowerCase();
				map.put(header, split[1]);
			}
		}
		
		if(map.containsKey("includeonlyspecificfileextensions") == true && map.containsKey("fileextensionslist") == true)
		{
			extensionList = new ArrayList<String>();
			String[] split = map.get("fileextensionslist").split(",");
			for(int i = 0; i < split.length; i++)
			{
				extensionList.add(split[i]);
			}
		}
			
	}
	
	private static void loadSettings()
	{
		// scan the file:
//		while(scanner.hasNext() == true)
//		{
//			
//			String data = nextLine.split("//")[0];
//			if(data.replaceAll("\\s","").equals("") == false)
//			{
//				String header = data.split("=")[0];
//				map.put(header, data.split("=")[1])
//			}
//		}
	}
	
	public static void generateUniqueID()
	{
		
	}
	
	public static void saveSettings(File destination)
	{
		
	}
}
