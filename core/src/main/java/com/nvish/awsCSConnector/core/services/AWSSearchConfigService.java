package com.nvish.awsCSConnector.core.services;

import com.nvish.awsCSConnector.core.services.impl.AWSSearchConfigFactoryService;
import org.osgi.service.component.annotations.Activate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public   interface   AWSSearchConfigService{
	static final Logger LOG = LoggerFactory.getLogger(AWSSearchConfigService.class);

    public String getDomainName();
    public String getAccessKey();
    public String getSecertKey();

}


	/**
	 * We are using this class in AWS Search.
	 */


//	@Property(label = "RESULTS_PER_PAGE", value = "10", description = "Results per page")
//	public static final String RESULTS_PER_PAGE = "RESULTS_PER_PAGE";


