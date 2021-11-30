/*
 * Copyright 2020 Google Inc.
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

package com.green.bubuddies.util;

import com.google.android.gms.wallet.WalletConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This file contains several constants
 * Please take a look at PaymentsUtil.java to see where the constants are used
 */
public class Constants {

  /**
   * Changing this to ENVIRONMENT_PRODUCTION will make the API return chargeable card information.
   *
   * @value #PAYMENTS_ENVIRONMENT
   */
  public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;

  /**
   * The allowed networks to be requested from the API. If the user has cards from networks not
   * specified here in their account, these will not be offered for them to choose in the popup.
   *
   * @value #SUPPORTED_NETWORKS
   */
  public static final List<String> SUPPORTED_NETWORKS = Arrays.asList(
      "AMEX",
      "DISCOVER",
      "JCB",
      "MASTERCARD",
      "VISA");

  /**
   * The Google Pay API may return cards on file on Google.com (PAN_ONLY) and/or a device token on
   * an Android device authenticated with a 3-D Secure cryptogram (CRYPTOGRAM_3DS).
   *
   * @value #SUPPORTED_METHODS
   */
  public static final List<String> SUPPORTED_METHODS = Arrays.asList(
      "PAN_ONLY",
      "CRYPTOGRAM_3DS");

  /**
   * Required by the API, but not visible to the user.
   *
   * @value #COUNTRY_CODE Your local country
   */
  public static final String COUNTRY_CODE = "US";

  /**
   * Required by the API, but not visible to the user.
   *
   * @value #CURRENCY_CODE Your local currency
   */
  public static final String CURRENCY_CODE = "USD";

  /**
   * Supported countries for shipping (use ISO 3166-1 alpha-2 country codes). Relevant only when
   * requesting a shipping address.
   *
   * @value #SHIPPING_SUPPORTED_COUNTRIES
   */
  public static final List<String> SHIPPING_SUPPORTED_COUNTRIES = Arrays.asList("US", "GB");

  /**
   * The name of your payment processor/gateway. Please refer to their documentation for more
   * information.
   *
   * @value #PAYMENT_GATEWAY_TOKENIZATION_NAME
   */
  public static final String PAYMENT_GATEWAY_TOKENIZATION_NAME = "stripe";

  /**
   * Custom parameters required by the processor/gateway.
   *
   * @value #PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS
   */
  public static final HashMap<String, String> PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS =
      new HashMap<String, String>() {{
        put("gateway", PAYMENT_GATEWAY_TOKENIZATION_NAME);
        //put("gatewayMerchantId", "exampleGatewayMerchantId"); //not required for stripe
        // Your processor may require additional parameters.
      }};

  /**
   * Only used for {@code DIRECT} tokenization. Can be removed when using {@code PAYMENT_GATEWAY}
   * tokenization.
   *
   * @value #DIRECT_TOKENIZATION_PUBLIC_KEY
   */
  public static final String DIRECT_TOKENIZATION_PUBLIC_KEY = "1234";

  /**
   * Parameters required for {@code DIRECT} tokenization.
   * Only used for {@code DIRECT} tokenization. Can be removed when using {@code PAYMENT_GATEWAY}
   * tokenization.
   *
   * @value #DIRECT_TOKENIZATION_PARAMETERS
   */
  public static final HashMap<String, String> DIRECT_TOKENIZATION_PARAMETERS =
      new HashMap<String, String>() {{
        put("protocolVersion", "ECv2");
        put("publicKey", DIRECT_TOKENIZATION_PUBLIC_KEY);
      }};
}
