package com.github.orangegangsters.lollipin.lib.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.github.orangegangsters.lollipin.lib.PinCompatActivity;
import com.github.orangegangsters.lollipin.lib.PinFragmentActivity;
import com.github.orangegangsters.lollipin.lib.encryption.Encryptor;
import com.github.orangegangsters.lollipin.lib.enums.Algorithm;
import com.github.orangegangsters.lollipin.lib.interfaces.LifeCycleInterface;
import com.github.orangegangsters.lollipin.lib.models.PinConfig;

import java.security.SecureRandom;
import java.util.Arrays;

public class AppLockImpl<T extends AppLockActivity> extends AppLock implements LifeCycleInterface {

    public static final String TAG = "AppLockImpl";
    /**
     * The {@link SharedPreferences} key used to store the PinConfig pojo.
     */
    private String PIN_CONFIG_PREFERENCE_KEY;
    /**
     * The prefix attached to the {@value #PIN_CONFIG_PREFERENCE_PREFIX}
     */
    private static final String PIN_CONFIG_PREFERENCE_PREFIX = "PIN_CONFIG_PREFERENCE_KEY";
    /**
     * The default password salt
     */
    private static final String DEFAULT_PASSWORD_SALT = "7xn7@c$";
    /**
     * The key algorithm used to generating the dynamic salt
     */
    private static final String KEY_ALGORITHM = "PBEWithMD5AndDES";
    /**
     * The key length of the salt
     */
    private static final int KEY_LENGTH = 256;
    /**
     * The number of iterations used to generate a dynamic salt
     */
    private static final int KEY_ITERATIONS = 20;

    /**
     * The {@link android.content.SharedPreferences} used to store the password, the last active time etc...
     */
    private SharedPreferences mSharedPreferences;

    /**
     * The activity class that extends {@link com.github.orangegangsters.lollipin.lib.managers.AppLockActivity}
     */
    private Class<T> mActivityClass;

    /**
     * Static instance of {@link AppLockImpl}
     */
    private static AppLockImpl mInstance;
    /**
     * Stores the pin related configuration.
     */
    private PinConfig pinConfig;

    /**
     * Static method that allows to get back the current static Instance of {@link AppLockImpl}
     *
     * @param context       The current context of the {@link Activity}
     * @param activityClass The activity extending {@link AppLockActivity}
     * @return The instance.
     */
    public static AppLockImpl getInstance(Context context, Class<? extends AppLockActivity> activityClass) {
        synchronized (LockManager.class) {
            if (mInstance == null) {
                mInstance = new AppLockImpl<>(context, activityClass);
            }
        }
        return mInstance;
    }

    private AppLockImpl(Context context, Class<T> activityClass) {
        super();
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mActivityClass = activityClass;
    }

    @Override
    public void setTimeout(long timeout) {
        pinConfig.setTimeoutMills(timeout);
        putConfigInPref();
    }

    public String getSalt() {
        String salt = getConfigFromPref().getPasswordSalt();
        if (salt == null) {
            salt = generateSalt();
            setSalt(salt);
        }
        return salt;
    }

    private void setSalt(String salt) {
        pinConfig.setPasswordSalt(salt);
        putConfigInPref();
    }

    private String generateSalt() {
        byte[] salt = new byte[KEY_LENGTH];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(System.currentTimeMillis());
            sr.nextBytes(salt);
            return Arrays.toString(salt);
        } catch (Exception e) {
            salt = DEFAULT_PASSWORD_SALT.getBytes();
        }
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    @Override
    public long getTimeout() {
        return getConfigFromPref().getTimeoutMills();
    }

    @Override
    public void setLogoId(int logoId) {
        pinConfig.setLogoId(logoId);
        putConfigInPref();
    }

    @Override
    public int getLogoId() {
        return getConfigFromPref().getLogoId();
    }

    @Override
    public void setShouldShowForgot(boolean showForgot) {
        pinConfig.setShowForgot(showForgot);
        putConfigInPref();
    }

    @Override
    public boolean pinChallengeCancelled() {
        return getConfigFromPref().isPinChallengeCancelled();
    }

    @Override
    public void setPinChallengeCancelled(boolean backedOut) {
        pinConfig.setPinChallengeCancelled(backedOut);
        putConfigInPref();
    }

    @Override
    public boolean shouldShowForgot() {
        return getConfigFromPref().shouldShowForgot();
    }

    @Override
    public boolean onlyBackgroundTimeout() {
        return getConfigFromPref().isOnlyBackgroundTimeout();
    }

    @Override
    public void setOnlyBackgroundTimeout(boolean onlyBackgroundTimeout) {
        pinConfig.setOnlyBackgroundTimeout(onlyBackgroundTimeout);
        putConfigInPref();
    }

    @Override
    public void enable() {
        PinActivity.setListener(this);
        PinCompatActivity.setListener(this);
        PinFragmentActivity.setListener(this);
    }

    @Override
    public void disable() {
        PinActivity.clearListeners();
        PinCompatActivity.clearListeners();
        PinFragmentActivity.clearListeners();
    }

    @Override
    public void disableAndRemoveConfiguration() {
        PinActivity.clearListeners();
        PinCompatActivity.clearListeners();
        PinFragmentActivity.clearListeners();
        mSharedPreferences.edit().remove(PIN_CONFIG_PREFERENCE_KEY)
                .apply();
    }

    @Override
    public long getLastActiveMillis() {
        return getConfigFromPref().getLastActiveMills();
    }

    @Override
    public boolean isFingerprintAuthEnabled() {
        return getConfigFromPref().isFingerPrintAuthEnabled();
    }

    @Override
    public void setFingerprintAuthEnabled(boolean enabled) {
        pinConfig.setFingerPrintAuthEnabled(enabled);
        putConfigInPref();
    }

    @Override
    public void setLastActiveMillis() {
        pinConfig.setLastActiveMills(System.currentTimeMillis());
        putConfigInPref();
    }

    @Override
    public boolean checkPasscode(String passcode) {
        PinConfig pinConfig = getConfigFromPref();
        Algorithm algorithm = Algorithm.getFromText(pinConfig.getAlgorithmString());

        String salt = getSalt();
        passcode = salt + passcode + salt;
        passcode = Encryptor.getSHA(passcode, algorithm);
        String storedPasscode = "";

        if (pinConfig.getPassCode() != null) {
            storedPasscode = pinConfig.getPassCode();
        }

        if (storedPasscode.equalsIgnoreCase(passcode)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setPasscode(String passcode) {
        String salt = getSalt();
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        if (passcode == null) {
            pinConfig.setPassCode(null);
            putConfigInPref();
            this.disable();
        } else {
            passcode = salt + passcode + salt;
            setAlgorithm(Algorithm.SHA256);
            passcode = Encryptor.getSHA(passcode, Algorithm.SHA256);
            pinConfig.setPassCode(passcode);
            putConfigInPref();
            this.enable();
        }

        return true;
    }

    /**
     * Set the algorithm used in {@link #setPasscode(String)}
     */
    private void setAlgorithm(Algorithm algorithm) {
        pinConfig.setAlgorithmString(algorithm.getValue());
        putConfigInPref();
    }

    @Override
    public boolean isPasscodeSet() {
        if(getConfigFromPref().getPassCode()!=null){
            return true;
        }

        return false;
    }

    @Override
    public boolean isIgnoredActivity(Activity activity) {
        String clazzName = activity.getClass().getName();

        // ignored activities
        if (mIgnoredActivities.contains(clazzName)) {
            Log.d(TAG, "ignore activity " + clazzName);
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldLockSceen(Activity activity) {
        Log.d(TAG, "Lollipin shouldLockSceen() called");

        // previously backed out of pin screen
        if (pinChallengeCancelled()) {
            return true;
        }

        // already unlock
        if (activity instanceof AppLockActivity) {
            AppLockActivity ala = (AppLockActivity) activity;
            if (ala.getType() == AppLock.UNLOCK_PIN) {
                Log.d(TAG, "already unlock activity");
                return false;
            }
        }

        // no pass code set
        if (!isPasscodeSet()) {
            Log.d(TAG, "lock passcode not set.");
            return false;
        }

        // no enough timeout
        long lastActiveMillis = getLastActiveMillis();
        long passedTime = System.currentTimeMillis() - lastActiveMillis;
        long timeout = getTimeout();
        if (lastActiveMillis > 0 && passedTime <= timeout) {
            Log.d(TAG, "no enough timeout " + passedTime + " for "
                    + timeout);
            return false;
        }

        return true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (isIgnoredActivity(activity)) {
            return;
        }

        String clazzName = activity.getClass().getName();
        Log.d(TAG, "onActivityPaused " + clazzName);

        if ((onlyBackgroundTimeout() || !shouldLockSceen(activity)) && !(activity instanceof AppLockActivity)) {
            setLastActiveMillis();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (isIgnoredActivity(activity)) {
            return;
        }

        String clazzName = activity.getClass().getName();
        Log.d(TAG, "onActivityResumed " + clazzName);

        if (shouldLockSceen(activity)) {
            Log.d(TAG, "mActivityClass.getClass() " + mActivityClass);
            Intent intent = new Intent(activity.getApplicationContext(),
                    mActivityClass);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplication().startActivity(intent);
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            return;
        }

        if (!shouldLockSceen(activity) && !(activity instanceof AppLockActivity)) {
            setLastActiveMillis();
        }
    }

    public void putConfigInPref() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PIN_CONFIG_PREFERENCE_KEY, PinConfig.toJsonString(pinConfig));
        editor.apply();
    }

    public PinConfig getConfigFromPref() {
        return PinConfig.fromJson(mSharedPreferences.getString(PIN_CONFIG_PREFERENCE_KEY, null));
    }

    public String getSharedPrefKey(){
        return PIN_CONFIG_PREFERENCE_KEY;
    }

    public void setSharedPrefKey(String email){
        if(email==null){
            PIN_CONFIG_PREFERENCE_KEY = null;
        }
        else{
            PIN_CONFIG_PREFERENCE_KEY = PIN_CONFIG_PREFERENCE_PREFIX + email;
        }
    }

    @Override
    public void setPinConfig(PinConfig pinConfig) {
        this.pinConfig = pinConfig;
    }
}
