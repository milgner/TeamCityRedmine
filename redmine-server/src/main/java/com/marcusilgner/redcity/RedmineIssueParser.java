package com.marcusilgner.redcity;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.CollectionsUtil;
import jetbrains.buildServer.util.ExceptionUtil;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static jetbrains.buildServer.issueTracker.IssueData.*;
import static jetbrains.buildServer.issueTracker.IssueData.PRIORITY_FIELD;

class RedmineIssueParser {

  IssueData parseIssueData(@NotNull final InputStream input, @NotNull final String url) throws IOException {
    try {
      final Map map = new XmlMapper().readValue(input, Map.class);
      String summary = getStringValue(map, "subject");
      String id = getStringValue(map, "id");
      String state = getStringValue( (Map)map.get("status"), "name");
      String doneRatio = getStringValue(map, "done_ratio");

      if (id == null || summary == null || state == null) {
        Loggers.ISSUE_TRACKERS.warn("Failed to parse issue data. Enable debug for details");
        Loggers.ISSUE_TRACKERS.debug(IOUtils.toString(input, "UTF-8"));
        throw new RuntimeException("Failed to parse issue data");
      }
      boolean resolved = state.equalsIgnoreCase("Closed") || (doneRatio != null && doneRatio.equals("100"));
      final String type = getStringValue((Map) map.get("tracker"), "name");
      return new IssueData(
              id,
              CollectionsUtil.asMap(SUMMARY_FIELD, summary,
                      STATE_FIELD, state,
                      TYPE_FIELD, type,
                      PRIORITY_FIELD, getStringValue((Map)map.get("priority"), "name")),
              resolved,
              "feature".equalsIgnoreCase(type),
              url);
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      ExceptionUtil.rethrowAsRuntimeException(e);
    }
    return null;
  }

  private String getStringValue(Map map, String key) {
    if (map != null) {
      return (String)map.get(key);
    }
    return null;
  }
}
