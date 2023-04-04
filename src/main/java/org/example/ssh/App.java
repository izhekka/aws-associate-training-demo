package org.example.ssh;

import com.sshtools.client.SshClient;
import com.sshtools.common.publickey.InvalidPassphraseException;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.ssh.SshException;
import com.sshtools.common.ssh.components.SshKeyPair;

import java.io.File;
import java.io.IOException;

public class App {
  private static final String USER = "ubuntu";
  private static final String HOST = "public_ec2_address";
  private static final int PORT = 22;
  private static final String PRIVATE_KEY = "pem_file_path";

  public static void main(String[] args) throws InvalidPassphraseException, IOException, SshException {
    final SshKeyPair pair = SshKeyUtils.getRSAPrivateKeyWithSHA256Signature(new File(PRIVATE_KEY), null);

    try (final SshClient ssh = new SshClient(HOST, PORT, USER, pair)) {
      final String result = ssh.executeCommand("pwd");
      System.out.println(result);
    }
  }
}
