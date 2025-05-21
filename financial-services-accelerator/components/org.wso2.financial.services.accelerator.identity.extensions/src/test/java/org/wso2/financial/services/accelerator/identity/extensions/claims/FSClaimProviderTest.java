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

package org.wso2.financial.services.accelerator.identity.extensions.claims;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AuthorizeReqDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AuthorizeRespDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.financial.services.accelerator.common.constant.FinancialServicesConstants;
import org.wso2.financial.services.accelerator.common.util.JWTUtils;
import org.wso2.financial.services.accelerator.identity.extensions.internal.IdentityExtensionsDataHolder;
import org.wso2.financial.services.accelerator.identity.extensions.util.IdentityCommonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for FSClaimProvider.
 */
public class FSClaimProviderTest {

    private FSClaimProvider fsClaimProvider;
    private static MockedStatic<JWTUtils> mockedJwtUtils;
    private static MockedStatic<IdentityCommonUtils> mockedIdentityCommonUtils;

    @BeforeClass
    public void beforeClass() {
        fsClaimProvider = new FSClaimProvider();
        mockedJwtUtils = Mockito.mockStatic(JWTUtils.class);
        mockedIdentityCommonUtils = Mockito.mockStatic(IdentityCommonUtils.class);

        mockedIdentityCommonUtils.when(() -> IdentityCommonUtils
                .removeInternalScopes(Mockito.any())).thenReturn(new String[]{"accounts", "payments"});

        Map<String, Object> configMap = new HashMap<>();
        configMap.put(FinancialServicesConstants.CONSENT_ID_CLAIM_NAME, "consent_id");
        configMap.put(FinancialServicesConstants.APPEND_CONSENT_ID_TO_TOKEN_ID_TOKEN, "true");
        IdentityExtensionsDataHolder.getInstance().setConfigurationMap(configMap);
    }

    @AfterClass
    public static void afterClass() {
        mockedJwtUtils.close();
        mockedIdentityCommonUtils.close();
    }

    @Test
    public void testDefaultGetAdditionalClaimsAuthzResponse() throws IdentityOAuth2Exception {
        OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext = new OAuthAuthzReqMessageContext(
                new OAuth2AuthorizeReqDTO());
        OAuth2AuthorizeRespDTO oAuth2AuthorizeRespDTO = new OAuth2AuthorizeRespDTO();

        fsClaimProvider.getAdditionalClaims(oAuthAuthzReqMessageContext, oAuth2AuthorizeRespDTO);
    }

    @Test
    public void testDefaultGetAdditionalClaimsTokenResponse() throws IdentityOAuth2Exception {
        OAuthTokenReqMessageContext oAuthTokenReqMessageContext = new OAuthTokenReqMessageContext(
                new OAuth2AccessTokenReqDTO());
        OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO = new OAuth2AccessTokenRespDTO();
        oAuth2AccessTokenRespDTO.setAuthorizedScopes("accounts payments");

        fsClaimProvider.getAdditionalClaims(oAuthTokenReqMessageContext, oAuth2AccessTokenRespDTO);
    }

    @Test
    public void testDefaultClassGetAdditionalClaimsAuthzResponse() throws IdentityOAuth2Exception {
        OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext = new OAuthAuthzReqMessageContext(
                new OAuth2AuthorizeReqDTO());
        OAuth2AuthorizeRespDTO oAuth2AuthorizeRespDTO = new OAuth2AuthorizeRespDTO();

        FSClaimProvider.setClaimProvider(new FSDefaultClaimProvider());
        fsClaimProvider.getAdditionalClaims(oAuthAuthzReqMessageContext, oAuth2AuthorizeRespDTO);
    }

    @Test
    public void testDefaultClassGetAdditionalClaimsTokenResponse() throws IdentityOAuth2Exception {
        OAuthTokenReqMessageContext oAuthTokenReqMessageContext = new OAuthTokenReqMessageContext(
                new OAuth2AccessTokenReqDTO());
        OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO = new OAuth2AccessTokenRespDTO();
        oAuth2AccessTokenRespDTO.setAuthorizedScopes("accounts payments");

        FSClaimProvider.setClaimProvider(new FSDefaultClaimProvider());
        fsClaimProvider.getAdditionalClaims(oAuthTokenReqMessageContext, oAuth2AccessTokenRespDTO);
    }

    @Test
    public void testUpdateScopeInTokenResponseBody() throws IdentityOAuth2Exception {
        OAuthTokenReqMessageContext oAuthTokenReqMessageContext = new OAuthTokenReqMessageContext(
                new OAuth2AccessTokenReqDTO());
        OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO = new OAuth2AccessTokenRespDTO();
        oAuth2AccessTokenRespDTO.setAuthorizedScopes("accounts payments internal_scope");

        mockedIdentityCommonUtils.when(() -> IdentityCommonUtils
                .removeInternalScopes(Mockito.any()))
                .thenReturn(new String[]{"accounts", "payments"});

        fsClaimProvider.getAdditionalClaims(oAuthTokenReqMessageContext, oAuth2AccessTokenRespDTO);

        assert oAuth2AccessTokenRespDTO.getAuthorizedScopes().equals("accounts payments");
    }

}
