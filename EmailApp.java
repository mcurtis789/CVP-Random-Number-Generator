//These classes are used by custom configurable elements.
import com.audium.server.AudiumException;
import com.audium.server.voiceElement.ActionElementBase;
import com.audium.server.voiceElement.ElementInterface;
import com.audium.server.voiceElement.ExitState;
import com.audium.server.voiceElement.Setting;
import com.audium.server.voiceElement.ElementData;
import com.audium.server.voiceElement.ElementException;
import com.audium.server.xml.ActionElementConfig;
// This class is used by action elements.
import com.audium.server.session.ActionElementData;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * This is the skeleton of a configurable action element. This is different 
 * from a standard action in that it is pre-built and the developer 
 * configures it in Studio. The methods implemented here 
 * apply primarily to define the display in Studio.
 */
public class EmailApp extends ActionElementBase implements ElementInterface 
{
	// This method returns the name of the action element that appears in Studio's Element view
    public String getElementName()
    {
        return "EmailApp";
    }
	// This method returns the name of a folder that will contain the action element 
	// in Studio's Element view. Use return null if you don't want it in a folder.
    public String getDisplayFolderName()
    {
        return "Custom Elements";
    }
	// This method returns a description of the element that will display in Studio
	// when the cursor hovers over the element in the Element view
    public String getDescription() 
    {
        return "This App is intended to be able to send an Email with configurable parameters and to be stand alone in it functionality.";
    }
    
    //This method returns the settings to display in the element's configuration view
	public Setting[] getSettings() throws ElementException 
	 {	
	 	 //You must define the number of settings here
		 Setting[] settingArray = new Setting[8];
		 
       	//each setting must specify: real name, display name, description,
       	//is it required?, can it only appear once?, does it allow substitution?,
       	//and the type of entry allowed
       	
       	//Note that the settingArray starts indexing at 0
		 settingArray[0] = new Setting("ToEmail", "To Email Address", 
						   "To Email Address.",
				  true,   // It is required
				  true,   // It appears only once
				  true,   // It allows substitution
				  Setting.STRING);
		 settingArray[0].setDefaultValue("to@example.com");
		 settingArray[1] = new Setting("FromEmail", "From Email Address", 
				   "From Email Address",
				   true,   // It is required
				   true,   // It appears only once
				   true,  // It does allow substitution
				   Setting.STRING);
		 settingArray[1].setDefaultValue("from@example.com");
		 settingArray[2] = new Setting("SMTPServer", "IP Address of Server", 
						   "Enter the IP Address of Server.",
				   true,   // It is required
				   true,   // It appears only once
				   true,  // It does allow substitution
				   Setting.STRING);
		 settingArray[2].setDefaultValue("SMTPServer");
		 settingArray[3] = new Setting("SMTPPort", "SMTP Port", 
			   "Subject Line",
			   		true,   // It is required
			   		true,   // It appears only once
			   		true,  // It does NOT allow substitution
			   		Setting.STRING);
		 settingArray[3].setDefaultValue("25");	
		 settingArray[4] = new Setting("SubjectLine", "Subject Line", 
					   "Subject Line",
					true,   // It is required
					true,   // It appears only once
					true,  // It does NOT allow substitution
					Setting.STRING);
		settingArray[4].setDefaultValue("SubjectLine");	
		settingArray[5] = new Setting("EmailBody", "Email Body", 
			  "Email Message Body",
			  		true,   // It is required
			  		true,   // It appears only once
			  		true,   // It allows substitution
			  		Setting.STRING);	
		settingArray[5].setDefaultValue("EmailBody");
		settingArray[6] = new Setting("ServerConnectTimeout", "Server Connection Timeout", 
				"Server Connection Timeout",
					true,   // It is required
					true,   // It appears only once
					true,   // It allows substitution
					Setting.STRING);	
		settingArray[6].setDefaultValue("30000");
		settingArray[7] = new Setting("ServerRequestTimeout", "Server Request Timeout", 
				"Server Request Timeout",
					true,   // It is required
					true,   // It appears only once
					true,   // It allows substitution
					Setting.STRING);	
		settingArray[7].setDefaultValue("30000");
		 //This is how you can specify a default value to appear when the element is first
		 //used in Studio	


		return settingArray;
	 }

	/**
	 * This method returns an array of ElementData created by the element.
	 * It is not used in CVP 3.1, but is used in CVP4. This method should return null 
	 * if the action element does not create any Element Data.
	 */
    public ExitState[] getExitStates()
            throws ElementException
        {
            ExitState exitStateArray[] = new ExitState[1];
            exitStateArray[0] = new ExitState("done", "done", "done");
            return exitStateArray;
        }
	/**
	 * This is the run time code, executed by CVP VXML Server when it reaches the element 
	 * in the call flow. 
	 */
	public void	doAction(String name, ActionElementData actionData) throws AudiumException
	{    	
		
			// Get the configuration
			ActionElementConfig config = actionData.getActionElementConfig();
			//now retrieve each setting value using its 'real' name as defined in the getSettings method above
			//each setting is returned as a String type, but can be converted.
		    // Recipient's email ID needs to be mentioned.
		    String strToMailId = config.getSettingValue("ToEmail",actionData);
		    String strFromMailId = config.getSettingValue("FromEmail",actionData);
		    String strHostName = config.getSettingValue("SMTPServer",actionData);
		    String strPort = config.getSettingValue("SMTPPort",actionData);
		    String strSubject = config.getSettingValue("SubjectLine",actionData);
		    String strBody = config.getSettingValue("EmailBody",actionData);
		    String strConnTimeout = config.getSettingValue("ServerConnectTimeout",actionData);
		    String strSocketTimeout = config.getSettingValue("ServerRequestTimeout",actionData);
		    // Get system properties
		    Properties props = new Properties();
		    props.put("mail.smtp.host", strHostName);
		    props.put("mail.smtp.port", strPort);
		    props.put("mail.smtp.connectiontimeout",strConnTimeout);
		    props.put("mail.smtp.timeout",strSocketTimeout);
		    Session session = Session.getInstance(props);	
		try {   
	         MimeMessage message = new MimeMessage(session);
	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(strFromMailId));
	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(strToMailId));
	         // Set Subject: header field
	         message.setSubject(strSubject);
	         // Now set the actual message
	         message.setText(strBody);
	         // Send message
	         Transport.send(message);

		    actionData.setElementData("status","success");
		} catch (Exception e) {
			//If anything goes wrong, create Element data 'status' with the value 'failure'
			//and return an empty string into the variable requested by the caller
			e.printStackTrace();
			actionData.setElementData("status","failure");
		}
	}

}
