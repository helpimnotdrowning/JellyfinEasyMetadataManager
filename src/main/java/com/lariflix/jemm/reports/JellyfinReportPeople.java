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

import com.lariflix.jemm.core.LoadItemMetadata;
import com.lariflix.jemm.core.LoadPeople;
import com.lariflix.jemm.dtos.JellyfinCadPeopleItems;
import com.lariflix.jemm.dtos.JellyfinInstanceDetails;
import com.lariflix.jemm.dtos.JellyfinItem;
import com.lariflix.jemm.dtos.JellyfinItemMetadata;
import static com.lariflix.jemm.reports.JellyfinReportPeople.instanceData;
import static com.lariflix.jemm.reports.JellyfinReportTypes.GENRES_BASIC;
import static com.lariflix.jemm.reports.JellyfinReportTypes.GENRES_FULL;
import com.lariflix.jemm.utils.JellyfinUtilFunctions;
import com.lariflix.jemm.utils.JemmVersion;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
public class JellyfinReportPeople {

    static JellyfinInstanceDetails instanceData = new JellyfinInstanceDetails();    
    private JellyfinReportPeopleStructure items = new JellyfinReportPeopleStructure();
    private ArrayList<JellyfinItem> nonOrdenedEpisodes = new ArrayList();
    private JellyfinReportTypes reportType = null;  
    
    public JellyfinReportPeople(JellyfinReportTypes rpType) {
        this.reportType = rpType;
    }

    public JellyfinReportPeople(JellyfinInstanceDetails instanceData, JellyfinReportTypes rpType) {
        JellyfinReportPeople.instanceData = instanceData;
        this.reportType = rpType;
    }
    
    public void loadReportItems() throws IOException, MalformedURLException, ParseException, JRException{
        
        switch(reportType) {
            case PEOPLE_BASIC:
                this.loadItems();
                break;
            case PEOPLE_FULL:
                this.loadItems();
                this.loadEpisodes();
                break;
        }
    }
    
    private void loadItems() throws IOException, MalformedURLException, ParseException{
        
        LoadPeople loadPeople = new LoadPeople();
        loadPeople.setJellyfinInstanceUrl(instanceData.getCredentials().getBaseURL());
        loadPeople.setApiToken(instanceData.getCredentials().getTokenAPI());
        loadPeople.setcUserAdminID(instanceData.adminUser.getId());        
        JellyfinCadPeopleItems people = loadPeople.requestPeople();
        
        
        for(int nI = 0; nI < people.getTotalRecordCount();nI++){            
            JellyfinReportPeopleItem item = new JellyfinReportPeopleItem();
            
            item.setItem( people.getItems().get(nI) );
            
            /*Get People Metadata*/
            LoadItemMetadata loadPepopleMetadata = new LoadItemMetadata();
            loadPepopleMetadata.setJellyfinInstanceUrl(instanceData.getCredentials().getBaseURL());
            loadPepopleMetadata.setApiToken(instanceData.getCredentials().getTokenAPI());
            loadPepopleMetadata.setcUserAdminID(instanceData.getAdminUser().getId());
            loadPepopleMetadata.setcItemID(item.getId());                
            JellyfinItemMetadata peopleMetadata = loadPepopleMetadata.requestItemMetadata();            
            item.setPeopleMetadata(peopleMetadata);
            
            items.add(item);
        }
        
        items.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    private void loadEpisodes() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
            case PEOPLE_BASIC:
                
                if(instanceData.isDebug()){                    
                    //DEBUG WAY
                    System.out.println("This Application is running on Netbeans, in DEBUG MODE");                    
                    jrxmlFile = localReportBasePath.concat("/InstancePeopleBasic/JemmInstancePeopleBasic.jrxml");       
                    draw = JRXmlLoader.load( jrxmlFile );
                    report =  JasperCompileManager.compileReport( draw );
                } else {
                    //EMBEBED WAY
                    jrxmlFile = resorceReportBasePath.concat("/InstancePeopleBasic/JemmInstancePeopleBasic.jrxml");
                    InputStream drawIS = getClass().getResourceAsStream(jrxmlFile);
                    report =  JasperCompileManager.compileReport( drawIS );
                }
                break;
            
            case PEOPLE_FULL:
            
                if(instanceData.isDebug() ){                    
                    //DEBUG WAY
                    System.out.println("This Application is running on Netbeans, in DEBUG MODE");   
                    jrxmlFile = localReportBasePath.concat("/InstancePeopleFull/JemmInstancePeopleFull.jrxml");       
                    draw = JRXmlLoader.load( jrxmlFile );
                    report =  JasperCompileManager.compileReport( draw );
                    
                    
                    jrxmlSubRepFile = localReportBasePath.concat("/InstancePeopleFull/JemmInstancePeopleFullSubItems.jrxml");       
                    drawSub = JRXmlLoader.load( jrxmlFile );
                    subreport = JasperCompileManager.compileReport( drawSub );
                    
                    //Use a localfile subreport
                    JasperCompileManager.compileReportToFile(jrxmlSubRepFile,tempFilesPath.concat("JemmInstancePeopleFullSubItems.jasper"));
                } else {
                    //EMBEBED WAY
                    jrxmlFile = resorceReportBasePath.concat("/InstancePeopleFull/JemmInstancePeopleFull.jrxml");
                    InputStream drawIS = getClass().getResourceAsStream(jrxmlFile);
                    report =  JasperCompileManager.compileReport( drawIS );
                    
                    //Extract subreport from resource to a localfile                    
                    jrxmlSubRepFile =  resorceReportBasePath.concat("/InstancePeopleFull/JemmInstancePeopleFullSubItems.jrxml");                    
                    if (commonFunctions.extractFileFromJar(jrxmlSubRepFile, "JemmInstancePeopleFullSubItems.jrxml") ){
                        JasperCompileManager.compileReportToFile(tempFilesPath.concat("JemmInstancePeopleFullSubItems.jrxml"),tempFilesPath.concat("JemmInstancePeopleFullSubItems.jasper"));                    
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
        //reportParameters.put("TOTAL_CONTENT",Integer.toString(this.totalsubItems));
        reportParameters.put("SUBREPORT_JASPER_FILE",tempFilesPath.concat("JemmInstancePeopleFullSubItems.jasper"));
        
        //Paint Report
        JasperPrint paintedReport = JasperFillManager.fillReport( report , reportParameters,  dataSource);
        
        //Show Report
        JasperViewer viewer = new JasperViewer( paintedReport , false );
        viewer.setTitle("Jellyfin Easy Metadata Manager - People Report");
        viewer.show();
       
    }
    
    public JellyfinReportPeopleStructure getItems() {
        return items;
    }
    
}
