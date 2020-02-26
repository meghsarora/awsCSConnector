package com.nvish.awsCSConnector.core.services.impl;

import com.nvish.awsCSConnector.core.services.AWSSearchConfigService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        immediate = true,
        service = AWSSearchConfigService.class
)
@Designate(
        ocd = AWSSearchConfigFactoryService.class
)

public class AWSSearchConfigServiceImpl implements AWSSearchConfigService {
    static final Logger LOG = LoggerFactory.getLogger(AWSSearchConfigServiceImpl.class);

    /**
     * Instance of the OSGi configuration class
     */
    private AWSSearchConfigFactoryService configuration;

    public static String awsDomainName;
    public static String awsAccessKey;
    public static String awsAccessSecret;

    @Activate
    public void activate(AWSSearchConfigFactoryService config) {
        this.configuration = config;
    }
    @Override
            public String getDomainName(){
        LOG.info("----------< Reading the config values >----------");
             awsDomainName = configuration.DOMAIN_NAME();
            return awsDomainName;

    }
    @Override
    public String getAccessKey(){
        awsAccessKey = configuration.AWS_API_ACCESS_KEY();
        return awsAccessKey;
    }
    @Override
    public String getSecertKey(){
        awsAccessSecret  = configuration.AWS_API_SECERET_KEY();
        return awsAccessSecret;
    }

}



