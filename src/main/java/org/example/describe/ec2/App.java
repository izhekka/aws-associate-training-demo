package org.example.describe.ec2;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

import static org.example.utils.StringUtils.optional;

public class App {

  public static void main(String[] args) {
    new App().run();
  }

  private void run() {
    try (final Ec2Client client = buildClient()) {
      describeEC2Instances(client);
    }
  }

  private Ec2Client buildClient() {
    return Ec2Client.builder()
        .region(Region.EU_WEST_1)
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();
  }

  private void describeEC2Instances(final Ec2Client client) {
    String nextToken = null;

    do {
      final DescribeInstancesRequest request = DescribeInstancesRequest.builder()
          .maxResults(10)
          .nextToken(nextToken)
          .build();
      final DescribeInstancesResponse response = client.describeInstances(request);

      for (final Reservation reservation : response.reservations()) {
        for (final Instance instance : reservation.instances()) {
          System.out.println("Instance Id: " + instance.instanceId());
          System.out.println("  type: " + instance.instanceType());
          System.out.println("  private IP: " +  instance.privateIpAddress());
          System.out.println("  public IP: " +  optional(instance.publicIpAddress()));
          System.out.println("  public DNS name: " +  optional(instance.publicDnsName()));
          System.out.println("  state: " +  instance.state().name());
        }
      }

      nextToken = response.nextToken();
    } while (nextToken != null);
  }
}
