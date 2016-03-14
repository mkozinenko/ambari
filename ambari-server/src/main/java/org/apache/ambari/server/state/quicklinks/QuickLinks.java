package org.apache.ambari.server.state.quicklinks;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by incu6us on 1/22/16.
 */

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuickLinks {
    @JsonProperty("description")
    private String description;
    @JsonProperty("name")
    private String name;
    @JsonProperty("configuration")
    private QuickLinksConfiguration quickLinksConfiguration;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QuickLinksConfiguration getQuickLinksConfiguration() {
        return quickLinksConfiguration;
    }

    public void setQuickLinksConfiguration(QuickLinksConfiguration quickLinksConfiguration) {
        this.quickLinksConfiguration = quickLinksConfiguration;
    }

    public void mergeWithParent(QuickLinks parent) {
        if (parent == null) {
            return;
        }

        if (name == null) {
            name = parent.name;
        }

        if (description == null) {
            description = parent.description;
        }

        if (quickLinksConfiguration == null) {
            quickLinksConfiguration = parent.quickLinksConfiguration;
        } else {
            quickLinksConfiguration.mergeWithParent(parent.quickLinksConfiguration);
        }
    }

    @Override
    public String toString() {
        return "QuickLinks{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", quickLinksConfiguration=" + quickLinksConfiguration +
                '}';
    }
}
