package org.apache.ambari.server.api.rest;

import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.api.services.rolesupdate.RolesAdapter;
import org.apache.ambari.server.api.services.rolesupdate.pojo.Roles;
import org.apache.ambari.server.security.encryption.FileBasedCredentialStore;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.Properties;

/**
 * Created by incu6us on 2/26/16.
 */

@Path("/update")
@Produces(MediaType.APPLICATION_JSON)
public class RolesUpdate {

    private static final Logger LOG = Logger.getLogger(RolesUpdate.class);

    private static String url;

    public RolesUpdate(){
        init();
    }

    public static void init(){
        InputStream properties = Thread.currentThread().getContextClassLoader().getResourceAsStream(AmbariMetaInfo.ROLES_UPDATE_FILE_NAME);

        LOG.info(properties);

        Properties props = new Properties();
        try {
            props.load(properties);
            properties.close();

            url = props.getProperty("url");
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @GET
    public Roles getRoles(){
        RolesAdapter adapter = new RolesAdapter(url);
        return adapter.getRoles();
    }
}
