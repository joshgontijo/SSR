package com.josue.micro.service.registry.service;

import com.josue.micro.service.registry.ServiceException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;
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
        control.register("id", new ServiceInstance());
    }

    @Test(expected = ServiceException.class)
    public void registerNoAddress() throws Exception {
        ServiceInstance instance = new ServiceInstance();
        instance.setName("serviceA");

        control.register("id", instance);
    }

    @Test
    public void register() throws Exception {
        String serviceName = "serviceA";
        ServiceInstance serviceConfig = registerService("id", serviceName);
        assertNotNull(serviceConfig.getSince());

        Set<ServiceConfig> services = control.getServices();
        ServiceConfig byName = getByName(services, serviceName);
        assertEquals(1, services.size());
        assertEquals(serviceConfig, byName.getInstances().iterator().next());
    }

    @Test
    public void registerExistingLink() throws Exception {
        String serviceName = "sourceService";

        ServiceInstance source = registerService("id-123", serviceName);
        ServiceInstance target = registerService("id-456", "targetService");

        control.addLink(source.getId(), target);

        ServiceInstance newService = new ServiceInstance();
        newService.setName(serviceName);
        newService.setAddress("http://localhost:8080/serviceA");
        control.register("second-instance-id-123", newService);

        ServiceConfig serviceConfig = getByName(control.getServices(serviceName), serviceName);
        assertEquals(1, serviceConfig.getInstances().size());

        assertEquals(newService, serviceConfig.getInstances().iterator().next());
        assertEquals(1, serviceConfig.getLinks().size());
        assertEquals(target.getName(), serviceConfig.getLinks().iterator().next());
    }

    @Test
    public void getAllServices() throws Exception {
        String serviceName = "serviceA";
        ServiceInstance instance = registerService("id-123", serviceName);

        Set<ServiceConfig> services = control.getServices(null);
        ServiceConfig serviceConfig = getByName(services, serviceName);
        assertEquals(1, services.size());
        assertEquals(1, serviceConfig.getInstances().size());
        assertEquals(instance, serviceConfig.getInstances().iterator().next());
    }

    @Test
    public void getServices() throws Exception {
        String serviceName = "serviceA";
        ServiceInstance instance = registerService("id-123", serviceName);

        Set<ServiceConfig> services = control.getServices(serviceName);
        ServiceConfig serviceConfig = getByName(services, serviceName);
        assertEquals(1, services.size());
        assertEquals(1, serviceConfig.getInstances().size());
        assertEquals(instance, serviceConfig.getInstances().iterator().next());
    }


    @Test
    public void deregister() throws Exception {
        String id = "id-123";
        String serviceName = "serviceA";
        registerService(id, serviceName);

        ServiceInstance deregistered = control.deregister(id);
        assertFalse(deregistered.isAvailable());

        Collection<ServiceConfig> found = control.getServices();
        assertEquals(1, found.size());
        assertFalse(found.iterator().next().getInstances().iterator().next().isAvailable());
    }

    @Test(expected = ServiceException.class)
    public void addLinkTargetNotFound() throws Exception {
        String id = "id-123";
        String sourceService = "sourceService";
        registerService(id, sourceService);

        String targetService = "targetService";
        ServiceInstance instance = new ServiceInstance();
        instance.setName(targetService);

        control.addLink(id, instance);
    }

    @Test
    public void addLink() throws Exception {
        String id = "id-123";
        String sourceService = "sourceService";
        registerService(id, sourceService);

        String targetService = "targetService";
        ServiceInstance instance = registerService("id-123344", targetService);

        ServiceConfig updated = control.addLink(id, instance);
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
        ServiceInstance source = registerService(id, sourceService);

        String targetService = "targetService";
        ServiceInstance instance = registerService("id-123344", targetService);

        control.deregister(source.getId());

        ServiceConfig updated = control.addLink(id, instance);
        assertEquals(1, updated.getLinks().size());
        assertEquals(targetService, updated.getLinks().iterator().next());

        Collection<ServiceConfig> found = control.getServices().stream()
                .filter(sc -> sc.getName().equals(sourceService))
                .collect(Collectors.toList());
        assertEquals(targetService, found.iterator().next().getLinks().iterator().next());
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

    private ServiceInstance registerService(String id, String serviceName) throws ServiceException {
        ServiceInstance instance = new ServiceInstance();
        instance.setName(serviceName);
        instance.setAddress("http://localhost:8080/serviceA");
        return control.register(id, instance);
    }

    private ServiceConfig getByName(Set<ServiceConfig> serviceConfigs, String name) {
        return serviceConfigs.stream().filter(s -> s.getName().equals(name)).findFirst().get();
    }
}