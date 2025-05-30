/**
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
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

package org.wso2.bfsi.test.framework.automation

import org.openqa.selenium.remote.RemoteWebDriver
import java.util.concurrent.TimeUnit

/**
 * Class for Navigate Automation step
 */
class NavigationAutomationStep implements BrowserAutomationStep {

    private String authorizeUrl
    private int timeOutSeconds

    /**
     * Initialize Basic Auth Flow.
     *
     * @param authorizeUrl authorise URL.
     */
    NavigationAutomationStep(String authorizeUrl, int timeOut) {
        this.authorizeUrl = authorizeUrl
        this.timeOutSeconds = timeOut
    }

    /**
     * Execute automation using driver.
     *
     * @param webDriver driver object.
     * @param context automation context.
     */
    @Override
    void execute(RemoteWebDriver webDriver, BrowserAutomation.AutomationContext context) {
        webDriver.navigate().to(authorizeUrl)
        webDriver.manage().timeouts().implicitlyWait(timeOutSeconds, TimeUnit.SECONDS)
    }

}

