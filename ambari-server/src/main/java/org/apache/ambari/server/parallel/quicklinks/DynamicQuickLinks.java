package org.apache.ambari.server.parallel.quicklinks;

import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.api.services.rolesupdate.pojo.Roles;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.AmbariManagementControllerImpl;
import org.apache.ambari.server.controller.internal.ResourceImpl;
import org.apache.ambari.server.controller.spi.NoSuchParentResourceException;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.stack.ServiceDirectory;
import org.apache.ambari.server.stack.StackDirectory;
import org.apache.ambari.server.state.QuickLinksConfigurationInfo;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.server.state.quicklinks.Links;
import org.apache.ambari.server.utils.ConsulUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by incu6us on 3/3/16.
 */
public class DynamicQuickLinks {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicQuickLinks.class);

    public static final String STACK_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "stack_name");
    public static final String STACK_VERSION_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "stack_version");
    public static final String STACK_SERVICE_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "service_name");
    public static final String QUICKLINK_DEFAULT_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "default");
    public static final String QUICKLINK_FILE_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "file_name");
    public static final String QUICKLINK_CONSUL_URL_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "consul_url");
    public static final String QUICKLINK_CONSUL_KEY_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "consul_key");
    public static final String QUICKLINK_DATA_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "quicklink_data");

    private static Map<String, String> ambariProperties;

    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private Document doc;
    private NodeList nList;

    private String consulUrl;
    private String consulKey;

    private Links links;

    public Links getQuickLinks(String consulUrl, String consulKey){
        ConsulUtils consul = new ConsulUtils(consulUrl);
        links = consul.getLinksConfigurationByKey(consulKey);

        return links;
    }

    /**
     * Not USED
     * @param stackName
     * @param stackVersion
     * @param stackService
     * @return
     */
    private Links getQuickLinks(String stackName, String stackVersion, String stackService){

//        Map<String, String> getAmbariProperties()
        Configuration conf = new Configuration();
        ambariProperties = conf.getAmbariProperties();

        LOG.info("ambariProps: " + ambariProperties.toString());
        LOG.info(Configuration.METADATA_DIR_PATH);

        String stackDir = ambariProperties.get(Configuration.METADATA_DIR_PATH)+ File.separator+stackName + File.separator+stackVersion;//+File.separator+"services"+File.separator+serviceName;
//        try {
//            StackDirectory stack = new StackDirectory(stackDir);
//            Iterator<ServiceDirectory> serviceIter = stack.getServiceDirectories().iterator();
//            while(serviceIter.hasNext()){
//                ServiceDirectory serviceDirectory = serviceIter.next();
//                if(stackName.equals(serviceDirectory.getName())) {
//                    LOG.info(serviceDirectory.getAbsolutePath()); // var/lib/ambari-server/resources/stacks/pangea_mesos/2.2/services/ELASTICSEARCH
//                    LOG.info(serviceDirectory.getPackageDir()); // stacks/pangea_mesos/2.2/services/ELASTICSEARCH/package
//                    LOG.info(serviceDirectory.getName()); // ELASTICSEARCH
//                    LOG.info(serviceDirectory.getPath()); // /var/lib/ambari-server/resources/stacks/pangea_mesos/2.2/services/ELASTICSEARCH
//                    LOG.info(serviceDirectory.getMetaInfoFile().getServices().toString());
//
//                    Set<Resource> resources = new LinkedHashSet<Resource>();

                    ConsulUtils consul = new ConsulUtils("http://localhost:8500");
                    links = consul.getLinksConfigurationByKey("quicklinks_"+stackService);

//                    dbf = DocumentBuilderFactory.newInstance();
//                    try {
//                        db = dbf.newDocumentBuilder();
//                        doc = db.parse(new URL("").openStream());
//
//                        nList = doc.getElementsByTagName("role");
//                        for(int el=0; el < nList.getLength(); el++){
//                            Node node = nList.item(el);
//
//                            if(node.getNodeType() == Node.ELEMENT_NODE){
//                                Element element = (Element) node;
//                                roles.setRole(element);
//                            }
//                            LOG.debug("New Roles: " + roles.getRole().toString());
//                        }
//
//                    } catch (ParserConfigurationException | SAXException | IOException e1) {
//                        LOG.error(e1.getMessage());
//                    }



//                }
//            }
//        } catch (AmbariException e) {
//            LOG.error(e.getMessage());
//        }

        ////
        return links;
    }
}
