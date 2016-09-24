package com.josue.micro.registry.client.config;

import org.junit.After;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Josue on 27/08/2016.
 */
public class PropertiesManagerTest {

    PropertiesManager propertiesManager;

    private static final int DEFAULT_SERVER_PORT = 9090;

    @After
    public void cleanUp(){
        Properties properties = System.getProperties();
        properties.remove(PropertiesManager.REGISTRY_HOST);
        properties.remove(PropertiesManager.REGISTRY_PORT);
        properties.remove(PropertiesManager.ENVIRONMENT_SELECTOR);
        properties.remove(PropertiesManager.IS_AWS);
        properties.remove(PropertiesManager.PROPERTIES_FILE_NAME);
        properties.remove(PropertiesManager.SERVICE_HOST);
        properties.remove(PropertiesManager.SERVICE_PORT);
        properties.remove(PropertiesManager.USE_HOST);
    }

    @Test
    public void defaultsWithoutFile() throws Exception {
        String dummy = "dummy-env";
        System.setProperty(PropertiesManager.ENVIRONMENT_SELECTOR, dummy);
        propertiesManager = new PropertiesManager();

        assertFalse(propertiesManager.isAws());
        assertNotNull(propertiesManager.getServiceHost());
        assertEquals(8080, propertiesManager.getServicePort());
        assertNotNull(propertiesManager.getRegistryHost());
        assertEquals(DEFAULT_SERVER_PORT, propertiesManager.getRegistryPort());
        assertEquals(dummy, propertiesManager.getEnvironment());
        assertTrue(propertiesManager.useHostname());
    }

    @Test
    public void propertyOverride() throws Exception {
        String dummy = "dummy-env";
        String hostOverride = "my-host-override";
        System.setProperty(PropertiesManager.ENVIRONMENT_SELECTOR, dummy);
        System.setProperty(PropertiesManager.SERVICE_HOST, hostOverride);
        propertiesManager = new PropertiesManager();

        assertFalse(propertiesManager.isAws());
        assertEquals(hostOverride, propertiesManager.getServiceHost());
        assertEquals(8080, propertiesManager.getServicePort());
        assertNotNull(propertiesManager.getRegistryHost());
        assertEquals(DEFAULT_SERVER_PORT, propertiesManager.getRegistryPort());
        assertEquals(dummy, propertiesManager.getEnvironment());
    }

    @Test
    public void fromFile_default_env() throws Exception {
        System.setProperty(PropertiesManager.ENVIRONMENT_SELECTOR, "");
        propertiesManager = new PropertiesManager();//do not extract to init, it needs to load after System.setProperty

        assertFalse(propertiesManager.isAws());
        assertEquals("MY-SERVICE-HOST", propertiesManager.getServiceHost());
        assertEquals(4321, propertiesManager.getServicePort());
        assertEquals("MY-REGISTRY-HOST", propertiesManager.getRegistryHost());
        assertEquals(1234, propertiesManager.getRegistryPort());
        assertEquals("default", propertiesManager.getEnvironment());
    }

    @Test
    public void fromFile_with_env() throws Exception {
        String env = "dev";
        System.setProperty(PropertiesManager.ENVIRONMENT_SELECTOR, env);
        propertiesManager = new PropertiesManager();//do not remove, it needs to load after System.setProperty

        assertFalse(propertiesManager.isAws());
        assertEquals("MY-SERVICE-HOST-DEV", propertiesManager.getServiceHost());
        assertEquals(2222, propertiesManager.getServicePort());
        assertEquals("MY-REGISTRY-HOST-DEV", propertiesManager.getRegistryHost());
        assertEquals(1111, propertiesManager.getRegistryPort());
        assertEquals(env, propertiesManager.getEnvironment());
        assertFalse(propertiesManager.useHostname());
    }

    @Test
    public void fromFileWithEnv_propertyOverride() throws Exception {
        String env = "dev";
        String hostOverride = "my-host-override";
        System.setProperty(PropertiesManager.ENVIRONMENT_SELECTOR, env);
        System.setProperty(PropertiesManager.REGISTRY_HOST, hostOverride);
        System.setProperty(PropertiesManager.USE_HOST, hostOverride);
        propertiesManager = new PropertiesManager();//do not remove, it needs to load after System.setProperty

        assertFalse(propertiesManager.isAws());
        assertEquals("MY-SERVICE-HOST-DEV", propertiesManager.getServiceHost());
        assertEquals(2222, propertiesManager.getServicePort());
        assertEquals(hostOverride, propertiesManager.getRegistryHost());
        assertEquals(1111, propertiesManager.getRegistryPort());
        assertEquals(env, propertiesManager.getEnvironment());
        assertFalse(propertiesManager.useHostname());
    }


}