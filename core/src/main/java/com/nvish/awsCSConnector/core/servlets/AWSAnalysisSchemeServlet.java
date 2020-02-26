package com.nvish.awsCSConnector.core.servlets;

//import com.day.cq.wcm.api.Page;

import com.google.gson.JsonObject;
import com.nvish.awsCSConnector.core.services.AWSAnalysisSchemeService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component(service= Servlet.class,
		immediate = true,
		property={


				"sling.servlet.methods=" + HttpConstants.METHOD_POST,

				"sling.servlet.paths=/bin/aws/awsanalysisschemeservlet"

		})
//@SlingServlet(methods = { "POST" }, paths = { "/bin/aws/schemeservlet" })
public class AWSAnalysisSchemeServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 3617806403446339290L;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	AWSAnalysisSchemeService awsAnalysisSchemeService;

	private static final Logger LOG = LoggerFactory.getLogger(AWSAnalysisSchemeServlet.class);

	
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		LOG.info("******in AWSSearchAnalysisSchemeServlet:  ");
		String comp = request.getParameter("comp");
		String alias = request.getParameter("Alias");
		String group = request.getParameter("Group");
		String schemeName = request.getParameter("schemeName");
		LOG.info("comp : " + comp);
		LOG.info("Alias : " + alias);
		LOG.info("Group : " + group);
		LOG.info("schemeName : " + schemeName);


		JsonObject jsonUplaodStatus = new JsonObject();

		try {

			if (comp != null && !comp.isEmpty()) {

				LOG.info("AWSSearchAnalysisSchemeServlet comp : " + comp);
//				jsonUplaodStatus = indexService.uploadAwsDocument(alias);
				jsonUplaodStatus = awsAnalysisSchemeService.getAnalysisScheme(comp);

			}

//			if (resourceResolver != null) {
//				resourceResolver.close();
//			}
			}
			catch (Exception e){

				jsonUplaodStatus.addProperty("status","failure");
				LOG.error("Error while Uploading document :", e);
				
			}
			response.getWriter().print(jsonUplaodStatus.toString());

		}


}
