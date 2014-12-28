/*
 *
 * This file is part of the XiPKI project.
 * Copyright (c) 2014 Lijun Liao
 * Author: Lijun Liao
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * THE AUTHOR LIJUN LIAO. LIJUN LIAO DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the XiPKI software without
 * disclosing the source code of your own applications.
 *
 * For more information, please contact Lijun Liao at this
 * address: lijun.liao@gmail.com
 */

package org.xipki.ca.qa.certprofile.x509;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.validation.SchemaFactory;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.xipki.ca.qa.certprofile.x509.jaxb.AlgorithmType;
import org.xipki.ca.qa.certprofile.x509.jaxb.CertificatePolicyInformationType;
import org.xipki.ca.qa.certprofile.x509.jaxb.ConstantExtensionType;
import org.xipki.ca.qa.certprofile.x509.jaxb.CurveType;
import org.xipki.ca.qa.certprofile.x509.jaxb.CurveType.Encodings;
import org.xipki.ca.qa.certprofile.x509.jaxb.ECParameterType;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionType;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.Admission;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.AuthorityKeyIdentifier;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.CertificatePolicies;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.ConstantExtensions;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.ExtendedKeyUsage;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.InhibitAnyPolicy;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.NameConstraints;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.PolicyConstraints;
import org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.PolicyMappings;
import org.xipki.ca.qa.certprofile.x509.jaxb.GeneralNameType;
import org.xipki.ca.qa.certprofile.x509.jaxb.GeneralNameType.OtherName;
import org.xipki.ca.qa.certprofile.x509.jaxb.GeneralSubtreeBaseType;
import org.xipki.ca.qa.certprofile.x509.jaxb.GeneralSubtreesType;
import org.xipki.ca.qa.certprofile.x509.jaxb.KeyUsageType;
import org.xipki.ca.qa.certprofile.x509.jaxb.ObjectFactory;
import org.xipki.ca.qa.certprofile.x509.jaxb.OidWithDescType;
import org.xipki.ca.qa.certprofile.x509.jaxb.ParameterType;
import org.xipki.ca.qa.certprofile.x509.jaxb.PolicyIdMappingType;
import org.xipki.ca.qa.certprofile.x509.jaxb.RdnType;
import org.xipki.ca.qa.certprofile.x509.jaxb.SubjectInfoAccessType;
import org.xipki.ca.qa.certprofile.x509.jaxb.SubjectInfoAccessType.Access;
import org.xipki.ca.qa.certprofile.x509.jaxb.X509ProfileType;
import org.xipki.ca.qa.certprofile.x509.jaxb.X509ProfileType.KeyAlgorithms;
import org.xipki.ca.qa.certprofile.x509.jaxb.X509ProfileType.Subject;
import org.xipki.common.ObjectIdentifiers;
import org.xipki.common.SecurityUtil;

/**
 * @author Lijun Liao
 */

public class ProfileConfCreatorDemo
{
    private static final ASN1ObjectIdentifier id_gematik = new ASN1ObjectIdentifier("1.2.276.0.76.4");

    private static final String REGEX_FQDN =
            "(?=^.{1,254}$)(^(?:(?!\\d+\\.|-)[a-zA-Z0-9_\\-]{1,63}(?<!-)\\.?)+(?:[a-zA-Z]{2,})$)";
    private static final String REGEX_SN = "[\\d]{1,}";

    private static final Map<ASN1ObjectIdentifier, String> oidDescMap;

    static
    {
        oidDescMap = new HashMap<>();
        oidDescMap.put(Extension.auditIdentity, "auditIdentity");
        oidDescMap.put(Extension.authorityInfoAccess, "authorityInfoAccess");
        oidDescMap.put(Extension.authorityKeyIdentifier, "authorityKeyIdentifier");
        oidDescMap.put(Extension.basicConstraints, "basicConstraints");
        oidDescMap.put(Extension.biometricInfo, "biometricInfo");
        oidDescMap.put(Extension.certificateIssuer, "certificateIssuer");
        oidDescMap.put(Extension.certificatePolicies, "certificatePolicies");
        oidDescMap.put(Extension.cRLDistributionPoints, "cRLDistributionPoints");
        oidDescMap.put(Extension.cRLNumber, "cRLNumber");
        oidDescMap.put(Extension.deltaCRLIndicator, "deltaCRLIndicator");
        oidDescMap.put(Extension.extendedKeyUsage, "extendedKeyUsage");
        oidDescMap.put(Extension.freshestCRL, "freshestCRL");
        oidDescMap.put(Extension.inhibitAnyPolicy, "inhibitAnyPolicy");
        oidDescMap.put(Extension.instructionCode, "instructionCode");
        oidDescMap.put(Extension.invalidityDate, "invalidityDate");
        oidDescMap.put(Extension.issuerAlternativeName, "issuerAlternativeName");
        oidDescMap.put(Extension.issuingDistributionPoint, "issuingDistributionPoint");
        oidDescMap.put(Extension.keyUsage, "keyUsage");
        oidDescMap.put(Extension.logoType, "logoType");
        oidDescMap.put(Extension.nameConstraints, "nameConstraints");
        oidDescMap.put(Extension.noRevAvail, "noRevAvail");
        oidDescMap.put(ObjectIdentifiers.id_extension_pkix_ocsp_nocheck, "pkixOcspNocheck");
        oidDescMap.put(Extension.policyConstraints, "policyConstraints");
        oidDescMap.put(Extension.policyMappings, "policyMappings");
        oidDescMap.put(Extension.privateKeyUsagePeriod, "privateKeyUsagePeriod");
        oidDescMap.put(Extension.qCStatements, "qCStatements");
        oidDescMap.put(Extension.reasonCode, "reasonCode");
        oidDescMap.put(Extension.subjectAlternativeName, "subjectAlternativeName");
        oidDescMap.put(Extension.subjectDirectoryAttributes, "subjectDirectoryAttributes");
        oidDescMap.put(Extension.subjectInfoAccess, "subjectInfoAccess");
        oidDescMap.put(Extension.subjectKeyIdentifier, "subjectKeyIdentifier");
        oidDescMap.put(Extension.targetInformation, "targetInformation");
    }

    public static void main(String[] args)
    {
        try
        {
            Marshaller m = JAXBContext.newInstance(ObjectFactory.class).createMarshaller();
            final SchemaFactory schemaFact = SchemaFactory.newInstance(
                    javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = X509CertProfileQA.class.getResource("/xsd/qa-x509certprofile.xsd");
            m.setSchema(schemaFact.newSchema(url));
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty("com.sun.xml.internal.bind.indentString", "  ");

            // RootCA
            X509ProfileType profile = CertProfile_RootCA(false);
            marshall(m, profile, "QA-CertProfile_RootCA.xml");

            // RootCA-Cross
            profile = CertProfile_RootCA(true);
            marshall(m, profile, "QA-CertProfile_RootCA_Cross.xml");

            // SubCA
            profile = CertProfile_SubCA();
            marshall(m, profile, "QA-CertProfile_SubCA.xml");

            profile = CertProfile_SubCA_Complex();
            marshall(m, profile, "QA-CertProfile_SubCA_Complex.xml");

            // OCSP
            profile = CertProfile_OCSP();
            marshall(m, profile, "QA-CertProfile_OCSP.xml");

            // TLS
            profile = CertProfile_TLS();
            marshall(m, profile, "QA-CertProfile_TLS.xml");

            // TLS_C
            profile = CertProfile_TLS_C();
            marshall(m, profile, "QA-CertProfile_TLS_C.xml");

            // TLSwithIncSN
            profile = CertProfile_TLSwithIncSN();
            marshall(m, profile, "QA-CertProfile_TLSwithIncSN.xml");

            //gSMC-K
            profile = CertProfile_gSMC_K();
            marshall(m, profile, "QA-CertProfile_gSMC_K.xml");

            //multiple-OUs
            profile = CertProfile_MultipleOUs();
            marshall(m, profile, "QA-CertProfile_multipleOUs.xml");

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void marshall(Marshaller m, X509ProfileType profile, String filename)
    throws Exception
    {
        File file = new File("tmp", filename);
        file.getParentFile().mkdirs();
        JAXBElement<X509ProfileType> root = new ObjectFactory().createX509Profile(profile);
        FileOutputStream out = new FileOutputStream(file);
        try
        {
            m.marshal(root, out);
        }finally
        {
            out.close();
        }

    }

    private static X509ProfileType CertProfile_RootCA(boolean cross)
    throws Exception
    {
        X509ProfileType profile = getBaseProfile("CertProfile RootCA" + (cross ? " Cross" : ""),
                true, "10y", false);

        // Subject
        Subject subject = profile.getSubject();

        List<RdnType> occurrences = subject.getRdn();
        occurrences.add(createRDN(ObjectIdentifiers.DN_C, 1, 1, new String[]{"DE|FR"}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_O, 1, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_OU, 0, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_SN, 0, 1, new String[]{REGEX_SN}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_CN, 1, 1, null));

        // Extensions
        // Extensions - general
        ExtensionsType extensions = profile.getExtensions();

        // Extensions - occurrences
        List<ExtensionType> list = extensions.getExtension();
        list.add(createExtension(Extension.subjectKeyIdentifier, true, false));
        if(cross)
        {
            list.add(createExtension(Extension.authorityKeyIdentifier, true, false));
        }
        list.add(createExtension(Extension.authorityInfoAccess, false, false));
        list.add(createExtension(Extension.cRLDistributionPoints, false, false));
        list.add(createExtension(Extension.freshestCRL, false, false));
        list.add(createExtension(Extension.keyUsage, true, true));
        list.add(createExtension(Extension.basicConstraints, true, true));

        // Extensions - keyUsage
        extensions.setKeyUsage(createKeyUsages(KeyUsageType.KEYCERT_SIGN, KeyUsageType.CRL_SIGN));
        return profile;
    }

    private static X509ProfileType CertProfile_SubCA()
    throws Exception
    {
        X509ProfileType profile = getBaseProfile("CertProfile SubCA", true, "8y", false);

        // Subject
        Subject subject = profile.getSubject();

        List<RdnType> occurrences = subject.getRdn();
        occurrences.add(createRDN(ObjectIdentifiers.DN_C, 1, 1, new String[]{"DE|FR"}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_O, 1, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_OU, 0, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_SN, 0, 1, new String[]{REGEX_SN}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_CN, 1, 1, null));

        // Extensions
        // Extensions - general
        ExtensionsType extensions = profile.getExtensions();
        extensions.setPathLen(1);

        // Extensions - occurrences
        List<ExtensionType> list = extensions.getExtension();
        list.add(createExtension(Extension.subjectKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityInfoAccess, false, false));
        list.add(createExtension(Extension.cRLDistributionPoints, false, false));
        list.add(createExtension(Extension.freshestCRL, false, false));
        list.add(createExtension(Extension.keyUsage, true, true));
        list.add(createExtension(Extension.basicConstraints, true, true));

        // Extensions - keyUsage
        extensions.setKeyUsage(createKeyUsages(KeyUsageType.KEYCERT_SIGN, KeyUsageType.CRL_SIGN));
        return profile;
    }

    private static X509ProfileType CertProfile_SubCA_Complex()
    throws Exception
    {
        X509ProfileType profile = getBaseProfile("CertProfile SubCA with most extensions", true, "8y", false);

        // Subject
        Subject subject = profile.getSubject();

        List<RdnType> occurrences = subject.getRdn();
        occurrences.add(createRDN(ObjectIdentifiers.DN_C, 1, 1, new String[]{"DE|FR"}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_O, 1, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_OU, 0, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_SN, 0, 1, new String[]{REGEX_SN}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_CN, 1, 1, null, "PREFIX ", " SUFFIX"));

        // Extensions
        // Extensions - general
        ExtensionsType extensions = profile.getExtensions();
        extensions.setPathLen(1);

        // Extensions - occurrences
        List<ExtensionType> list = extensions.getExtension();
        list.add(createExtension(Extension.subjectKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityInfoAccess, false, false));
        list.add(createExtension(Extension.cRLDistributionPoints, false, false));
        list.add(createExtension(Extension.freshestCRL, false, false));
        list.add(createExtension(Extension.keyUsage, true, true));
        list.add(createExtension(Extension.basicConstraints, true, true));
        list.add(createExtension(Extension.subjectAlternativeName, true, false));
        list.add(createExtension(Extension.subjectInfoAccess, true, false));

        list.add(createExtension(Extension.policyMappings, true, true));
        list.add(createExtension(Extension.nameConstraints, true, true));
        list.add(createExtension(Extension.policyConstraints, true, true));
        list.add(createExtension(Extension.inhibitAnyPolicy, true, true));

        ASN1ObjectIdentifier customExtensionOid = new ASN1ObjectIdentifier("1.2.3.4");
        list.add(createExtension(customExtensionOid, true, false, "custom extension 1"));

        // Extensions - keyUsage
        extensions.setKeyUsage(createKeyUsages(KeyUsageType.KEYCERT_SIGN, KeyUsageType.CRL_SIGN));

        // Certificate Policies
        ExtensionsType.CertificatePolicies certificatePolicies = createCertificatePolicies(
                new ASN1ObjectIdentifier("1.2.3.4.5"), new ASN1ObjectIdentifier("2.4.3.2.1"));
        extensions.setCertificatePolicies(certificatePolicies);

        // Policy Mappings
        PolicyMappings policyMappings = new PolicyMappings();
        extensions.setPolicyMappings(policyMappings);
        policyMappings.getMapping().add(createPolicyIdMapping(
                new ASN1ObjectIdentifier("1.1.1.1.1"),
                new ASN1ObjectIdentifier("2.1.1.1.1")));
        policyMappings.getMapping().add(createPolicyIdMapping(
                new ASN1ObjectIdentifier("1.1.1.1.2"),
                new ASN1ObjectIdentifier("2.1.1.1.2")));

        // Policy Constraints
        extensions.setPolicyConstraints(createPolicyConstraints(2, 2));

        // Name Constrains
        extensions.setNameConstraints(createNameConstraints());

        // Inhibit anyPolicy
        extensions.setInhibitAnyPolicy(createInhibitAnyPolicy(1));

        // SubjectAltName
        GeneralNameType subjectAltNameMode = new GeneralNameType();
        extensions.setSubjectAltName(subjectAltNameMode);

        OtherName otherName = new OtherName();
        otherName.getType().add(createOidType(ObjectIdentifiers.DN_O));
        subjectAltNameMode.setOtherName(otherName);
        subjectAltNameMode.setRfc822Name("");
        subjectAltNameMode.setDNSName("");
        subjectAltNameMode.setDirectoryName("");
        subjectAltNameMode.setEdiPartyName("");
        subjectAltNameMode.setUniformResourceIdentifier("");
        subjectAltNameMode.setIPAddress("");
        subjectAltNameMode.setRegisteredID("");

        // SubjectInfoAccess
        SubjectInfoAccessType subjectInfoAccessMode = new SubjectInfoAccessType();
        extensions.setSubjectInfoAccess(subjectInfoAccessMode);

        Access access = new Access();
        access.setAccessMethod(createOidType(ObjectIdentifiers.id_ad_caRepository));

        GeneralNameType accessLocation = new GeneralNameType();
        access.setAccessLocation(accessLocation);
        accessLocation.setDirectoryName("");
        accessLocation.setUniformResourceIdentifier("");

        subjectInfoAccessMode.getAccess().add(access);

        // Custom Extension
        ConstantExtensions constantExts = new ConstantExtensions();
        extensions.setConstantExtensions(constantExts);

        ConstantExtensionType constantExt = new ConstantExtensionType();
        constantExts.getConstantExtension().add(constantExt);

        OidWithDescType type = createOidType(customExtensionOid, "custom extension 1");
        constantExt.setType(type);
        constantExt.setValue(DERNull.INSTANCE.getEncoded());

        return profile;
    }

    private static X509ProfileType CertProfile_OCSP()
    throws Exception
    {
        X509ProfileType profile = getBaseProfile("CertProfile OCSP", false, "5y", false);

        // Subject
        Subject subject = profile.getSubject();

        List<RdnType> occurrences = subject.getRdn();
        occurrences.add(createRDN(ObjectIdentifiers.DN_C, 1, 1, new String[]{"DE|FR"}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_O, 1, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_OU, 0, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_SN, 0, 1, new String[]{REGEX_SN}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_CN, 1, 1, null));

        // Extensions
        // Extensions - general
        ExtensionsType extensions = profile.getExtensions();

        // Extensions - occurrences
        List<ExtensionType> list = extensions.getExtension();
        list.add(createExtension(Extension.subjectKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityInfoAccess, false, false));
        list.add(createExtension(Extension.cRLDistributionPoints, false, false));
        list.add(createExtension(Extension.freshestCRL, false, false));
        list.add(createExtension(Extension.keyUsage, true, true));
        list.add(createExtension(Extension.basicConstraints, true, true));
        list.add(createExtension(Extension.extendedKeyUsage, true, false));
        list.add(createExtension(ObjectIdentifiers.id_extension_pkix_ocsp_nocheck, false,false));

        // Extensions - keyUsage
        extensions.setKeyUsage(createKeyUsages(KeyUsageType.CONTENT_COMMITMENT));

        // Extensions - extenedKeyUsage

        extensions.setExtendedKeyUsage(createExtendedKeyUsage(
                ObjectIdentifiers.id_kp_OCSPSigning));

        return profile;
    }

    private static X509ProfileType CertProfile_TLS()
    throws Exception
    {
        X509ProfileType profile = getBaseProfile("CertProfile TLS", false, "5y", true);

        // Subject
        Subject subject = profile.getSubject();

        List<RdnType> occurrences = subject.getRdn();
        occurrences.add(createRDN(ObjectIdentifiers.DN_C, 1, 1, new String[]{"DE|FR"}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_O, 1, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_OU, 0, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_SN, 0, 1, new String[]{REGEX_SN}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_CN, 1, 1, new String[]{REGEX_FQDN}));

        // Extensions
        // Extensions - general
        ExtensionsType extensions = profile.getExtensions();

        // Extensions - occurrences
        List<ExtensionType> list = extensions.getExtension();
        list.add(createExtension(Extension.subjectKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityInfoAccess, false, false));
        list.add(createExtension(Extension.cRLDistributionPoints, false, false));
        list.add(createExtension(Extension.freshestCRL, false, false));
        list.add(createExtension(Extension.keyUsage, true, true));
        list.add(createExtension(Extension.basicConstraints, true, true));
        list.add(createExtension(Extension.extendedKeyUsage, true, false));
        list.add(createExtension(ObjectIdentifiers.id_extension_admission, true, false));

        // Extensions - keyUsage
        extensions.setKeyUsage(createKeyUsages(KeyUsageType.DIGITAL_SIGNATURE,
                KeyUsageType.DATA_ENCIPHERMENT, KeyUsageType.KEY_ENCIPHERMENT));

        // Extensions - extenedKeyUsage
        extensions.setExtendedKeyUsage(createExtendedKeyUsage(
                ObjectIdentifiers.id_kp_clientAuth, ObjectIdentifiers.id_kp_serverAuth));

        // Admission - just DEMO, does not belong to TLS certificate
        Admission admission = createAdmission(new ASN1ObjectIdentifier("1.1.1.2"), "demo item");
        extensions.setAdmission(admission);

        return profile;
    }

    private static X509ProfileType CertProfile_TLS_C()
    throws Exception
    {
        X509ProfileType profile = getBaseProfile("CertProfile TLS_C", false, "5y", false);

        // Subject
        Subject subject = profile.getSubject();

        List<RdnType> occurrences = subject.getRdn();
        occurrences.add(createRDN(ObjectIdentifiers.DN_C, 1, 1, new String[]{"DE|FR"}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_O, 1, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_OU, 0, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_SN, 0, 1, new String[]{REGEX_SN}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_CN, 1, 1, null));

        // Extensions
        // Extensions - general
        ExtensionsType extensions = profile.getExtensions();
        // Extensions - occurrences
        List<ExtensionType> list = extensions.getExtension();
        list.add(createExtension(Extension.subjectKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityInfoAccess, false, false));
        list.add(createExtension(Extension.cRLDistributionPoints, false, false));
        list.add(createExtension(Extension.freshestCRL, false, false));
        list.add(createExtension(Extension.keyUsage, true, true));
        list.add(createExtension(Extension.basicConstraints, true, true));
        list.add(createExtension(Extension.extendedKeyUsage, true, false));

        // Extensions - keyUsage
        extensions.setKeyUsage(createKeyUsages(KeyUsageType.DIGITAL_SIGNATURE,
                KeyUsageType.DATA_ENCIPHERMENT, KeyUsageType.KEY_ENCIPHERMENT));

        // Extensions - extenedKeyUsage
        extensions.setExtendedKeyUsage(createExtendedKeyUsage(
                ObjectIdentifiers.id_kp_clientAuth));
        return profile;
    }

    private static X509ProfileType CertProfile_TLSwithIncSN()
    throws Exception
    {
        X509ProfileType profile = getBaseProfile("CertProfile TLSwithIncSN", false, "5y", false);

        // Subject
        Subject subject = profile.getSubject();

        List<RdnType> occurrences = subject.getRdn();
        occurrences.add(createRDN(ObjectIdentifiers.DN_C, 1, 1, new String[]{"DE|FR"}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_O, 1, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_OU, 0, 1, null));
        occurrences.add(createRDN(ObjectIdentifiers.DN_SN, 0, 1, new String[]{REGEX_SN}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_CN, 1, 1, new String[]{REGEX_FQDN}));

        // Extensions
        // Extensions - general
        ExtensionsType extensions = profile.getExtensions();

        // Extensions - occurrences
        List<ExtensionType> list = extensions.getExtension();
        list.add(createExtension(Extension.subjectKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityInfoAccess, false, false));
        list.add(createExtension(Extension.cRLDistributionPoints, false, false));
        list.add(createExtension(Extension.freshestCRL, false, false));
        list.add(createExtension(Extension.keyUsage, true, true));
        list.add(createExtension(Extension.basicConstraints, true, true));
        list.add(createExtension(Extension.extendedKeyUsage, true, false));

        // Extensions - keyUsage
        extensions.setKeyUsage(createKeyUsages(KeyUsageType.DIGITAL_SIGNATURE,
                KeyUsageType.DATA_ENCIPHERMENT, KeyUsageType.KEY_ENCIPHERMENT));

        // Extensions - extenedKeyUsage
        extensions.setExtendedKeyUsage(createExtendedKeyUsage(
                ObjectIdentifiers.id_kp_clientAuth, ObjectIdentifiers.id_kp_serverAuth));

        return profile;
    }

    private static RdnType createRDN(ASN1ObjectIdentifier type, int min, int max, String[] regexArrays)
    {
        return createRDN(type, min, max, regexArrays, null, null);
    }

    private static RdnType createRDN(ASN1ObjectIdentifier type, int min, int max, String[] regexArrays,
            String prefix, String suffix)
    {
        RdnType ret = new RdnType();
        ret.setType(createOidType(type));
        ret.setMinOccurs(min);
        ret.setMaxOccurs(max);
        ret.setPrefix(prefix);
        ret.setSuffix(suffix);
        if(regexArrays != null)
        {
            if(regexArrays.length != max)
            {
                throw new IllegalArgumentException("regexArrays.length " + regexArrays.length + " != max " + max);
            }
            for(String regex : regexArrays)
            {
                ret.getRegex().add(regex);
            }
        }
        return ret;
    }

    private static ExtensionType createExtension(ASN1ObjectIdentifier type, boolean required, boolean critical)
    {
        return createExtension(type, required, critical, null);
    }

    private static ExtensionType createExtension(ASN1ObjectIdentifier type, boolean required, boolean critical,
            String description)
    {
        ExtensionType ret = new ExtensionType();
        ret.setValue(type.getId());
        ret.setRequired(required);
        ret.setCritical(critical);
        if(description == null)
        {
            description = getDescription(type);
        }

        if(description != null)
        {
            ret.setDescription(description);
        }
        return ret;
    }

    private static org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.KeyUsage createKeyUsages(
            KeyUsageType... keyUsages)
    {
        org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.KeyUsage ret =
                new org.xipki.ca.qa.certprofile.x509.jaxb.ExtensionsType.KeyUsage();
        for(KeyUsageType usage : keyUsages)
        {
            ret.getUsage().add(usage);
        }
        return ret;
    }

    private static Admission createAdmission(ASN1ObjectIdentifier oid, String item)
    {
        Admission ret = new Admission();
        ret.getProfessionItem().add(item);
        ret.getProfessionOid().add(createOidType(oid));
        return ret;
    }

    private static ExtensionsType.CertificatePolicies createCertificatePolicies(
            ASN1ObjectIdentifier... policyOids)
    {
        if(policyOids == null || policyOids.length == 0)
        {
            return null;
        }

        ExtensionsType.CertificatePolicies ret = new ExtensionsType.CertificatePolicies();
        List<CertificatePolicyInformationType> l = ret.getCertificatePolicyInformation();
        for(ASN1ObjectIdentifier oid : policyOids)
        {
            CertificatePolicyInformationType single = new CertificatePolicyInformationType();
            l.add(single);
            single.setPolicyIdentifier(createOidType(oid));
        }

        return ret;
    }

    private static ExtendedKeyUsage createExtendedKeyUsage(
            ASN1ObjectIdentifier... extKeyUsages)
    {
        ExtendedKeyUsage ret = new ExtendedKeyUsage();
        for(ASN1ObjectIdentifier usage : extKeyUsages)
        {
            ret.getUsage().add(createOidType(usage));
        }
        return ret;
    }

    private static String getDescription(ASN1ObjectIdentifier oid)
    {
        String desc = ObjectIdentifiers.getName(oid);
        if(desc == null)
        {
            desc = oidDescMap.get(oid);
        }

        return desc;
    }

    private static PolicyIdMappingType createPolicyIdMapping(
        ASN1ObjectIdentifier issuerPolicyId,
        ASN1ObjectIdentifier subjectPolicyId)
    {
        PolicyIdMappingType ret = new PolicyIdMappingType();
        ret.setIssuerDomainPolicy(createOidType(issuerPolicyId));
        ret.setSubjectDomainPolicy(createOidType(subjectPolicyId));

        return ret;
    }

    private static PolicyConstraints createPolicyConstraints(Integer inhibitPolicyMapping,
            Integer requireExplicitPolicy)
    {
        PolicyConstraints ret = new PolicyConstraints();
        if(inhibitPolicyMapping != null)
        {
            ret.setInhibitPolicyMapping(inhibitPolicyMapping);
        }

        if(requireExplicitPolicy != null)
        {
            ret.setRequireExplicitPolicy(requireExplicitPolicy);
        }
        return ret;
    }

    private static NameConstraints createNameConstraints()
    {
        NameConstraints ret = new NameConstraints();
        GeneralSubtreesType permitted = new GeneralSubtreesType();
        GeneralSubtreeBaseType single = new GeneralSubtreeBaseType();
        single.setDirectoryName("O=example organization, C=DE");
        permitted.getBase().add(single);
        ret.setPermittedSubtrees(permitted);

        GeneralSubtreesType excluded = new GeneralSubtreesType();
        single = new GeneralSubtreeBaseType();
        single.setDirectoryName("OU=bad OU, O=example organization, C=DE");
        excluded.getBase().add(single);
        ret.setExcludedSubtrees(excluded);

        return ret;
    }

    private static InhibitAnyPolicy createInhibitAnyPolicy(int skipCerts)
    {
        InhibitAnyPolicy ret = new InhibitAnyPolicy();
        ret.setSkipCerts(skipCerts);
        return ret;
    }

    private static OidWithDescType createOidType(ASN1ObjectIdentifier oid)
    {
        return createOidType(oid, null);
    }

    private static OidWithDescType createOidType(ASN1ObjectIdentifier oid, String description)
    {
        OidWithDescType ret = new OidWithDescType();
        ret.setValue(oid.getId());
        if(description == null)
        {
            description = getDescription(oid);
        }
        if(description != null)
        {
            ret.setDescription(description);
        }
        return ret;
    }

    private static X509ProfileType CertProfile_gSMC_K()
    throws Exception
    {
        X509ProfileType profile = getBaseProfile("CertProfile gSMC_K", false, "5y", false);

        profile.setSpecialBehavior("gematik_gSMC_K");

        // Subject
        Subject subject = profile.getSubject();

        List<RdnType> rdns = subject.getRdn();
        rdns.add(createRDN(ObjectIdentifiers.DN_C, 1, 1, new String[]{"DE"}));
        rdns.add(createRDN(ObjectIdentifiers.DN_O, 1, 1, null));
        rdns.add(createRDN(ObjectIdentifiers.DN_OU, 0, 1, null));
        rdns.add(createRDN(ObjectIdentifiers.DN_ST, 0, 1, null));
        rdns.add(createRDN(ObjectIdentifiers.DN_L, 0, 1, null));
        rdns.add(createRDN(ObjectIdentifiers.DN_POSTAL_CODE, 0, 1, null));
        rdns.add(createRDN(ObjectIdentifiers.DN_STREET, 0, 1, null));
        // regex: ICCSN-yyyyMMdd
        String regex = "80276[\\d]{15,15}-20\\d\\d(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])";
        rdns.add(createRDN(ObjectIdentifiers.DN_CN, 1, 1, new String[]{regex}));

        // Extensions
        // Extensions - general
        ExtensionsType extensions = profile.getExtensions();

        // Extensions - occurrences
        List<ExtensionType> list = extensions.getExtension();
        list.add(createExtension(Extension.subjectKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityInfoAccess, true, false));
        list.add(createExtension(Extension.cRLDistributionPoints, false, false));
        list.add(createExtension(Extension.keyUsage, true, true));
        list.add(createExtension(Extension.subjectAlternativeName, false, false));
        list.add(createExtension(Extension.basicConstraints, true, true));
        list.add(createExtension(Extension.certificatePolicies, true, false));
        list.add(createExtension(ObjectIdentifiers.id_extension_admission, true, false));
        list.add(createExtension(Extension.extendedKeyUsage, true, false));

        // Extensions - keyUsage
        extensions.setKeyUsage(createKeyUsages(KeyUsageType.DIGITAL_SIGNATURE, KeyUsageType.KEY_ENCIPHERMENT));

        // Extensions - extenedKeyUsage
        extensions.setExtendedKeyUsage(createExtendedKeyUsage(
                ObjectIdentifiers.id_kp_clientAuth, ObjectIdentifiers.id_kp_serverAuth));

        // Extensions - Policy
        CertificatePolicies policies = new CertificatePolicies();
        extensions.setCertificatePolicies(policies);

        ASN1ObjectIdentifier[] policyIds = new ASN1ObjectIdentifier[]
        {
                id_gematik.branch("79"), id_gematik.branch("163")};
        for(ASN1ObjectIdentifier id : policyIds)
        {
            CertificatePolicyInformationType policyInfo = new CertificatePolicyInformationType();
            policies.getCertificatePolicyInformation().add(policyInfo);
            policyInfo.setPolicyIdentifier(createOidType(id));
        }

        // Extension - Adminssion
        Admission admission = new Admission();
        extensions.setAdmission(admission);
        admission.getProfessionOid().add(createOidType(id_gematik.branch("103")));
        admission.getProfessionItem().add("Anwendungskonnektor");

        return profile;
    }

    private static X509ProfileType CertProfile_MultipleOUs()
    throws Exception
    {
        X509ProfileType profile = getBaseProfile("CertProfile Multiple OUs DEMO", false, "5y", false);

        // Subject
        Subject subject = profile.getSubject();

        List<RdnType> occurrences = subject.getRdn();
        occurrences.add(createRDN(ObjectIdentifiers.DN_C, 1, 1, new String[]{"DE|FR"}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_O, 1, 1, null));

        final String regex_ou1 = "[A-Z]{1,1}[\\d]{5,5}";
        final String regex_ou2 = "[\\d]{5,5}";
        occurrences.add(createRDN(ObjectIdentifiers.DN_OU, 2, 2, new String[]{regex_ou1,regex_ou2}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_SN, 0, 1, new String[]{REGEX_SN}));
        occurrences.add(createRDN(ObjectIdentifiers.DN_CN, 1, 1, null));

        // Extensions
        // Extensions - general
        ExtensionsType extensions = profile.getExtensions();

        // Extensions - occurrences
        List<ExtensionType> list = extensions.getExtension();
        list.add(createExtension(Extension.subjectKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityKeyIdentifier, true, false));
        list.add(createExtension(Extension.authorityInfoAccess, false, false));
        list.add(createExtension(Extension.cRLDistributionPoints, false, false));
        list.add(createExtension(Extension.freshestCRL, false, false));
        list.add(createExtension(Extension.keyUsage, true, true));
        list.add(createExtension(Extension.basicConstraints, true, true));

        // Extensions - keyUsage
        extensions.setKeyUsage(createKeyUsages(KeyUsageType.CONTENT_COMMITMENT));

        return profile;
    }

    private static X509ProfileType getBaseProfile(String description, boolean ca,
            String validity, boolean useMidnightNotBefore)
    {
        X509ProfileType profile = new X509ProfileType();
        profile.setDescription(description);
        profile.setCa(ca);
        profile.setVersion(3);
        profile.setValidity(validity);
        profile.setNotBeforeTime(useMidnightNotBefore ? "midnight" : "current");

        // Subject
        Subject subject = new Subject();
        profile.setSubject(subject);

        // Key
        profile.setKeyAlgorithms(createKeyAlgorithms());

        // Extensions
        // Extensions - general
        ExtensionsType extensions = new ExtensionsType();
        profile.setExtensions(extensions);

        AuthorityKeyIdentifier akiType = new AuthorityKeyIdentifier();
        akiType.setIncludeIssuerAndSerial(Boolean.FALSE);
        extensions.setAuthorityKeyIdentifier(akiType);

        return profile;
    }

    private static KeyAlgorithms createKeyAlgorithms()
    {
        KeyAlgorithms ret = new KeyAlgorithms();
        List<AlgorithmType> list = ret.getAlgorithm();

        // RSA
        {
            AlgorithmType rsa = new AlgorithmType();
            list.add(rsa);

            rsa.setAlgorithm(createOidType(PKCSObjectIdentifiers.rsaEncryption, "RSA"));
            List<ParameterType> params = rsa.getParameter();

            ParameterType param = new ParameterType();
            params.add(param);
            param.setName(X509CertProfileQA.MODULUS_LENGTH);
            param.setMin(2048);
            param.setMax(2048);

            param = new ParameterType();
            param.setName(X509CertProfileQA.MODULUS_LENGTH);
            params.add(param);
            param.setMin(3072);
            param.setMax(3072);
        }

        // DSA
        {
            AlgorithmType dsa = new AlgorithmType();
            list.add(dsa);

            dsa.setAlgorithm(createOidType(X9ObjectIdentifiers.id_dsa, "DSA"));
            List<ParameterType> params = dsa.getParameter();

            ParameterType param = new ParameterType();
            params.add(param);
            param.setName(X509CertProfileQA.P_LENGTH);
            param.setMin(1024);
            param.setMax(1024);

            param = new ParameterType();
            params.add(param);
            param.setName(X509CertProfileQA.P_LENGTH);
            param.setMin(2048);
            param.setMax(2048);

            param = new ParameterType();
            params.add(param);
            param.setName(X509CertProfileQA.Q_LENGTH);
            param.setMin(160);
            param.setMax(160);

            param = new ParameterType();
            params.add(param);
            param.setName(X509CertProfileQA.Q_LENGTH);
            param.setMin(224);
            param.setMax(224);

            param = new ParameterType();
            params.add(param);
            param.setName(X509CertProfileQA.Q_LENGTH);
            param.setMin(256);
            param.setMax(256);
        }

        // EC
        {
            AlgorithmType ec = new AlgorithmType();
            ec.setAlgorithm(createOidType(X9ObjectIdentifiers.id_ecPublicKey, "EC"));

            list.add(ec);

            ECParameterType params = new ECParameterType();
            ec.setEcParameter(params);

            ASN1ObjectIdentifier[] curveIds = new ASN1ObjectIdentifier[]
                    { SECObjectIdentifiers.secp256r1, TeleTrusTObjectIdentifiers.brainpoolP256r1};

            for(ASN1ObjectIdentifier curveId : curveIds)
            {
                String name = SecurityUtil.getCurveName(curveId);
                CurveType curve = new CurveType();
                curve.setOid(createOidType(curveId, name));

                Encodings encodings = new Encodings();
                encodings.getEncoding().add((byte) 4); // uncompressed
                curve.setEncodings(encodings);

                params.getCurve().add(curve);
            }
        }

        return ret;
    }

}
