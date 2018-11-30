package refactor;

public class Campaign {
    private CampaignType type;
    private int oversell;
    private float oversellFactor;

    public Campaign(CampaignType type, int oversell, float oversellFactor) {
        this.type = type;
        this.oversell = oversell;
        this.oversellFactor = oversellFactor;
    }

    public int getOversell() {
        return oversell;
    }

    public void setOversell(int oversell) {
        this.oversell = oversell;
    }

    public CampaignType getType() {
        return type;
    }

    public void setType(CampaignType type) {
        this.type = type;
    }

    public float getOversellFactor() {
        return oversellFactor;
    }

    public void setOversellFactor(float oversellFactor) {
        this.oversellFactor = oversellFactor;
    }

    public enum CampaignType {
        A, B, C, Z
    }
}
