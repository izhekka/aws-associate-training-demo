package org.example.rds;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.sql.*;

public class App {

  private static final String SECRET_NAME = "secret_name";

  public static void main(String[] args) {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");

      new App().run();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  private void run() throws SQLException, JsonProcessingException {
    try (final SecretsManagerClient client = buildClient()) {
      final ConnectionProperties properties = retrieveSecrets(client);
      fetchData(properties);
    }
  }

  private SecretsManagerClient buildClient() {
    return SecretsManagerClient.builder()
        .region(Region.EU_WEST_1)
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();
  }

  private ConnectionProperties retrieveSecrets(final SecretsManagerClient client) throws JsonProcessingException {
    final GetSecretValueRequest request = GetSecretValueRequest.builder()
        .secretId(SECRET_NAME)
        .build();

    final GetSecretValueResponse response = client.getSecretValue(request);

    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(response.secretString(), ConnectionProperties.class);
  }

  private void fetchData(final ConnectionProperties properties) throws SQLException {
    final String url = "jdbc:mysql://localhost:3306/" + properties.getDbName();
      Connection connection = DriverManager.getConnection(url, properties.getUsername(), properties.getPassword());

      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("select * from images");

      while (resultSet.next()) {
        System.out.println(resultSet.getInt(1) + "  " + resultSet.getString(2) + "  " + resultSet.getString(3));
      }

      connection.close();
  }
}
