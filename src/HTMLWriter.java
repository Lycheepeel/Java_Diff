import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import Utilities.GlobalVariables;

public class HTMLWriter 
{	
	public enum HTMLFileType
	{
		differenceFile,
		identicalFile,
		MasterDocument,
		CommentFile,
		copyFile,
		;
		
		public boolean getRequiredFile(HTMLFileType fileType)
		{
			switch(fileType)
			{
				case differenceFile:
					break;
				case identicalFile:
					break;
				case MasterDocument:
					break;
				case CommentFile:
					break;
				default:
					return false;
			}
			return false;
		}
	}
	
	private FileWriter fstream;
	private BufferedWriter out;
	private String destinationPath;
	private File reference;
	private HTMLFileType fileType;
	private String pathToMain;
	
	private String fileName;
	
	private Scanner scanner;
	
	private ExecutableMain parent;
	
	public HTMLWriter(String destinationPath, HTMLFileType fileType, String currentDirectory, String pathToMain, ExecutableMain parent)
	{
		this.parent = parent;
		this.pathToMain = pathToMain;
		this.destinationPath = destinationPath;
		this.fileType = fileType;
		
		if(fileType != null && fileType != HTMLFileType.copyFile)
			copyPaste(new File(currentDirectory + "\\res\\HTML\\global.css"));
		
		switch(fileType)
		{
			case differenceFile:
				reference = new File(currentDirectory + "\\res\\HTML\\difference_file_template.html");
				copyPaste(new File(currentDirectory + "\\res\\HTML\\difference_file.css"));
				break;
			case identicalFile:
				reference = new File(currentDirectory + "\\res\\HTML\\file_report_template.html");
				copyPaste(new File(currentDirectory + "\\res\\HTML\\file_report.css"));
				break;
			case MasterDocument:
				reference = new File(currentDirectory + "\\res\\HTML\\master_report_template.html");
				copyPaste(new File(currentDirectory + "\\res\\HTML\\master_report.css"));
				break;
			case CommentFile:
				reference = new File(currentDirectory + "\\res\\HTML\\comment_template.html");
				copyPaste(new File(currentDirectory + "\\res\\HTML\\comment.css"));
				break;
			default:
				break;
		}
	}
	
	public void close()
	{
		try {
			out.flush();
			out.close();
			fstream.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void initilize(String fileName)
	{
		initilize (fileName, true);
	}
	
	public void initilize(String fileName, boolean isHTMLFile)
	{
		writeToLog("Reseting the HTMLWriter for: " + fileType.toString() + "\n\tConfigured to now write to: " + fileName + "\n\tPath: " + destinationPath, true);
		if(this.destinationPath.endsWith("\\") == false)
		{
			this.destinationPath += "\\";
		}
		if(fileName.endsWith(".html") == false && isHTMLFile == true)
		{
			fileName += ".html";
		}
		
		String output;
		try 
		{
			fstream = new FileWriter(this.destinationPath +  fileName);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		out = new BufferedWriter(fstream);
		
		this.fileName = fileName;
	}
	
	public void copyPaste (File referencePath)
	{
		initilize(referencePath.getName(), false);
		try 
		{
			scanner = new Scanner(referencePath);
		} 
		catch (FileNotFoundException e) 
		{
			LogBook.logFailure("Could not find source file for copy and paste for " + referencePath);
		}
		
		while(scanner.hasNext() == true)
		{
			writeFile(scanner.nextLine());
		}
		close();
	}
		
	public void generateHTMLFileReport(String fileSource)
	{
		File source = new File(fileSource);
		initilize(source.getName());
		Scanner fileReadScanner = null;
		try 
		{
			scanner = new Scanner(this.reference);
			fileReadScanner = new Scanner(new File(fileSource));
		} 
		catch (FileNotFoundException e) 
		{
			LogBook.logFailure("Could not find reference files");
		}
		
		String nextLine = "";
		while(nextLine.contains("</html>") == false)
		{
			if(scanner.hasNext() == false)
			{
				ExecutableMain.reportAFailure("Couldn't generate Entirety of the MasterHTMLDocument Start ");
				break;
			}
			nextLine = scanner.nextLine();
			if(nextLine.contains("<head>"))
			{
				writeFile(nextLine);
				generateHTMLHeader();
			}
			else if(nextLine.contains("topbar") && nextLine.contains("div"))
			{
				writeFile(nextLine);
				generateTopBar(source.getName());
			}
			else if(nextLine.contains("<tbody>"))
			{
				writeFile(nextLine);
				while(nextLine.contains("</tbody>") == false)
					nextLine = scanner.nextLine();
				
				int counter = 1;
				if(new File(fileSource).exists())
				{
					while(fileReadScanner.hasNext())
					{
						String tr = "<tr";
						tr += " onmouseover=\"this.style.backgroundColor='#8888FF'; " +
								"updateD(" + counter + ",'TEXT GOES HERE', false)\"";
						tr += "onmouseout=\"this.style.backgroundColor=''\"";
						tr += "onclick=\"updateD(" + counter + ",'TEXT GOES HERE', true)\"";
						tr += ">";
						writeFile(tr + "<td>" + counter + "</td><td>" + fileReadScanner.nextLine() + "</td></tr>");
						counter++;
					}
				}
				else
				{
					
					writeToLog("We have a problem, a file: " + fileSource + " doesn't exist, could not write");
					ExecutableMain.reportAFailure(("We have a problem, a file: " + fileSource + " doesn't exist, could not write to file"));
				}
				
				writeFile(nextLine);
			}
			else if(nextLine.contains("sidebar") && nextLine.contains("id"))
			{
				writeFile(nextLine);
				generateSideBar();
			}
			else
				writeFile(nextLine);
		}
		fileReadScanner.close();
	}
	
	public void generateDifferenceReport(String fileName, FileComparer fileComp, ArrayList<String[][]> sidebarBottomData)
	{
		initilize(fileName);
		try 
		{
			scanner = new Scanner(this.reference);
		} 
		catch (FileNotFoundException e) 
		{
			LogBook.logFailure("Could not write Master File");
		}
		
		String nextLine = "";
		while(nextLine.contains("</html>") == false)
		{
			if(scanner.hasNext() == false)
			{
				ExecutableMain.reportAFailure("Couldn't generate The Difference Report for:" + fileName);
				break;
			}
			nextLine = scanner.nextLine();
			
			if(nextLine.contains("<head>"))
			{
				writeFile(nextLine);
				generateHTMLHeader();
			}
			else if(nextLine.contains("topbar") && nextLine.contains("div"))
			{
				writeFile(nextLine);
				generateTopBar(fileName);
			}
			else if(nextLine.contains("<thead>"))
			{
				writeFile(nextLine);
				generateTableHeader();
			}
			else if(nextLine.contains("<tbody>"))
			{
				writeFile(nextLine);
				while(nextLine.contains("</tbody>") == false)
				{
					nextLine = scanner.nextLine();
				}
				generateDifferenceReportTable(fileComp.getOutput());
				writeFile(nextLine);
			}
			else if(nextLine.contains("sidebar") && nextLine.contains("id"))
			{
				writeFile(nextLine);
				generateSideBar(sidebarBottomData);
			}
			else
				writeFile(nextLine);
		}
	}
	
	private void generateDifferenceReportTable(String[][] fileLines)
	{
		for(int i = 0; i < fileLines.length; i++)
		{
			/*
			 * output[0] = fileAText formatted for HTML 
			 * output[1] = fileALineIndex
			 * output[2] = fileBText formated for HTML
			 * output[3] = fileBLineIndex
			 * output[4] = HTML Line Number (in NATURAL format, therefore Line #1 not Line #0, corresponds to Line #1 in the code
			 * output[5] = HTML Line Formatting
			 * output[6] = Match Score
			 */
			String tr = "<tr ";
			switch(Integer.parseInt(fileLines[i][5]))
			{
				case -2:	// Singular left
					tr += "class=leftSingular";
					break;
				case -1:	// Singular right
					tr += "class=rightSingular";
					break;
				case 0:		// All good
					break;
				case 1:		// level 1 uncertainty
					tr += "class=levelOne";
					break;
				case 2:		// level 2 uncertainty
					tr += "class=levelTwo";
					break;
				case 3:		// level 3 uncertainty
					tr += "class=levelThree";
					break;
				case 4:		// level 4 uncertainty
					tr += "class=levelFour";
					break;
				case 5:		// level 5 uncertainty
					tr += "class=levelFive";
					break;
				default:
					break;
			}
			tr += " onmouseover=\"this.style.backgroundColor='#8888FF'; " +
					"updateD(" + fileLines[i][4] + ",'TEXT GOES HERE', false)\"";
			tr += "onmouseout=\"this.style.backgroundColor=''\"";
			tr += "onclick=\"updateD(" + fileLines[i][4] + ",'TEXT GOES HERE', true)\"";
			tr += ">";
			
			writeFile(tr, "tr");
			for(int j = 0; j < 6; j++)
			{
				String td = "<td>";
				switch(j)
				{
					case 0:
						td+= fileLines[i][4];
						break;
					case 1:
						td+= fileLines[i][1];
						break;
					case 2:
						td+= fileLines[i][0];
						break;
					case 3:
						td+= fileLines[i][3];
						break;
					case 4:
						td+= fileLines[i][2];
						break;
					case 5:
						td+= fileLines[i][6];
						break;
				}
				td += "</td>";
				writeFile(td, "td");
			}
			writeFile("</tr>");
		}
	}
	
	public void generateMasterHTMLDocumentStart(String fileName)
	{
		initilize(fileName);
		try 
		{
			scanner = new Scanner(this.reference);
		} 
		catch (FileNotFoundException e) 
		{
			LogBook.logFailure("Could not write Master File");
		}
		
		String nextLine = "";
		while(nextLine.contains("<tbody>") == false)
		{
			if(scanner.hasNext() == false)
			{
				ExecutableMain.reportAFailure("Couldn't generate Entirety of the MasterHTMLDocument Start ");
				break;
			}
			nextLine = scanner.nextLine();
			if(nextLine.contains("<head>"))
			{
				writeFile(nextLine);
				generateHTMLHeader();
			}
			else if(nextLine.contains("topbar") && nextLine.contains("div"))
			{
				writeFile(nextLine);
				generateTopBar(fileName);
			}
			else if(nextLine.contains("<thead>"))
			{
				writeFile(nextLine);
				generateTableHeader();
			}
			else
				writeFile(nextLine);
		}
		
		while(nextLine.contains("</tbody>") == false)
			nextLine = scanner.nextLine();
	}
	
	public void generateMasterHTMLDocumentTableRow(String[] line, int rowColour)
	{
		/*
		 * [0] relativePath
		 */
		String output = "<tr";
		if (rowColour != 0)
		{
			switch(rowColour)
			{
				case 1: 
					output += " class=levelOne";
					break;
				case 2: 
					output += " class=levelTwo";
					break;
			}
		}
		
		String name =  new File(line[0]).getName();
		
		output += " onmouseover=\"this.style.backgroundColor='#8888FF'; " +
				"updateD('" + name + "','TEXT GOES HERE', false)\"";
		output += " onmouseout=\"this.style.backgroundColor=''\"";
		output += "onclick=\"updateD('" + name + "','TEXT GOES HERE', true)\"";
		output += ">";
		
		writeFile(output);
		for(int i = 0; i < line.length; i++)
		{
			output = "<td>";
			if(i == 0)
			{
				output += "<a href=\"";
				output += pathToMain + line[0];
				output += "\">";
				output += name;
				output += "</a>";
			}
			else
			{
				output += line[i];
			}
			output += "</td>";
			writeFile(output);
		}
		writeFile("</tr>");
	}
	
	public void generateMasterHTMLDocumentFinishReport(ArrayList<String[][]> data)
	{
		writeFile("</tbody>");
		String nextLine = "";
		while(nextLine.contains("</html>") == false)
		{
			if(scanner.hasNext() == false)
			{
				ExecutableMain.reportAFailure("Couldn't generate the End of the Master HTML Document for: " + fileName);
				break;
			}
			nextLine = scanner.nextLine();
			
			if(nextLine.contains("sidebar") && nextLine.contains("id"))
			{
				writeFile(nextLine);
				generateSideBar(data);
			}
			else
				writeFile(nextLine);	
		}
	}
	
	public void generateCommentFile(String destination)
	{
		initilize(destination);
		try 
		{
			scanner = new Scanner(this.reference);
		} 
		catch (FileNotFoundException e) 
		{
			LogBook.logFailure("Could not write Master File");
		}
		
		String nextLine = "";
		while(nextLine.contains("</html>") == false)
		{
			if(scanner.hasNext() == false)
			{
				ExecutableMain.reportAFailure("Couldn't generate Entirety of the Comment File ");
				break;
			}
			nextLine = scanner.nextLine();
			if(nextLine.contains("<head>"))
			{
				writeFile(nextLine);
				generateHTMLHeader();
			}
			else if(nextLine.contains("topbar") && nextLine.contains("div"))
			{
				writeFile(nextLine);
				generateTopBar(destination);
			}
			else if(nextLine.contains("<tbody>"))
			{
				writeFile(nextLine);
				while(nextLine.contains("</tbody>") == false)
					nextLine = scanner.nextLine();
				
				writeFile(nextLine);
			}
			else if(nextLine.contains("sidebar") && nextLine.contains("id"))
			{
				writeFile(nextLine);
				generateSideBar();
			}
			else
				writeFile(nextLine);
		}
	}
	
	public void editCommentFile(String targetName, String mainPath, boolean editComment, String[] data)
	{
		String reference = mainPath + "\\tmp\\" + targetName;
		String destination = mainPath + "\\comments\\comments-" + targetName;
		initilize(destination);
		try 
		{
			scanner = new Scanner(new File(reference));
		} 
		catch (FileNotFoundException e) 
		{
			ExecutableMain.reportAFailure("Comment File - " + targetName, "Has reported a failure, as it could not find the temp file in tmp");
			return;
		}
		
		if(editComment == true)
		{
			String nextLine = "";
			while(nextLine.contains("form name = \"Form-Description\" ") == false)
			{
				if(scanner.hasNext() == false)
				{
					ExecutableMain.reportAFailure("Couldn't Edit the Comment File's Description ");
					return;
				}
				nextLine = scanner.nextLine();
				writeFile(nextLine);
			}
			String textArea = "<textarea name=\"description\" placeholder = \"Insert Script Description\" id=\"description\">";
			if(data != null)
			{
				writeFile(textArea);
				writeFile(data[0]);
				writeFile("</textarea>");
			}
			else
			{
				textArea += "</textarea>";
				writeFile(textArea);
			}
			while(scanner.hasNext() == true)
				writeFile(scanner.nextLine());
		}
		else
		{
			String nextLine = "";
			while(nextLine.contains("<tbody>") == false)
			{
				if(scanner.hasNext() == false)
				{
					ExecutableMain.reportAFailure("Couldn't Edit the Comment File, line comment");
					return;
				}
				nextLine = scanner.nextLine();
				writeFile(nextLine);
			}
			while(nextLine.contains("</tbody>") == false)
			{
				if(scanner.hasNext() == false)
				{
					ExecutableMain.reportAFailure("Couldn't Edit the Comment File, line comment");
					return;
				}
				nextLine = scanner.nextLine();
				if(nextLine.contains("<tr>") == true)
				{
					if(scanner.hasNext() == false)
					{
						ExecutableMain.reportAFailure("Couldn't Edit the Comment File, line comment");
						return;
					}
					nextLine = scanner.nextLine();
					String tmp = new String(nextLine);
					tmp = tmp.replaceAll("\\s", "");
					tmp = tmp.replaceAll("<td>", "");
					tmp = tmp.replaceAll("</td>", "");
					if(Integer.parseInt(tmp) == Integer.parseInt(data[0]))
					{
						//TODO: It also needs to modify the old original, to remove the # and also the action bar... le *sigh* 
						break;
					}
					else if(Integer.parseInt(tmp) > Integer.parseInt(data[0]))
					{
						//TODO: DO SOMETHING
						writeFile(nextLine);
						break;
					}
					else
						writeFile(nextLine);
				}
			}
			writeFile(nextLine);
			while(scanner.hasNext() == true)
				writeFile(scanner.nextLine());
		}
	}
	
	private void generateTableHeader()
	{
		String nextLine = "";
		while(nextLine.contains("</thead>") == false)
		{
			if(scanner.hasNext() == false)
			{
				ExecutableMain.reportAFailure("Couldn't generate a Table Header for: ");
				break;
			}
			nextLine = scanner.nextLine();
			
			if(nextLine.contains("MainInfo") && nextLine.contains("span") && nextLine.contains("class"))
			{
				
			}
			else
				writeFile(nextLine);	
		}
	}
	
	private void generateHTMLHeader()
	{
		String nextLine = "";
		while(nextLine.contains("</head>") == false)
		{
			if(scanner.hasNext() == false)
			{
				ExecutableMain.reportAFailure("Couldn't generate a HTML Header for: ");
				break;
			}
			nextLine = scanner.nextLine();
			writeFile(nextLine);
		}
	}
	
	private void generateTopBar(String fileName)
	{
		String nextLine = "";
		while(nextLine.contains("</div>") == false)
		{
			if(scanner.hasNext() == false)
			{
				ExecutableMain.reportAFailure("Couldn't generate a TopBar Element for: ");
				break;
			}
			nextLine = scanner.nextLine();
			
			if(nextLine.contains("MainInfo") && nextLine.contains("span") && nextLine.contains("class"))
			{
				writeFile(nextLine);
				scanner.nextLine();
				writeFile(fileName);
			}
			else if(nextLine.contains("rightInfo") && nextLine.contains("span") && nextLine.contains("class"))
			{
				writeFile(nextLine);
				nextLine = scanner.nextLine();
				nextLine = nextLine.replaceAll("INSERT", GlobalVariables.leftFolderName);
				writeFile(nextLine);
				nextLine = scanner.nextLine();
				nextLine = nextLine.replaceAll("INSERT", GlobalVariables.rightFolderName);
				writeFile(nextLine);
				nextLine = scanner.nextLine();
				nextLine = nextLine.replaceAll("INSERT", GlobalVariables.executionDate);
				writeFile(nextLine);
			}
			else
				writeFile(nextLine);
		}
	}
	
	private void generateSideBar()
	{
		generateSideBar(null);
	}
	
	private void generateSideBar(ArrayList<String[][]> sidebarBottomData)
	{
		String nextLine = "";
		while(nextLine.contains("</div>") == false || nextLine.contains("sidebar") == false)
		{
			if(scanner.hasNext() == false)
			{
				ExecutableMain.reportAFailure("Couldn't generate a Sidebar Element for: " + fileName);
				break;
			}
			nextLine = scanner.nextLine();
			
			if(nextLine.contains("top") && nextLine.contains("div"))
			{
				writeFile(nextLine);
				generateSideBarTop();
			}
			else if(nextLine.contains("bottom") && nextLine.contains("div"))
			{
				writeFile(nextLine);
				generateSideBarBottom(sidebarBottomData);
			}
			else
				writeFile(nextLine);
		}
	}
	
	private void generateSideBarTop()
	{
		LogBook.writeToDebug("\nDEBUG FOR:" + fileName +"\n\t---" + fileType.toString(), true);
		/*
		 * SidebarTopData:
		 * 0 = Main Report name
		 */
		String nextLine = "";
		while(nextLine.contains("</div>") == false)
		{
			if(scanner.hasNext() == false)
			{
				ExecutableMain.reportAFailure("Couldn't generate a sidebar Top Element for: " + fileName);
				break;
			}
			nextLine = scanner.nextLine();
			
			if(nextLine.contains("MainLink") && nextLine.contains("Main Page") && nextLine.contains("<a>"))
			{
				String replacement = "<a href=\"" + pathToMain + parent.getReportName() + ".html\">";
				nextLine = nextLine.replace("<a>", replacement);
				writeFile(nextLine);
			}
			else if(nextLine.contains("table") && nextLine.contains("dropdownMenu"))
			{
				writeFile(nextLine);
				
				while(nextLine.contains("</table>") == false)
				{
					if(scanner.hasNext() == false)
					{
						ExecutableMain.reportAFailure("Couldn't generate the Sublinks in the SideBar for: " + fileName);
						break;
					}
					
					nextLine = scanner.nextLine();
					
					if(nextLine.contains("div") && nextLine.contains("sidebarDropDownMenu") && nextLine.contains("<!--") && nextLine.contains("-->"))
					{
						LogBook.writeToDebug("DropDown List Container found in line", true);
						if(nextLine.contains("Main Links"))
						{
							LogBook.writeToDebug("\tdd Main Links", true);
							writeFile(nextLine);
							while(nextLine.contains("</ul>") == false)
							{
								String temp = pathToMain + "subReports/" + parent.getReportName();
								if(scanner.hasNext() == false)
								{
									ExecutableMain.reportAFailure("Couldn't generate the Sublinks in the SideBar for: " + fileName);
									break;
								}
								
								nextLine = scanner.nextLine();
								
								if(nextLine.contains("<li>"))
								{
									if(nextLine.contains("1") == true)
										writeFile("<li><a href=\"" + temp +"-differenceOnly.html\">" + "Differences Only" + "</a></li>");
									if(nextLine.contains("2") == true)
										writeFile("<li><a href=\"" + temp + "-identicalOnly.html\">" + "Identical Only" + "</a></li>");
									if(nextLine.contains("3") == true)
										writeFile(nextLine);
									if(nextLine.contains("4") == true)
										writeFile("<li><a href=\"" + temp +"-" + parent.leftInputName + "Only.html\">" + parent.leftInputName + " only</a></li>");
									if(nextLine.contains("5") == true)
										writeFile("<li><a href=\"" + temp +"-" + parent.rightInputName + "Only.html\">" + parent.rightInputName + " only</a></li>");
								}
								else
								{
									writeFile(nextLine);
								}
							}
							LogBook.writeToDebug("\t\twhileloopbroken", true);
						}
						else if(nextLine.contains("Information Bar"))
						{
							LogBook.writeToDebug("\tInside thy info bar", true);
							writeFile(nextLine);
							while(nextLine.contains("</ul>") == false)
							{
								if(scanner.hasNext() == false)
								{
									ExecutableMain.reportAFailure("Couldn't generate the Sublinks in the SideBar for: " + fileName);
									break;
								}
								
								nextLine = scanner.nextLine();
								writeFile(nextLine);
							}
							LogBook.writeToDebug("\t\twhileloopbroken", true);
						}
						else if(nextLine.contains("View Originals"))
						{
							writeFile(nextLine);
							LogBook.writeToDebug("Viewing Originals" , true);
						}
						else if(nextLine.contains("Parent") && nextLine.contains("Master"))
						{
							
							if(this.parent.getParent() != null)
							{
								writeFile(nextLine);
								while(nextLine.contains("</ul>") == false)
								{
									if(scanner.hasNext() == false)
									{
										ExecutableMain.reportAFailure("Couldn't generate the Sublinks in the SideBar for: " + fileName);
										break;
									}
									
									nextLine = scanner.nextLine();
									if(fileType == HTMLFileType.MasterDocument)
									{
										if(nextLine.contains("SecondaryHeading"))
										{
											writeFile(nextLine);
											nextLine = scanner.nextLine();
											String temp2 = "<a href=\"..\\..\\" + this.parent.getParent().getReportName() +".html\">" + "View Parent's Master Report" + "</a>";
											writeFile(temp2);
										}
										else
											writeFile(nextLine);
									}
									else
										if(nextLine.contains("SecondaryHeading"))
										{
											writeFile(nextLine);
											nextLine = scanner.nextLine();
											String temp2 = "<a href=\"..\\..\\..\\" + this.parent.getParent().getReportName() +".html\">" + "View Parent's Master Report" + "</a>";
											writeFile(temp2);
										}
										else
											writeFile(nextLine);
								}
							}
							else
							{
								//nextLine = scanner.nextLine(); // This allow sus to skip it for the first main
								while(nextLine.contains("</div>") == false)
								{
									//writeFile(nextLine);
									if(scanner.hasNext() == false)
									{
										ExecutableMain.reportAFailure("Couldn't generate the Sublinks in the SideBar for: " + fileName);
										break;
									}
									nextLine = scanner.nextLine();
								}
							}
						}
						else if(nextLine.contains("Comment") && nextLine.contains("File"))
						{
							writeFile(nextLine);
							while(nextLine.contains("</ul>") == false)
							{
								if(scanner.hasNext() == false)
								{
									ExecutableMain.reportAFailure("Couldn't generate the Sublinks in the SideBar for: " + fileName);
									break;
								}
								
								nextLine = scanner.nextLine();
								
								if(nextLine.contains("SecondaryHeading"))
								{
									writeFile(nextLine);
									nextLine = scanner.nextLine();
									String temp2 = "<a href=\"..\\comments\\comments-" + this.fileName + "\">" + "View Comment File" + "</a>";
									writeFile(temp2);
								}
								else
									writeFile(nextLine);
							}
						}
						else
						{
							ExecutableMain.reportAFailure("Problem...");
							// ERROR:
						}
					}
					else
					{
						writeFile(nextLine);
					}
				}
			}
			else
				writeFile(nextLine);
		}
		LogBook.writeToDebug("DONE TOPBAR", true);
	}
	
	//TODO: 
	private void generateSideBarBottom(ArrayList<String[][]> sidebarBottomData)
	{
		if(sidebarBottomData != null)
		{
			String nextLine = "";
			
			while(nextLine.contains("</div>") == false || nextLine.contains("bottom") == false)
			{
				if(scanner.hasNext() == false)
				{
					ExecutableMain.reportAFailure("Couldn't generate a sidebar Bottom Element for: ");
					break;
				}
				nextLine = scanner.nextLine();
				
				if(nextLine.contains("A") && nextLine.contains("div"))
				{
					writeFile(nextLine);
					while(nextLine.contains("</div>") == false)
					{
						if(scanner.hasNext() == false)
						{
							ExecutableMain.reportAFailure("Couldn't generate a sidebar Bottom WITHIN A Failure: ");
							break;
						}
						nextLine = scanner.nextLine();
						
						switch(fileType)
						{
							case differenceFile:
							{
								if(nextLine.contains("statistics") && nextLine.contains("table"))
								{
									writeFile(nextLine);
									int statsCounter = 0;
									while(nextLine.contains("</table>") == false)
									{
										if(scanner.hasNext() == false)
										{
											ExecutableMain.reportAFailure("Couldn't generate a sidebar Bottom Statistics  Failure for: " + fileName);
											break;
										}
										nextLine = scanner.nextLine();
										
										if(nextLine.contains("INSERT"))
										{
											nextLine = nextLine.replace("INSERT", Utilities.General.removeHTMLLiterals(sidebarBottomData.get(0)[statsCounter][1]));
											statsCounter++;
										}
										
										writeFile(nextLine);
									}
								}
								else
									writeFile(nextLine);
								
								break;
							}
							case MasterDocument:
							{
								if(nextLine.contains("statistics") && nextLine.contains("table"))
								{
									writeFile(nextLine);
									int statsCounter = 0;
									while(nextLine.contains("</table>") == false)
									{
										if(scanner.hasNext() == false)
										{
											ExecutableMain.reportAFailure("Couldn't generate a sidebar Bottom Statistics  Failure for: " + fileName);
											break;
										}
										nextLine = scanner.nextLine();
										
										if(nextLine.contains("INSERT"))
										{
											nextLine = nextLine.replace("INSERT", Utilities.General.removeHTMLLiterals(sidebarBottomData.get(0)[0][statsCounter]));
											statsCounter++;
										}
										writeFile(nextLine);
									}
								}
								else
									writeFile(nextLine);
								
								break;
							}
							default:
								writeFile(nextLine);
						}
					}
				}
				else if(nextLine.contains("B") && nextLine.contains("div"))
				{
					writeFile(nextLine);
				}
				else if(nextLine.contains("C") && nextLine.contains("div"))
				{
					writeFile(nextLine);
					if(fileType == HTMLFileType.differenceFile)
					{
						String output = "";
						while(nextLine.contains("</div>") == false)
						{
							if(scanner.hasNext() == false)
							{
								ExecutableMain.reportAFailure("Couldn't generate a sidebar Bottom C Failure for: ");
								break;
							}
							nextLine = scanner.nextLine();
						}
						
						System.out.println(sidebarBottomData.get(1).length);
						System.out.println(sidebarBottomData.get(1)[0].length);
						for(int i = 0; i < sidebarBottomData.get(1).length; i++)
						{
							for(int j = 0; j < sidebarBottomData.get(1)[0].length; j++)
							{
								System.out.println("i:" + i + "\tj:" + j + "\t" + sidebarBottomData.get(1)[i][j]);
								output += "<br>\n";
								if(sidebarBottomData.get(1)[i][j] == null)
								{
									output += "NULL --- ERROR";
									ExecutableMain.reportAWarning("HTML WRITTER - BOTTOM BAR", "We couldn't get a string representation of the settings Object", parent);
								}
								else
									output += Utilities.General.removeHTMLLiterals(sidebarBottomData.get(1)[i][j]);
							}
							output += "<br>\n";
						}
						writeFile(output);
						writeFile(nextLine);
					}
					else
					{
						
					}
				}
				else if(nextLine.contains("D") && nextLine.contains("div"))
				{
					writeFile(nextLine);
				}
				else
					writeFile(nextLine);
			}
			LogBook.writeToDebug("\tExited Sidebar Bottom", true);
		}
		else
		{
			String nextLine = "";
			while(nextLine.contains("</div>") == false || nextLine.contains("bottom") == false)
			{
				nextLine = scanner.nextLine();
				writeFile(nextLine);
			}
		}
	}
	
	
	
	
	//TODO: OLD METHOD FIX
	
	//TODO Syntax highlighting
	private String highlightSyntax(String line, Settings.FileComparer.HTMLStringArray settings)
	{
		String output = "";
		return output;
	}
	
	private void writeFile(String text)
	{
		writeFile(text, "null");
	}
	
	private void writeFile(String text, String source)
	{
	    try 
	    {
			out.write(text + "\n");
			if(SettingSRC.consoleDisplay_FileManipulation == true)
				System.out.println("Writting: " + text);
			if(SettingSRC.FileManipulation_EnableDetailedLog == true)
				writeToLog("Source: " + source + "\t Writting: " + text);
	    } 
		catch (IOException e) 
	    {
			
	    }
	    try 
	    {
			out.flush();
		} 
	    catch (IOException e) 
	    {
			e.printStackTrace();
		}
	}
	
	private void writeToLog(String entry)
	{
		writeToLog(entry, false);
	}
	
	private void writeToLog(String entry, String source)
	{
		writeToLog(entry, false);
	}
	
	private void writeToLog(String entry, boolean forceTimeStamp)
	{
		LogBook.getLog(Log.logType.File_Manipulation).writeToLog(entry, forceTimeStamp);
	}
}
