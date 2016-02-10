package org.apache.ambari.server.utils;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.apache.ambari.server.state.quicklinks.Link;
import org.apache.ambari.server.state.quicklinks.Links;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * Created by incu6us on 2/5/16.
 */
public class ConsulUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ConsulUtils.class);

    private String url;
    private ConsulClient consul;
    private Links links;

    public ConsulUtils(String url) {
        this.url = url;
        init();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        init();
    }

    public Links getLinksConfigurationByKey(String keyName) {
        links = new Links();
        Link link;
        try {
            String value = new String(Base64.decodeBase64(consul.getKVValue(keyName).getValue().getValue().getBytes()));
            LOG.info("linksValue -> " + value);
            links = new ObjectMapper().readValue(value, Links.class);
            LOG.info("links -> " + links);
        }catch(Exception e){
            LOG.info("getLinksConfigurationByKey -> "+e);
        }
        return links;
    }

    private void init(){
        try {
            this.consul = new ConsulClient(url);
        }catch (Exception e){
            LOG.info("Consul init error -> "+e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this)+", "+
                "links = "+links.toString();
    }
}
