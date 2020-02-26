package com.nvish.awsCSConnector.core.services.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomainClient;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.amazonaws.services.cloudsearchv2.AbstractAmazonCloudSearch;
import com.amazonaws.services.cloudsearchv2.AmazonCloudSearchClient;
import com.amazonaws.services.cloudsearchv2.model.*;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nvish.awsCSConnector.core.constants.GlobalConstants;
import com.nvish.awsCSConnector.core.services.AWSIndexService;
import com.nvish.awsCSConnector.core.services.AWSSearchConfigService;
import com.nvish.awsCSConnector.core.utilities.CommonUtil;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jsoup.Jsoup;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component(
        immediate = true,
        service = AWSIndexService.class
)
public class AWSIndexServiceImpl extends AbstractAmazonCloudSearch implements AWSIndexService {

//        AWSSearchConfigServiceImpl awsSearchConfigService = new AWSSearchConfigServiceImpl();
@Reference
private AWSSearchConfigService awsSearchConfigService;
    static final Logger LOG = LoggerFactory.getLogger(AWSIndexServiceImpl.class);

    private String awsSearchUrl  = AWSSearchConfigServiceImpl.awsDomainName;

    private String awsDomainName = AWSSearchConfigServiceImpl.awsDomainName;

    private String awsAccessKey = AWSSearchConfigServiceImpl.awsAccessKey;

    private String awsAccessSecret = AWSSearchConfigServiceImpl.awsAccessSecret;
    JsonObject childJson = new JsonObject();
    StringBuffer contentOfPage ;

@Override
    public JsonObject getJsonUpload(ResourceResolver resourceResolver, Page parentPage, String includeChildren, String operation,Map hashMap) {


        JsonObject jsonUplaodStatus = new JsonObject();

        try {


            if (parentPage != null) {
                JsonArray json;
                json = getIndexJson(resourceResolver, parentPage, includeChildren, operation,hashMap);

                long jsonLength = 0;
                if (json != null && json.size() > 0) {
                    for (int i = 0; i < json.size(); i++) {
                        JsonElement elem = json.get(i);
                        JsonObject obj = elem.getAsJsonObject();
                        if (obj != null && obj.get("fields") != null) {
                            JsonObject fieldsObj = obj.get("fields").getAsJsonObject();
                            if (fieldsObj != null) {
                                if (fieldsObj.get("title") != null) {
                                    String title = fieldsObj.get("title").getAsString();
                                    jsonLength = jsonLength + title.getBytes("UTF-8").length
                                            - title.toCharArray().length;
                                }
                                if (fieldsObj.get("description") != null) {
                                    String description = fieldsObj.get("description").getAsString();
                                    jsonLength = jsonLength + description.getBytes("UTF-8").length
                                            - description.toCharArray().length;
                                }
                                if (fieldsObj.get("content_of_page") != null) {
                                    String content_of_page = fieldsObj.get("content_of_page").getAsString();
                                    jsonLength = jsonLength + content_of_page.getBytes("UTF-8").length
                                            - content_of_page.toCharArray().length;
                                }
                            }
                        }

                    }
                }

                String amazonDocJson = json.toString();
                LOG.info("amazonDocJson : " + amazonDocJson);
                LOG.info("JSON Length : " + jsonLength);
                uploadAwsDocument(amazonDocJson, jsonLength);

            }


            jsonUplaodStatus.addProperty("status", "success");
            return jsonUplaodStatus;

        } catch (LoginException loginException) {
            jsonUplaodStatus.addProperty("status", "failure");
            LOG.error("Login exception occured::" + loginException.getMessage());
            return jsonUplaodStatus;

        } catch (Exception e) {
            jsonUplaodStatus.addProperty("status", "failure");
            LOG.error("Error while Uploading document :", e);
            return jsonUplaodStatus;

        }

    }

@Override
    public void uploadAwsDocument(String amazonDocJson, long jsonLength)
            throws IOException {
    String awsDomainName = awsSearchConfigService.getDomainName();
    String awsAccessKey = awsSearchConfigService.getAccessKey();
    String awsAccessSecret = awsSearchConfigService.getSecertKey();
        LOG.info("*****awsDomainName: " + awsDomainName +"******AWS AccessKey: "+awsAccessKey+"****AWS Secret Key: "+awsAccessSecret);
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsAccessSecret);
        AmazonCloudSearchDomainClient domain = new AmazonCloudSearchDomainClient(credentials);
        DescribeDomainsRequest describeDomainsRequest = new DescribeDomainsRequest();
    describeDomainsRequest.withDomainNames(awsDomainName);
    DescribeDomainsResult describeDomainsResult  = new DescribeDomainsResult();



    AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsAccessSecret);;
    AmazonCloudSearchClient client = new AmazonCloudSearchClient(awsCredentials);
    describeDomainsResult = client.describeDomains(describeDomainsRequest);
    String endpoint = describeDomainsResult.getDomainStatusList().get(0).getDocService().getEndpoint().toString();
    domain.setEndpoint(endpoint);
    LOG.info("************ endpoint: "+  endpoint);

    describeDomainsResult.getDomainStatusList().forEach(domainStatus ->LOG.info("**** dn namin: "+ domainStatus.getDocService().toString()));
    String domainName = client.describeDomains().getDomainStatusList().get(0).getDomainName();
        InputStream docAsStream = new ByteArrayInputStream(amazonDocJson.getBytes("UTF-8"));
        UploadDocumentsRequest documentUploadRequest = new UploadDocumentsRequest().withDocuments(docAsStream)
                .withContentLength(new Long(amazonDocJson.length() + jsonLength))
                .withContentType(GlobalConstants.CONTENT_JSON_TYPE);
        UploadDocumentsResult result = domain.uploadDocuments(documentUploadRequest);
        LOG.info("Document result status: " + result);

    }

    @Override
    public Map<String,String> getIndexOptions()
            throws IOException {


        LOG.info("*****Get Index Option: " );
        String awsDomainName = awsSearchConfigService.getDomainName();
        String awsAccessKey = awsSearchConfigService.getAccessKey();
        String awsAccessSecret = awsSearchConfigService.getSecertKey();

        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsAccessSecret);;
        AmazonCloudSearchClient client = new AmazonCloudSearchClient(awsCredentials);
        LOG.info("*********list of domain: "+client.describeDomains().getDomainStatusList().size());

        DescribeDomainsRequest describeDomainsRequest = new DescribeDomainsRequest();
        LOG.info("**** Domain Request: "+ describeDomainsRequest.withDomainNames());
        DescribeIndexFieldsRequest describeIndexFieldsRequest = new DescribeIndexFieldsRequest();
        describeIndexFieldsRequest.setDomainName(awsDomainName);
        DescribeIndexFieldsResult  describeIndexFieldsResult= client.describeIndexFields(describeIndexFieldsRequest);
        List<IndexFieldStatus> indexFieldNameList= describeIndexFieldsResult.getIndexFields();
        final Map hashMap = new HashMap<>();
        for (final IndexFieldStatus fieldName : indexFieldNameList) {
            hashMap.put(fieldName.getOptions().getIndexFieldName(), fieldName.getOptions().getIndexFieldType());
        }

        LOG.info("*********  map: "+hashMap.size());

        LOG.info("**** describeIndexFieldsRequest Sizw:"+  describeIndexFieldsResult.getIndexFields().size());
        return hashMap;

    }

   @Override
    public JsonArray getIndexJson(ResourceResolver resourceResolver, Page parentPage, String includeChildren, String operation, Map hashMap) throws Exception {
        JsonArray indexJson = new JsonArray();
        if (operation.equalsIgnoreCase(GlobalConstants.ADD)) {
            indexJson = createAddIndexJson(resourceResolver, parentPage, includeChildren, hashMap);

        } else {
            indexJson = deleteIndexJson(parentPage, includeChildren);

        }

        return indexJson;
    }
@Override
    public JsonArray deleteIndexJson(Page parentPage, String includeChildren) {
        JsonArray indexJson = new JsonArray();

        if (includeChildren != null && includeChildren.equalsIgnoreCase(GlobalConstants.YES)) {

            if (parentPage.getPath().split("/").length > 5) {
                JsonObject indexobject = new JsonObject();
                indexobject.addProperty("type", GlobalConstants.DELETE);
                indexobject.addProperty("id", getResourceId(parentPage));
                if (indexobject != null) {
                    indexJson.add(indexobject);
                }
            }

            Iterator<Page> rootPageIterator = parentPage.listChildren(null, true);

            if (rootPageIterator != null) {
                while (rootPageIterator.hasNext()) {
                    Page page = rootPageIterator.next();
                    JsonObject indexobject = new JsonObject();
                    indexobject.addProperty("type", GlobalConstants.DELETE);
                    indexobject.addProperty("id", getResourceId(page));
                    if (indexobject != null) {
                        indexJson.add(indexobject);
                    }
                }
            }
        } else {
            JsonObject indexobject = new JsonObject();
            indexobject.addProperty("type", GlobalConstants.DELETE);
            indexobject.addProperty("id", getResourceId(parentPage));
            if (indexobject != null) {
                indexJson.add(indexobject);
            }
        }

        return indexJson;
    }
@Override
    public JsonArray createAddIndexJson(ResourceResolver resourceResolver, Page parentPage, String includeChildren, Map hashMap) throws Exception {

        JsonArray indexJson = new JsonArray();

        if (includeChildren.equalsIgnoreCase(GlobalConstants.YES)) {

LOG.info("********* Get page path Length: "+parentPage.getPath().split("/").length);
            {
                JsonObject object = getResourceAWSIndexProperties(resourceResolver, parentPage, hashMap);
                if (object != null) {
                    indexJson.add(object);
                }
            }
            Iterator<Page> rootPageIterator = parentPage.listChildren(null, true);

            if (rootPageIterator != null) {
                while (rootPageIterator.hasNext()) {
                    Page page = rootPageIterator.next();

                    JsonObject object = getResourceAWSIndexProperties(resourceResolver, page, hashMap);
                    if (object != null) {
                        indexJson.add(object);
                    }
                }
            }
        } else {
           JsonObject  object = getResourceAWSIndexProperties(resourceResolver, parentPage, hashMap);
            if (object != null) {
                indexJson.add(object);
            }
        }

        return indexJson;

    }
@Override
    public JsonObject getResourceAWSIndexProperties(ResourceResolver resourceResolver, Page page,Map hashMap) throws Exception {

    JsonObject object = null;
        ReplicationStatus replicationStatus = page.adaptTo(ReplicationStatus.class);
        //	if (replicationStatus != null && replicationStatus.isActivated()) {
        ValueMap map = page.getProperties();
        Node contentPageNode = page.adaptTo(Node.class);
    childJson = new JsonObject();
    contentOfPage = new StringBuffer();
    findContent(contentPageNode,hashMap);
        object = new JsonObject();
        object.addProperty("type", GlobalConstants.ADD);
        object.addProperty("id", getResourceId(page));

        String path = page.getPath();


        GregorianCalendar releaseDate = map.get(GlobalConstants.CQ_LASTREPLICATED) != null ? (GregorianCalendar) map
                .get(GlobalConstants.CQ_LASTREPLICATED) : null;


        Resource r = resourceResolver.getResource(page.getPath() + "/jcr:content");
        Node n = r.adaptTo(Node.class);
        AssetReferenceSearch ref = new AssetReferenceSearch(n, DamConstants.MOUNTPOINT_ASSETS, resourceResolver);
        List<String> damAssets = new ArrayList<String>();

        /***********Reading each Node of a Page********/

        for (Map.Entry<String, Asset> assetMap : ref.search().entrySet()) {
            String asset = assetMap.getValue().getName();
            damAssets.add(asset);
        }

    Gson gson = CommonUtil.getGSONInstance();
    String contentOfPageString = gson.toJson(contentOfPage.toString().replaceAll("\\'", "\\\\'").replaceAll("\"", " ").replaceAll("[^\\u0009\\u000a\\u000d\\u0020-\\uD7FF\\uE000-\\uFFFD]", ""));
    JsonElement contentOfPageElement = gson.fromJson(contentOfPageString, JsonElement.class);
    if(hashMap.containsKey("content_of_page")){

        childJson.addProperty("content_of_page",contentOfPageString);
    }
        childJson.addProperty("url", page.getPath() + GlobalConstants.HTML_EXTENSION);

        object.add("fields", childJson);

        //	}

        return object;

    }

    public void findContent(Node node, Map hashMap) throws Exception {
        NodeIterator nItr = node.getNodes();
        LOG.info("*** Node test: "+node.getPath());
        LOG.info("*** Node child Pages: "+nItr.getSize());

        while (nItr.hasNext()) {
            Node innerNode = (Node) nItr.next();
        if(innerNode.getPath().contains("jcr:content")){
            LOG.info("***** Inner Node: " + innerNode.getPath().toString());
            PropertyIterator prit = innerNode.getProperties();
            if (prit != null) {
                while (prit.hasNext()) {
                    Property property = (Property) prit.next();
                    String[] propertyNameArray = property.getName().split(":");
                    String propertyName = property.getName().toLowerCase();
                    LOG.info("***********NAME:::: " + innerNode.getName());
                    if (propertyNameArray.length > 1) {
                        propertyName = propertyNameArray[1].toLowerCase();
                    }

                    LOG.info("**** propertyNameArray: " + propertyName);
                    LOG.info("**** hashmap: " + hashMap.containsKey(propertyName));
                    if (property.isMultiple()) {
                        final Value[] values = property.getValues();
                        for (final Value value : values) {
                            String str = value.getString();
                            String htmlStr = Jsoup.parse(str).text().toString();
                            htmlStr = htmlStr.replaceAll("(?s)<[^>,]*>(\\s*<[^>,]*>)*", " ");
                            if (hashMap.containsKey(propertyName) && !hashMap.containsKey("content_of_page")) {
                                LOG.info("********** property Matching with hashMap: " + propertyName);
                                childJson.addProperty(propertyName, htmlStr.toString());
                                LOG.info("********* String"+innerNode.getPath() +"\t->\t" + propertyName +"  html: "+ htmlStr);
                            }
                            if(hashMap.containsKey("content_of_page"))
                                contentOfPage.append(" "+ htmlStr);
                        }
                    } else {

                        String str = property.getValue().getString();
                        String htmlStr = Jsoup.parse(str).text().toString();
                        htmlStr = htmlStr.replaceAll("(?s)<[^>,]*>(\\s*<[^>,]*>)*", " ");
                        if (hashMap.containsKey(propertyName)&& !hashMap.containsKey("content_of_page")) {
                            LOG.info("********** property Matching with hashMap: " + propertyName);
                            childJson.addProperty(propertyName.toString(), htmlStr.toString());
                        }
                        if(hashMap.containsKey("content_of_page"))
                          contentOfPage.append(" "+ htmlStr);
                    }
                }
            }
        }
        else{
            break;
        }
        findContent(innerNode,hashMap);
        }
    }

    @Override
    public String getResourceId(Page page) {

        String path = page.getPath();
        String pathArr[] = path.split("/");
//		String locale = pathArr[3] + "_" + pathArr[4];
//		if (pathArr.length > 4) {
//			for (int i = 5; i < pathArr.length; i++) {
//				locale = locale + "_" + pathArr[i];
//			}
//		}
        String docUniqueId = "aws" +path+ "_id";
        docUniqueId = docUniqueId.replace("__", "_");
        docUniqueId = docUniqueId.replace(" ", "_");

        return docUniqueId;
    }


}
