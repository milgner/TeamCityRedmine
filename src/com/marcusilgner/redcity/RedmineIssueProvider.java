package com.marcusilgner.redcity;

import jetbrains.buildServer.issueTracker.AbstractIssueProvider;
import jetbrains.buildServer.issueTracker.IssueFetcher;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An issue provider for Redmine
 * User: Marcus Ilgner <mail@marcusilgner.com>
 * Date: 24/01/11
 */
public class RedmineIssueProvider
        extends AbstractIssueProvider {

    public RedmineIssueProvider(@org.jetbrains.annotations.NotNull IssueFetcher fetcher) {
        super("redmine", fetcher);
    }

    @NotNull
    @Override
    protected Pattern compilePattern(@NotNull final Map<String, String> properties) {
        Pattern result = super.compilePattern(properties);
        if (myFetcher instanceof RedmineIssueFetcher) {
            ((RedmineIssueFetcher) myFetcher).setPattern(result);
        }
        return result;
    }

    @Override
    protected String extractId(@NotNull String match) {
        Matcher matcher = myPattern.matcher(match);
        matcher.find();
        if (matcher.groupCount() >= 1) {
            return matcher.group(1);
        } else {
            return super.extractId(match);
        }
    }
}
