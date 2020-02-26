package com.nvish.awsCSConnector.core.servlets;

//import com.day.cq.wcm.api.Page;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.amazonaws.services.cloudsearchv2.model.AnalysisSchemeLanguage;
import com.day.crx.JcrConstants;
import com.nvish.awsCSConnector.core.services.AWSAnalysisSchemeService;
import org.apache.abdera.i18n.rfc4646.enums.Language;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component(service= Servlet.class,
		immediate = true,
		property={
				Constants.SERVICE_DESCRIPTION + "= Service to get Analysis Scheme list",

				"sling.servlet.methods=" + HttpConstants.METHOD_GET,
				"sling.servlet.paths=/bin/analysisschemelanguagelist"
		})
public class AWSAnalysisSchemeLanguageListServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 3617806403446339290L;

	@Reference
	private AWSAnalysisSchemeService analysisSchemeService;




	private static final Logger LOG = LoggerFactory.getLogger(AWSAnalysisSchemeLanguageListServlet.class);


	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			List<KeyValue> dropDownList = new ArrayList<>();
			List<String> schemeNameLanguageList = null;
			LOG.info("******in AWSSearchAnalysisSchemeServletLanguageList:  "+ AnalysisSchemeLanguage.values().length);
			LOG.info("******in AWSSearchAnalysisSchemeServletLanguageList:  "+ AnalysisSchemeLanguage.values()[0].toString());

			schemeNameLanguageList = analysisSchemeService.getAnalysisSchemeLanguageList();
			schemeNameLanguageList.forEach(res -> {
				//ValueMap valueMap = res.getValueMap();

				dropDownList.add(new KeyValue(res, res));
			});
			@SuppressWarnings("unchecked")
			DataSource ds =
					new SimpleDataSource(
							new TransformIterator(
									dropDownList.iterator(),
									input -> {
										KeyValue keyValue = (KeyValue) input;
										ValueMap vm = new ValueMapDecorator(new HashMap<>());
										vm.put("value", keyValue.key);
										vm.put("text", keyValue.value);
										return new ValueMapResource(
												request.getResourceResolver(), new ResourceMetadata(),
												JcrConstants.NT_UNSTRUCTURED, vm);
									}));
			request.setAttribute(DataSource.class.getName(), ds);


			try {
				LOG.info("**List: " + schemeNameLanguageList.toString());
			} catch (Exception e) {

				schemeNameLanguageList.add("failure");
				LOG.error("Error while Fetching Analysis Scheme Language List :", e);

			}




		} catch (Exception e) {

			LOG.error("Error in Get Drop Down Values", e);

		}


	}
	private class KeyValue {

		/**
		 * key property.
		 */
		private String key;
		/**
		 * value property.
		 */
		private String value;

		/**
		 * constructor instance intance.
		 *
		 * @param newKey   -
		 * @param newValue -
		 */
		private KeyValue(final String newKey, final String newValue) {
			this.key = newKey;
			this.value = newValue;
		}
	}
}