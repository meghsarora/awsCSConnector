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

@Component(service= Servlet.class,
		immediate = true,
		property={


				"sling.servlet.methods=" + HttpConstants.METHOD_POST,

				"sling.servlet.paths=/bin/aws/awsdeleteanalysisschemeservlet"

		})
//@SlingServlet(methods = { "POST" }, paths = { "/bin/aws/schemeservlet" })
public class AWSDeleteAnalysisSchemeServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 3617806403446339290L;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	AWSAnalysisSchemeService analysisSchemeService;

	private static final Logger LOG = LoggerFactory.getLogger(AWSDeleteAnalysisSchemeServlet.class);

	
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		LOG.info("******in AWSDeleteAnalysisSchemeServlet:  ");
		String comp = request.getParameter("comp");
		JsonObject jsonUplaodStatus = new JsonObject();

		try {

			if (comp != null && !comp.isEmpty()) {
				LOG.info("AWSDeleteAnalysisSchemeServlet schemeName** : " + comp);
				jsonUplaodStatus=analysisSchemeService.deleteAnalysisScheme(comp);
				LOG.info("JSON UPLOAD STATUS: "+jsonUplaodStatus);
				response.getWriter().print(jsonUplaodStatus.toString());
			}
			}
			catch (Exception e){

				jsonUplaodStatus.addProperty("status","failure");
				LOG.error("Error while Uploading document :", e);
				response.getWriter().print(jsonUplaodStatus.toString());
				
			}


		}


}
