package com.wdtheprovider.inapppurchase.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;
import com.wdtheprovider.inapppurchase.adapters.SubscriptionAdapter;
import com.wdtheprovider.inapppurchase.helpers.FirebaseFunctions;
import com.wdtheprovider.inapppurchase.interfaces.RecycleViewInterface;
import com.wdtheprovider.inapppurchase.utilies.Prefs;
import com.wdtheprovider.inapppurchase.R;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionActivity extends AppCompatActivity implements RecycleViewInterface {
    Activity activity;
    Prefs prefs;
    private BillingClient billingClient;
    List<ProductDetails> productDetailsList;
    List<String> productIds;
    ProgressBar loadProducts;
    RecyclerView recyclerView;
    Handler handler;
    ExtendedFloatingActionButton btn_restore_fab;
    SubscriptionAdapter adapter;
    TextView manageSub, restoreSub;
    Button btnDismiss;

    FirebaseFunctions firebaseFunctions;

    private void initViews() {
        manageSub = findViewById(R.id.manageSub);
        btnDismiss = findViewById(R.id.btnDismiss);
        restoreSub = findViewById(R.id.restore);
        productIds = new ArrayList<>();
        productDetailsList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        btn_restore_fab = findViewById(R.id.fab);
        loadProducts = findViewById(R.id.loadProducts);

        productIds.add(0, "test_sub_weekly1");
        productIds.add(1, "test_sub_monthly1");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        initViews();
        handler = new Handler();

        activity = this;
        prefs = new Prefs(this);

        firebaseFunctions = new FirebaseFunctions(this);

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase : list) {
                                    verifySubPurchase(purchase);
                                }
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        establishConnection();

        //restore purchases
        restoreSub.setOnClickListener(v -> {
            restorePurchases();
            //  upgrade();
        });

        btnDismiss.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        //Manage subscription
        manageSub.setOnClickListener(v -> {
            showBottomSheetDialog();
        });
    }

    void establishConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    void showProducts() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productIds.get(0))
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productIds.get(1))
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, prodDetailsList) -> {
                    if (prodDetailsList.size() > 0) { // checking if there's a product returned then set the product(s)
                        // on the recycle viewer
                        saveOfferToken(prodDetailsList);
                        // Process the result
                        productDetailsList.clear();
                        handler.postDelayed(() -> {
                            loadProducts.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            productDetailsList.addAll(prodDetailsList);
                            adapter = new SubscriptionAdapter(getApplicationContext(), productDetailsList, SubscriptionActivity.this);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(SubscriptionActivity.this, LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(adapter);
                        }, 2000);
                    }
                }
        );
    }

    private void saveOfferToken(List<ProductDetails> prodDetailsList) {
        if (prodDetailsList.size() == productIds.size()) { // checking if the return products are of the same size we defined in our productId array
            for (int i = 0; i < prodDetailsList.size(); i++) {
                if (prodDetailsList.get(i).getProductId().equals(productIds.get(0))) { // checking productId weekSub
                    prefs.setString(productIds.get(0) + "_offerToken", prodDetailsList.get(i).getSubscriptionOfferDetails().get(0).getOfferToken());
                    Log.d("SaveToken", "Weekly OfferToken " + prefs.getString(productIds.get(0) + "_offerToken", ""));
                } else if (prodDetailsList.get(i).getProductId().equals(productIds.get(1))) { // checking productId MontSub
                    prefs.setString(productIds.get(1) + "_offerToken", prodDetailsList.get(i).getSubscriptionOfferDetails().get(0).getOfferToken());
                    Log.d("SaveToken", "Monthly OfferToken  " + prefs.getString(productIds.get(1) + "_offerToken", ""));
                }
            }
        }
    }

    void launchPurchaseFlow(ProductDetails productDetails) {
        assert productDetails.getSubscriptionOfferDetails() != null;
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    void verifySubPurchase(Purchase purchases) {
        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                //use prefs to set premium
                //Setting premium to 1 or you can use a boolean and set to true
                // 1 - premium
                // 0 - no premium
                prefs.setPremium(1);

                firebaseFunctions.updateSubscribed(prefs.getString("uid",""),true );

                //Optional but i will explain.
                for (int i = 0; i < productIds.size(); i++) {
                    if (purchases.getProducts().get(0).equals("test_sub_weekly1")) {
                        prefs.setString("subType", "Weekly Subscription");
                    } else if (purchases.getProducts().get(0).equals("test_sub_monthly1")) {
                        prefs.setString("subType", "Monthly Subscription");
                    }
                }

                String productId = purchases.getProducts().get(0); /// this one gets the product Id
                String purchaseToken = purchases.getPurchaseToken(); /// this one gets the purchase token

                Log.d("Test12345verifySubPurchase", purchases.toString());
                Log.d("Test12345verifySubPurchase", "Purchase token " + purchaseToken);
                Log.d("Test12345verifySubPurchase", "Product Id " + productId);

                //Save these values for upgrade purposes
                prefs.setString("purchasedToken", purchases.getPurchaseToken());
                prefs.setString("purchasedProductId", productId);

                handler.postDelayed(this::reloadScreen, 2000);
            }
        });
    }

    void restorePurchases() {

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener((billingResult, list) -> {
                }).build();

        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    if (list.size() > 0) {
                                        Log.d("Test1234restorePurchases", list.get(0).getPurchaseToken()); // This is the OldPurchaseToken
                                        prefs.setString("purchasedToken", list.get(0).getPurchaseToken());
                                        prefs.setString("purchasedProductId", list.get(0).getProducts().get(0));
                                        prefs.setPremium(1); // set 1 to activate premium feature
                                        showSnackBar(btn_restore_fab, "Successfully restored");
                                    } else {
                                        prefs.setString("subType", "No Subscription");
                                        prefs.setString("purchasedProductId", "");
                                        prefs.setString("purchasedToken", "");
                                        showSnackBar(btn_restore_fab, "Oops, No purchase found.");
                                        prefs.setPremium(0); // set 0 to de-activate premium feature
                                    }
                                }
                            });
                }
            }
        });
    }

    void upgradeOrDowngrade(String dynamicProductId) {

        Log.d("TestUpgrade", "The product list Size " + productDetailsList.size());
        Log.d("TestUpgrade", "The product list Details " + productDetailsList.toString());

        for (ProductDetails newProdDetails : productDetailsList) {

            if (newProdDetails.getProductId().equals(dynamicProductId)) {

                assert newProdDetails
                        .getSubscriptionOfferDetails() != null;

                String offerToken = newProdDetails.getSubscriptionOfferDetails().get(0).getOfferToken();

                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                                ImmutableList.of(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                                .setProductDetails(newProdDetails)
                                                .setOfferToken(offerToken)
                                                .build()))
                        .setSubscriptionUpdateParams(
                                BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                                        .setOldPurchaseToken(prefs.getString("purchasedToken", ""))
                                        .setReplaceProrationMode(BillingFlowParams.ProrationMode.IMMEDIATE_AND_CHARGE_FULL_PRICE)
                                        .build())
                        .build();

                //Opening the Billing flow
                billingClient.launchBillingFlow(activity, billingFlowParams);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet);


        LinearLayout mangeSub = bottomSheetDialog.findViewById(R.id.manageLayout);
        LinearLayout dismiss = bottomSheetDialog.findViewById(R.id.dismissLinearLayout);

        LinearLayout upgradeLinearLayout = bottomSheetDialog.findViewById(R.id.upgradeLinearLayout);
        LinearLayout downgradeLinearLayout = bottomSheetDialog.findViewById(R.id.downgradeLinearLayout);
        TextView currentSub = bottomSheetDialog.findViewById(R.id.currentSub);

        assert currentSub != null;
        assert downgradeLinearLayout != null;
        assert upgradeLinearLayout != null;

        if (prefs.getPremium() == 1) {
            currentSub.setText(prefs.getString("subType", "Checking Subscription"));
            if (prefs.getString("purchasedProductId", "").equals(productIds.get(0))) {
                upgradeLinearLayout.setVisibility(View.VISIBLE);
                downgradeLinearLayout.setVisibility(View.INVISIBLE);
            } else if (prefs.getString("purchasedProductId", "").equals(productIds.get(1))) {
                upgradeLinearLayout.setVisibility(View.INVISIBLE);
                downgradeLinearLayout.setVisibility(View.VISIBLE);
            }
        } else {
            upgradeLinearLayout.setVisibility(View.INVISIBLE);
            downgradeLinearLayout.setVisibility(View.INVISIBLE);
            currentSub.setText("No Subscription");
        }

        if (mangeSub != null) {
            mangeSub.setOnClickListener(v -> {
                String url = "https://play.google.com/store/account/subscriptions";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            });
        }

        upgradeLinearLayout.setOnClickListener(v -> {
            upgradeOrDowngrade(productIds.get(1));
        });

        downgradeLinearLayout.setOnClickListener(v -> {
            upgradeOrDowngrade(productIds.get(0));
        });

        if (dismiss != null) {
            dismiss.setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
            });
        }

        bottomSheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(activity, MainActivity.class));
        finish();
    }

    public void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int pos) {
        launchPurchaseFlow(productDetailsList.get(pos));
    }

    protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifySubPurchase(purchase);
                            }
                        }
                    }
                }
        );
    }

    private void reloadScreen() {
        //Reload the screen to activate the removeAd and remove the actual Ad off the screen.
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}