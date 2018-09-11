package com.marcusilgner.redcity;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.CollectionsUtil;
import jetbrains.buildServer.util.ExceptionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static jetbrains.buildServer.issueTracker.IssueData.*;

class RedmineIssueParser {

  IssueData parseIssueData(@NotNull final String input, @NotNull final String url) {
    try {
      final Map map = new XmlMapper().readValue(input, Map.class);
      String summary = getStringValue(map, "subject");
      String id = getStringValue(map, "id");
      String state = getStringValue( (Map)map.get("status"), "name");
      String doneRatio = getStringValue(map, "done_ratio");

      if (id == null || summary == null || state == null) {
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
    } catch (Exception e) {
      Loggers.ISSUE_TRACKERS.warn(
              String.format("IOException when trying to parse the issue data from %s. Enable debug for details", url),
              e);
      Loggers.ISSUE_TRACKERS.debug(input);
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
