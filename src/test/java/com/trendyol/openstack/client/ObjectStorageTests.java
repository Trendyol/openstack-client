package com.trendyol.openstack.client;

import com.google.common.collect.Maps;
import com.trendyol.openstack.client.config.OpenstackAuthService;
import com.trendyol.openstack.client.service.OpenstackManager;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openstack4j.model.storage.object.SwiftHeaders.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(suiteName = "Object Storage Tests")
public class ObjectStorageTests extends OpenstackAbstraction {

    private static final String JSON_CONTAINERS = "/storage/containers.json";
    private static final String NAME_BOOK = "Book";
    private static final String NAME_YEAR = "Year";
    OpenstackManager openstackManager;
    OpenstackAuthService openstackAuthService;

    @Override
    protected Service service() {
        return Service.OBJECT_STORAGE;
    }

    @BeforeSuite
    public void setUp() {

        openstackAuthService = new OpenstackAuthService(null, null);
        osv3();
        openstackAuthService.setKeyStone(keystoneToken);
        openstackManager = new OpenstackManager(openstackAuthService, null, null);
    }

    public void getObject() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put(CONTENT_LENGTH, "15");
        headers.put(CONTENT_TYPE, "application/json");
        headers.put(ETAG, "12345678901234567890");
        respondWith(headers, 200, "[\"hello world\"]");

        SwiftObject object = openstackManager.getObject("RegionOne", "test-container", "test-file");
        assertEquals(object.getContainerName(), "test-container");
        assertEquals(object.getName(), "test-file");
        assertEquals(object.getSizeInBytes(), 15);
        assertEquals(object.getMimeType(), "application/json");
        assertEquals(object.getETag(), "12345678901234567890");
    }

    public void existObject() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put(CONTENT_LENGTH, "15");
        headers.put(CONTENT_TYPE, "application/json");
        headers.put(ETAG, "12345678901234567890");
        respondWith(headers, 200, "[\"hello world\"]");

        boolean isExists = openstackManager.existsObject("RegionOne", "test-container", "test-file");
        assertThat(isExists).isTrue();
    }

    public void containerListingTest() throws Exception {
        respondWith(JSON_CONTAINERS);

        List<? extends SwiftContainer> containers = osv3().objectStorage().containers().list();
        assertEquals(2, containers.size());
        assertEquals(containers.get(0).getTotalSize(), 100);
        assertEquals(containers.get(0).getName(), "Test");
        assertEquals(containers.get(1).getName(), "marktwain");
    }

    public void containerMetadataTest() throws Exception {
        respondWith(generateContainerMetadataMap(), 204);

        Map<String, String> metadata = osv3().objectStorage().containers().getMetadata("Test");
        assertNotNull(metadata);
        assertEquals(metadata.get(NAME_YEAR), "2000");
        assertEquals(metadata.get(NAME_BOOK), "TestBook");
    }

    private Map<String, String> generateContainerMetadataMap() {
        Map<String, String> metadata = Maps.newHashMap();
        metadata.put(CONTAINER_METADATA_PREFIX + NAME_BOOK, "TestBook");
        metadata.put(CONTAINER_METADATA_PREFIX + NAME_YEAR, "2000");
        return metadata;
    }

    public void objectRetrievalTest() throws Exception {
        Map<String, String> headers = Maps.newHashMap();
        headers.put(CONTENT_LENGTH, "15");
        headers.put(CONTENT_TYPE, "application/json");
        headers.put(ETAG, "12345678901234567890");
        respondWith(headers, 200, "[\"hello world\"]");

        SwiftObject object = osv3().objectStorage().objects().get("test-container", "test-file");
        assertEquals(object.getContainerName(), "test-container");
        assertEquals(object.getName(), "test-file");
        assertEquals(object.getSizeInBytes(), 15);
        assertEquals(object.getMimeType(), "application/json");
        assertEquals(object.getETag(), "12345678901234567890");
    }
}