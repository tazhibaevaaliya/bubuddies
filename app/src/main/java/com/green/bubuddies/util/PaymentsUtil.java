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

import android.app.Activity;

import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Contains helper static methods for dealing with the Payments API.
 */
public class PaymentsUtil {

  public static final BigDecimal CENTS_IN_A_UNIT = new BigDecimal(100d);

  /**
   * Create a Google Pay API base request object with properties used in all requests.
   * @return Google Pay API base request object.
   * @throws JSONException
   */
  private static JSONObject getBaseRequest() throws JSONException {
    return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
  }

  /**
   * Creates an instance of {@link PaymentsClient} for use in an {@link Activity} using the
   * environment and theme set in {@link Constants}.
   * @param activity is the caller's activity.
   */
  public static PaymentsClient createPaymentsClient(Activity activity) {
    Wallet.WalletOptions walletOptions =
        new Wallet.WalletOptions.Builder().setEnvironment(Constants.PAYMENTS_ENVIRONMENT).build();
    return Wallet.getPaymentsClient(activity, walletOptions);
  }

  /**
   * Gateway Integration: Identify your gateway and your app's gateway merchant identifier.
   *
   * <p>The Google Pay API response will return an encrypted payment method capable of being charged
   * by a supported gateway after payer authorization.
   *
   * @return Payment data tokenization for the CARD payment method.
   * @throws JSONException
   */
  private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
    return new JSONObject() {{
      put("type", "PAYMENT_GATEWAY");
      put("parameters", new JSONObject() {{
        //Please don't share this information, it is hardcoded for now as we have not integrated this demo with firebase yet.
        put("gateway", "stripe");
        put("stripe:version", "2018-10-31");
        put("stripe:publishableKey", "pk_test_51Jw9fPCiYZgKwLkcenmOHAPC4kVeA2EoP3ijxdjIrImAx4ZH10mRPBSBFqezsUtwIFD7elBGyenHOiICF4kxBmBW00GObLhNsM");
      }});
    }};
  }

  /**
   * {@code DIRECT} Integration: Decrypt a response directly on your servers. This configuration has
   * additional data security requirements from Google and additional PCI DSS compliance complexity.
   *
   *
   * @return Payment data tokenization for the CARD payment method.
   * @throws JSONException
   */
  private static JSONObject getDirectTokenizationSpecification()
      throws JSONException, RuntimeException {
    if (Constants.DIRECT_TOKENIZATION_PARAMETERS.isEmpty()
        || Constants.DIRECT_TOKENIZATION_PUBLIC_KEY.isEmpty()
        || Constants.DIRECT_TOKENIZATION_PUBLIC_KEY == null
        || Constants.DIRECT_TOKENIZATION_PUBLIC_KEY == "REPLACE_ME") {
      throw new RuntimeException(
          "Please edit the Constants.java file to add protocol version & public key.");
    }
    JSONObject tokenizationSpecification = new JSONObject();

    tokenizationSpecification.put("type", "DIRECT");
    JSONObject parameters = new JSONObject(Constants.DIRECT_TOKENIZATION_PARAMETERS);
    tokenizationSpecification.put("parameters", parameters);

    return tokenizationSpecification;
  }

  /**
   * Card networks supported by your app and your gateway.
   *
   * @return Allowed card networks
   */
  private static JSONArray getAllowedCardNetworks() {
    return new JSONArray(Constants.SUPPORTED_NETWORKS);
  }

  /**
   * Card authentication methods supported by your app and your gateway.
   *
   *
   * @return Allowed card authentication methods.
   */
  private static JSONArray getAllowedCardAuthMethods() {
    return new JSONArray(Constants.SUPPORTED_METHODS);
  }

  /**
   * Describe your app's support for the CARD payment method.
   *
   *
   * @return A CARD PaymentMethod object describing accepted cards.
   * @throws JSONException
   */
  private static JSONObject getBaseCardPaymentMethod() throws JSONException {
    JSONObject cardPaymentMethod = new JSONObject();
    cardPaymentMethod.put("type", "CARD");

    JSONObject parameters = new JSONObject();
    parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
    parameters.put("allowedCardNetworks", getAllowedCardNetworks());
    // Optionally, you can add billing address/phone number associated with a CARD payment method.
    parameters.put("billingAddressRequired", true);

    JSONObject billingAddressParameters = new JSONObject();
    billingAddressParameters.put("format", "FULL");

    parameters.put("billingAddressParameters", billingAddressParameters);

    cardPaymentMethod.put("parameters", parameters);

    return cardPaymentMethod;
  }

  /**
   * Describe the expected returned payment data for the CARD payment method
   *
   * @return A CARD PaymentMethod describing accepted cards and optional fields.
   * @throws JSONException
   */
  private static JSONObject getCardPaymentMethod() throws JSONException {
    JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
    cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());

    return cardPaymentMethod;
  }

  /**
   * An object describing accepted forms of payment by your app, used to determine a viewer's
   * readiness to pay.
   *
   * @return API version and payment methods supported by the app.
  */
  public static Optional<JSONObject> getIsReadyToPayRequest() {
    try {
      JSONObject isReadyToPayRequest = getBaseRequest();
      isReadyToPayRequest.put(
          "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

      return Optional.of(isReadyToPayRequest);

    } catch (JSONException e) {
      return Optional.empty();
    }
  }

  /**
   * Provide Google Pay API with a payment amount, currency, and amount status.
   *
   * @return information about the requested payment.
   * @throws JSONException
  */
  private static JSONObject getTransactionInfo(String price) throws JSONException {
    JSONObject transactionInfo = new JSONObject();
    transactionInfo.put("totalPrice", price);
    transactionInfo.put("totalPriceStatus", "FINAL");
    transactionInfo.put("countryCode", Constants.COUNTRY_CODE);
    transactionInfo.put("currencyCode", Constants.CURRENCY_CODE);
    transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");

    return transactionInfo;
  }

  /**
   * Information about the merchant requesting payment information
   *
   * @return Information about the merchant.
   * @throws JSONException
   */
  private static JSONObject getMerchantInfo() throws JSONException {
    return new JSONObject().put("merchantName", "Example Merchant");
  }

  /**
   * An object describing information requested in a Google Pay payment sheet
   *
   * @return Payment data expected by your app.
   */
  public static Optional<JSONObject> getPaymentDataRequest(long priceCents) {

    final String price = PaymentsUtil.centsToString(priceCents);

    try {
      JSONObject paymentDataRequest = PaymentsUtil.getBaseRequest();
      paymentDataRequest.put(
          "allowedPaymentMethods", new JSONArray().put(PaymentsUtil.getCardPaymentMethod()));
      paymentDataRequest.put("transactionInfo", PaymentsUtil.getTransactionInfo(price));
      paymentDataRequest.put("merchantInfo", PaymentsUtil.getMerchantInfo());

      /* An optional shipping address requirement is a top-level property of the PaymentDataRequest
      JSON object. */
      paymentDataRequest.put("shippingAddressRequired", true);

      JSONObject shippingAddressParameters = new JSONObject();
      shippingAddressParameters.put("phoneNumberRequired", false);

      JSONArray allowedCountryCodes = new JSONArray(Constants.SHIPPING_SUPPORTED_COUNTRIES);

      shippingAddressParameters.put("allowedCountryCodes", allowedCountryCodes);
      paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters);
      return Optional.of(paymentDataRequest);

    } catch (JSONException e) {
      return Optional.empty();
    }
  }

  /**
   * Converts cents to a string format accepted by {@link PaymentsUtil#getPaymentDataRequest}.
   *
   * @param cents value of the price in cents.
   */
  public static String centsToString(long cents) {
    return new BigDecimal(cents)
        .divide(CENTS_IN_A_UNIT, RoundingMode.HALF_EVEN)
        .setScale(2, RoundingMode.HALF_EVEN)
        .toString();
  }
}
