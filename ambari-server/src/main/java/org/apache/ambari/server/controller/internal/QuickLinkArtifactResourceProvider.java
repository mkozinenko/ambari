package org.apache.ambari.server.controller.internal;

import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.NoSuchParentResourceException;
import org.apache.ambari.server.controller.spi.NoSuchResourceException;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.RequestStatus;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceAlreadyExistsException;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.spi.UnsupportedPropertyException;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.parallel.quicklinks.DynamicQuickLinks;
import org.apache.ambari.server.state.QuickLinksConfigurationInfo;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.server.state.quicklinks.Links;
import org.apache.ambari.server.state.quicklinks.QuickLinks;
import org.apache.ambari.server.state.quicklinks.QuickLinksConfiguration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by incu6us on 1/22/16.
 */
public class QuickLinkArtifactResourceProvider extends AbstractControllerResourceProvider {
    public static final String STACK_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "stack_name");
    public static final String STACK_VERSION_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "stack_version");
    public static final String STACK_SERVICE_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "service_name");
    public static final String QUICKLINK_DEFAULT_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "default");
    public static final String QUICKLINK_FILE_NAME_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "file_name");
    public static final String QUICKLINK_CONSUL_URL_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "consul_url");
    public static final String QUICKLINK_CONSUL_KEY_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "consul_key");
    public static final String QUICKLINK_DATA_PROPERTY_ID = PropertyHelper.getPropertyId("QuickLinkInfo", "quicklink_data");

    private DynamicQuickLinks consulQuickLinks;

    /**
     * primary key fields
     */
    public static Set<String> pkPropertyIds = new HashSet<String>();
    /**
     * map of resource type to fk field
     */
    public static Map<Resource.Type, String> keyPropertyIds =
            new HashMap<Resource.Type, String>();

    /**
     * resource properties
     */
    public static Set<String> propertyIds = new HashSet<String>();

    static {
        keyPropertyIds.put(Resource.Type.QuickLink, QUICKLINK_FILE_NAME_PROPERTY_ID);
        keyPropertyIds.put(Resource.Type.Stack, STACK_NAME_PROPERTY_ID);
        keyPropertyIds.put(Resource.Type.StackVersion, STACK_VERSION_PROPERTY_ID);
        keyPropertyIds.put(Resource.Type.StackService, STACK_SERVICE_NAME_PROPERTY_ID);

        pkPropertyIds.add(QUICKLINK_FILE_NAME_PROPERTY_ID);

        // resource properties
        propertyIds.add(STACK_NAME_PROPERTY_ID);
        propertyIds.add(STACK_VERSION_PROPERTY_ID);
        propertyIds.add(STACK_SERVICE_NAME_PROPERTY_ID);
        propertyIds.add(QUICKLINK_FILE_NAME_PROPERTY_ID);
        propertyIds.add(QUICKLINK_CONSUL_URL_PROPERTY_ID);
        propertyIds.add(QUICKLINK_CONSUL_KEY_PROPERTY_ID);
        propertyIds.add(QUICKLINK_DATA_PROPERTY_ID);
        propertyIds.add(QUICKLINK_DEFAULT_PROPERTY_ID);
    }

    /**
     * Create a  new resource provider for the given management controller.
     *
     * @param managementController the management controller
     */
    protected QuickLinkArtifactResourceProvider(AmbariManagementController managementController) {
        super(propertyIds, keyPropertyIds, managementController);
    }

    @Override
    public RequestStatus createResources(Request request) throws SystemException, UnsupportedPropertyException, ResourceAlreadyExistsException,
            NoSuchParentResourceException {
        throw new UnsupportedOperationException("Creating of quick links is not supported");
    }

    @Override
    public Set<Resource> getResources(Request request, Predicate predicate) throws SystemException, UnsupportedPropertyException, NoSuchResourceException,
            NoSuchParentResourceException {

        Set<Resource> resources = new LinkedHashSet<Resource>();

        LOG.info("request -> "+request);
        LOG.info("predicate -> "+predicate);
        LOG.info("pkPropertyIds -> "+pkPropertyIds);
        LOG.info("keyPropertyIds -> "+keyPropertyIds);

        resources.addAll(getQuickLinks(request, predicate));
        // add other artifacts types here

        if (resources.isEmpty()) {
            throw new NoSuchResourceException(
                    "The requested resource doesn't exist: QuickLink not found, " + predicate);
        }

        return resources;

    }

    @Override
    public RequestStatus updateResources(Request request, Predicate predicate) throws SystemException, UnsupportedPropertyException, NoSuchResourceException,
            NoSuchParentResourceException {
        throw new UnsupportedOperationException("Updating of quick links is not supported");
    }

    @Override
    public RequestStatus deleteResources(Predicate predicate) throws SystemException, UnsupportedPropertyException, NoSuchResourceException, NoSuchParentResourceException {
        throw new UnsupportedOperationException("Deleting of quick links is not supported");
    }

    private Set<Resource> getQuickLinks(Request request, Predicate predicate) throws NoSuchParentResourceException,
            NoSuchResourceException, UnsupportedPropertyException, SystemException {

        Set<Resource> resources = new LinkedHashSet<Resource>();
        for (Map<String, Object> properties : getPropertyMaps(predicate)) {
            String quickLinksFileName = (String) properties.get(QUICKLINK_FILE_NAME_PROPERTY_ID);
            String stackName = (String) properties.get(STACK_NAME_PROPERTY_ID);
            String stackVersion = (String) properties.get(STACK_VERSION_PROPERTY_ID);
            String stackService = (String) properties.get(STACK_SERVICE_NAME_PROPERTY_ID);

            StackInfo stackInfo;
            try {
                stackInfo = getManagementController().getAmbariMetaInfo().getStack(stackName, stackVersion);
            } catch (AmbariException e) {
                throw new NoSuchParentResourceException(String.format(
                        "Parent stack resource doesn't exist: stackName='%s', stackVersion='%s'", stackName, stackVersion));
            }

            List<ServiceInfo> serviceInfoList = new ArrayList<ServiceInfo>();

            if (stackService == null) {
                serviceInfoList.addAll(stackInfo.getServices());
            } else {
                LOG.debug("stackService -> " + stackService);  // stackService -> MESOS

                consulQuickLinks= new DynamicQuickLinks();

                ServiceInfo service = stackInfo.getService(stackService);
                if (service == null) {
                    throw new NoSuchParentResourceException(String.format(
                            "Parent stack/service resource doesn't exist: stackName='%s', stackVersion='%s', serviceName='%s'",
                            stackName, stackVersion, stackService));
                }
                LOG.debug("service -> " + service);    //service -> Service name:MESOS
//                serviceInfoList.remove(service);
                serviceInfoList.add(service);
                LOG.info("serviceInfoList -> "+serviceInfoList);    // serviceQuickLinks -> [QuickLinksConfigurationInfo{deleted=false, fileName='quicklinks.json', consulUrl='http://lo...
            }

            for (ServiceInfo serviceInfo : serviceInfoList) {
                List<QuickLinksConfigurationInfo> serviceQuickLinks = new ArrayList<QuickLinksConfigurationInfo>();
                if (quickLinksFileName != null) {
                    LOG.debug("Getting quick links from service {}, quick links = {}", serviceInfo.getName(), serviceInfo.getQuickLinksConfigurationsMap());
                    LOG.info("getQuickLinksConfigurationsMap -> "+serviceInfo.getQuickLinksConfigurationsMap());
//                    serviceQuickLinks.remove(serviceInfo.getQuickLinksConfigurationsMap().get(quickLinksFileName));
                    serviceQuickLinks.add(serviceInfo.getQuickLinksConfigurationsMap().get(quickLinksFileName));
                } else {
                    for (QuickLinksConfigurationInfo quickLinksConfigurationInfo : serviceInfo.getQuickLinksConfigurationsMap().values()) {
                        LOG.info("quickLinksConfigurationInfo -> " + quickLinksConfigurationInfo);

//                        QuickLinksConfiguration qlConf = new QuickLinksConfiguration();
//                        qlConf.setLinks(consulQuickLinks.getQuickLinks(stackName, stackVersion, stackService).getLinks());
//                        QuickLinks ql = new QuickLinks();
//                        ql.setQuickLinksConfiguration(ql.getQuickLinksConfiguration());
//                        LOG.info("mapQl0 - > "+ql);
//                        Map<String, QuickLinks> mapQl = new HashMap<String, QuickLinks>();
//                        mapQl.put("consul", ql);
//                        LOG.info("mapQl1 - > "+mapQl);
//                        quickLinksConfigurationInfo.setQuickLinksConfigurationMap(mapQl);
//                        LOG.info("mapQl2 - > "+quickLinksConfigurationInfo);

                        if (quickLinksConfigurationInfo.getIsDefault()) {
//                            serviceQuickLinks.remove(0);
                            serviceQuickLinks.add(0, quickLinksConfigurationInfo );
                        } else {
//                            serviceQuickLinks.remove(quickLinksConfigurationInfo);
                            serviceQuickLinks.add(quickLinksConfigurationInfo);
                        }
                    }
                }

                ////

                ////
                LOG.info("serviceQuickLinks -> " + serviceQuickLinks);

                List<Resource> serviceResources = new ArrayList<Resource>();
                for (QuickLinksConfigurationInfo quickLinksConfigurationInfo : serviceQuickLinks) {
                    Resource resource = new ResourceImpl(Resource.Type.QuickLink);
                    Set<String> requestedIds = getRequestPropertyIds(request, predicate);
                    setResourceProperty(resource, QUICKLINK_FILE_NAME_PROPERTY_ID, quickLinksConfigurationInfo.getFileName(), requestedIds);
                    setResourceProperty(resource, QUICKLINK_CONSUL_URL_PROPERTY_ID, quickLinksConfigurationInfo.getConsulUrl(), requestedIds);
                    setResourceProperty(resource, QUICKLINK_CONSUL_KEY_PROPERTY_ID, quickLinksConfigurationInfo.getConsulKey(), requestedIds);
                    setResourceProperty(resource, QUICKLINK_DATA_PROPERTY_ID, quickLinksConfigurationInfo.getQuickLinksConfigurationMap(), requestedIds);
                    setResourceProperty(resource, QUICKLINK_DEFAULT_PROPERTY_ID, quickLinksConfigurationInfo.getIsDefault(), requestedIds);
                    setResourceProperty(resource, STACK_NAME_PROPERTY_ID, stackName, requestedIds);
                    setResourceProperty(resource, STACK_VERSION_PROPERTY_ID, stackVersion, requestedIds);
                    setResourceProperty(resource, STACK_SERVICE_NAME_PROPERTY_ID, serviceInfo.getName(), requestedIds);
                    serviceResources.add(resource);
                }

                resources.addAll(serviceResources);
            }
        }
        LOG.info("resources -> "+ resources);
        return resources;
    }

    @Override
    protected Set<String> getPKPropertyIds() {
        return null;
    }

}
