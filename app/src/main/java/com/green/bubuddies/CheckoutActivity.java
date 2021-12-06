
package com.green.bubuddies;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.green.bubuddies.databinding.ActivityCheckoutBinding;
import com.green.bubuddies.util.Json;
import com.green.bubuddies.util.PaymentsUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Optional;

/**
 * This is adapted from Google's google pay implementation
 * Checkout implementation for the app
 */
public class CheckoutActivity extends AppCompatActivity {

  // Arbitrarily-picked constant integer you define to track a request for payment data activity.
  private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

  private static final long SHIPPING_COST_CENTS = 90 * PaymentsUtil.CENTS_IN_A_UNIT.longValue();

  // A client for interacting with the Google Pay API.
  private PaymentsClient paymentsClient;

  private ActivityCheckoutBinding layoutBinding;
  private View googlePayButton;

  private String listingID;
  final FirebaseDatabase database = FirebaseDatabase.getInstance();
  DatabaseReference ref = database.getReference("listings");
  private String title;
  private Double price;
  private String picture;
  private String description = "Message the user for more information on the product and to discuss delivery/pick-up options prior to purchasing";
  private String ownerId;

  /**
   * Initialize the Google Pay API on creation of the activity
   *
   * @see Activity#onCreate(Bundle)
   */
  @Override
  //Discuss this
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    listingID = getIntent().getExtras().getString("Listing ID");
    if(listingID == null) listingID = "-MpOD-j6mZMu-UuLKXJC";

    initializeUi();

    displayProduct();

  }

  private void initializeUi() {

    // Use view binding to access the UI elements
    layoutBinding = ActivityCheckoutBinding.inflate(getLayoutInflater());
    setContentView(layoutBinding.getRoot());

    layoutBinding.btnMessageOwner.setClickable(false);
    layoutBinding.btnMessageOwner.setEnabled(false);

    // The Google Pay button is a layout file – take the root view
    googlePayButton = layoutBinding.googlePayButton.getRoot();
    googlePayButton.setOnClickListener(
            new View.OnClickListener() {
              @RequiresApi(api = Build.VERSION_CODES.N)
              @Override
              public void onClick(View view) {
                requestPayment(view);
              }
            });
  }

  private void displayProduct() {
    ref.child(listingID).addListenerForSingleValueEvent(new ValueEventListener() {
      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        title = snapshot.child("title").getValue(String.class);
        price = snapshot.child("price").getValue(Double.class);
        ownerId = snapshot.child("owner").getValue(String.class);
        try {
          picture = snapshot.child("picture").getValue(String.class);
        } catch (Exception e){
          picture = "https://firebasestorage.googleapis.com/v0/b/bubuddies-3272b.appspot.com/o/defaultcheckoutimg.jpg?alt=media&token=7c03e99d-b526-47cc-9756-f505e2c934c6";
        }
        if (picture == null){
          picture = "https://firebasestorage.googleapis.com/v0/b/bubuddies-3272b.appspot.com/o/defaultcheckoutimg.jpg?alt=media&token=7c03e99d-b526-47cc-9756-f505e2c934c6";
        }
        layoutBinding.btnMessageOwner.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent i = new Intent(CheckoutActivity.this,Chat.class);
            i.putExtra("chatwithid",ownerId);
            i.putExtra("from","checkout");
            Log.e("Passing chatwithid", ownerId);
            startActivity(i); // change to messaging tab.
          }
        });
        layoutBinding.btnMessageOwner.setEnabled(true);
        layoutBinding.btnMessageOwner.setClickable(true);

        layoutBinding.detailTitle.setText(title);
        layoutBinding.detailPrice.setText("$" + price.toString());
        Picasso.with(CheckoutActivity.this).load(picture).into(layoutBinding.detailImage);
        layoutBinding.detailDescription.setText(description);

        // Initialize a Google Pay API client for an environment suitable for testing.
        paymentsClient = PaymentsUtil.createPaymentsClient(CheckoutActivity.this);
        possiblyShowGooglePayButton();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }


  /**
   * Determine the viewer's ability to pay with a payment method supported by your app and display a
   * Google Pay payment button.
   */
  //Disucss this
  @RequiresApi(api = Build.VERSION_CODES.N)
  private void possiblyShowGooglePayButton() {

    final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
    if (!isReadyToPayJson.isPresent()) {
      return;
    }

    // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
    // OnCompleteListener to be triggered when the result of the call is known.
    IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
    Task<Boolean> task = paymentsClient.isReadyToPay(request);
    task.addOnCompleteListener(this,
            new OnCompleteListener<Boolean>() {
              @Override
              public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                  setGooglePayAvailable(task.getResult());
                } else {
                  Log.w("isReadyToPay failed", task.getException());
                }
              }
            });
  }

  /**
   * If isReadyToPay returned {@code true}, show the button and hide the "checking" text. Otherwise,
   * notify the user that Google Pay is not available. Please adjust to fit in with your current
   * user flow. You are not required to explicitly let the user know if isReadyToPay returns {@code
   * false}.
   *
   * @param available isReadyToPay API response.
   */
  private void setGooglePayAvailable(boolean available) {
    if (available) {
      googlePayButton.setVisibility(View.VISIBLE);
    } else {
      Toast.makeText(this, R.string.googlepay_status_unavailable, Toast.LENGTH_LONG).show();
    }
  }

  /**
   * PaymentData response object contains the payment information, as well as any additional
   * requested information, such as billing and shipping address.
   *
   * @param paymentData A response object returned by Google after a payer approves payment.
   */
  // IMPORTANT TO DISCUSS
  private void handlePaymentSuccess(PaymentData paymentData) {

    // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
    final String paymentInfo = paymentData.toJson();
    if (paymentInfo == null) {
      return;
    }

    try {
      JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");

      final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
      final String tokenizationType = tokenizationData.getString("type");
      final String token = tokenizationData.getString("token");

      if ("PAYMENT_GATEWAY".equals(tokenizationType) && "examplePaymentMethodToken".equals(token)) {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(getString(R.string.gateway_replace_name_example))
                .setPositiveButton("OK", null)
                .create()
                .show();
      }

      final JSONObject info = paymentMethodData.getJSONObject("info");
      final String billingName = info.getJSONObject("billingAddress").getString("name");
      Toast.makeText(
              this, getString(R.string.payments_show_name, billingName),
              Toast.LENGTH_LONG).show();

      // Logging token string.
      Log.d("Google Pay token: ", token);

    } catch (JSONException e) {
      throw new RuntimeException("The information cannot be parsed from the list of elements");
    }
  }

  /**
   * At this stage, the user has already seen a popup informing them an error occurred. Normally,
   * only logging is required.
   *
   * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
   *                   WalletConstants.ERROR_CODE_* constants.
   */
  private void handleError(int statusCode) {
    Log.e("loadPaymentData failed", String.format("Error code: %d", statusCode));
  }

  // IMPORTANT to Discuss
  @RequiresApi(api = Build.VERSION_CODES.N)
  public void requestPayment(View view) {

    // Disables the button to prevent multiple clicks.
    googlePayButton.setClickable(false);

    // The price provided to the API should include taxes and shipping.
    // This price is not displayed to the user.
    double productPrice = price;
    long productPriceCents = Math.round(productPrice * PaymentsUtil.CENTS_IN_A_UNIT.longValue());
    long priceCents = productPriceCents + SHIPPING_COST_CENTS;

    Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents);
    if (!paymentDataRequestJson.isPresent()) {
      return;
    }

    PaymentDataRequest request =
            PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

    // Since loadPaymentData may show the UI asking the user to select a payment method, we use
    // AutoResolveHelper to wait for the user interacting with it. Once completed,
    // onActivityResult will be called with the result.
    if (request != null) {
      AutoResolveHelper.resolveTask(
              paymentsClient.loadPaymentData(request),
              this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }

  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      // value passed in AutoResolveHelper
      case LOAD_PAYMENT_DATA_REQUEST_CODE:
        switch (resultCode) {

          case Activity.RESULT_OK:
            PaymentData paymentData = PaymentData.getFromIntent(data);
            handlePaymentSuccess(paymentData);
            ref.child(listingID).removeValue();
            Intent i = new Intent(CheckoutActivity.this, StoreActivity.class);
            startActivity(i);
            break;

          case Activity.RESULT_CANCELED:
            // The user cancelled the payment attempt
            break;

          case AutoResolveHelper.RESULT_ERROR:
            Status status = AutoResolveHelper.getStatusFromIntent(data);
            handleError(status.getStatusCode());
            break;
        }

        // Re-enables the Google Pay payment button.
        googlePayButton.setClickable(true);
    }
  }

}
