import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import Utilities.Search;
import Utilities.QuickSort.*;

public class FolderComparer 
{
	private File directoryA;
	private File directoryB;
	
	private ArrayList<String> containedInBoth;
	private ArrayList<String> containedUniquelyInA;
	private ArrayList<String> containedUniquelyInB;
	
	private boolean extensionLimit;
	private ArrayList<String> extensionList;
	
//	public static void main (String[] args)
//	{
//		File fileA = new File(args[0]);
//		File fileB = new File(args[1]);
//		
//		FolderComparer comp = new FolderComparer(fileA, fileB);
//		comp.start();
//	}
	
	public FolderComparer(File directoryA, File directoryB)
	{
		this.directoryA = directoryA;
		this.directoryB = directoryB;
		
		containedInBoth = new ArrayList<String>();
		containedUniquelyInA = new ArrayList<String>();
		containedUniquelyInB = new ArrayList<String>();
		
		String objectCreation = "" +
				"******************************************" +
				"\nFolder Comparer Object Created between the folders:\n" + directoryA + "\n" + directoryB +
				"\n******************************************";
		
		this.extensionLimit = false;
		this.extensionList = null;
		
		writeToLog(objectCreation, true);
	}
	
	public FolderComparer(File directoryA, File directoryB, ArrayList<String> extensionList)
	{
		this.directoryA = directoryA;
		this.directoryB = directoryB;
		
		containedInBoth = new ArrayList<String>();
		containedUniquelyInA = new ArrayList<String>();
		containedUniquelyInB = new ArrayList<String>();
		
		String objectCreation = "" +
				"******************************************" +
				"\nFolder Comparer Object Created between the folders:\n" + directoryA + "\n" + directoryB +
				"\n******************************************";
		
		if(extensionList != null)
		{
			this.extensionLimit = true;
			this.extensionList = extensionList;
		}
		else
		{
			this.extensionLimit = false;
		}
		
		writeToLog(objectCreation, true);
	}
	
	public void start()
	{
		if(checkRequirements() == false)
		{
			System.exit(0);
		}
		
		generateExistenceReport();
		
//		//System.out.println("Files Contained Uniquely in A:");
//		for(int i = 0; i < containedUniquelyInA.size(); i++)
//		{
//			//System.out.println("\t" + containedUniquelyInA.get(i));
//		}
//		
//		//System.out.println("Files Contained Uniquely in B:");
//		for(int i = 0; i < containedUniquelyInA.size(); i++)
//		{
//			//System.out.println("\t" + containedUniquelyInB.get(i));
//		}
//		
//		//System.out.println("Files Contained in Both:");
//		for(int i = 0; i < containedInBoth.size(); i++)
//		{
//			//System.out.println("\t" + containedInBoth.get(i));
//		}
	}
	
	private boolean checkRequirements()
	{
		writeToLog("Checking Requirements:\n" + directoryA + "\n" + directoryB, false);
		boolean result = true;
		if(directoryA.exists() == false)
		{
			writeToLog(directoryA.toString() + " does not exist", false);
			generateTerminationReport(directoryA.toString() + " does not exist");
			result = false;
		}
		else if(directoryA.isDirectory() == false)
		{
			writeToLog(directoryA.toString() + " does not exist", false);
			generateTerminationReport(directoryA.toString() + " is Not a Directory");
			result = false;
		}
		if(directoryB.exists() == false)
		{
			writeToLog(directoryB.toString() + " does not exist", false);
			generateTerminationReport(directoryB.toString() + " does not exist");
			result = false;
		}
		else if(directoryB.isDirectory() == false)
		{
			writeToLog(directoryB.toString() + " does not exist", false);
			generateTerminationReport(directoryB.toString() + " is Not a Directory");
			result = false;
		}
		
		writeToLog("Initilization Complete, all resources accounted for", false);
		return result;
	}
	
	private void generateExistenceReport()
	{
		File[] AFiles = directoryA.listFiles();
		File[] BFiles = directoryB.listFiles();
	
		writeToLog("Obtaining an array containg the lsit of all the file names in directory", true);
		
		String[] AFileNames = createNameArrayForDirectory(AFiles);
		String[] BFileNames = createNameArrayForDirectory(BFiles);
		
		writeToLog("\nListing Files in " + directoryA.toString());
		for(int i = 0 ; i < AFileNames.length; i++)
		{
			writeToLog("\t" + AFileNames[i]);
		}
		
		writeToLog("\nListing Files in " + directoryB.toString());
		for(int i = 0 ; i < BFileNames.length; i++)
		{
			writeToLog("\t" + BFileNames[i]);
		}
		
		writeToLog(" Sorting Arrays, to perform binary search", true);
		
		containedUniquelyInA = moveStringArrayIntoList(AFileNames);
		containedUniquelyInB = moveStringArrayIntoList(BFileNames);
		
		if(extensionLimit == true)
		{
			int index = 0;
			while(index < containedUniquelyInA.size())
			{
				String[] split = containedUniquelyInA.get(index).split("\\.");
				boolean removed = false;
				for(int i = 0; i < extensionList.size(); i++)
				{
					if(split[split.length - 1].equals(extensionList.get(i)))
					{
						removed = true;
						break;
					}
				}
				
				if(removed == true)
					containedUniquelyInA.remove(index);
				else
					index++;
			}
			
			index = 0;
			
			while(index < containedUniquelyInB.size())
			{
				String[] split = containedUniquelyInB.get(index).split("\\.");
				boolean removed = false;
				for(int i = 0; i < extensionList.size(); i++)
				{
					if(split[split.length - 1].equals(extensionList.get(i)))
					{
						removed = true;
						break;
					}
				}
				
				if(removed == true)
					containedUniquelyInB.remove(index);
				else
					index++;
			}
		}

		Collections.sort(containedUniquelyInA);
		Collections.sort(containedUniquelyInB);
		
		writeToLog("\n Sorting Complete at" + LogBook.getTimeStamp() +"\n Now Performing a Binary Search for files in A, within B", false);
		
		int index = 0;
		while(index < containedUniquelyInA.size())
		{
			int searchResult = Collections.binarySearch(containedUniquelyInB, containedUniquelyInA.get(index));
			if(searchResult >= 0)
			{
				writeToLog("\t" + containedUniquelyInA.get(index) + " ---> exists in both folders", false);
				containedInBoth.add(containedUniquelyInA.get(index));
				containedUniquelyInA.remove(index);
				containedUniquelyInB.remove(searchResult);
			}
			else
			{
				index++;
			}
		}
		
		writeToLog("Binary Search for identical pairs complete", true);
	}
	
	public ArrayList<String> getAOnlyList()
	{
		return containedUniquelyInA;
	}
	
	public ArrayList<String> getBOnlyList()
	{
		return containedUniquelyInB;
	}
	
	public ArrayList<String> getContainedInBothList()
	{
		return containedInBoth;
	}
	
	private String[] createNameArrayForDirectory(File[] directoryList)
	{
		String[] result = new String[directoryList.length];
		for(int i = 0; i < result.length; i++)
		{
			result[i] = directoryList[i].getName();
		}
		return result;
	}
	
	private ArrayList<String> moveStringArrayIntoList(String[] array)
	{
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i < array.length; i++)
		{
			result.add(array[i]);
		}
		return result;
	}
	
	private void writeToLog(String entry)
	{
		writeToLog(entry, false);
	}
	
	private void writeToLog(String entry, boolean forceTimeStamp)
	{
		LogBook.getLog(Log.logType.Folder_Comparision).writeToLog(entry, forceTimeStamp);
	}
	
	private void generateTerminationReport(String rationale)
	{
		ExecutableMain.terminateProgram("Folder Comparision Initilization failed between\n\t" + directoryA + "\n\t" + directoryB +"\nBecause: " + rationale);
	}
}
