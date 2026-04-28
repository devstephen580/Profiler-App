package com.devStephen.profiler_api.util;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class NaturalLanguageParser {

  @Data
  @Builder
  public static class ParsedQuery {
    private String gender;
    private String ageGroup;
    private Integer minAge;
    private Integer maxAge;
    private String countryId;
    private boolean interpreted;
  }

  public static ParsedQuery parse(String query) {
    if (query == null || query.isBlank()) {
      return ParsedQuery.builder().interpreted(false).build();
    }

    String q = query.trim().toLowerCase();
    log.debug("Parsing natural language query: {}", q);

    ParsedQuery.ParsedQueryBuilder builder = ParsedQuery.builder();
    boolean hasMatch = false;

    // ── Gender ─
    if (q.contains("female") || q.contains("females")) {
      builder.gender("female");
      hasMatch = true;
    } else if (q.contains("male") || q.contains("males")) {
      builder.gender("male");
      hasMatch = true;
    }

    // ── Age Group ──
    if (q.contains("child") || q.contains("children")) {
      builder.ageGroup("child");
      hasMatch = true;
    } else if (q.contains("teenager") || q.contains("teenagers") || q.contains("teen")) {
      builder.ageGroup("teenager");
      hasMatch = true;
    } else if (q.contains("adult") || q.contains("adults")) {
      builder.ageGroup("adult");
      hasMatch = true;
    } else if (q.contains("senior") || q.contains("seniors")) {
      builder.ageGroup("senior");
      hasMatch = true;
    }

    // ── Young (special case — not a stored age group) ────
    if (q.contains("young")) {
      builder.minAge(16);
      builder.maxAge(24);
      hasMatch = true;
    }

    // ── Above X ──
    Matcher aboveMatcher = Pattern.compile("above\\s+(\\d+)").matcher(q);
    if (aboveMatcher.find()) {
      builder.minAge(Integer.parseInt(aboveMatcher.group(1)));
      hasMatch = true;
    }

    // ── Below X ──
    Matcher belowMatcher = Pattern.compile("below\\s+(\\d+)").matcher(q);
    if (belowMatcher.find()) {
      builder.maxAge(Integer.parseInt(belowMatcher.group(1)));
      hasMatch = true;
    }

    // ── Over X ─
    Matcher overMatcher = Pattern.compile("over\\s+(\\d+)").matcher(q);
    if (overMatcher.find()) {
      builder.minAge(Integer.parseInt(overMatcher.group(1)));
      hasMatch = true;
    }

    // ── Under X ──
    Matcher underMatcher = Pattern.compile("under\\s+(\\d+)").matcher(q);
    if (underMatcher.find()) {
      builder.maxAge(Integer.parseInt(underMatcher.group(1)));
      hasMatch = true;
    }

    // ── Country ──
    Matcher fromMatcher = Pattern.compile("from\\s+([a-z\\s]+?)(?:\\s+(?:above|below|over|under|aged?|who|and|$)|$)").matcher(q);
    if (fromMatcher.find()) {
      String countryName = fromMatcher.group(1).trim();
      String countryCode = CountryMapper.getCountryCode(countryName);
      if (countryCode != null) {
        builder.countryId(countryCode);
        hasMatch = true;
      }
    }

    builder.interpreted(hasMatch);
    return builder.build();
  }
}
