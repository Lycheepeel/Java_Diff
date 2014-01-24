import java.text.SimpleDateFormat;
import java.util.Date;

import Utilities.GlobalVariables;

public class RunTestCases 
{
	public static void main (String[] args)
	{
		String executableDirectory = System.getProperty("user.dir");
		runSubFolderCompTestCase(executableDirectory);
	}
	
	private static void runSubFolderCompTestCase(String execDir)
	{
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat ("dd MM yyyy");
		String executionDate = dateFormat.format(date);
		
		String left = execDir + "\\Test Cases\\SubFolder Comparision\\Version 1";
		String right = execDir + "\\Test Cases\\SubFolder Comparision\\Version 2";
		String output = execDir + "\\Test Cases\\SubFolder Comparision\\Results";
		String modifier = "-name";
		String name = "SubFolder Comparision Test Case - " + executionDate; 
		
		String[] args = {left, right, output, modifier, name};
		
		ExecutableMain test = new ExecutableMain(args, null);
		ExecutableMain.fileCompSettingsList.add(Settings.FileComparer.generateDefault());
		test.runProgram();
	}
}
