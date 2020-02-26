package com.nvish.awsCSConnector.core.servlets;

//import com.day.cq.wcm.api.Page;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.crx.JcrConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nvish.awsCSConnector.core.services.AWSAnalysisSchemeService;
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component(service= Servlet.class,
		immediate = true,
		property={
				Constants.SERVICE_DESCRIPTION + "= Service to get Analysis Scheme list",

				"sling.servlet.methods=" + HttpConstants.METHOD_GET,

				"sling.servlet.paths=/bin/aws/analysisschemelist",
		})
public class AWSAnalysisSchemeListServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 3617806403446339290L;

	@Reference
	private AWSAnalysisSchemeService awsAnalysisSchemeService;




	private static final Logger LOG = LoggerFactory.getLogger(AWSAnalysisSchemeListServlet.class);


	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
            List<KeyValue> dropDownList = new ArrayList<>();
            Gson gson = getGson();
            List<String> schemeNameList = new ArrayList<>();
            LOG.info("******in AWSSearchAnalysisSchemeServletList:  ");

            schemeNameList = awsAnalysisSchemeService.getAnalysisSchemeList();
            LOG.info("***schemeNameList:  "+schemeNameList.size());
            if (schemeNameList.size() > 0) {
				schemeNameList.forEach(res -> {
					//ValueMap valueMap = res.getValueMap();

					dropDownList.add(new KeyValue(res, res));
				});
			}
			else{
				dropDownList.add(new KeyValue("NoValue", "NoValue"));
				LOG.info("******DschemeNameList Size is zero  " );

			}
            LOG.info("******in side Do get");
//			Map<String, String> result1 = schemeNameList.stream().collect(
//					Collectors.toMap(SchemeNameListModel::getValue, SchemeNameListModel::getText));

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

            LOG.info("******DATA source:  " );
            request.setAttribute(DataSource.class.getName(), ds);



//			response.setContentType(com.adobe.granite.rest.Constants.CT_JSON);
//			getWriter(response).write(gson.toJson(result1));




		} catch (Exception e) {

			LOG.error("Error in Get Drop Down Values", e);

		}


	}





	/**
	 * Get request PrintWriter
	 */
	private PrintWriter getWriter(SlingHttpServletResponse resp) throws IOException{
		return resp.getWriter();
	}

	/**
	 * Get a new cusom Gson to handle json
	 */
	private Gson getGson(){
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
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