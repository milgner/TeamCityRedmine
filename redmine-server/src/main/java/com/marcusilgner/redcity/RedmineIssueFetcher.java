package com.marcusilgner.redcity;

import jetbrains.buildServer.issueTracker.AbstractIssueFetcher;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.util.cache.EhCacheUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
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

  private final RedmineIssueParser myParser = new RedmineIssueParser();

  @SuppressWarnings({"WeakerAccess", "deprecation"})
  public RedmineIssueFetcher(@NotNull final EhCacheUtil cacheUtil) {
    super(cacheUtil);
  }

  @NotNull
  public IssueData getIssue(@NotNull final String host, @NotNull final String id, @Nullable final org.apache.commons.httpclient.Credentials credentials)
          throws Exception {
    final String url = getUrl(host, id);
    return getFromCacheOrFetch(url, () -> {
      try {
        return myParser.parseIssueData(fetchUrlWithRedmineHeader(url + ".xml"), url);
      } catch (IOException e) {
        throw new RuntimeException(String.format("Error reading XML for issue '%s' on '%s'.", id, host));
      }
    });
  }

  private InputStream fetchUrlWithRedmineHeader(String url) throws IOException {
    try {
      HttpClient httpClient = new HttpClient();
      HttpMethod httpMethod;
      if (url.toLowerCase().startsWith("https:") && ignoreSSL) {
        HttpsURL httpsURL = new HttpsURL(url);
        Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), httpsURL.getPort());
        httpClient.getHostConfiguration().setHost(httpsURL.getHost(), httpsURL.getPort(), easyhttps);
        httpMethod = new GetMethod(httpsURL.getPath());
      } else {
        httpMethod = new GetMethod(url);
      }
      httpMethod.setRequestHeader("X-Redmine-API-Key", apiToken);
      httpClient.executeMethod(httpMethod);
      return httpMethod.getResponseBodyAsStream();
    } catch (URIException e) {
      // too lazy to write exception handling :P
      throw new RuntimeException(e.getMessage());
    }
  }

  @NotNull
  public String getUrl(@NotNull final String host, @NotNull final String id) {
    String realId = id;
    Matcher matcher = myPattern.matcher(id);
    if (matcher.find()) {
      realId = matcher.group(1);
    }
    StringBuilder url = new StringBuilder();
    url.append(host);
    if (!host.endsWith("/")) {
      url.append("/");
    }
    url.append("issues/");
    url.append(realId);
    return url.toString();
  }

  void setPattern(final Pattern pattern) {
    myPattern = pattern;
  }
  void ignoreSSL(boolean ignore) { ignoreSSL = ignore; }
  void setApiToken(final String apiToken) { this.apiToken = apiToken; }
}