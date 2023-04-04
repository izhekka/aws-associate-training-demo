package org.example.utils;

public final class StringUtils {
  public static final String NONE = "<none>";

  private StringUtils() {}

  public static <T> String optional(T value) {
    if (value == null) return NONE;
    if (value instanceof String && "".equals(((String) value).trim())) return NONE;

    return value.toString();
  }
}
