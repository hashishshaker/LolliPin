package com.github.orangegangsters.lollipin.lib.models;

import com.google.gson.Gson;

/**
 * Created by vishesh on 13/7/16.
 */
public class PinConfig {

    private String passCode;
    private String algorithmString;
    private long lastActiveMills;
    private long timeoutMills;
    private int logoId;
    private boolean showForgot;
    private boolean onlyBackgroundTimeout;
    private boolean pinChallengeCancelled;
    private String passwordSalt;
    private boolean fingerPrintAuthEnabled;

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

    public boolean isShowForgot() {
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
