package com.nvish.awsCSConnector.core.services;

import com.google.gson.JsonObject;
import com.nvish.awsCSConnector.core.models.SchemeNameListModel;

import java.io.IOException;
import java.util.List;


public interface AWSAnalysisSchemeService {

    public JsonObject updateAnalysisScheme(String schemeName, String alias, String stopwords, String language) throws IOException;
    public JsonObject deleteAnalysisScheme(String schemeName) throws IOException;
    public JsonObject createAnalysisScheme(String schemeName, String alias, String stopwords, String language) throws IOException;

    public List<String> getAnalysisSchemeList();

    public List<String> getAnalysisSchemeLanguageList();
    public JsonObject getAnalysisScheme(String schemeName);


}
