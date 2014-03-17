package uk.co.pilllogger.stats;

import uk.co.pilllogger.models.Pill;

/**
 * Created by Alex on 17/03/14.
 */
public class PillAmount {
    private int _amount = 0;
    private Pill _pill;

    public int getAmount() {
        return _amount;
    }

    public void setAmount(int amount) {
        _amount = amount;
    }

    public Pill getPill() {
        return _pill;
    }

    public void setPill(Pill pill) {
        _pill = pill;
    }
}
