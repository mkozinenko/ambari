package org.apache.ambari.server.api.services.rolesupdate.pojo;
import org.w3c.dom.Element;

/**
 * Created by incu6us on 2/15/16.
 */
public class Role {
    private String id;
    private String version;
    private String old_version;
    private String description;
    private boolean updatable;

    public Role(){

    }

    public Role(Element element){
        this.id = element.getElementsByTagName("id").item(0).getTextContent().toUpperCase();
        this.version = element.getElementsByTagName("version").item(0).getTextContent();
        try {
            this.description = element.getElementsByTagName("description").item(0).getTextContent();
        }catch (NullPointerException e){
            this.description = "";
        }
    }

    public String getId() {
        return id.toUpperCase();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion ()
    {
        return version;
    }

    public void setVersion (String version)
    {
        this.version = version;
    }

    public String getOld_version() {
        return old_version;
    }

    public void setOld_version(String old_version) {
        this.old_version = old_version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    @Override
    public String toString()
    {
        return "[name = \""+id+"\", version = \""+version+"\", old_version = \""+old_version+"\", description = \""+description+"\", updatable = \""+updatable+"\"]";
    }
}
