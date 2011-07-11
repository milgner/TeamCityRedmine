package com.marcusilgner.redcity;

import jetbrains.buildServer.issueTracker.AbstractIssueFetcher;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.log.Log4jFactory;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.cache.EhCacheUtil;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fetches issues from a Redmine installation
 * User: Marcus Ilgner <mail@marcusilgner.com>
 * Date: 24/01/11
 */
public class RedmineIssueFetcher extends AbstractIssueFetcher {

    //private final static Log LOGGER = LogFactory.getLog(RedmineIssueFetcher.class);

    private Pattern myPattern;

    public void setPattern(final Pattern _myPattern) {
        myPattern = _myPattern;
    }

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
        public IssueData fetch() {
           String url = getUrl(host, id) + ".xml";
           //LOGGER.debug(String.format("Fetching issue data from %s", url));
           try {
               InputStream xml = fetchHttpFile(url, credentials);
               IssueData result = parseIssue(xml);
               //LOGGER.debug("IssueData: " + result.toString());
               return result;
           }   catch (IOException e) {
               //LOGGER.fatal(e);
               throw new RuntimeException("Error fetching issue data", e);
           }
        }

        private IssueData parseIssue(final InputStream _xml) {
            //LOGGER.debug("Parsing issue data");
            try {
                Element issue = FileUtil.parseDocument(_xml, false);
                String summary = getChildContent(issue, "subject");
                String state = getAttribute(issue.getChild("status"), "name");
                String url = getUrl(host, id);
                boolean resolved = state.equalsIgnoreCase("Closed") || getChildContent(issue, "done_ratio").equals("100");
                IssueData result = new IssueData(id, summary, state, url, resolved);
                return result;
            } catch (JDOMException e) {
                //LOGGER.error(e);
                throw new RuntimeException(String.format("Error parsing XML for issue '%s' on '%s'.", id, host));
            } catch (IOException e) {
                //LOGGER.error(e);
                throw new RuntimeException(String.format("Error reading XML for issue '%s' on '%s'.", id, host));
            }
        }
    }

    public RedmineIssueFetcher(@org.jetbrains.annotations.NotNull EhCacheUtil cacheUtil) {
        super(cacheUtil);
    }

    public IssueData getIssue(@org.jetbrains.annotations.NotNull final String _host, @org.jetbrains.annotations.NotNull final String _id, @org.jetbrains.annotations.Nullable final org.apache.commons.httpclient.Credentials _credentials)
            throws Exception {
        //LOGGER.debug(String.format("getIssue called: %s, %s", _host, _id));
        String url = getUrl(_host, _id);
        RedmineFetchFunction fetchFunction = new RedmineFetchFunction(_host, _id, _credentials);
        return getFromCacheOrFetch(url, fetchFunction);
        //return fetchFunction.fetch();
    }

    public String getUrl(@org.jetbrains.annotations.NotNull final String _host, @org.jetbrains.annotations.NotNull final String _id) {
        //LOGGER.debug(String.format("Getting URL for issue %s, using pattern %s to match", _id, myPattern.toString()));
        String realId = _id;
        Matcher matcher = myPattern.matcher(_id);
        if (matcher.find()) {
            realId = matcher.group(1);
        }
        StringBuilder url = new StringBuilder();
        url.append(_host);
        if (!_host.endsWith("/")) {
            url.append("/");
        }
        url.append("issues/");
        url.append(realId);
        //LOGGER.debug(String.format("URL is %s", url.toString()));
        return url.toString();
    }

}