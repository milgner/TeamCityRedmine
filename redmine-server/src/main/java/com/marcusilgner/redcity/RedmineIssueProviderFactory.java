package com.marcusilgner.redcity;

import jetbrains.buildServer.issueTracker.AbstractIssueProviderFactory;
import jetbrains.buildServer.issueTracker.IssueFetcher;
import jetbrains.buildServer.issueTracker.IssueProvider;
import jetbrains.buildServer.issueTracker.IssueProviderType;
import org.jetbrains.annotations.NotNull;

/**
 * User: Marcus Ilgner <mail@marcusilgner.com>
 * Date: 24/01/11
 */
public class RedmineIssueProviderFactory extends AbstractIssueProviderFactory {

    public RedmineIssueProviderFactory(@NotNull final IssueProviderType type, @NotNull IssueFetcher fetcher) {
        super(type, fetcher);
    }

    @NotNull
    public IssueProvider createProvider() {
        return new RedmineIssueProvider(myType, myFetcher);
    }
}
