package uk.co.pilllogger.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import uk.co.pilllogger.fragments.ConsumptionInfoDialogFragment;
import uk.co.pilllogger.models.Consumption;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.events.
 */
public class TakeConsumptionAgainEvent {
    private final Consumption _consumption;
    private final ConsumptionInfoDialogFragment _consumptionInfoDialogFragment;
    private List<Consumption> _consumptionsToInsert;

    public TakeConsumptionAgainEvent(Consumption consumption, ConsumptionInfoDialogFragment consumptionInfoDialogFragment) {
        _consumption = consumption;
        _consumptionInfoDialogFragment = consumptionInfoDialogFragment;

        Date consumptionDate = new Date();
        String consumptionGroup = UUID.randomUUID().toString();

        _consumption.setDate(consumptionDate);
        _consumption.setGroup(consumptionGroup);

        _consumptionsToInsert = new ArrayList<Consumption>();
        for(int i = 0; i < _consumption.getQuantity(); i++){
            Consumption newConsumption = new Consumption(_consumption);
            newConsumption.setId(0);
            newConsumption.setQuantity(1);

            _consumptionsToInsert.add(newConsumption);
        }
    }

    public Consumption getConsumption() {
        return _consumption;
    }

    public ConsumptionInfoDialogFragment getConsumptionInfoDialogFragment() {
        return _consumptionInfoDialogFragment;
    }

    public List<Consumption> getConsumptionsToInsert(){
        return _consumptionsToInsert;
    }
}
