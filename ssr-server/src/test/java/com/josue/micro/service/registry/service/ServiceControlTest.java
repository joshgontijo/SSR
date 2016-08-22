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
        control.register("id", "instanceId", null);
    }

    @Test(expected = ServiceException.class)
    public void registerNoName() throws Exception {
        control.register("id", "instanceId", new Service(null));
    }

    @Test(expected = ServiceException.class)
    public void registerInstances() throws Exception {
        Service service = new Service("serviceA");
        control.register("id", "instanceId",  service);
    }

    @Test(expected = ServiceException.class)
    public void registerNoAddress() throws Exception {
        Service service = new Service("serviceA");
        service.getInstances().add(new ServiceInstance());

        control.register("id", service);
    }

    @Test
    public void register() throws Exception {
        String serviceName = "serviceA";
        Service service = registerService("id", serviceName);
        ServiceInstance instance = firstInstance(service);
        assertNotNull(instance.getSince());

        Set<Service> services = control.getServices();
        Service byName = getByName(services, serviceName);
        assertEquals(1, services.size());
        assertEquals(instance, firstInstance(byName));
    }

    @Test
    public void registerExistingLink() throws Exception {
        String serviceName = "sourceService";

        String sourceId = "id-123";
        Service source = registerService(sourceId, serviceName);
        Service target = registerService("id-456", "targetService");

        control.addLink(sourceId, target);

        ServiceInstance newInstance = new ServiceInstance();
        newInstance.setAddress(source.getInstances().iterator().next().getAddress());
        Service config = new Service(serviceName);
        config.getInstances().add(newInstance);

        control.register("another-id", config);

        Service found = first(control.getServices(serviceName));

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
        Service service = registerService("id-123", serviceName);
        ServiceInstance instance = firstInstance(service);


        Set<Service> services = control.getServices(null);
        Service byName = getByName(services, serviceName);
        assertEquals(1, services.size());
        assertEquals(1, byName.getInstances().size());
        assertEquals(instance, firstInstance(byName));
    }

    @Test
    public void getServices() throws Exception {
        String serviceName = "serviceA";
        Service service = registerService("id-123", serviceName);

        Set<Service> services = control.getServices(serviceName);
        Service byName = getByName(services, serviceName);
        assertEquals(1, services.size());
        assertEquals(service, byName);
        assertEquals(1, byName.getInstances().size());
    }


    @Test
    public void deregister() throws Exception {
        String id = "id-123";
        String serviceName = "serviceA";
        registerService(id, serviceName);

        Service deregistered = control.deregister(id);
        assertFalse(firstInstance(deregistered).isAvailable());

        Set<Service> found = control.getServices();
        assertEquals(1, found.size());
        assertFalse(firstInstance(first(found)).isAvailable());
    }

    @Test(expected = ServiceException.class)
    public void addLinkTargetNotFound() throws Exception {
        String id = "id-123";
        String sourceService = "sourceService";
        registerService(id, sourceService);

        Service service = new Service("targetService");

        control.addLink("anotherId", service);
    }

    @Test
    public void addLink() throws Exception {
        String id = "id-123";
        String sourceService = "sourceService";
        registerService(id, sourceService);

        String targetService = "targetService";
        Service service = registerService("id-123344", targetService);

        Service updated = control.addLink(id, service);
        assertEquals(1, updated.getLinks().size());
        assertEquals(targetService, updated.getLinks().iterator().next());

        Set<Service> found = control.getServices(sourceService);
        Service byName = getByName(found, sourceService);

        assertEquals(targetService, byName.getLinks().iterator().next());
    }

    @Test
    public void addLinkDisabledService() throws Exception {
        String id = "id-123";
        String sourceService = "sourceService";
        Service source = registerService(id, sourceService);
        registerService("second-id-123", sourceService);

        String targetService = "targetService";
        Service instance = registerService("id-123344", targetService);

        control.deregister(id);

        Service updated = control.addLink(id, instance);
        assertEquals(1, updated.getLinks().size());
        assertEquals(targetService, updated.getLinks().iterator().next());

        Set<Service> found = control.getServices().stream()
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
        Collection<Service> found = control.getServices();
        assertEquals(1, found.size());

        control.deleteUnavailableNodes(serviceName);
        found = control.getServices();
        assertTrue(found.isEmpty());
    }

    private Service registerService(String id, String serviceName) throws ServiceException {
        Service service = new Service(serviceName);
        ServiceInstance instance = new ServiceInstance();
        instance.setAddress("http://localhost:8080/" + UUID.randomUUID().toString().substring(0, 4));

        service.getInstances().add(instance);

        return control.register(id, service);
    }

    private Service getByName(Set<Service> services, String name) {
        return services.stream().filter(s -> s.getName().equals(name)).findFirst().get();
    }

    private Service first(Set<Service> services) {
        return services.iterator().next();
    }

    private ServiceInstance firstInstance(Service service) {
        return service.getInstances().iterator().next();
    }
}