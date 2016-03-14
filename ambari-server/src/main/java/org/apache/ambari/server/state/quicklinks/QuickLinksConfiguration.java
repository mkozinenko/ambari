package org.apache.ambari.server.state.quicklinks;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by incu6us on 1/22/16.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuickLinksConfiguration {
    @JsonProperty("protocol")
    private Protocol protocol;

    @JsonProperty("links")
    private List<Link> links;

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void mergeWithParent(QuickLinksConfiguration parent) {
        if (parent == null) {
            return;
        }

        //protocol uses override merge, if the child has it, then use it
        if(protocol == null)
            protocol = parent.getProtocol();

        if (links == null) {
            links = parent.getLinks();
        } else if (parent.getLinks() != null) {
            links = mergeLinks(parent.getLinks(), links);
        }
    }

    private List<Link> mergeLinks(List<Link> parentLinks, List<Link> childLinks) {
        Map<String, Link> mergedLinks = new HashMap<String, Link>();

        for (Link parentLink : parentLinks) {
            mergedLinks.put(parentLink.getLabel(), parentLink);
        }

        for (Link childLink : childLinks) {
            if (childLink.getName() != null && childLink.getLabel() != null) {
                if(childLink.isRemoved()){
                    mergedLinks.remove(childLink.getLabel());
                } else {
                    Link parentLink = mergedLinks.get(childLink.getLabel());
                    childLink.mergeWithParent(parentLink);
                    mergedLinks.put(childLink.getLabel(), childLink);
                }
            }
        }
        return new ArrayList<Link>(mergedLinks.values());
    }

    @Override
    public String toString() {
        return "QuickLinksConfiguration{" +
                "protocol=" + protocol +
                ", links=" + links +
                '}';
    }
}
