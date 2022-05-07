package utils.verificationLevel;

public class ReactionVerification {

    private final String userId, messageId, reactionId, channelId;

    public ReactionVerification(String userId, String messageId, String channelId, String reactionId){
        this.userId = userId;
        this.messageId = messageId;
        this.reactionId = reactionId;
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getReactionId() {
        return reactionId;
    }

    public String getChannelId() {
        return channelId;
    }
}
