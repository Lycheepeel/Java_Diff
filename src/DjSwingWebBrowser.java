

import java.awt.BorderLayout;   
import java.awt.FlowLayout;   
import java.awt.event.ItemEvent;   
import java.awt.event.ItemListener;   
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
  
import javax.swing.BorderFactory;   
import javax.swing.JCheckBox;   
import javax.swing.JComponent;   
import javax.swing.JFrame;   
import javax.swing.JPanel;   
import javax.swing.SwingUtilities;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;

public class DjSwingWebBrowser
{
	public static JComponent createContent(String filePath)
	{
		System.setProperty("nativeswing.swt.debug.device","true");
	    JPanel contentPane = new JPanel(new BorderLayout());   
	    JPanel webBrowserPanel = new JPanel(new BorderLayout());   
	    webBrowserPanel.setBorder(BorderFactory.createTitledBorder("Native Web Browser component"));   
	    final JWebBrowser webBrowser = new JWebBrowser();
	    if(filePath != null)
	    	webBrowser.navigate(filePath);
	    else
	    	webBrowser.navigate("http://www.google.ca");
	    webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
	    webBrowser.setLocationBarVisible(false);
	    webBrowser.setButtonBarVisible(true);
	    webBrowser.setMenuBarVisible(false);
	    
	    /*
	     * I could think about using a function that uses the
	     * getHTMLContent() and then rePrint that entire thing with modification...
	     * we can get the HTML file Location using getResourceLocation() then use a scanner to modify 
	     */
	    
	    /*
	     * http://www.html5rocks.com/en/tutorials/file/dndfiles/
	     * We can use the HTML5 api to allow for reading local files, to therefore read the files from a gigantic folder, these can be listed as plain text. 
	     */
	    
	    webBrowser.addWebBrowserListener
	    (
	    	new WebBrowserAdapter()
	    	{
	    		public void recieveData(WebBrowserCommandEvent e)
	    		{
	    			String command = e.getCommand();
	    			Object[] parameters = e.getParameters();
	    			String[] args = new String[parameters.length];
	    			for(int i = 0; i < parameters.length; i++)
	    			{
	    				args[i] = parameters[i].toString();
	    			}
	    			modifyCommentFile(command, args, webBrowser.getResourceLocation());
	    		}
	    	}
	    );	 
	    
	    contentPane.add(webBrowserPanel, BorderLayout.CENTER);   
	    
	    System.out.println(webBrowser.getBrowserType() + " running: " + webBrowser.getBrowserVersion());
	    return contentPane; 
	}
	
	public static void main(String[] args)
	{
		NativeInterface.open();
		UIUtils.setPreferredLookAndFeel();
		SwingUtilities.invokeLater
		(
			new Runnable() 
			{   
			     public void run() 
			     {  
			    	 String execDirString = System.getProperty("user.dir");
			    	 String filePath = null;
			    	 
			    	 File execDirFile = new File(execDirString);
			    	 if(execDirFile.isDirectory() == false)
			    	 {
			    		 
			    	 }
			    	 
			    	 File[] list = execDirFile.listFiles();
			    	 for(int i = 0; i < list.length; i++)
			    	 {
			    		 if(list[i].getName().endsWith(".html"));
			    		 {
			    		 	filePath = list[i].toString();
			    		 	break;
			    		 }
			    	 }
			    	 
			    	 JFrame frame = new JFrame("DJ Native Swing Test");   
			    	 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
			    	 frame.getContentPane().add(createContent(filePath), BorderLayout.CENTER);   
			    	 frame.setSize(800, 600);   
			    	 frame.setLocationByPlatform(true);   
			    	 frame.setVisible(true);   
			     }   
			}
		 );   

		NativeInterface.runEventPump();
	}
	
    /*
     *	Let's say that all of the data will be stored as a JSON.
     *  The only time it will need to call up to the java program is if it's writting data therefore...
     *  The first parameter will be what is it storing
     *  	A Script Descrption or a Line Comment
     *  If it's Script description
     *  	Two Args
     *  		0 => Additional path
     *  		1 => Text
     *  If it's a line comment
     *  		0 => Additional path
     *  	Argument will then be 
     *  		Line #
     *  		Priority #
     *  		Line Comment
     *  
     *  TODO: we'll ahve to modify the HTML writer, to detect if this is a subfolder comp probably with if(this.parent.parent == null)
     */
	
	private static void modifyCommentFile(String command, String[] args, String location)
	{
		File path = new File(location);
		String name = path.getName();
		String commentsPathString = path.getParentFile().getParentFile().toString() + "\\comments\\";
		String tempPathString = path.getParentFile().getParentFile().toString() + "\\tmp\\";
		
		String filePath = commentsPathString + "comments-" + name;
		
		File source = new File(filePath);

		HTMLWriter fileMover = new HTMLWriter(tempPathString, HTMLWriter.HTMLFileType.copyFile, null, null, null);
		fileMover.copyPaste(new File(filePath));
		
		HTMLWriter commentWriter = new HTMLWriter(commentsPathString, HTMLWriter.HTMLFileType.CommentFile, null, null, null);
		if(command.equals("description"))
			commentWriter.editCommentFile(name, System.getProperty("user.dir"), false, args);
		else
			commentWriter.editCommentFile(name, System.getProperty("user.dir"), true, args);
	}
}
