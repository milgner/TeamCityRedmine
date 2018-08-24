package com.marcusilgner.redcity;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.issueTracker.IssueData;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RedmineIssueParserTest extends BaseTestCase {

  private RedmineIssueParser myParser;

  @Override
  @BeforeMethod
  public void setUp() throws Exception {
    super.setUp();
    myParser = new RedmineIssueParser();
  }

  @Test
  public void testBug() throws Exception {
    final IssueData data = getIssueDataFromFile("bug.xml");
    assertEquals("1", data.getId());
    assertEquals("New", data.getState());
    assertEquals("This is an open bug!", data.getSummary());
    assertFalse(data.isResolved());
    assertFalse(data.isFeatureRequest());
  }

  @Test
  public void testFeature() throws Exception {
    final IssueData data = getIssueDataFromFile("feature.xml");
    assertEquals("2", data.getId());
    assertEquals("New", data.getState());
    assertEquals("This is a feature", data.getSummary());
    assertFalse(data.isResolved());
    assertTrue(data.isFeatureRequest());
  }

  @Test
  public void testSupportRequest() throws Exception {
    final IssueData data = getIssueDataFromFile("supportRequest.xml");
    assertEquals("3", data.getId());
    assertEquals("New", data.getState());
    assertEquals("This is a support request", data.getSummary());
    assertFalse(data.isResolved());
    assertFalse(data.isFeatureRequest());
  }

  @Test
  public void testResolved_State() throws Exception {
    final IssueData data = getIssueDataFromFile("resolved_state.xml");
    assertTrue(data.isResolved());
  }

  @Test
  public void testResolved_DoneRatio() throws Exception {
    final IssueData data = getIssueDataFromFile("resolved_done.xml");
    assertTrue(data.isResolved());
  }

  private IssueData getIssueDataFromFile(@NotNull final String filename) throws Exception {
    return myParser.parseIssueData(RedmineIssueParserTest.class.getResourceAsStream("/" + filename), "http://fake");
  }
}
