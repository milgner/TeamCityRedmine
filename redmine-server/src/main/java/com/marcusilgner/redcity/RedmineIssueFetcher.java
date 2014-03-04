package com.marcusilgner.redcity;

import jetbrains.buildServer.issueTracker.AbstractIssueFetcher;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.cache.EhCacheUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fetches issues from a Redmine installation
 * User: Marcus Ilgner <mail@marcusilgner.com>
 * Date: 24/01/11
 */
public class RedmineIssueFetcher extends AbstractIssueFetcher {

  private Pattern myPattern;
  private String apiToken;
  private boolean ignoreSSL;

  public void setPattern(final Pattern _myPattern) {
    myPattern = _myPattern;
  }
  public void ignoreSSL(boolean _doIgnore) { ignoreSSL = _doIgnore; }
  public void setApiToken(final String _apiToken) { apiToken = _apiToken; }

  public class RedmineFetchFunction implements FetchFunction {
    private String host;
    private String id;
    private String apiToken;

    public RedmineFetchFunction(final String _host, final String _id, final String _apiToken) {
      host = _host;
      id = _id;
      apiToken = _apiToken;
    }

    @NotNull
    public IssueData fetch() {
      String url = getUrl(host, id) + ".xml";
      try {
        InputStream xml = fetchUrlWithRedmineHeader(url);
        IssueData result = parseIssue(xml);
        return result;
      }   catch (IOException e) {
        throw new RuntimeException("Error fetching issue data", e);
      }
    }

    private InputStream fetchUrlWithRedmineHeader(String _url) throws IOException {
      try {
        HttpClient httpClient = new HttpClient();
        org.apache.commons.httpclient.HttpURL url;
        if (_url.toLowerCase().startsWith("https:")) {
            url = new HttpsURL(_url);
          if (ignoreSSL) {
            Protocol easyhttps = new Protocol("https", new org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory(), url.getPort());
            httpClient.getHostConfiguration().setHost(url.getHost(), url.getPort(), easyhttps);
          }
        } else {
            url = new HttpURL(_url);
        }
        HttpMethod httpMethod = new GetMethod(url.getPath());
        httpMethod.setRequestHeader("X-Redmine-API-Key", apiToken);
        httpClient.executeMethod(httpMethod);
        return httpMethod.getResponseBodyAsStream();
      } catch (URIException e) {
        // too lazy to write exception handling :P
        throw new RuntimeException(e.getMessage());
      }
    }

    private IssueData parseIssue(final InputStream _xml) {
      try {
        Element issue = FileUtil.parseDocument(_xml, false);
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

  public RedmineIssueFetcher(@NotNull EhCacheUtil cacheUtil) {
    super(cacheUtil);
  }

  @NotNull
  public IssueData getIssue(@NotNull final String _host, @NotNull final String _id, @Nullable final org.apache.commons.httpclient.Credentials _credentials)
          throws Exception {
    String url = getUrl(_host, _id);
    return getFromCacheOrFetch(url, new RedmineFetchFunction(_host, _id, apiToken));
  }

  public String getUrl(@NotNull final String _host, @NotNull final String _id) {
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
    return url.toString();
  }

}