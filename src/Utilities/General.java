package Utilities;

public class General 
{
	public static String scrubString(String input, Settings.FileComparer.StringMatch settings)
	{
		if(settings.scrubString == false)
			return input;
		
		String output = input;
		
		if(settings.ignoreComments == true)
		{
			if(settings.ignoreSingleLineComments == true)
			{
				if(output.contains(settings.commentLineIndicator))
				{
					output = output.substring(0, output.indexOf(settings.commentLineIndicator) + settings.commentLineIndicator.length());
				}
			}
			if(settings.ignoreMultiLineComments == true)
			{
				if(output.contains(settings.multiLineCommentStartIndicator))
				{
					output = output.substring(0, output.indexOf(settings.commentLineIndicator) + settings.commentLineIndicator.length());
				}
			}
		}
		
		if(settings.ignoreNumbers == true)
			output = output.replaceAll("\\d", "");
		
		if(settings.ignoreWhiteSpace == true)
			output = output.replaceAll("\\s", "");
		
		return output;
	}
	
	public static String removeHTMLLiterals(String input)
	{
		String result = input.replaceAll("<", "&lt;");
		result = result.replaceAll(">", "&gt;");
		result = result.replaceAll("/", "&#47;");
		result = result.replaceAll("\n", "<br>");
		//x = x.replaceAll("[", "&#93");
		result = result.replaceAll("]", "&#91;");
		result = result.replaceAll("\"", "&#34;");
		result = result.replaceAll("\'", "&#39;");
		return result;
	}
	
	public static String formatMilSec(long input)
	{
		String output = "";
		int seconds = (int) (input/((long) 1000));
		int milSec = (int) (input % (long) 1000);
		
		int minutes = seconds/60;
		seconds = seconds%60;
		
		int hours = minutes/60;
		minutes = minutes%60;
		
		String secondString;
		String minuteString;
		String hourString;
		
		if(seconds < 10)
			secondString = "0" + seconds;
		else
			secondString = seconds + "";
		
		if(minutes < 10)
			minuteString = "0" + minutes;
		else
			minuteString = minutes + "";
		
		if(hours < 10)
			hourString = "0" + hours;
		else
			hourString = hours + "";
		
		if(seconds == 0)
		{
			return "00:00:00:" + input;
		}
		else
		{
			if(minutes == 0)
			{
				return "00:00:" + secondString + ":" + milSec + "";
			}
			else
			{
				if(hours == 0)
				{
					return "00:" + minuteString + ":" + secondString + ":" + milSec;
				}
				else
				{
					return hourString + ":" + minuteString + ":" + secondString + ":" + milSec;
				}
			}
		}
	}
}
