package org.xipki.ca.sdk;

import java.math.BigInteger;

/**
 *
 * @author Lijun Liao
 * @since 6.0.0
 */

public class OldCertInfo {

  /**
   * Whether to reu-use the public key in the old certificate for the new one.
   */
  private boolean reusePublicKey;

  public boolean isReusePublicKey() {
    return reusePublicKey;
  }

  public void setReusePublicKey(boolean reusePublicKey) {
    this.reusePublicKey = reusePublicKey;
  }
}
