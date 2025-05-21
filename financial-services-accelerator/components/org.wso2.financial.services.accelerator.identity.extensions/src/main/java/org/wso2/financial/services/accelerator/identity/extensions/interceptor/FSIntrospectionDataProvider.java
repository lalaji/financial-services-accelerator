/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 * <p>
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.financial.services.accelerator.identity.extensions.interceptor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.IntrospectionDataProvider;
import org.wso2.carbon.identity.oauth2.dto.OAuth2IntrospectionResponseDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationRequestDTO;
import org.wso2.financial.services.accelerator.common.constant.FinancialServicesConstants;
import org.wso2.financial.services.accelerator.common.util.Generated;
import org.wso2.financial.services.accelerator.identity.extensions.internal.IdentityExtensionsDataHolder;
import org.wso2.financial.services.accelerator.identity.extensions.util.IdentityCommonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * FS specific introspection data provider.
 * Main purpose of extending this class is to modify the token introspection response.
 */
public class FSIntrospectionDataProvider implements IntrospectionDataProvider {

    private static final Log log = LogFactory.getLog(FSIntrospectionDataProvider.class);
    private static IntrospectionDataProvider introspectionDataProvider;
    private Map<String, Object> identityConfigurations = IdentityExtensionsDataHolder.getInstance()
            .getConfigurationMap();

    @Override
    public Map<String, Object> getIntrospectionData(OAuth2TokenValidationRequestDTO oAuth2TokenValidationRequestDTO,
                                                    OAuth2IntrospectionResponseDTO oAuth2IntrospectionResponseDTO)
            throws IdentityOAuth2Exception {

        // Perform FS default behaviour
        Map<String, Object> additionalDataMap = new HashMap<>(getDefaultIntrospectionData(
                oAuth2IntrospectionResponseDTO));

        if (getIntrospectionDataProvider() != null) {
            // Perform FS customized behaviour
            additionalDataMap.putAll(getIntrospectionDataProvider()
                    .getIntrospectionData(oAuth2TokenValidationRequestDTO, oAuth2IntrospectionResponseDTO));
        }

        String[] nonInternalScopes = IdentityCommonUtils.removeInternalScopes(oAuth2IntrospectionResponseDTO.getScope()
                .split(FinancialServicesConstants.SPACE_SEPARATOR));
        oAuth2IntrospectionResponseDTO.setScope(StringUtils.join(nonInternalScopes,
                FinancialServicesConstants.SPACE_SEPARATOR));
        additionalDataMap.put(FinancialServicesConstants.SCOPE, StringUtils.join(nonInternalScopes,
                FinancialServicesConstants.SPACE_SEPARATOR));
        oAuth2IntrospectionResponseDTO.setProperties(additionalDataMap);
        return additionalDataMap;
    }

    private Map<String, Object> getDefaultIntrospectionData(
            OAuth2IntrospectionResponseDTO oAuth2IntrospectionResponseDTO) {

        if (oAuth2IntrospectionResponseDTO.isActive()) {

            if (Boolean.parseBoolean((String) identityConfigurations
                    .get(FinancialServicesConstants.APPEND_CONSENT_ID_TO_TOKEN_INTROSPECT_RESPONSE))) {
                Map<String, Object> additionalClaims = new HashMap<>();

                String consentIdClaimName = IdentityCommonUtils.getConfiguredConsentIdClaimName();
                additionalClaims.put(consentIdClaimName, IdentityCommonUtils
                        .getConsentIdFromScopesArray(oAuth2IntrospectionResponseDTO.getScope()
                                .split(FinancialServicesConstants.SPACE_SEPARATOR)));
                return additionalClaims;
            } else {
                return oAuth2IntrospectionResponseDTO.getProperties();
            }
        } else {
            return new HashMap<>();
        }
    }

    @Generated(message = "Ignoring since method contains no logics")
    public static IntrospectionDataProvider getIntrospectionDataProvider() {

        return introspectionDataProvider;
    }

    @Generated(message = "Ignoring since method contains no logics")
    public static void setIntrospectionDataProvider(IntrospectionDataProvider introspectionDataProvider) {

        FSIntrospectionDataProvider.introspectionDataProvider = introspectionDataProvider;
    }
}
