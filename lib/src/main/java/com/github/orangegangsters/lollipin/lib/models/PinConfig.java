package com.github.orangegangsters.lollipin.lib.models;

import com.github.orangegangsters.lollipin.lib.enums.Algorithm;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;
import com.google.gson.Gson;

/**
 * Created by vishesh on 13/7/16.
 */
public class PinConfig {

    /**
     * Stores the password
     */
    private String passCode;
    /**
     * Stores the {@link Algorithm}
     */
    private String algorithmString;
    /**
     * Stores the last active time
     */
    private long lastActiveMills;
    /**
     * Stores the timeout.
     */
    private long timeoutMills;
    /**
     * Stores the logo resource id.
     */
    private int logoId;
    /**
     * Stores the forgot option.
     */
    private boolean showForgot;
    /**
     * Stores the background timeout option.
     */
    private boolean onlyBackgroundTimeout;
    /**
     * Stores whether the user has backed out of the {@link AppLockActivity}
     */
    private boolean pinChallengeCancelled;
    /**
     * Stores the dynamically generated password salt.
     */
    private String passwordSalt;
    /**
     * Stores whether the caller has enabled fingerprint authentication.
     * Defaults to true for backwards compatibility.
     */
    private boolean fingerPrintAuthEnabled = true;


    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public String getAlgorithmString() {
        return algorithmString;
    }

    public void setAlgorithmString(String algorithmString) {
        this.algorithmString = algorithmString;
    }

    public long getLastActiveMills() {
        return lastActiveMills;
    }

    public void setLastActiveMills(long lastActiveMills) {
        this.lastActiveMills = lastActiveMills;
    }

    public long getTimeoutMills() {
        return timeoutMills;
    }

    public void setTimeoutMills(long timeoutMills) {
        this.timeoutMills = timeoutMills;
    }

    public int getLogoId() {
        return logoId;
    }

    public void setLogoId(int logoId) {
        this.logoId = logoId;
    }

    public boolean shouldShowForgot() {
        return showForgot;
    }

    public void setShowForgot(boolean showForgot) {
        this.showForgot = showForgot;
    }

    public boolean isOnlyBackgroundTimeout() {
        return onlyBackgroundTimeout;
    }

    public void setOnlyBackgroundTimeout(boolean onlyBackgroundTimeout) {
        this.onlyBackgroundTimeout = onlyBackgroundTimeout;
    }

    public boolean isPinChallengeCancelled() {
        return pinChallengeCancelled;
    }

    public void setPinChallengeCancelled(boolean pinChallengeCancelled) {
        this.pinChallengeCancelled = pinChallengeCancelled;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public boolean isFingerPrintAuthEnabled() {
        return fingerPrintAuthEnabled;
    }

    public void setFingerPrintAuthEnabled(boolean fingerPrintAuthEnabled) {
        this.fingerPrintAuthEnabled = fingerPrintAuthEnabled;
    }

    public static String toJsonString(PinConfig pinConfig){
        Gson gson = new Gson();
        String jsonString = gson.toJson(pinConfig);
        return jsonString;
    }

    public static PinConfig fromJson(String jsonString){
        Gson gson = new Gson();
        PinConfig pinConfig = gson.fromJson(jsonString, PinConfig.class);
        return pinConfig;
    }
}
