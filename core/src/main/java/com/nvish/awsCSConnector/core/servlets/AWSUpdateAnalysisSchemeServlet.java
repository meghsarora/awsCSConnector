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

				"sling.servlet.paths=/bin/awsupdateanalysisschemeservlet"

		})
//@SlingServlet(methods = { "POST" }, paths = { "/bin/aws/schemeservlet" })
public class AWSUpdateAnalysisSchemeServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 3617806403446339290L;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	AWSAnalysisSchemeService indexService;

	private static final Logger LOG = LoggerFactory.getLogger(AWSUpdateAnalysisSchemeServlet.class);

	
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		LOG.info("******in AWSSaveSearchAnalysisSchemeServlet:  ");
		String comp = request.getParameter("comp");
		String alias = request.getParameter("Alias");
		String group = request.getParameter("Group");
		String schemeName = request.getParameter("schemeName");
		String stopWords = request.getParameter("stopwords");
		String language = request.getParameter("language");
		String getlanguage = request.getParameter("languageupdate");
		LOG.info("***Alias : " + alias);
		LOG.info("******group: "+ group);
		LOG.info("stopWords : " + stopWords);
		LOG.info("******Language: "+ language);
		LOG.info("******getlanguage: "+ getlanguage);
//        LOG.info("******Language: "+ LanguageList.valueOf(language));

//		LOG.info("******Language: "+ Language.valueOf(language).name().toLowerCase());
		JsonObject jsonUplaodStatus = new JsonObject();

		try {

			if (schemeName != null && !schemeName.isEmpty()) {
				comp = schemeName;
				LOG.info("AWSSearchAnalysisSchemeServlet schemeName** : " + comp);
			}
			StringBuffer synonym = new StringBuffer();
			StringBuffer stopword = new StringBuffer();
			stopWords=stopWords.replace("[","").replace("]","");
			stopword.append("[");
			if (stopWords != null && !stopWords.isEmpty()) {

				stopword.append(stopWords);
			}
			stopword.append("]");
			synonym.append("{");
			if (alias != null && !alias.isEmpty()) {

				synonym.append("\"aliases\":" + alias);
			}
			else{

				synonym.append("\"aliases\": {}" );
			}
			if ((group != null && !group.isEmpty())) {
				synonym.append(",\"groups\":" + group);
			}
			else{
				synonym.append(",\"groups\": []" );
			}
			synonym.append("}");
				if(getlanguage!=null && !getlanguage.isEmpty()){
					language = getlanguage;
				}
				LOG.info("******** language after change: "+language);
					LOG.info("AWSSearchAnalysisSchemeServlet synonnym : " + synonym);
					//				jsonUplaodStatus = indexService.uploadAwsDocument(alias);
					JsonObject jsonObject = indexService.updateAnalysisScheme(comp.trim(), synonym.toString(),stopword.toString(),language);
					String status = jsonObject.get("status").toString().replace("\"", "");
					LOG.info("AWSSearchAnalysisSchemeServlet jsonObject*** :" + status);
					jsonUplaodStatus = indexService.getAnalysisScheme(comp);
					jsonUplaodStatus.addProperty("status","Success");

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
