import java.io.File;
import java.util.ArrayList;


public class Tester2 
{
	private File directoryA;
	private File directoryB;
	
	private ArrayList<String> containedInBoth;
	private ArrayList<String> containedUniquelyInA;
	private ArrayList<String> containedUniquelyInB;
	
	public static void main (String[] args)
	{
		File fileA = new File(args[0]);
		File fileB = new File(args[1]);
		
		FolderComparer comp = new FolderComparer(fileA, fileB);
		comp.start();
	}
}
