/*
 *
 * Copyright (c) 2013 - 2019 Lijun Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xipki.ocsp.api;

import java.util.Date;

import org.bouncycastle.asn1.ocsp.CrlID;
import org.xipki.security.CertRevocationInfo;
import org.xipki.security.HashAlgo;
import org.xipki.util.Args;

/**
 * CertStatus information.
 *
 * @author Lijun Liao
 * @since 2.0.0
 */

public class CertStatusInfo {

  public static enum CertStatus {
    GOOD,
    REVOKED,
    UNKNOWN,
    IGNORE,
    ISSUER_UNKNOWN,
    CRL_EXPIRED
  }

  public enum UnknownCertBehaviour {
    unknown,
    good,
    malformedRequest,
    internalError,
    tryLater;
  }

  public enum UnknownIssuerBehaviour {
    unknown,
    malformedRequest,
    internalError,
    unauthorized,
    tryLater;
  }

  @SuppressWarnings("unused")
  private static class UnmodifiableCertStatusInfo extends CertStatusInfo {

    private UnmodifiableCertStatusInfo(CertStatus certStatus, Date thisUpdate, Date nextUpdate,
        String certprofile) {
      super(certStatus, thisUpdate, nextUpdate, certprofile);
    }

    @Override
    public void setThisUpdate(Date thisUpdate) {
      throw new UnsupportedOperationException("setThisUpdate() unsupported");
    }

    @Override
    public void setNextUpdate(Date nextUpdate) {
      throw new UnsupportedOperationException("setNextUpdate() unsupported");
    }

    @Override
    public void setCertStatus(CertStatus certStatus) {
      throw new UnsupportedOperationException("setCertStatus() unsupported");
    }

    @Override
    public void setCertprofile(String certprofile) {
      throw new UnsupportedOperationException("setCertprofile() unsupported");
    }

    @Override
    public void setCrlId(CrlID crlId) {
      throw new UnsupportedOperationException("setCrlId() unsupported");
    }

    @Override
    public void setArchiveCutOff(Date archiveCutOff) {
      throw new UnsupportedOperationException("setArchiveCutOff() unsupported");
    }

  }

  private static final CertStatusInfo crlExpiredStatus =
      new UnmodifiableCertStatusInfo(CertStatus.CRL_EXPIRED, null, null, null);

  private CertStatus certStatus;

  private CertRevocationInfo revocationInfo;

  private HashAlgo certHashAlgo;

  private byte[] certHash;

  private Date thisUpdate;

  private Date nextUpdate;

  private String certprofile;

  private CrlID crlId;

  private Date archiveCutOff;

  private CertStatusInfo(CertStatus certStatus, Date thisUpdate, Date nextUpdate,
      String certprofile) {
    this.certStatus = Args.notNull(certStatus, "certStatus");
    this.thisUpdate = Args.notNull(thisUpdate, "thisUpdate");
    this.nextUpdate = nextUpdate;
    this.certprofile = certprofile;
  }

  public Date getThisUpdate() {
    return thisUpdate;
  }

  public void setThisUpdate(Date thisUpdate) {
    this.thisUpdate = thisUpdate;
  }

  public Date getNextUpdate() {
    return nextUpdate;
  }

  public void setNextUpdate(Date nextUpdate) {
    this.nextUpdate = nextUpdate;
  }

  public CertStatus getCertStatus() {
    return certStatus;
  }

  public void setCertStatus(CertStatus certStatus) {
    this.certStatus = Args.notNull(certStatus, "certStatus");
  }

  public CertRevocationInfo getRevocationInfo() {
    return revocationInfo;
  }

  public HashAlgo getCertHashAlgo() {
    return certHashAlgo;
  }

  public byte[] getCertHash() {
    return certHash;
  }

  public String getCertprofile() {
    return certprofile;
  }

  public void setCertprofile(String certprofile) {
    this.certprofile = certprofile;
  }

  public CrlID getCrlId() {
    return crlId;
  }

  public void setCrlId(CrlID crlId) {
    this.crlId = crlId;
  }

  public Date getArchiveCutOff() {
    return archiveCutOff;
  }

  public void setArchiveCutOff(Date archiveCutOff) {
    this.archiveCutOff = archiveCutOff;
  }

  public static CertStatusInfo getCrlExpiredStatusInfo() {
    return crlExpiredStatus;
  }

  public static CertStatusInfo getUnknownCertStatusInfo(Date thisUpdate, Date nextUpdate) {
    return new CertStatusInfo(CertStatus.UNKNOWN, thisUpdate, nextUpdate, null);
  }

  public static CertStatusInfo getIgnoreCertStatusInfo(Date thisUpdate, Date nextUpdate) {
    return new CertStatusInfo(CertStatus.IGNORE, thisUpdate, nextUpdate, null);
  }

  public static CertStatusInfo getIssuerUnknownCertStatusInfo(Date thisUpdate, Date nextUpdate) {
    return new CertStatusInfo(CertStatus.ISSUER_UNKNOWN, thisUpdate, nextUpdate, null);
  }

  public static CertStatusInfo getGoodCertStatusInfo(HashAlgo certHashAlgo, byte[] certHash,
      Date thisUpdate, Date nextUpdate, String certprofile) {
    CertStatusInfo ret = new CertStatusInfo(CertStatus.GOOD, thisUpdate, nextUpdate, certprofile);
    ret.certHashAlgo = certHashAlgo;
    ret.certHash = certHash;
    return ret;
  }

  public static CertStatusInfo getGoodCertStatusInfo(Date thisUpdate, Date nextUpdate) {
    return new CertStatusInfo(CertStatus.GOOD, thisUpdate, nextUpdate, null);
  }

  public static CertStatusInfo getRevokedCertStatusInfo(CertRevocationInfo revocationInfo,
      HashAlgo certHashAlgo, byte[] certHash, Date thisUpdate, Date nextUpdate,
      String certprofile) {
    if (revocationInfo == null) {
      throw new IllegalArgumentException("revocationInfo may not be null");
    }
    CertStatusInfo ret = new CertStatusInfo(CertStatus.REVOKED, thisUpdate, nextUpdate,
        certprofile);
    ret.revocationInfo = revocationInfo;
    ret.certHashAlgo = certHashAlgo;
    ret.certHash = certHash;
    return ret;
  }

  public static CertStatusInfo getRevokedCertStatusInfo(CertRevocationInfo revocationInfo,
      Date thisUpdate, Date nextUpdate) {
    if (revocationInfo == null) {
      throw new IllegalArgumentException("revocationInfo may not be null");
    }
    CertStatusInfo ret = new CertStatusInfo(CertStatus.REVOKED, thisUpdate, nextUpdate, null);
    ret.revocationInfo = revocationInfo;
    return ret;
  }

}
