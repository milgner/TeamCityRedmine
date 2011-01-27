package com.marcusilgner.redcity;

import jetbrains.buildServer.issueTracker.AbstractIssueFetcher;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.cache.EhCacheUtil;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Fetches issues from a Redmine installation
 * User: Marcus Ilgner <mail@marcusilgner.com>
 * Date: 24/01/11
 */
public class RedmineIssueFetcher extends AbstractIssueFetcher {

    public class RedmineFetchFunction implements FetchFunction {
            private String host;
        private String id;
        private Credentials credentials;

        public RedmineFetchFunction(final String _host, final String _id, final Credentials _credentials) {
            host = _host;
            id = _id;
            credentials = _credentials;
        }

        @NotNull
        public IssueData fetch() throws Exception {
           String url = getUrl(host, id) + ".xml";
           InputStream xml = fetchHttpFile(url, credentials);
            if (xml == null) {
                throw new RuntimeException(String.format("Failed to fetch issue from \"%s\".", url));
            }
            IssueData result = parseIssue(xml);
            return result;
        }

        private IssueData parseIssue(final InputStream _xml) {
            try {
                Element root = FileUtil.parseDocument(_xml, false);
                Element issue = root.getChild("issue");
                String summary = getChildContent(issue, "subject");
                String state = getAttribute(issue.getChild("status"), "name");
                String url = getUrl(host, id);
                boolean resolved = state.equalsIgnoreCase("Closed") || getChildContent(issue, "done_ratio").equals("100");
                IssueData result = new IssueData(id, summary, state, url, resolved);
                return result;
            } catch (JDOMException e) {
                throw new RuntimeException(String.format("Error parsing XML for issue '%s' on '%s'.", id, host));
            } catch (IOException e) {
                throw new RuntimeException(String.format("Error reading XML for issue '%s' on '%s'.", id, host));
            }
        }
    }

    public RedmineIssueFetcher(@org.jetbrains.annotations.NotNull EhCacheUtil cacheUtil) {
        super(cacheUtil);
    }

    public IssueData getIssue(@org.jetbrains.annotations.NotNull final String _host, @org.jetbrains.annotations.NotNull final String _id, @org.jetbrains.annotations.Nullable final org.apache.commons.httpclient.Credentials _credentials)
            throws Exception {
        String url = getUrl(_host, _id);
        return getFromCacheOrFetch(url, new RedmineFetchFunction(_host, _id, _credentials));
    }

    public String getUrl(@org.jetbrains.annotations.NotNull final String _host, @org.jetbrains.annotations.NotNull final String _id) {
        StringBuilder url = new StringBuilder();
        url.append(_host);
        if (!_host.endsWith("/")) {
            url.append("/");
        }
        url.append("issues/");
        url.append(_id);
        return url.toString();
    }

}