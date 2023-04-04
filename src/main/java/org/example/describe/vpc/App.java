package org.example.describe.vpc;

import org.example.utils.StringUtils;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;

public class App {

  public static void main(String[] args) {
    new App().run();
  }

  private void run() {
    try (final Ec2Client client = buildClient()) {
      describeVPCs(client);
    }
  }

  private Ec2Client buildClient() {
    return Ec2Client.builder()
        .region(Region.EU_WEST_1)
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();
  }

  private void describeVPCs(final Ec2Client client) {
    final DescribeVpcsRequest vpcsRequest = DescribeVpcsRequest.builder()
        .build();
    final DescribeVpcsResponse vpcsResponse = client.describeVpcs(vpcsRequest);

    final DescribeSubnetsRequest subnetsRequest = DescribeSubnetsRequest.builder()
        .build();
    final DescribeSubnetsResponse subnetResponse = client.describeSubnets(subnetsRequest);

    for (final Vpc vpc : vpcsResponse.vpcs()) {
      System.out.println("VPC Id: " + vpc.vpcId());
      System.out.println("  CIDR: " + vpc.cidrBlock());
      System.out.println("  state: " + vpc.stateAsString());

      System.out.println("  subnets:");
      subnetResponse.subnets().stream()
          .filter(subnet -> subnet.vpcId().equals(vpc.vpcId()))
          .forEach(subnet -> {
            System.out.println("    - Subnet Id: " + subnet.subnetId());
            System.out.println("      CIDR: " + subnet.cidrBlock());
            System.out.println("      availability zone: " + subnet.availabilityZone());
            System.out.println("      tags:");
            System.out.println("        - Name: " + getTagValue(subnet.tags(), "Name"));
            System.out.println("        - aws-cdk:subnet-type: " + getTagValue(subnet.tags(), "aws-cdk:subnet-type"));
          });

      System.out.println("  tags:");
      System.out.println("    - Name: " + getTagValue(vpc.tags(), "Name"));
      System.out.println("    - cloudx: " + getTagValue(vpc.tags(), "cloudx"));
    }
  }

  private static String getTagValue(final List<Tag> tagList, final String tagKey) {
    return tagList.stream()
        .filter(tag -> tag.key().equalsIgnoreCase(tagKey))
        .findFirst()
        .map(Tag::value)
        .orElse(StringUtils.NONE);
  }
}
