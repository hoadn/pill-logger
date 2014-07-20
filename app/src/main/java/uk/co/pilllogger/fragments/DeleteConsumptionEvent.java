package uk.co.pilllogger.fragments;

import uk.co.pilllogger.models.Consumption;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.fragments.
 */
public class DeleteConsumptionEvent {
    private final Consumption _consumption;
    private final ConsumptionInfoDialogFragment _consumptionInfoDialogFragment;

    public DeleteConsumptionEvent(Consumption consumption, ConsumptionInfoDialogFragment consumptionInfoDialogFragment) {
        _consumption = consumption;
        _consumptionInfoDialogFragment = consumptionInfoDialogFragment;
    }

    public Consumption getConsumption() {
        return _consumption;
    }

    public ConsumptionInfoDialogFragment getConsumptionInfoDialogFragment() {
        return _consumptionInfoDialogFragment;
    }
}
