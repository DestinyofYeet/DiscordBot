package utils.verificationLevel;

public class CaptchaVerification {

    private final String userId, captchaKey, channelId;

    public CaptchaVerification(String userId, String channelId, String captchaKey){
        this.userId = userId;
        this.captchaKey = captchaKey;
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCaptchaKey() {
        return captchaKey;
    }

    public String getChannelId() {
        return channelId;
    }
}
