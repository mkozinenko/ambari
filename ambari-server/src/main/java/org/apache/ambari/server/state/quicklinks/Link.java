package org.apache.ambari.server.state.quicklinks;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by incu6us on 1/22/16.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {
    @JsonProperty("name")
    private String name;

    @JsonProperty("label")
    private String label;

    @JsonProperty("requires_user_name")
    private String requiresUserName;

    @JsonProperty("url")
    private String url;

    @JsonProperty("target")
    private String target;

    @JsonProperty("template")
    private String template;

    @JsonProperty("port")
    private Port port;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getRequiresUserName() {
        return requiresUserName;
    }

    public void setRequiresUserName(String requiresUserName) {
        this.requiresUserName = requiresUserName;
    }

    public Port getPort() {
        return port;
    }

    public void setPort(Port port) {
        this.port = port;
    }

    public boolean isRemoved(){
        //treat a link as removed if the section only contains a name
        return (null == port && null == url && null == template && null == label && null == requiresUserName);
    }

    public void mergeWithParent(Link parentLink) {
        if (null == parentLink)
            return;

    /* merge happens when a child link has some infor but not all of them.
     * If a child link has nothing but a name, it's treated as being removed from the link list
     */
        if(null == template && null != parentLink.getTemplate())
            template = parentLink.getTemplate();

        if(null == label && null != parentLink.getLabel())
            label = parentLink.getLabel();

        if(null == url && null != parentLink.getUrl())
            url = parentLink.getUrl();

        if(null == target && null != parentLink.getTarget())
            target = parentLink.getTarget();

        if(null == template && null != parentLink.getTemplate())
            template = parentLink.getTemplate();

        if(null == requiresUserName && null != parentLink.getRequiresUserName())
            requiresUserName = parentLink.getRequiresUserName();

        if(null == port){
            port = parentLink.getPort();
        } else {
            port.mergetWithParent(parentLink.getPort());
        }
    }

}
