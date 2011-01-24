package com.marcusilgner.redcity;

import jetbrains.buildServer.issueTracker.AbstractIssueProvider;
import jetbrains.buildServer.issueTracker.IssueFetcher;

/**
 * An issue provider for Redmine
 * User: Marcus Ilgner <mail@marcusilgner.com>
 * Date: 24/01/11
 */
public class RedmineIssueProvider
        extends AbstractIssueProvider {
    public RedmineIssueProvider(@org.jetbrains.annotations.NotNull IssueFetcher fetcher) {
        super("Redmine", fetcher);
    }
}
