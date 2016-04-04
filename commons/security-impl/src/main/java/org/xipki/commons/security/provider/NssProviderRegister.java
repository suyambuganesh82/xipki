/*
 *
 * This file is part of the XiPKI project.
 * Copyright (c) 2013 - 2016 Lijun Liao
 * Author: Lijun Liao
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 *
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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

package org.xipki.commons.security.provider;

import java.io.ByteArrayInputStream;
import java.security.Provider;
import java.security.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xipki.commons.common.util.LogUtil;
import org.xipki.commons.security.api.XiSecurityConstants;

/**
 * @author Lijun Liao
 * @since 2.0.0
 */

public class NssProviderRegister {
    public static final String PROVIDER_NAME = XiSecurityConstants.PROVIDER_NAME_NSS;

    private static final Logger LOG = LoggerFactory.getLogger(NssProviderRegister.class);

    @SuppressWarnings("restriction")
    public void regist() {
        try {
            // check whether there exists an NSS provider registered by OpenJDK
            Provider nssProvider = Security.getProvider(PROVIDER_NAME);
            if (nssProvider == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("name=").append(PROVIDER_NAME).append("\n");
                sb.append("nssDbMode=noDb\n");
                sb.append("attributes=compatibility\n");
                String nssLib = System.getProperty("NSSLIB");
                if (nssLib != null) {
                    sb.append("\nnssLibraryDirectory=").append(nssLib);
                }

                nssProvider = new sun.security.pkcs11.SunPKCS11(
                        new ByteArrayInputStream(sb.toString().getBytes()));
                Security.addProvider(nssProvider);
                LOG.info("added security provider {}", PROVIDER_NAME);
            } else {
                LOG.info("security provider {} already initialized by other service",
                        PROVIDER_NAME);
            }
        } catch (Throwable th) {
            String msg = "could not initialize SunPKCS11 NSS provider";
            if (LOG.isInfoEnabled()) {
                LOG.info(LogUtil.getErrorLog(msg), th.getClass().getName(), th.getMessage());
            }
            LOG.debug(msg, th);
        }
    }

    public void unregist() {
        if (Security.getProperty(PROVIDER_NAME) != null) {
            Security.removeProvider(PROVIDER_NAME);
            LOG.info("removed security provider {}", PROVIDER_NAME);
        }
    }

}
