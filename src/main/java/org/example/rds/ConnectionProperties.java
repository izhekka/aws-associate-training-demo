package org.example.rds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ConnectionProperties {

  @JsonProperty
  private String dbname;

  @JsonProperty
  private String username;

  @JsonProperty
  private String password;

  public String getDbName() {
    return dbname;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
