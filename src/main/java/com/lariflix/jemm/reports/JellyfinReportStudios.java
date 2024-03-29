/*
 * Copyright (C) 2024 cesarbianchi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.lariflix.jemm.reports;

import com.lariflix.jemm.core.LoadFolders;
import com.lariflix.jemm.core.LoadItemMetadata;
import com.lariflix.jemm.core.LoadItems;
import com.lariflix.jemm.core.LoadStudios;
import com.lariflix.jemm.dtos.JellyfinCadStudioItems;
import com.lariflix.jemm.dtos.JellyfinFolders;
import com.lariflix.jemm.dtos.JellyfinInstanceDetails;
import com.lariflix.jemm.dtos.JellyfinItem;
import com.lariflix.jemm.dtos.JellyfinItemMetadata;
import com.lariflix.jemm.dtos.JellyfinItems;
import static com.lariflix.jemm.reports.JellyfinReportStudios.instanceData;
import com.lariflix.jemm.utils.JellyfinReportTypes;
import com.lariflix.jemm.utils.JellyfinUtilFunctions;
import com.lariflix.jemm.utils.JemmVersion;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.json.simple.parser.ParseException;

/**
 *
 * @author cesarbianchi
 */
public class JellyfinReportStudios {
    static JellyfinInstanceDetails instanceData = new JellyfinInstanceDetails();    
    private JellyfinReportStudiosStructure items = new JellyfinReportStudiosStructure();
    private ArrayList<JellyfinItem> nonOrdenedEpisodes = new ArrayList();
    private int totalsubItems = 0;
    private JellyfinReportTypes reportType = null;  
    
    public JellyfinReportStudios(JellyfinReportTypes rpType) {
        this.reportType = rpType;
    }

    public JellyfinReportStudios(JellyfinInstanceDetails instanceData, JellyfinReportTypes rpType) {
        JellyfinReportStudios.instanceData = instanceData;
        this.reportType = rpType;
    }
    
    public void loadReportItems() throws IOException, MalformedURLException, ParseException, JRException{
        
        switch(reportType) {
            case STUDIOS_BASIC:
                this.loadItems();
                break;
            case STUDIOS_FULL:
                this.loadItems();
                this.loadSubItems();
                break;
        }
    }

    private void loadItems() throws IOException, MalformedURLException, ParseException {
        LoadStudios loadStudios = new LoadStudios();
        loadStudios.setJellyfinInstanceUrl(instanceData.getCredentials().getBaseURL());
        loadStudios.setApiToken(instanceData.getCredentials().getTokenAPI());
        loadStudios.setcUserAdminID(instanceData.adminUser.getId());        
        JellyfinCadStudioItems studios = loadStudios.requestStudios();
        
        
        for(int nI = 0; nI < studios.getTotalRecordCount();nI++){            
            JellyfinReportStudiosItem item = new JellyfinReportStudiosItem();
            item.setItem( studios.getItems().get(nI) );
            
            //Get Studio Item Metadata
            LoadItemMetadata loadStudioItemMetadata = new LoadItemMetadata();
            loadStudioItemMetadata.setJellyfinInstanceUrl(instanceData.getCredentials().getBaseURL());
            loadStudioItemMetadata.setApiToken(instanceData.getCredentials().getTokenAPI());
            loadStudioItemMetadata.setcUserAdminID(instanceData.getAdminUser().getId());
            loadStudioItemMetadata.setcItemID(item.getId());                
            JellyfinItemMetadata studioItemMetadata = loadStudioItemMetadata.requestItemMetadata();            
            item.setStudioMetadata(studioItemMetadata);
            
            items.add(item);
        }

        items.sort((o1, o2) -> o1.getName().toUpperCase().compareTo(o2.getName().toUpperCase()));
    }

    private void loadSubItems() {
        
        //1* Get All Folders
        LoadFolders loadItems = new LoadFolders();
        loadItems.setJellyfinInstanceUrl(instanceData.getCredentials().getBaseURL());
        loadItems.setApiToken(instanceData.getCredentials().getTokenAPI());
        loadItems.setcUserAdminID(instanceData.adminUser.getId());        
        JellyfinFolders folders;
        try {
            folders = loadItems.requestFolders();
            
            //2* Get All Itens for each folder
            LoadItems loadSubItems = new LoadItems();
            loadSubItems.setJellyfinInstanceUrl(instanceData.getCredentials().getBaseURL());
            loadSubItems.setApiToken(instanceData.getCredentials().getTokenAPI());
            loadSubItems.setcUserAdminID(instanceData.adminUser.getId());        
            
            for (int nI = 0; nI< folders.getItems().size(); nI++){            
                loadSubItems.setcParentID(folders.getItems().get(nI).getId());                            
                JellyfinItems subItems = loadSubItems.requestItems();                
                
                for (int nJ = 0; nJ < subItems.getItems().size(); nJ++){                    
                    nonOrdenedEpisodes.add(subItems.getItems().get(nJ));
                }
            }
            
            //3* For each episode, Get the metadata and check if the studio episode is the same of studios-item
            LoadItemMetadata loadEpisodeMetadata = new LoadItemMetadata();
            loadEpisodeMetadata.setJellyfinInstanceUrl(instanceData.getCredentials().getBaseURL());
            loadEpisodeMetadata.setApiToken(instanceData.getCredentials().getTokenAPI());
            loadEpisodeMetadata.setcUserAdminID(instanceData.getAdminUser().getId());
            for (int nI = 0; nI < this.items.size(); nI++){
            
                //for (int nJ = 0; nJ < 10; nJ++){
                for (int nJ = 0; nJ < nonOrdenedEpisodes.size(); nJ++){
                    
                    loadEpisodeMetadata.setcItemID(nonOrdenedEpisodes.get(nJ).getId());                
                    JellyfinItemMetadata studioItemMetadata = loadEpisodeMetadata.requestItemMetadata();                            
                    for (int nK = 0; nK < studioItemMetadata.getStudios().size(); nK++){
                    
                        if (studioItemMetadata.getStudios().get(nK).getId().equals(this.getItems().get(nI).getId())){
                            
                            this.items.get(nI).addStudioEpisode(nonOrdenedEpisodes.get(nJ),studioItemMetadata);
                        }                        
                    }
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(JellyfinReportStudios.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(JellyfinReportStudios.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void printReport() throws JRException, MalformedURLException, IOException {
        String localReportBasePath = new JellyfinUtilFunctions().getJRXMLLocalPath();
        String resorceReportBasePath =  new  JellyfinUtilFunctions().getJRXMLResourcePath();
        
        String jrxmlFile = new String();
        JasperDesign draw = null;
        JasperReport report = null;
        
        String jrxmlSubRepFile = new String();
        JasperReport subreport = null;
        JasperDesign drawSub = null;
        JellyfinUtilFunctions commonFunctions = new JellyfinUtilFunctions();
        String tempFilesPath = System.getProperty("java.io.tmpdir");
        
        switch(reportType) {
            case STUDIOS_BASIC:
                
                if(instanceData.isDebug()){                    
                    //DEBUG WAY
                    System.out.println("This Application is running on Netbeans, in DEBUG MODE");                    
                    jrxmlFile = localReportBasePath.concat("/InstanceStudiosBasic/JemmInstanceStudiosBasic.jrxml");       
                    draw = JRXmlLoader.load( jrxmlFile );
                    report =  JasperCompileManager.compileReport( draw );
                } else {
                    //EMBEBED WAY
                    jrxmlFile = resorceReportBasePath.concat("/InstanceStudiosBasic/JemmInstanceStudiosBasic.jrxml");
                    InputStream drawIS = getClass().getResourceAsStream(jrxmlFile);
                    report =  JasperCompileManager.compileReport( drawIS );
                }
                break;
            
            case STUDIOS_FULL:
            
                if(instanceData.isDebug() ){                    
                    //DEBUG WAY
                    System.out.println("This Application is running on Netbeans, in DEBUG MODE");   
                    jrxmlFile = localReportBasePath.concat("/InstanceStudiosFull/JemmInstanceStudiosFull.jrxml");       
                    draw = JRXmlLoader.load( jrxmlFile );
                    report =  JasperCompileManager.compileReport( draw );
                    
                    
                    jrxmlSubRepFile = localReportBasePath.concat("/InstanceStudiosFull/JemmInstanceStudiosFullSubItems.jrxml");       
                    drawSub = JRXmlLoader.load( jrxmlFile );
                    subreport = JasperCompileManager.compileReport( drawSub );
                    
                    //Use a localfile subreport
                    JasperCompileManager.compileReportToFile(jrxmlSubRepFile,tempFilesPath.concat("JemmInstanceStudiosFullSubItems.jasper"));
                } else {
                    //EMBEBED WAY
                    jrxmlFile = resorceReportBasePath.concat("/InstanceStudiosFull/JemmInstanceStudiosFull.jrxml");
                    InputStream drawIS = getClass().getResourceAsStream(jrxmlFile);
                    report =  JasperCompileManager.compileReport( drawIS );
                    
                    //Extract subreport from resource to a localfile                    
                    jrxmlSubRepFile =  resorceReportBasePath.concat("/InstanceStudiosFull/JemmInstanceStudiosFullSubItems.jrxml");                    
                    if (commonFunctions.extractFileFromJar(jrxmlSubRepFile, "JemmInstanceStudiosFullSubItems.jrxml") ){
                        JasperCompileManager.compileReportToFile(tempFilesPath.concat("JemmInstanceStudiosFullSubItems.jrxml"),tempFilesPath.concat("JemmInstanceStudiosFullSubItems.jasper"));                    
                    }
                }
                
                                
                break;
                
        }       

        //Set report Data Source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(this.getItems());

        //Set Report Parameters
        Map reportParameters = new HashMap();
        reportParameters.put("INSTANCE_URL",instanceData.getCredentials().getBaseURL());
        reportParameters.put("JEMM_VERSION",new JemmVersion().getVersion() );
        reportParameters.put("TOTAL_FOLDERITEMS",Integer.toString(this.items.size()));
        reportParameters.put("TOTAL_CONTENT",Integer.toString(this.totalsubItems));
        reportParameters.put("SUBREPORT_JASPER_FILE",tempFilesPath.concat("JemmInstanceStudiosFullSubItems.jasper"));
        
        //Paint Report
        JasperPrint paintedReport = JasperFillManager.fillReport( report , reportParameters,  dataSource);
        
        //Show Report
        JasperViewer viewer = new JasperViewer( paintedReport , false );
        viewer.setTitle("Jellyfin Easy Metadata Manager - Studios Report");
        viewer.show();
       
    }
    
    public JellyfinReportStudiosStructure getItems() {
        return items;
    }

    public int getTotalsubItems() {
        return totalsubItems;
    }

    public void setTotalsubItems(int totalsubItems) {
        this.totalsubItems = totalsubItems;
    }
    
    
    
}
