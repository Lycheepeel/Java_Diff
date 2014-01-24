package Browser;

import javax.swing.JComponent;

import java.awt.BorderLayout;   
import java.awt.FlowLayout;   
import java.awt.event.ItemEvent;   
import java.awt.event.ItemListener;   
import java.util.Arrays;
  
import javax.swing.BorderFactory;   
import javax.swing.JCheckBox;   
import javax.swing.JComponent;   
import javax.swing.JFrame;   
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;   
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;



public class test 
{
	protected static final String LS = System.getProperty("line.separator");   
	
	 public static JComponent createContent() {   
		    JPanel contentPane = new JPanel(new BorderLayout(5, 5));   
		    JPanel commandPanel = new JPanel(new BorderLayout());   
		    commandPanel.add(new JLabel("Received command: "), BorderLayout.WEST);   
		    commandPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));   
		    final JTextField receivedCommandTextField = new JTextField();   
		    commandPanel.add(receivedCommandTextField, BorderLayout.CENTER);   
		    contentPane.add(commandPanel, BorderLayout.SOUTH);   
		    JPanel webBrowserPanel = new JPanel(new BorderLayout());   
		    webBrowserPanel.setBorder(BorderFactory.createTitledBorder("Native Web Browser component"));   
		    final JWebBrowser webBrowser = new JWebBrowser();
		    webBrowser.setBarsVisible(false);   
		    webBrowser.setStatusBarVisible(true);   
		    webBrowser.addWebBrowserListener(new WebBrowserAdapter() {   
		      @Override  
		      public void commandReceived(WebBrowserCommandEvent e) {   
		        String command = e.getCommand();   
		        Object[] parameters = e.getParameters();   
		        receivedCommandTextField.setText(webBrowser.getResourceLocation());   
		        if("store".equals(command)) {   
		          String data = (String)parameters[0] + " " + (String)parameters[1];   
		          if(JOptionPane.showConfirmDialog(webBrowser, "Do you want to store \"" + data + "\" in a database?\n(Not for real of course!)", "Data received from the web browser", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {   
		            // Data should be used here   
		          }   
		        }   
		      }   
		    });
		    webBrowser.navigate("C:\\Users\\ESTEYEE\\Documents\\File Comparision Program\\Test Cases\\java browser\\test.html");
		    webBrowserPanel.add(webBrowser, BorderLayout.CENTER);   
		    contentPane.add(webBrowserPanel, BorderLayout.CENTER);   
		    return contentPane;   
		  }   
		  
		  /* Standard main method to try that test as a standalone application. */  
		  public static void main(String[] args) {   
		    NativeInterface.open();   
		    UIUtils.setPreferredLookAndFeel();   
		    SwingUtilities.invokeLater(new Runnable() {   
		      public void run() {   
		        JFrame frame = new JFrame("DJ Native Swing Test");   
		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
		        frame.getContentPane().add(createContent(), BorderLayout.CENTER);   
		        frame.setSize(800, 600);   
		        frame.setLocationByPlatform(true);   
		        frame.setVisible(true);   
		      }   
		    });   
		    NativeInterface.runEventPump();   
		  }   
}
