package com.marcusilgner.redcity;

import jetbrains.buildServer.issueTracker.AbstractIssueProvider;
import jetbrains.buildServer.issueTracker.IssueFetcher;
import jetbrains.buildServer.issueTracker.IssueProviderType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * An issue provider for Redmine
 * User: Marcus Ilgner <mail@marcusilgner.com>
 * Date: 24/01/11
 */
public class RedmineIssueProvider extends AbstractIssueProvider {

    public RedmineIssueProvider(@NotNull final IssueProviderType type, @NotNull final IssueFetcher fetcher) {
        super(type.getType(), fetcher);
    }

    @NotNull
    @Override
    protected Pattern compilePattern(@NotNull final Map<String, String> properties) {
        Pattern result = super.compilePattern(properties);
        if (myFetcher instanceof RedmineIssueFetcher) {
            RedmineIssueFetcher fetcher = (RedmineIssueFetcher)myFetcher;
            fetcher.setPattern(result);
            fetcher.setApiToken(properties.get("apiToken"));
            String ignoreVal = properties.get("ignoreSSL");
            fetcher.ignoreSSL(ignoreVal.equals("true"));
        }
        return result;
    }
}
