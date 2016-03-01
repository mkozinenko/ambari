package org.apache.ambari.server.api.services.rolesupdate.pojo;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by incu6us on 2/15/16.
 */
public class Roles {
    private List<Role> roles;

    public Roles(){
        roles = new ArrayList<Role>();
    }

    public List<Role> getRole ()
    {
        return roles;
    }

    public void setRole (List<Role> roles)
    {
        this.roles = roles;
    }

    public void setRole(Element element) {
        roles.add(new Role(element));
    }

}
