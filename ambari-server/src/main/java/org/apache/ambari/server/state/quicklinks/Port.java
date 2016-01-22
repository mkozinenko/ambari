package org.apache.ambari.server.state.quicklinks;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by incu6us on 1/22/16.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Port {
    @JsonProperty("http_property")
    private String httpProperty;

    @JsonProperty("http_default_port")
    private String httpDefaultPort;

    @JsonProperty("https_property")
    private String httpsProperty;

    @JsonProperty("https_default_port")
    private String httpsDefaultPort;

    @JsonProperty("regex")
    private String regex;

    @JsonProperty("site")
    private String site;

    public String getHttpProperty() {
        return httpProperty;
    }

    public void setHttpProperty(String httpProperty) {
        this.httpProperty = httpProperty;
    }

    public String getHttpDefaultPort() {
        return httpDefaultPort;
    }

    public void setHttpDefaultPort(String httpDefaultPort) {
        this.httpDefaultPort = httpDefaultPort;
    }

    public String getHttpsProperty() {
        return httpsProperty;
    }

    public void setHttpsProperty(String httpsProperty) {
        this.httpsProperty = httpsProperty;
    }

    public String getHttpsDefaultPort() {
        return httpsDefaultPort;
    }

    public void setHttpsDefaultPort(String httpsDefaultPort) {
        this.httpsDefaultPort = httpsDefaultPort;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void mergetWithParent(Port parentPort){
        if(null == parentPort)
            return;

        if(null == httpProperty && null != parentPort.getHttpProperty())
            httpProperty = parentPort.getHttpProperty();

        if(null == httpDefaultPort && null != parentPort.getHttpDefaultPort())
            httpDefaultPort = parentPort.getHttpDefaultPort();

        if(null == httpsProperty && null != parentPort.getHttpsProperty())
            httpsProperty = parentPort.getHttpsProperty();

        if(null == httpsDefaultPort && null != parentPort.getHttpsDefaultPort())
            httpsDefaultPort = parentPort.getHttpsDefaultPort();

        if(null == regex && null != parentPort.getRegex())
            regex = parentPort.getRegex();

        if(null == site && null != parentPort.getSite())
            site = parentPort.getSite();
    }

}
