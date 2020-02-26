package com.nvish.awsCSConnector.core.utilities;

import com.google.gson.Gson;
import com.nvish.awsCSConnector.core.constants.GlobalConstants;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;


public class CommonUtil {

	private static final transient Logger LOG = LoggerFactory.getLogger(CommonUtil.class);
	private static Gson gson;

	private CommonUtil() {

	}

	public static String getPropertyValue(Node componentNode, String propertyName) throws RepositoryException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("In getPropertyValue method  -- propertyName: {0} componentNode : {1}", propertyName,
					componentNode);
		}
		Validate.notNull(componentNode, "componentNode is null");
		if (null != componentNode && componentNode.hasProperty(propertyName)) {
			LOG.debug("componentNode is not null and has property {}", componentNode.getProperty(propertyName)
					.getString());
			return componentNode.getProperty(propertyName).getString();
		}
		LOG.debug("Out of getPropertyValue method");
		return "";
	}

	public static Map<String, Object> getServiceUserMap() {
		LOG.info("In getServiceUserMap method");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "readService");
		if (LOG.isDebugEnabled()) {
			LOG.debug("Out of getServiceUserMap method {}", param.toString());
		}
		LOG.info("PARAM: "+param.get("sling.service.subservice"));
		return param;
	}

	
	public static void closeResourceResolver(ResourceResolver resourceResolver) {

		LOG.debug("START OF closeResourceResolver METHOD");
		if (resourceResolver != null) {
			resourceResolver.close();
		}
		LOG.debug("END OF closeResourceResolver METHOD");
	}
	
	public static String getStatusFailMessage(String message) {
		JSONObject failMessage = new JSONObject();
		try {
			failMessage.put(GlobalConstants.STATUS, GlobalConstants.FAIL);
			failMessage.put(GlobalConstants.MESSAGE, message);
		} catch (JSONException e) {
			LOG.error("In Catch block of JSON Exception of Common Util", e);
		}
		return failMessage.toString();
	}
	
	
	
	public static Map<String, String> getFacetsFieldsList(String facetFields) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(facetFields)) {
			String[] fieldsArr = facetFields.split(",");
			if (fieldsArr != null && fieldsArr.length > 0) {
				for (String facet : fieldsArr) {
					String key = facet.split("=")[0];
					String value = facet.split("=")[1];
					map.put(key.toLowerCase().trim(), value.trim());
				}
			}
		}
		return map;
	}

	/**
	 * Gets the child nodes.
	 * 
	 * @param path
	 *            the path
	 * @return the child nodes
	 * @throws RepositoryException
	 */
	public static NodeIterator getChildNodes(ResourceResolver resourceResolver, String path) throws RepositoryException {
		NodeIterator nit = null;
		Resource page = resourceResolver.getResource(path);
		if (page != null) {
			Node node = page.adaptTo(Node.class);
			if (node != null) {
				nit = node.getNodes();
			}
		}
		return nit;
	}

	public String[] getPropertyAsArray(Object obj) {
		String[] paths = { "" };
		if (obj != null) {
			if (obj instanceof String[]) {
				paths = (String[]) obj;
			} else {
				paths = new String[1];
				paths[0] = (String) obj;
			}
		}
		return paths;
	}

	/**
	 * getHostName - Retrieves the host name
	 * 
	 * @return localhost-hostname
	 */
	public static String getHostName() {
		String hostName = "";
		try {
			InetAddress ipAddr = InetAddress.getLocalHost();
			hostName = ipAddr.getHostName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
		LOG.info("hostName : " + hostName);
		return hostName;
	}
	
	public static Gson getGSONInstance() {
		if (gson == null) {
			gson = new Gson();
		}

		return gson;
	}

}
