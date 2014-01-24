import java.util.ArrayList;

public class Statistics 
{
	// Statistics regarding how much time is spent on each object
	public enum Operation
	{
		fileCompAlgorithmsTime,
		folderCompTime,
		setUpTime,
		logWriteTime,
		HTMLWriteTime
	}
	
	private long[] fileCompAlgorithmTimes;
	private String[] fileCompAlgorithmNames;
	private long folderCompTime;
	private long setUpTime;
	private long logWriteTime;
	private long HTMLWriteTime;
	
	// Statistics regarding how much time is spent during the FolderComparision Object
	public enum FolderComparision
	{
		fileASize,
		numFileA,
		numFolA,
		fileBSize,
		numFileB,
		numFolB,
	}
	
	private int sizeOfFilesInA;
	private int numOfFilesinA;
	private int numOfFoldersinA;
	private int sizeOfFilesInB;
	private int numOfFilesInB;
	private int numOfFoldersInB;
	
	private int FolderComparisionObjectsCreated;
	
	public void addTimeStamp(Statistics.FolderComparision type)
	{
		
	}
	
	public void increment(Statistics.FolderComparision type)
	{
		
	}
	
	// Statistics regarding how much time is spent during the FileComparision Object 
	public enum FileComparision
	{
		stringsAnalyzed
	}
	
	private long StringsAnalyzed;
	private int FileComparisionObjectsStarted; 
	private int FileComparisionObjectsCreated;
	
	private long BToABetterCount;	// Tells us how many times a B -> A comp was successful
	private long invertBetterCount;	// Tells us how many times an inversion was successful
	
	private long[] StringAlgorithmTimes;
	private long averageFileComparisionTime;
	private long longestFileComparisionTime;
	private long shortestFileComparisionTime;
	
	// Statistics regarding the statistics 
	private long StatisticsOverheadTime;
	
	private void calculateStatisticOverheadTime()
	{
		
	}
	
	private ArrayList<Long> startTimeStamps;
	
	private Statistics(int numOfFileCompAlgorithms)
	{
		fileCompAlgorithmTimes = new long[numOfFileCompAlgorithms];
		folderCompTime = 0;
		setUpTime = 0;
		logWriteTime = 0;
		HTMLWriteTime = 0;
		StringsAnalyzed = 0;
	}
	
	public String generateTimeReport()
	{
		String output = "";
		return output;
	}
	
	public String generateFileCompReport()
	{
		String output = "";
		return output;
	}
	
	private ArrayList<Double> misMatchList = new ArrayList<Double>();
	
	private double generateMisMatchAverage()
	{
		double result = 0;
		
		for(int i = 0; i < misMatchList.size(); i++)
		{
			result += misMatchList.get(i);
		}
		result = result / misMatchList.size();
		
		return result;
	}
}
