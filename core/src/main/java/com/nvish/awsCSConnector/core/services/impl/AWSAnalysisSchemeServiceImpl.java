package com.nvish.awsCSConnector.core.services.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudsearchv2.AbstractAmazonCloudSearch;
import com.amazonaws.services.cloudsearchv2.AmazonCloudSearchClient;
import com.amazonaws.services.cloudsearchv2.model.*;
import com.day.cq.wcm.api.Page;
import com.google.gson.JsonObject;
import com.nvish.awsCSConnector.core.constants.LanguageList;
import com.nvish.awsCSConnector.core.models.SchemeNameListModel;
import com.nvish.awsCSConnector.core.services.AWSAnalysisSchemeService;
import com.nvish.awsCSConnector.core.services.AWSSearchConfigService;
import org.apache.abdera.i18n.rfc4646.enums.Language;
import org.apache.commons.lang.StringEscapeUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nvish.awsCSConnector.core.services.impl.AWSSearchConfigServiceImpl.awsAccessKey;


@Component(
        immediate = true,
        service = AWSAnalysisSchemeService.class
)
//@Designate(
//        ocd = AWSSearchConfigFactoryService.class
//)
public class AWSAnalysisSchemeServiceImpl extends AbstractAmazonCloudSearch implements AWSAnalysisSchemeService {

    @Reference
    private AWSSearchConfigService awsSearchConfigService;
    static final Logger LOG = LoggerFactory.getLogger(AWSAnalysisSchemeServiceImpl.class);



@Override
    public JsonObject updateAnalysisScheme(String schemeName, String synonyms, String stopwords, String language)
            throws IOException {
    String awsDomainName = awsSearchConfigService.getDomainName();
    String awsAccessKey = awsSearchConfigService.getAccessKey();
    String awsAccessSecret = awsSearchConfigService.getSecertKey();
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsAccessSecret);;
        AmazonCloudSearchClient client = new AmazonCloudSearchClient(awsCredentials);
        String domainName = awsDomainName;

        LOG.info("***** Domain name from createAnalysisScheme: "+domainName);
        JsonObject jsonUplaodStatus = new JsonObject();
        DefineAnalysisSchemeRequest analysisSchemeRequest  =  new DefineAnalysisSchemeRequest();
        analysisSchemeRequest.setDomainName(domainName);
        AnalysisScheme analysisScheme = new AnalysisScheme();
        AnalysisOptions analysisOptions = new AnalysisOptions();
//    String name = "Test1";
    LOG.info("***** Scheme name from updateAnalysisScheme: "+schemeName);
    String langCode  = LanguageList.valueOf(language).value();
    LanguageList e = Enum.valueOf(LanguageList.class, language);
        if(stopwords!=null&&!stopwords.isEmpty())
            analysisOptions.setStopwords(stopwords);
        if(synonyms!=null  &&  !synonyms.isEmpty())
            analysisOptions.setSynonyms(synonyms);
//     ss   analysisOptions.setStemmingDictionary("");
//        analysisOptions.setJapaneseTokenizationDictionary("");
//        analysisOptions.setAlgorithmicStemming("");
        analysisSchemeRequest.setAnalysisScheme(analysisScheme);


        analysisScheme.setAnalysisSchemeName(schemeName);

        analysisScheme.setAnalysisSchemeLanguage(langCode);
        analysisScheme.setAnalysisOptions(analysisOptions);
        LOG.info("**  AnalysisScheme Name: "+analysisScheme.getAnalysisSchemeName());
        LOG.info("**  AnalysisScheme Language: "+analysisScheme.getAnalysisSchemeLanguage());
        LOG.info("**  AnalysisSchemeRequest : "+analysisSchemeRequest);

        DefineAnalysisSchemeResult defineAnalysisSchemeResult = new DefineAnalysisSchemeResult();
        defineAnalysisSchemeResult= client.defineAnalysisScheme(analysisSchemeRequest);
        LOG.info("defineAnalysisSchemeResult**"+ defineAnalysisSchemeResult.toString());
        jsonUplaodStatus.addProperty("status", "success");
        return jsonUplaodStatus;
    }


    @Override
    public JsonObject deleteAnalysisScheme(String schemeName)
            throws IOException {
        String awsDomainName = awsSearchConfigService.getDomainName();
        String awsAccessKey = awsSearchConfigService.getAccessKey();
        String awsAccessSecret = awsSearchConfigService.getSecertKey();
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsAccessSecret);;
        AmazonCloudSearchClient client = new AmazonCloudSearchClient(awsCredentials);

        JsonObject jsonUplaodStatus = new JsonObject();
        DeleteAnalysisSchemeRequest deleteAnalysisSchemeRequest = new DeleteAnalysisSchemeRequest();
        deleteAnalysisSchemeRequest.setDomainName(awsDomainName);
        deleteAnalysisSchemeRequest.setAnalysisSchemeName(schemeName);
        DeleteAnalysisSchemeResult deleteAnalysisSchemeResult = new DeleteAnalysisSchemeResult();

        deleteAnalysisSchemeResult  = client.deleteAnalysisScheme(deleteAnalysisSchemeRequest);
        LOG.info("**** Deleted Successfully"+deleteAnalysisSchemeResult);
        jsonUplaodStatus.addProperty("status", "success");
        return jsonUplaodStatus;
    }



    @Override
    public JsonObject createAnalysisScheme(String schemeName, String synonyms, String stopwords, String language)
            throws IOException {
        String awsDomainName = awsSearchConfigService.getDomainName();
        String awsAccessKey = awsSearchConfigService.getAccessKey();
        String awsAccessSecret = awsSearchConfigService.getSecertKey();
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsAccessSecret);;
        AmazonCloudSearchClient client = new AmazonCloudSearchClient(awsCredentials);
        String domainName = awsDomainName;
        List<String> schemeNameList = new ArrayList<>();

        schemeNameList = getAnalysisSchemeList();
        LOG.info("***** Domain name from createAnalysisScheme: "+domainName);
        JsonObject jsonUplaodStatus = new JsonObject();
        LOG.info("******* Scheme Name: "+schemeName +"******** in list: "+schemeNameList.contains(schemeName));
        if(schemeNameList.contains(schemeName)){
            jsonUplaodStatus.addProperty("status", "Analysis scheme "+schemeName+" already exists. Specify a unique name for your new analysis scheme.");
        }else {
            DefineAnalysisSchemeRequest analysisSchemeRequest = new DefineAnalysisSchemeRequest();
            analysisSchemeRequest.setDomainName(domainName);
            AnalysisScheme analysisScheme = new AnalysisScheme();
            AnalysisOptions analysisOptions = new AnalysisOptions();
//    String name = "Test1";
            String langCode = LanguageList.valueOf(language).value();
            LanguageList e = Enum.valueOf(LanguageList.class, language);
            if (stopwords != null && !stopwords.isEmpty())
                analysisOptions.setStopwords(stopwords);
            if (synonyms != null && !synonyms.isEmpty())
                analysisOptions.setSynonyms(synonyms);
//     ss   analysisOptions.setStemmingDictionary("");
//        analysisOptions.setJapaneseTokenizationDictionary("");
//        analysisOptions.setAlgorithmicStemming("");
            analysisSchemeRequest.setAnalysisScheme(analysisScheme);


            analysisScheme.setAnalysisSchemeName(schemeName);

            analysisScheme.setAnalysisSchemeLanguage(langCode);
            analysisScheme.setAnalysisOptions(analysisOptions);
            LOG.info("**  AnalysisScheme Name: " + analysisScheme.getAnalysisSchemeName());
            LOG.info("**  AnalysisScheme Language: " + analysisScheme.getAnalysisSchemeLanguage());
            LOG.info("**  AnalysisSchemeRequest : " + analysisSchemeRequest);

            DefineAnalysisSchemeResult defineAnalysisSchemeResult = new DefineAnalysisSchemeResult();
            defineAnalysisSchemeResult = client.defineAnalysisScheme(analysisSchemeRequest);
            LOG.info("defineAnalysisSchemeResult**" + defineAnalysisSchemeResult.toString());
            jsonUplaodStatus.addProperty("status", "success");
        }
        return jsonUplaodStatus;
    }

    public List<String> getAnalysisSchemeList(){
    String awsDomainName = awsSearchConfigService.getDomainName();
    String awsAccessKey = awsSearchConfigService.getAccessKey();
    String awsAccessSecret = awsSearchConfigService.getSecertKey();

    LOG.info("***** Access Key: "+awsAccessKey+" ******* Seceret Key: "+awsAccessSecret+" ***domain name: "+awsDomainName);
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsAccessSecret);;
        List<String> schemeNameList = new ArrayList<>();
        try {
            AmazonCloudSearchClient client = new AmazonCloudSearchClient(awsCredentials);

            LOG.info("*********Domain: " + awsDomainName);

            DescribeAnalysisSchemesRequest describeAnalysisSchemesRequest = new DescribeAnalysisSchemesRequest();
            describeAnalysisSchemesRequest.setDomainName(awsDomainName);
            DescribeAnalysisSchemesResult describeAnalysisSchemesResult = client.describeAnalysisSchemes(describeAnalysisSchemesRequest);
            List<AnalysisSchemeStatus> analysisSchemeList = describeAnalysisSchemesResult.getAnalysisSchemes();

            analysisSchemeList.forEach(analysisSchemeName -> {
                schemeNameList.add(analysisSchemeName.getOptions().getAnalysisSchemeName());
//            schemeNameList.add(analysisSchemeName.getOptions().getAnalysisSchemeName());
            });
        }
        catch (Exception e){

        }


//        String analysisSchemeName = describeAnalysisSchemesResult.getAnalysisSchemes().get(0).getOptions().getAnalysisSchemeName();
//        LOG.info("*********describeAnalysisSchemesResult: "+analysisSchemeName);
        return schemeNameList;

    }

    public List<String> getAnalysisSchemeLanguageList(){

                List<String> enumNames = Stream.of(LanguageList.values())
                .map(Enum::name)
                .collect(Collectors.toList());

                LOG.info("***** EnumList: "+enumNames);


        return enumNames;

    }


    public JsonObject getAnalysisScheme(String schemeName){
        String awsDomainName = awsSearchConfigService.getDomainName();
        String awsAccessKey = awsSearchConfigService.getAccessKey();
        String awsAccessSecret = awsSearchConfigService.getSecertKey();
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsAccessSecret);;
        AmazonCloudSearchClient client = new AmazonCloudSearchClient(awsCredentials);
        String domainName = awsDomainName;
        JsonObject analysisScheme = new JsonObject();


        LOG.info("*********getAnalysisScheme: "+schemeName);

        DescribeAnalysisSchemesRequest describeAnalysisSchemesRequest = new DescribeAnalysisSchemesRequest();
        describeAnalysisSchemesRequest.setDomainName(domainName);
        DescribeAnalysisSchemesResult describeAnalysisSchemesResult= client.describeAnalysisSchemes(describeAnalysisSchemesRequest);
        List<AnalysisSchemeStatus> analysisSchemeList =  describeAnalysisSchemesResult.getAnalysisSchemes();

        analysisSchemeList.forEach(analysisSchemeName -> {

            if(analysisSchemeName.getOptions().getAnalysisSchemeName().equals(schemeName.trim())){
                String langCode = new String();
                langCode= analysisSchemeName.getOptions().getAnalysisSchemeLanguage();
                LOG.info("*******stopword: "+analysisSchemeName.getOptions().getAnalysisOptions().getSynonyms());
                analysisScheme.addProperty("Synonyms", analysisSchemeName.getOptions().getAnalysisOptions().getSynonyms());
                analysisScheme.addProperty("Stopwords",analysisSchemeName.getOptions().getAnalysisOptions().getStopwords());
                analysisScheme.addProperty("Language",LanguageList.getEnumByString(analysisSchemeName.getOptions().getAnalysisSchemeLanguage()));
            }

        });


        LOG.info("*********schemeNameList: "+analysisScheme);
        return analysisScheme;
    }







}
