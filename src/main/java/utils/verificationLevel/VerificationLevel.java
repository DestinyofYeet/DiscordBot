package utils.verificationLevel;

public enum VerificationLevel {
    NOTHING(0), REACTION(1), CAPTCHA(2);
    private final int level;

    VerificationLevel(int level){
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static VerificationLevel getFromLevel(int level){
        for (VerificationLevel vLevel: VerificationLevel.values()){
            if (vLevel.getLevel() == level) return vLevel;
        }

        return null;
    }
}
