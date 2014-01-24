
import java.io.*;
import java.util.Scanner;

public class Log 
{
	public enum logType
	{
		Critical_Output,
		Folder_Manipulation,
		File_Manipulation,
		Folder_Comparision,
		File_Comparision,
		HTML_Report,
		Failure_Report,
		Requested_Report,
		Warning_Report,
		Failure_Summary,
		Warning_Summary,
		Debug_Report
		;
		
		public boolean checkSettings(Log log)
		{
			switch(log.getType())
			{
				case Critical_Output:
					return SettingSRC.consoleDisplay_CriticalInformation;
				case Folder_Manipulation:
					return SettingSRC.consoleDisplay_FolderManipulation;
				case File_Manipulation:
					return SettingSRC.consoleDisplay_FileManipulation;
				case Folder_Comparision:
					return SettingSRC.consoleDisplay_FolderComparision;
				case File_Comparision:
					return SettingSRC.consoleDisplay_FileComparision;
				case HTML_Report:
					return SettingSRC.consoleDispaly_HTMLReport;
				case Failure_Report:
					return SettingSRC.consoleDisplay_FailureReport;
				case Requested_Report:
					return SettingSRC.consoleDisplay_RequestedReport;
				case Warning_Report:
					return SettingSRC.consoleDisplay_WarningReport;
				default:
					return false;
			}
		}
	}
	
	public enum logSeverity
	{
		Report,
		Output,
		Summary
	}
	
	private logType type;
	
	private File file;
	private boolean initialized;
	private FileWriter fstream = null;
	private BufferedWriter out = null;

	public Log(String directory, logType type)
	{
		this.type = type;
		
		String filePath = directory + "\\" + type.toString() + ".txt";
		this.file = new File(filePath);
		
		try 
		{
			fstream = new FileWriter(file);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		out = new BufferedWriter(fstream);
		
		writeToFile(type.toString() + " Log Initalized on: " + LogBook.getTimeStamp());
		
		initialized = true;
	}
	
	public void reInitilize()
	{
		file.delete();
		try 
		{
			fstream = new FileWriter(file);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		out = new BufferedWriter(fstream);
		
		writeToFile(type.toString() + " Log Initalized on: " + LogBook.getTimeStamp());
		
		initialized = true;
	}
	
	public boolean writeToLog(String entry)
	{
		return createLogEntry(entry);
	}
	
	public boolean writeToLog(String entry, boolean forceTimeStamp)
	{
		return createLogEntry(entry, forceTimeStamp);
	}
	
	public boolean createLogEntry(String entry, boolean forceTimeStamp)
	{
		if(SettingSRC.generateLogFiles == false)
			return false;
		
		String output = "";
		if(SettingSRC.displayTimeStampInLogs == true || forceTimeStamp)
		{
			output += "\n---------------------------\n";
			output += "Entry Time:\t" + LogBook.getTimeStamp() + "\n";
			output += "Time Program has been running: " + (System.currentTimeMillis() - LogBook.getProgramStartTime()) +"ms\n\n";
		}
		output += entry;
		
		if(type.checkSettings(this) == true)
		{
			if(SettingSRC.consoleDisplay_timeStamp == true && SettingSRC.displayTimeStampInLogs == true)
				System.out.print(output);
			else
				System.out.print("\n" + entry);
		}
		
		return writeToFile(output);
	}
	
	public boolean createLogEntry(String entry)
	{
		return createLogEntry(entry, false);
	}
	
	public logType getType()
	{
		return type;
	}
	
	private boolean writeToFile(String entry)
	{
		boolean result = false;
		try 
	    {
			out.write(entry + "\n");
			if(SettingSRC.consoleDisplay_FileManipulation_File == true)
				System.out.println("Writting: " + entry);
			out.flush();
	    } 
		catch (IOException e) 
	    {
			
	    }
		
		return result;
	}
	
	public void closeLog()
	{
		initialized = false;
		
		try 
		{
			out.close();
			out.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
}
