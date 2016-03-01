package org.apache.ambari.server.api.services.rolesupdate;

import org.apache.ambari.server.api.services.rolesupdate.pojo.Roles;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;

/**
 * Created by incu6us on 2/15/16.
 */
public class RolesAdapter {


    private static final Logger LOG = Logger.getLogger(RolesAdapter.class);

    private String url;

    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private Document doc;
    private NodeList nList;

    private Roles roles;

    public RolesAdapter(String url){
        this.url = url;
    }

    public Roles getRoles(){
        dbf = DocumentBuilderFactory.newInstance();
        roles = new Roles();
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(new URL(url).openStream());

            nList = doc.getElementsByTagName("role");
            for(int el=0; el < nList.getLength(); el++){
                Node node = nList.item(el);

                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    roles.setRole(element);
                }
                LOG.debug("New Roles: " + roles.getRole().toString());
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error(e);
        }

        return roles;
    }

    @Override
    public String toString() {
        getRoles();
        return "RolesAdapter{" +
                "roles=" + roles.getRole() +
                '}';
    }
}
