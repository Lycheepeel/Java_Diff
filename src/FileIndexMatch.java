public class FileIndexMatch 
{
	private int correspondingIndex;
	private int score;
	private boolean hasMatch;
	
	public FileIndexMatch(int correspondingIndex, int score)
	{
		this.correspondingIndex = correspondingIndex;
		this.score = score;
		
		if(correspondingIndex < 0)
			hasMatch = false;
		else
			hasMatch = true;
	}
	
	public void setIndex(int index)
	{
		this.correspondingIndex = index;
	}
	
	public void setScore(int score)
	{
		this.score = score;
	}
	
	public int getCorrespondingIndex()
	{
		return correspondingIndex;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public boolean hasMatch()
	{
		return hasMatch;
	}
}
