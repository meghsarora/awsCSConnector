package com.nvish.awsCSConnector.core.servlets;

//import com.day.cq.wcm.api.Page;

import com.day.cq.wcm.api.Page;
import com.google.gson.JsonObject;
import com.nvish.awsCSConnector.core.services.AWSIndexService;
import com.nvish.awsCSConnector.core.utilities.CommonUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

@Component(service= Servlet.class,
		immediate = true,
		property={
				"sling.servlet.methods=POST",
				"sling.servlet.paths=/bin/aws/awssearchindexservlet"
		})
public class AWSSearchIndexServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 3617806403446339290L;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private AWSIndexService indexService;

	private static final Logger LOG = LoggerFactory.getLogger(AWSSearchIndexServlet.class);

	
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		LOG.info("******in Servlet:  ");
		String resourcePath = request.getParameter("indexPagePath");
		LOG.info("resourcePath : " + resourcePath);
		String includeChildren = "";
		includeChildren = request.getParameter("includeChildren");
		String operation = request.getParameter("operation");

//        String includeChildren = "no";
//        String operation = "Add";

		Map<String, Object> param = CommonUtil.getServiceUserMap();

		JsonObject jsonUplaodStatus = new JsonObject();

		LOG.info("includeChildren : " + includeChildren);
		LOG.info("operation : " + operation);
		try {
			ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(param);

			
			  LOG.info("***AWSIndexServlet Resource resolver : "+resourceResolver.getUserID());
			
			if (!resourcePath.isEmpty() && resourcePath != null && resourcePath.contains("content/")) {

				Resource parentResource = resourceResolver.getResource(resourcePath);
				Page parentPage = parentResource.adaptTo(Page.class);

				LOG.info("AWSIndexServlet parentPage : "+parentPage);
				Map hashMap =  indexService.getIndexOptions();
				jsonUplaodStatus = indexService.getJsonUpload(resourceResolver,parentPage, includeChildren, operation,hashMap);
		
			}
			if (resourceResolver != null) {
				resourceResolver.close();
			}
			}
			catch (Exception e){
			
				jsonUplaodStatus.addProperty("status", "failure");
				LOG.error("Error while Uploading document :", e);
				
			}
			response.getWriter().print(jsonUplaodStatus.toString());

		}
		

}
