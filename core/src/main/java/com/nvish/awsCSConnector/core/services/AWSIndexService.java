package com.nvish.awsCSConnector.core.services;

import com.day.cq.wcm.api.Page;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import java.io.IOException;
import java.util.Map;

public interface AWSIndexService  {



    public JsonObject getJsonUpload(ResourceResolver resourceResolver, Page parentPage, String includeChildren, String operation, Map hashMap);


    public void uploadAwsDocument(String amazonDocJson, long jsonLength)
            throws IOException;

    public Map<String, String> getIndexOptions()
            throws IOException;

    public JsonArray getIndexJson(ResourceResolver resourceResolver, Page parentPage, String includeChildren, String operation, Map hashMap) throws Exception;
    public JsonArray deleteIndexJson(Page parentPage, String includeChildren);

    public JsonArray createAddIndexJson(ResourceResolver resourceResolver, Page parentPage, String includeChildren, Map hashMap) throws Exception;
    public JsonObject getResourceAWSIndexProperties(ResourceResolver resourceResolver, Page page, Map hashMap) throws Exception;


    public void findContent(Node node, Map hashMap) throws Exception;
    public String getResourceId(Page page);

}
