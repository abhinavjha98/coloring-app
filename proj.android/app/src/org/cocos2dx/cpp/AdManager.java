package org.cocos2dx.cpp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;


import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardedAd;

import java.net.MalformedURLException;
import java.net.URL;

import com.yourcompany.mycoloringbook.R;           //add your package name after write .R

public class AdManager  {

    private static boolean IS_TESTING  = false;//consent popup is for europian union only make this true if you want to test on your device.
    private static String testDeviceid = "51368C353DFBE6720D340EC2C07BEE23";
    private static SharedPreferences prefs = null;
    private static AdView madView;
    private static InterstitialAd minterstitialAd;

    private static Bundle extra = null;
    private static Activity activity;
    private static ConsentInformation consentInformation;
    private static ConsentForm form;
    private static RelativeLayout rl;

    private static String TAG="Special LOg";

    public AdManager(Activity act){

        activity =  act;


        String PUBLISHER_ID = activity.getApplicationContext().getString(R.string.admob_publisher_id);
        String APPID = activity.getApplicationContext().getString(R.string.admob_application_id);
        String BANNER_ID = activity.getApplicationContext().getString(R.string.admob_banner_id);
        String INTERSTITIAL_ID = activity.getApplicationContext().getString(R.string.admob_interstitial_id);
        String PRIVACY_POLICY_URL = activity.getApplicationContext().getString(R.string.privacy_url);

        MobileAds.initialize(activity, APPID);

        prefs=  activity.getSharedPreferences("adpref", Context.MODE_PRIVATE);
        int t= prefs.getInt("adtype", 99);


        if(t == 99)
            PrepareConsentInformation();
        else
            setUPAds(t);


    }

    private void PrepareConsentInformation(){

        if(IS_TESTING){

            ConsentInformation.getInstance(activity).addTestDevice(testDeviceid);

            ConsentInformation.getInstance(activity).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        }

        ConsentInformation consentInformation = ConsentInformation.getInstance(activity);
        String PUBLISHER_ID = activity.getApplicationContext().getString(R.string.admob_publisher_id);
        String[] publisherIds = {PUBLISHER_ID};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {}
            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) { }
        });



        URL privacyUrl = null;

        try {
            String PRIVACY_POLICY_URL = activity.getApplicationContext().getString(R.string.privacy_url);
            privacyUrl = new URL(PRIVACY_POLICY_URL);
        } catch (MalformedURLException e) {e.printStackTrace(); }

        form = new ConsentForm.Builder(activity, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        Log.e("AMIT CONSENT3", "Consent Form Loaded" );
                        form.show();

                    }

                    @Override
                    public void onConsentFormOpened() { }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {

                        int t = 0;
                        t  = consentStatus == ConsentStatus.PERSONALIZED ? 2 :1;
                        prefs.edit().putInt("adtype", t).commit();
                        setUPAds(t);
                        Log.e("AMIT CONSENT5", "Consent Form Closed" );
                        // Consent form was closed.
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {

                        if(ConsentInformation.getInstance(activity).getDebugGeography() != DebugGeography.DEBUG_GEOGRAPHY_EEA){
                            int t = 1;
                            prefs.edit().putInt("adtype", t).commit();
                            setUPAds(t);
                        }
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();

    }


    private void setUPAds(int t){

        extra = new Bundle();
        extra.putString("npa", t+"");
        LoadBanner();
        LoadInterstitial();
    }


    public void BannerDisplay(){

        madView.setVisibility(madView.VISIBLE);

    }

    public void BannerHide(){
        Log.d(TAG, "hide call");
        madView.setVisibility(madView.GONE);
    }

    public boolean isInterstitialReady(){
        return minterstitialAd.isLoaded();
    }

    public void ShowInterstitial(){
        if (minterstitialAd.isLoaded()) {
            minterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    private void LoadInterstitial(){

        minterstitialAd = new InterstitialAd(activity);
        String INTERSTITIAL_ID = activity.getApplicationContext().getString(R.string.admob_interstitial_id);
        minterstitialAd.setAdUnitId(INTERSTITIAL_ID);
       // minterstitialAd.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class,extra).build());
        minterstitialAd.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class,extra).tagForChildDirectedTreatment(true).build());
        minterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                AppActivity.admobfullpageavailable = false;
               // minterstitialAd.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class,extra).build());
                minterstitialAd.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class,extra).tagForChildDirectedTreatment(true).build());
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                AppActivity.admobfullpageavailable = false;
               // minterstitialAd.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class,extra).build());
                minterstitialAd.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class,extra).tagForChildDirectedTreatment(true).build());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                AppActivity.admobfullpageavailable = true;
              //  minterstitialAd.show();
            }
        });
    }


    private void LoadBanner(){
        try{
            madView = new AdView(activity);
            // madView.setAdSize(AdSize.FULL_BANNER);
            madView.setAdSize(new AdSize(AdSize.FULL_WIDTH,AdSize.BANNER.getHeight()));
            //  RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) madView.getLayoutParams();
            //rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
            String BANNER_ID = activity.getApplicationContext().getString(R.string.admob_banner_id);
            madView.setAdUnitId(BANNER_ID);

            rl = new RelativeLayout(activity);

            rl.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

            final RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            // Just Add below line to get ad at the bottom of screen. That's it.
            lay.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lay.addRule(RelativeLayout.CENTER_IN_PARENT);

            AdRequest adRequest = null;
            madView.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extra).build());

            madView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    // TODO Auto-generated method stub
                    super.onAdLoaded();
                    if (rl.getChildCount() == 0) {
                        rl.addView(madView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT));

                    }
                    rl.invalidate();

                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // TODO Auto-generated method stub
                    super.onAdFailedToLoad(errorCode);
                }
            });
            activity.addContentView(rl, lay);
            // ----------------------
        } catch (Exception e) {
            // FlurryAgent.logEvent("ADMOB ERROR: "+e);
            e.printStackTrace();
        }
    }


}
