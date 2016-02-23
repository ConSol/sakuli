/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sakuli.services.forwarder.icinga2;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.filter.LoggingFilter;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * @author tschneck
 *         Date: 2/22/16
 */
@ProfileIcinga2
@Component
public class Icinga2RestCient {

    private static final Logger LOGGER = LoggerFactory.getLogger(Icinga2RestCient.class);

    @Autowired
    private Icinga2Properties properties;
    private Client icingaClient;

    @PostConstruct
    public void initIcingaClient() {
        icingaClient = ClientBuilder.newBuilder()
                //disable hostname verification
                .hostnameVerifier((s, sslSession) -> true)
                //ignore SSLHandshakes
                .sslContext(getTrustEverythingSSLContext())
                .build()
                .register(HttpAuthenticationFeature.basic(properties.getApiUsername(), properties.getApiPassword()))
                .register(new ErrorResponseFilter());
        if (LOGGER.isDebugEnabled()) {
            icingaClient.register(LoggingFilter.class);
        }
    }

    /**
     * @return the ready configered URL as {@link WebTarget}, to make a REST-Call to the Icinga2 API
     */
    public WebTarget getTargetCheckResult() {
        if (icingaClient == null) {
            initIcingaClient();
        }
        return icingaClient.target(properties.getApiURL());
    }

    private SSLContext getTrustEverythingSSLContext() {
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");

            // set up a TrustManager that trusts everything
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            throw new SakuliRuntimeException("Unable to create SSL-Context", e);
        }
    }

    class ErrorResponseFilter implements ClientResponseFilter {

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            // for non-200 response, deal with the custom error messages
            if (responseContext.getStatus() != Response.Status.OK.getStatusCode()) {
                if (responseContext.hasEntity()) {
                    // get the "real" error message
                    String error = IOUtils.toString(responseContext.getEntityStream());

                    throw new IOException("[" + responseContext.getStatusInfo().getStatusCode() + "] " + responseContext.getStatusInfo() + ": " + error);
                }
            }
        }
    }
}
