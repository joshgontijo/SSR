package com.josue.micro.service.registry.service;

import com.josue.micro.service.registry.ServiceException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Josue on 09/07/2016.
 */
public class ServiceControlTest {

    private ServiceControl control = new ServiceControl();

    @Before
    public void init() {
        control.store.clear();
    }


    @Test(expected = ServiceException.class)
    public void registerInvalidServiceConfig() throws Exception {
        control.register("id", null);
    }

    @Test(expected = ServiceException.class)
    public void registerNoName() throws Exception {
        control.register("id", new ServiceConfig(null));
    }

    @Test(expected = ServiceException.class)
    public void registerInstances() throws Exception {
        ServiceConfig serviceConfig = new ServiceConfig("serviceA");
        control.register("id", serviceConfig);
    }

    @Test(expected = ServiceException.class)
    public void registerNoAddress() throws Exception {
        ServiceConfig serviceConfig = new ServiceConfig("serviceA");
        serviceConfig.getInstances().add(new ServiceInstance());

        control.register("id", serviceConfig);
    }

    @Test
    public void register() throws Exception {
        String serviceName = "serviceA";
        ServiceConfig serviceConfig = registerService("id", serviceName);
        ServiceInstance instance = firstInstance(serviceConfig);
        assertNotNull(instance.getSince());

        Set<ServiceConfig> services = control.getServices();
        ServiceConfig byName = getByName(services, serviceName);
        assertEquals(1, services.size());
        assertEquals(instance, firstInstance(byName));
    }

    @Test
    public void registerExistingLink() throws Exception {
        String serviceName = "sourceService";

        String sourceId = "id-123";
        ServiceConfig source = registerService(sourceId, serviceName);
        ServiceConfig target = registerService("id-456", "targetService");

        control.addLink(sourceId, target);

        ServiceInstance newInstance = new ServiceInstance();
        newInstance.setAddress(source.getInstances().iterator().next().getAddress());
        ServiceConfig config = new ServiceConfig(serviceName);
        config.getInstances().add(newInstance);

        control.register("another-id", config);

        ServiceConfig found = first(control.getServices(serviceName));

        assertEquals(config, found);//only name is checked
        assertEquals(1, found.getInstances().size());

        ServiceInstance foundInstance = firstInstance(found);
        assertEquals(newInstance, foundInstance);
        assertEquals(1, found.getLinks().size());
        assertEquals(target.getName(), found.getLinks().iterator().next());
    }

    @Test
    public void getAllServices() throws Exception {
        String serviceName = "serviceA";
        ServiceConfig serviceConfig = registerService("id-123", serviceName);
        ServiceInstance instance = firstInstance(serviceConfig);


        Set<ServiceConfig> services = control.getServices(null);
        ServiceConfig byName = getByName(services, serviceName);
        assertEquals(1, services.size());
        assertEquals(1, byName.getInstances().size());
        assertEquals(instance, firstInstance(byName));
    }

    @Test
    public void getServices() throws Exception {
        String serviceName = "serviceA";
        ServiceConfig serviceConfig = registerService("id-123", serviceName);

        Set<ServiceConfig> services = control.getServices(serviceName);
        ServiceConfig byName = getByName(services, serviceName);
        assertEquals(1, services.size());
        assertEquals(serviceConfig, byName);
        assertEquals(1, byName.getInstances().size());
    }


    @Test
    public void deregister() throws Exception {
        String id = "id-123";
        String serviceName = "serviceA";
        registerService(id, serviceName);

        ServiceConfig deregistered = control.deregister(id);
        assertFalse(firstInstance(deregistered).isAvailable());

        Set<ServiceConfig> found = control.getServices();
        assertEquals(1, found.size());
        assertFalse(firstInstance(first(found)).isAvailable());
    }

    @Test(expected = ServiceException.class)
    public void addLinkTargetNotFound() throws Exception {
        String id = "id-123";
        String sourceService = "sourceService";
        registerService(id, sourceService);

        ServiceConfig serviceConfig = new ServiceConfig("targetService");

        control.addLink("anotherId", serviceConfig);
    }

    @Test
    public void addLink() throws Exception {
        String id = "id-123";
        String sourceService = "sourceService";
        registerService(id, sourceService);

        String targetService = "targetService";
        ServiceConfig serviceConfig = registerService("id-123344", targetService);

        ServiceConfig updated = control.addLink(id, serviceConfig);
        assertEquals(1, updated.getLinks().size());
        assertEquals(targetService, updated.getLinks().iterator().next());

        Set<ServiceConfig> found = control.getServices(sourceService);
        ServiceConfig byName = getByName(found, sourceService);

        assertEquals(targetService, byName.getLinks().iterator().next());
    }

    @Test
    public void addLinkDisabledService() throws Exception {
        String id = "id-123";
        String sourceService = "sourceService";
        ServiceConfig source = registerService(id, sourceService);
        registerService("second-id-123", sourceService);

        String targetService = "targetService";
        ServiceConfig instance = registerService("id-123344", targetService);

        control.deregister(id);

        ServiceConfig updated = control.addLink(id, instance);
        assertEquals(1, updated.getLinks().size());
        assertEquals(targetService, updated.getLinks().iterator().next());

        Set<ServiceConfig> found = control.getServices().stream()
                .filter(sc -> sc.getName().equals(sourceService))
                .collect(Collectors.toSet());
        assertEquals(targetService, first(found).getLinks().iterator().next());
    }

    @Test
    public void deleteUnavailableNodes() throws Exception {
        String id = "id-123";
        String serviceName = "serviceA";
        registerService(id, serviceName);

        control.deregister(id);
        Collection<ServiceConfig> found = control.getServices();
        assertEquals(1, found.size());

        control.deleteUnavailableNodes(serviceName);
        found = control.getServices();
        assertTrue(found.isEmpty());
    }

    private ServiceConfig registerService(String id, String serviceName) throws ServiceException {
        ServiceConfig serviceConfig = new ServiceConfig(serviceName);
        ServiceInstance instance = new ServiceInstance();
        instance.setAddress("http://localhost:8080/" + UUID.randomUUID().toString().substring(0, 4));

        serviceConfig.getInstances().add(instance);

        return control.register(id, serviceConfig);
    }

    private ServiceConfig getByName(Set<ServiceConfig> serviceConfigs, String name) {
        return serviceConfigs.stream().filter(s -> s.getName().equals(name)).findFirst().get();
    }

    private ServiceConfig first(Set<ServiceConfig> serviceConfigs) {
        return serviceConfigs.iterator().next();
    }

    private ServiceInstance firstInstance(ServiceConfig serviceConfig) {
        return serviceConfig.getInstances().iterator().next();
    }
}