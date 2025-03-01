package com.lariflix.jemm;

import com.lariflix.jemm.dtos.JellyfinCredentials;
import com.lariflix.jemm.dtos.JellyfinInstanceDetails;
import com.lariflix.jemm.forms.LoginWindow;
import com.lariflix.jemm.utils.JellyfinLookAndFell;

/**
 * Main class of the application.
 * 
 * This class is responsible for starting the application.
 * 
 * It creates an instance of JellyfinInstanceDetails and passes it to the LoginWindow.
 * @author Cesar Bianchi
 * @version 1.0.0
 * @since 1.0.0
 * @see com.lariflix.jemm.dtos.JellyfinInstanceDetails
 * @see com.lariflix.jemm.forms.LoginWindow
 * 
 */
public class Jemm {

    static JellyfinInstanceDetails instanceData = new JellyfinInstanceDetails();
    
    /**
     * The main entry point for the application.
     * This method creates a new LoginWindow and makes it visible.
     * 
     * @param args An array of command-line arguments for the application.
     * @author Cesar Bianchi
     * @since 1.0
     
     */
    public static void main(String[] args) {
        boolean isDebug = false;
                
        //If URL and apiToken was sent by parameters on application load
        if (args.length > 0) {
            String cURL = args[0];
            String cTokenAPI = args[1];
            
            if (args.length > 2){
                if (args[2].trim().equals("DEBUG")){
                    isDebug = true;
                }                
            }
            
            JellyfinCredentials credentials = new JellyfinCredentials(cURL,cTokenAPI);            
            instanceData.setCredentials(credentials);
            instanceData.setIsDebug(isDebug);
        }
        
        //Set Look And Feel Apereance
        JellyfinLookAndFell lookAndFeel = new JellyfinLookAndFell();
    	//lookAndFeel.setLookAndFeelFlatDarculaLaf();
        //lookAndFeel.setLookAndFeelNimbus();
        //lookAndFeel.setLookAndFeelFlatLightLaf();
        //lookAndFeel.setLookAndFeelFlatDarkLaf();
        //lookAndFeel.setLookAndFeelFlatIntelijLaf();
        //lookAndFeel.setLookAndFeelArcOrange();
        lookAndFeel.setLookAndFeelFlatDarkPurpleIJTheme();
        //lookAndFeel.setLookAndFeelFlatFlatNordTheme();
        
        
        LoginWindow loginFrame = new LoginWindow(instanceData);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    /**
     * Retrieves the instanceData property of the Jemm class.
     *
     * @return A JellyfinInstanceDetails object representing the details of the Jellyfin instance. This includes the URL, API token, and other necessary details.
     * @since 1.0
     * @author Cesar Bianchi
     */
    public static JellyfinInstanceDetails getInstanceData() {
        return instanceData;
    }
    
}
