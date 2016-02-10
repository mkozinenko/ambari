package org.apache.ambari.server.stack;

import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.state.QuickLinksConfigurationInfo;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.quicklinks.Link;
import org.apache.ambari.server.state.quicklinks.Links;
import org.apache.ambari.server.state.quicklinks.QuickLinks;
import org.apache.ambari.server.state.quicklinks.QuickLinksConfiguration;
import org.apache.ambari.server.utils.ConsulUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by incu6us on 1/22/16.
 */
public class QuickLinksConfigurationModule extends BaseModule<QuickLinksConfigurationModule, QuickLinksConfigurationInfo> implements Validable{
    private static final Logger LOG = LoggerFactory.getLogger(QuickLinksConfigurationModule.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String QUICKLINKS_CONFIGURATION_KEY = "QuickLinksConfiguration";

    static {
    }


    private QuickLinksConfigurationInfo moduleInfo;
    private boolean valid = true;
    private Set<String> errors = new HashSet<String>();

    public QuickLinksConfigurationModule(File quickLinksConfigurationFile) {
        this(quickLinksConfigurationFile, new QuickLinksConfigurationInfo());
    }

    public QuickLinksConfigurationModule(File quickLinksConfigurationFile, QuickLinksConfigurationInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
        if (!moduleInfo.isDeleted() && quickLinksConfigurationFile != null) {
            LOG.debug("Looking for quicklinks in {}", quickLinksConfigurationFile.getAbsolutePath());
            ConsulUtils consul = null;
            Links links = null;
//            QuickLinks qlMain = null;
            FileReader reader = null;
            QuickLinksConfiguration qlConf = null;

            try {
                reader = new FileReader(quickLinksConfigurationFile);
            } catch (FileNotFoundException e) {
                LOG.error("Quick links file not found");
            }
            try {
                QuickLinks quickLinksConfig = mapper.readValue(reader, QuickLinks.class);
                Map<String, QuickLinks> map = new HashMap<String, QuickLinks>();

                // Read consul key for quicklinks
                LOG.info("Read Consul");
                if(moduleInfo.getConsulUrl() != null && moduleInfo.getConsulKey() != null){
                    LOG.info("module -> "+moduleInfo.getConsulUrl());
                    consul = new ConsulUtils(moduleInfo.getConsulUrl());
                    LOG.info("Consul loaded");
                    try {
                        links = consul.getLinksConfigurationByKey(moduleInfo.getConsulKey());
                        LOG.info("links created -> " + links.toString());

                        qlConf = new QuickLinksConfiguration();
                        qlConf.setLinks(links.getLinks());
                        quickLinksConfig.getQuickLinksConfiguration().mergeWithParent(qlConf);
                        LOG.info("links merged");
                    }catch (Exception e){
                        LOG.error("Can't load links -> "+e);
                    }
                }

                map.put(QUICKLINKS_CONFIGURATION_KEY, quickLinksConfig);
                moduleInfo.setQuickLinksConfigurationMap(map);
                LOG.debug("Loaded quicklinks configuration: {}", moduleInfo);
            } catch (IOException e) {
                String errorMessage = String.format("Unable to parse quicklinks configuration file %s", quickLinksConfigurationFile.getAbsolutePath());
                LOG.error(errorMessage,  e);
                setValid(false);
                setErrors(errorMessage);
            }
        }
    }

    public QuickLinksConfigurationModule(QuickLinksConfigurationInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    @Override
    public void resolve(QuickLinksConfigurationModule parent, Map<String, StackModule> allStacks, Map<String, ServiceModule> commonServices) throws AmbariException {
        QuickLinksConfigurationInfo parentModuleInfo = parent.getModuleInfo();

        if (parent.getModuleInfo() != null && !moduleInfo.isDeleted()) {
            if (moduleInfo.getQuickLinksConfigurationMap() == null || moduleInfo.getQuickLinksConfigurationMap().isEmpty()) {
                moduleInfo.setQuickLinksConfigurationMap(parentModuleInfo.getQuickLinksConfigurationMap());
            } else if(parentModuleInfo.getQuickLinksConfigurationMap() != null && !parentModuleInfo.getQuickLinksConfigurationMap().isEmpty()) {
                QuickLinks child = moduleInfo.getQuickLinksConfigurationMap().get(QUICKLINKS_CONFIGURATION_KEY);
                QuickLinks parentConfig= parentModuleInfo.getQuickLinksConfigurationMap().get(QUICKLINKS_CONFIGURATION_KEY);
                child.mergeWithParent(parentConfig);
            }
        }
    }

    @Override
    public QuickLinksConfigurationInfo getModuleInfo() {
        return moduleInfo;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public String getId() {
        return moduleInfo.getFileName();
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public void setErrors(String error) {
        errors.add(error);
    }

    @Override
    public void setErrors(Collection<String> error) {
        errors.addAll(error);
    }

    @Override
    public Collection<String> getErrors() {
        return errors;
    }

}
