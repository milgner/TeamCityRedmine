package com.marcusilgner.redcity;

import jetbrains.buildServer.issueTracker.AbstractIssueProviderFactory;
import jetbrains.buildServer.issueTracker.IssueFetcher;
import jetbrains.buildServer.issueTracker.IssueProvider;
import org.jetbrains.annotations.NotNull;

/**
 * User: Marcus Ilgner <mail@marcusilgner.com>
 * Date: 24/01/11
 */
public class RedmineIssueProviderFactory extends AbstractIssueProviderFactory {
    protected RedmineIssueProviderFactory(@NotNull IssueFetcher fetcher) {
        super(fetcher, "Redmine");
    }

    @NotNull
    public IssueProvider createProvider() {
        return new RedmineIssueProvider(myFetcher);
    }
}
