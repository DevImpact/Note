package com.devimpact.inote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.appopen.AppOpenAdLoadCallback;

public class AppOpenManager {

    private final MyApplication myApplication;
    private AppOpenAd appOpenAd = null;
    private static boolean isShowingAd = false;
    private long loadTime = 0;

    /**
     * Constructor
     */
    public AppOpenManager(MyApplication myApplication) {
        this.myApplication = myApplication;
    }

    /**
     * Load the App Open Ad
     */
    public void fetchAd(String adUnitId) {
        // We won't fetch ad if one is already showing
        if (isShowingAd) {
            return;
        }

        // Only fetch ad if there is no currently active ad
        if (appOpenAd == null) {
            loadTime = System.currentTimeMillis();
             
            AppOpenAd.load(
                    myApplication,
                    adUnitId,
                    new AdRequest.Builder().build(),
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    new AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd ad) {
                            AppOpenManager.this.appOpenAd = ad;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle ad failed to load scenario
                        }

                    });
        }
    }

    /**
     * Method to show the App Open Ad
     */
    public void showAdIfAvailable() {
        // We won't show the ad if it is already showing or null
        if (!isAdAvailable() || isShowingAd) {
            return;
        }

        FullScreenContentCallback fullScreenContentCallback =
                new FullScreenContentCallback() {

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Set the AppOpenAd to null
                        AppOpenManager.this.appOpenAd = null;
                        isShowingAd = false;
                        
                        // Begin loading the next ad
                        fetchAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(final AdError adError) {
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        isShowingAd = true;
                    }
                };

        appOpenAd.show(myApplication.getCurrentActivity(), fullScreenContentCallback);
    }

    /**
     * Utility method to check if the ad is available to be shown
     */
    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = System.currentTimeMillis() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

}