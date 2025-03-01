package com.lariflix.jemm.reports;

import com.lariflix.jemm.utils.JellyfinReportTypes;
import com.lariflix.jemm.dtos.JellyfinInstanceDetails;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import org.json.simple.parser.ParseException;

/**
 * The JellyfinReportEngine class extends the Thread class and is used to generate reports from a Jellyfin server.
 * This class runs in its own thread to prevent blocking the main application thread.
 *
 * @author Cesar Bianchi
 */
public class JellyfinReportEngine extends Thread {
    private JellyfinReportTypes reportType = null;
    private JellyfinInstanceDetails instanceData = new JellyfinInstanceDetails();
    private boolean isDone = false;
    
    /**
     * Constructor for the JellyfinReportEngine class.
     *
     * @param rpType A JellyfinReportTypes object representing the type of report to generate. This could be any of the types defined in the JellyfinReportTypes class.
     * @param instData A JellyfinInstanceDetails object containing the details of the Jellyfin instance from which to generate the report. This includes the URL, API token, and other necessary details.
     * @since 1.1
     * @author Cesar Bianchi
     */
    public JellyfinReportEngine(JellyfinReportTypes rpType, JellyfinInstanceDetails instData){
        this.setReportType(rpType);
        this.setInstanceData(instData);
    }
    
    /**
     * The main execution method for the JellyfinReportEngine thread.
     *
     * This method is called when the thread is started. It generates a report based on the report type set in the JellyfinReportEngine object. 
     * The report is generated by calling the appropriate print method for the report type. Once the report is generated, the method sets the isDone flag to true and notifies any threads that are waiting on this object's monitor.
     *
     * @since 1.1
     * @author Cesar Bianchi
     */
    @Override
    public void run(){
        synchronized(this){
            
            JellyfinReportTypes rpType = this.getReportType();
            
            switch(rpType) {
                case INVENTORY_BASIC:
                    printInventoryReport();
                    isDone = true;
                    break;
                case INVENTORY_FULL:
                    printInventoryReport();                    
                    isDone = true;
                    break;
                case GENRES_BASIC:
                    printGenresReport();                    
                    isDone = true;
                    break;    
                case GENRES_FULL:
                    printGenresReport();                    
                    isDone = true;
                    break;
                case PEOPLE_BASIC:
                    printPeopleReport();                    
                    isDone = true;
                    break;  
                case PEOPLE_FULL:
                    printPeopleReport();                    
                    isDone = true;
                    break;
                case TAGS_BASIC:
                    printTagsReport();                    
                    isDone = true;
                    break;  
                case TAGS_FULL:
                    printTagsReport();                    
                    isDone = true;
                    break;
                case STUDIOS_BASIC:
                    printStudiosReport();                    
                    isDone = true;
                    break;     
                case STUDIOS_FULL:
                    printStudiosReport();                    
                    isDone = true;
                    break;
                case YEARS_FULL:
                    printYearsReport();                    
                    isDone = true;
                    break;
            }
            notify();
        }
    }
    
    /**
     * Retrieves the reportType property of this JellyfinReportEngine.
     *
     * @return A JellyfinReportTypes object representing the reportType of this JellyfinReportEngine. This could be any of the types defined in the JellyfinReportTypes class.
     * @since 1.1
     * @author Cesar Bianchi
     */
    public JellyfinReportTypes getReportType() {
        return reportType;
    }

    /**
     * Sets the reportType property of this JellyfinReportEngine.
     *
     * @param reportType A JellyfinReportTypes object that should be used as the new reportType for this JellyfinReportEngine. This could be any of the types defined in the JellyfinReportTypes class.
     * @since 1.1
     * @author Cesar Bianchi
     */
    public void setReportType(JellyfinReportTypes reportType) {
        this.reportType = reportType;
    }

    /**
     * Retrieves the instanceData property of this JellyfinReportEngine.
     *
     * @return A JellyfinInstanceDetails object representing the instanceData of this JellyfinReportEngine. This includes the URL, API token, and other necessary details.
     * @since 1.1
     * @author Cesar Bianchi
     */
    public JellyfinInstanceDetails getInstanceData() {
        return instanceData;
    }

    /**
     * Sets the instanceData property of this JellyfinReportEngine.
     *
     * @param instanceData A JellyfinInstanceDetails object that should be used as the new instanceData for this JellyfinReportEngine. This includes the URL, API token, and other necessary details.
     * @since 1.1
     * @author Cesar Bianchi
     */
    public void setInstanceData(JellyfinInstanceDetails instanceData) {
        this.instanceData = instanceData;
    }

    /**
     * Checks if the report generation is done.
     *
     * @return A boolean value indicating whether the report generation is done. Returns true if the report generation is done, false otherwise.
     * @since 1.1
     * @author Cesar Bianchi
     */
    public boolean isDone() {
        return isDone;
    }
    
    /**
     * Prints an inventory report.
     *
     * This method creates a new JellyfinReportInventory object using the instance data and report type of this JellyfinReportEngine. 
     * It then loads the report items and prints the report. If an error occurs while loading the report items or printing the report, it is logged and the method exits.
     *
     * @throws IOException If an I/O error occurs. This can happen if there's a problem with the network connection, the server, or the local machine.
     * @throws ParseException If there is an error parsing the server's response. This can happen if the server's response does not match the expected format.
     * @throws JRException If there is an error generating the report. This can happen if there's a problem with the report template, the data, or the JasperReports engine.
     * @since 1.1
     * @author Cesar Bianchi
     */
    private void printInventoryReport(){

        JellyfinReportInventory fullReport = new JellyfinReportInventory(this.getInstanceData(),this.getReportType());
        try {
            fullReport.loadReportItems();
            fullReport.printReport();
        } catch (IOException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Prints a genres report.
     *
     * This method creates a new JellyfinReportGenres object using the instance data and report type of this JellyfinReportEngine. 
     * It then loads the report items and prints the report. If an error occurs while loading the report items or printing the report, it is logged and the method exits.
     *
     * @throws IOException If an I/O error occurs. This can happen if there's a problem with the network connection, the server, or the local machine.
     * @throws ParseException If there is an error parsing the server's response. This can happen if the server's response does not match the expected format.
     * @throws JRException If there is an error generating the report. This can happen if there's a problem with the report template, the data, or the JasperReports engine.
     * @since 1.1
     * @author Cesar Bianchi
     */
    private void printGenresReport(){

        JellyfinReportGenres fullReport = new JellyfinReportGenres(this.getInstanceData(),this.getReportType());
        try {
            fullReport.loadReportItems();
            fullReport.printReport();
        } catch (IOException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Prints a people report.
     *
     * This method creates a new JellyfinReportPeople object using the instance data and report type of this JellyfinReportEngine. 
     * It then loads the report items and prints the report. If an error occurs while loading the report items or printing the report, it is logged and the method exits.
     *
     * @throws IOException If an I/O error occurs. This can happen if there's a problem with the network connection, the server, or the local machine.
     * @throws ParseException If there is an error parsing the server's response. This can happen if the server's response does not match the expected format.
     * @throws JRException If there is an error generating the report. This can happen if there's a problem with the report template, the data, or the JasperReports engine.
     * @since 1.1
     * @author Cesar Bianchi
     */
    private void printPeopleReport(){

        JellyfinReportPeople fullReport = new JellyfinReportPeople(this.getInstanceData(),this.getReportType());
        try {
            fullReport.loadReportItems();
            fullReport.printReport();
        } catch (IOException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Prints a tags report.
     *
     * This method creates a new JellyfinReportTags object using the instance data and report type of this JellyfinReportEngine. 
     * It then loads the report items and prints the report. If an error occurs while loading the report items or printing the report, it is logged and the method exits.
     *
     * @throws IOException If an I/O error occurs. This can happen if there's a problem with the network connection, the server, or the local machine.
     * @throws ParseException If there is an error parsing the server's response. This can happen if the server's response does not match the expected format.
     * @throws JRException If there is an error generating the report. This can happen if there's a problem with the report template, the data, or the JasperReports engine.
     * @since 1.1
     * @author Cesar Bianchi
     */
    private void printTagsReport(){

        JellyfinReportTags fullReport = new JellyfinReportTags(this.getInstanceData(),this.getReportType());
        try {
            fullReport.loadReportItems();
            fullReport.printReport();
        } catch (IOException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Prints a studios report.
     *
     * This method creates a new JellyfinReportStudios object using the instance data and report type of this JellyfinReportEngine. 
     * It then loads the report items and prints the report. If an error occurs while loading the report items or printing the report, it is logged and the method exits.
     *
     * @throws IOException If an I/O error occurs. This can happen if there's a problem with the network connection, the server, or the local machine.
     * @throws ParseException If there is an error parsing the server's response. This can happen if the server's response does not match the expected format.
     * @throws JRException If there is an error generating the report. This can happen if there's a problem with the report template, the data, or the JasperReports engine.
     * @since 1.0
     * @author Cesar Bianchi
     */
    private void printStudiosReport() {
        
        JellyfinReportStudios fullReport = new JellyfinReportStudios(this.getInstanceData(),this.getReportType());
        try {
            fullReport.loadReportItems();
            fullReport.printReport();
        } catch (IOException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Prints a years report.
     *
     * This method creates a new JellyfinReportYears object using the instance data and report type of this JellyfinReportEngine. It then loads the report items and prints the report. If an error occurs while loading the report items or printing the report, it is logged and the method exits.
     *
     * @throws IOException If an I/O error occurs. This can happen if there's a problem with the network connection, the server, or the local machine.
     * @throws ParseException If there is an error parsing the server's response. This can happen if the server's response does not match the expected format.
     * @throws JRException If there is an error generating the report. This can happen if there's a problem with the report template, the data, or the JasperReports engine.
     * @since 1.0
     * @author Cesar Bianchi
     */
    private void printYearsReport() {
        JellyfinReportYears fullReport = new JellyfinReportYears(this.getInstanceData(),this.getReportType());
        try {
            fullReport.loadReportItems();
            fullReport.printReport();
        } catch (IOException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            Logger.getLogger(JellyfinReportEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
