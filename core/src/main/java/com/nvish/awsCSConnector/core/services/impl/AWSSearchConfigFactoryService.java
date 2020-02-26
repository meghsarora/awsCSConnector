package com.nvish.awsCSConnector.core.services.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

//@Component(policy = ConfigurationPolicy.REQUIRE, immediate = true, label = " AWS Cloud Search Global Configuration", description = " AWS Cloud Global Search Configuration", metatype = true)
//@Properties({
//		@Property(name = Constants.SERVICE_DESCRIPTION, value = "  AWS Cloud Global Search Configuration"),
//		@Property(name = Constants.SERVICE_VENDOR, value = "Symantec") })
//@Service(value = AWSSearchConfigFactoryServiceImpl.class)

@ObjectClassDefinition(name = "AWS Cloud Search Global Configuration")
public @interface AWSSearchConfigFactoryService {
	@AttributeDefinition(
			name = "DOMAIN_NAME",
			description = "Enter Domain Name",
			type = AttributeType.STRING
	)
	String DOMAIN_NAME() default "AWS Domain Name";


	@AttributeDefinition(
			name = "AWS_API_ACCESS_KEY",
			description = "AWS API Access Key",
			type = AttributeType.STRING
	)
	String AWS_API_ACCESS_KEY() default "AWS API Access Key";

	@AttributeDefinition(
			name = "AWS_API_SECERET_KEY",
			description = "Aws API Secret Key",
			type = AttributeType.STRING
	)
	String AWS_API_SECERET_KEY() default "Aws API Secret Key";

//	@AttributeDefinition(
//			name = "AWS_SEARCH_SUGGESTION_ENDPOINT",
//			description = "AWS Search Suggestion URL",
//			type = AttributeType.STRING
//	)
//	String AWS_SEARCH_SUGGESTION_ENDPOINT() default "AWS Search Suggestion URL";
}


	/**
	 * We are using this class in AWS Search.
	 */


//	@Property(label = "RESULTS_PER_PAGE", value = "10", description = "Results per page")
//	public static final String RESULTS_PER_PAGE = "RESULTS_PER_PAGE";


