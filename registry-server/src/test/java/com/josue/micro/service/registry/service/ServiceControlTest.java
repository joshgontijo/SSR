package com.josue.micro.service.registry.service;

import com.josue.micro.service.registry.ServiceException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
        control.register("id", new ServiceConfig());
    }

    @Test(expected = ServiceException.class)
    public void registerAddress() throws Exception {
        String serviceName = "serviceA";
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setName(serviceName);

        control.register("id", new ServiceConfig());
    }

    @Test
    public void register() throws Exception {
        ServiceConfig serviceConfig = registerService("id", "serviceA");
        assertNotNull(serviceConfig.getSince());

        Collection<ServiceConfig> services = control.getServices();
        assertEquals(1, services.size());
        assertEquals(serviceConfig, services.iterator().next());
    }

    @Test
    public void registerExistingLink() throws Exception {
        String serviceName = "sourceService";

        ServiceConfig source = registerService("id-123", serviceName);
        ServiceConfig target = registerService("id-456", "targetService");

        control.addLink(source.getId(), target);

        ServiceConfig newService = new ServiceConfig();
        newService.setName(serviceName);
        newService.setAddress("http://localhost:8080/serviceA");
        control.register("second-instance-id-123", newService);

        Collection<ServiceConfig> services = control.getServices(serviceName).get(serviceName);
        assertEquals(1, services.size());

        ServiceConfig found = services.iterator().next();
        assertEquals(newService, found);
        assertEquals(1, found.getLinks().size());
        assertEquals(target.getName(), found.getLinks().iterator().next());
    }

    @Test
    public void getAllServices() throws Exception {
        String serviceName = "serviceA";
        ServiceConfig scfg = registerService("id-123", serviceName);

        Map<String, List<ServiceConfig>> services = control.getServices(null);
        assertEquals(1, services.size());
        assertEquals(1, services.get(serviceName).size());
        assertEquals(scfg, services.get(serviceName).get(0));
    }

    @Test
    public void getServices() throws Exception {
        String serviceName = "serviceA";
        ServiceConfig scfg = registerService("id-123", serviceName);

        Map<String, List<ServiceConfig>> services = control.getServices(serviceName);
        assertEquals(1, services.size());
        assertEquals(1, services.get(serviceName).size());
        assertEquals(scfg, services.get(serviceName).get(0));
    }


    @Test
    public void deregister() throws Exception {
        String id = "id-123";
        String serviceName = "serviceA";
        registerService(id, serviceName);

        ServiceConfig deregistered = control.deregister(id);
        assertFalse(deregistered.isAvailable());

        Collection<ServiceConfig> found = control.getServices();
        assertEquals(1, found.size());
        assertFalse(found.iterator().next().isAvailable());
    }

    @Test(expected = ServiceException.class)
    public void addLinkTargetNotFound() throws Exception {
        String id = "id-123";
        String sourceService = "sourceService";
        registerService(id, sourceService);

        String targetService = "targetService";
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setName(targetService);

        control.addLink(id, serviceConfig);
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

        Map<String, List<ServiceConfig>> found = control.getServices(sourceService);
        assertEquals(targetService, found.get(sourceService).get(0).getLinks().iterator().next());
    }

    @Test
    public void addLinkDisabledService() throws Exception {
        String id = "id-123";
        String sourceService = "sourceService";
        ServiceConfig source = registerService(id, sourceService);

        String targetService = "targetService";
        ServiceConfig serviceConfig = registerService("id-123344", targetService);

        control.deregister(source.getId());

        ServiceConfig updated = control.addLink(id, serviceConfig);
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

    private ServiceConfig registerService(String id, String serviceName) throws ServiceException {
        ServiceConfig scfg = new ServiceConfig();
        scfg.setName(serviceName);
        scfg.setAddress("http://localhost:8080/serviceA");
        return control.register(id, scfg);
    }
}