import java.io.File;
import java.util.ArrayList;

public class FileComparissionItterator 
{
	public enum ItterationProcess
	{
		complete,
		thorough,
		ideal,
		quick
	}
	
	public enum Itteratee
	{
		minConfidence,
		lineDecFactor,
		containsMinNumChar,
	}
	
	private FileComparer reference;
	private Settings.FileComparer settings;
	
	private String[][] bestSuccessSettingsString;
	private FileComparer bestFileComparer;
	
	private File fileA;
	private File fileB;
	
	private ItterationProcess itterationProcess;
	private int dimension;
	
	private ExecutableMain parent;
	
	public FileComparissionItterator()
	{
		
	}
	
	public void itterateThroughSettings()
	{
		
	}
	
	private void quickItteration()
	{
		int startingPoint;
		Itteratee itteratee = null;
		// This loop will perform the 
		for(int i = 0; i < dimension; i++)
		{
			// The pre loop exit criteria
			switch(itteratee)
			{
				case minConfidence:
					break;
				case lineDecFactor:
					break;
				case containsMinNumChar:
					break;
				default:
			}
			if(true)
			{
				
			}
		}
	}
	
	private FileComparer runWithNewSettings()
	{
		FileComparer fileComp = new FileComparer(fileA, fileB, settings, parent);
		fileComp.start(settings);
		return fileComp;
	}
	
	private boolean itterateMinConfidence()
	{
		if((settings.minConfidence <= settings.getOriginalMinConfidence() + settings.minConfidenceStep * settings.maxMinConfidenceStep))
			settings.minConfidence += settings.minConfidenceStep;
		else
			return false;
		return true;
	}
	
	private boolean itterateDecFactor()
	{
		if(settings.lineDecrementFactor >= settings.getOriginalLineDecFactor() - settings.lineDecrementFactorStep * settings.maxLineDecrementSteps)
			settings.lineDecrementFactor -= settings.lineDecrementFactorStep;
		else
			return false;
		return true;
	}
	
	private boolean itterateMinContainChar()
	{
		return false;
	}
}

