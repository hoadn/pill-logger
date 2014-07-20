package uk.co.pilllogger.events;

import uk.co.pilllogger.fragments.ConsumptionInfoDialogFragment;
import uk.co.pilllogger.models.Consumption;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.events.
 */
public class TakeConsumptionAgainEvent {
    private final Consumption _consumption;
    private final ConsumptionInfoDialogFragment _consumptionInfoDialogFragment;

    public TakeConsumptionAgainEvent(Consumption consumption, ConsumptionInfoDialogFragment consumptionInfoDialogFragment) {
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
