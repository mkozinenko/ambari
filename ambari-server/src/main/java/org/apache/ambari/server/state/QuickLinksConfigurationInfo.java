package org.apache.ambari.server.state;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.ambari.server.state.quicklinks.QuickLinks;

import java.util.Map;

/**
 * Created by incu6us on 1/22/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class QuickLinksConfigurationInfo {
    private String fileName;
    private String consulUrl;
    private String consulKey;
    @XmlElement(name = "default")
    private Boolean isDefault = false;
    private Boolean deleted = false;

    @XmlTransient
    private Map<String, QuickLinks> quickLinksConfigurationMap = null;

    public QuickLinksConfigurationInfo() {
    }

    public Map<String, QuickLinks> getQuickLinksConfigurationMap() {
        return quickLinksConfigurationMap;
    }

    public void setQuickLinksConfigurationMap(Map<String, QuickLinks> quickLinksConfigurationMap) {
        this.quickLinksConfigurationMap = quickLinksConfigurationMap;
    }

    @Override
    public String toString() {
        return "QuickLinksConfigurationInfo{" +
                "deleted=" + deleted +
                ", fileName='" + fileName + '\'' +
                ", consulUrl='" + consulUrl + '\'' +
                ", consulKey='" + consulKey + '\'' +
                ", isDefault=" + isDefault +
                ", quickLinksConfigurationMap=" + quickLinksConfigurationMap +
                '}';
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getConsulUrl() {
        return consulUrl;
    }

    public void setConsulUrl(String consulUrl) {
        this.consulUrl = consulUrl;
    }

    public String getConsulKey() {
        return consulKey;
    }

    public void setConsulKey(String consulKey) {
        this.consulKey = consulKey;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
