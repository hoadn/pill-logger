package uk.co.pilllogger.state;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;

/**
 * Created by alex on 15/12/2013.
 */
public class Observer {

    private static Observer _instance;
    private List<IPillsUpdated> _pillsUpdatedArrayList = new ArrayList<IPillsUpdated>();
    private List<IConsumptionAdded> _consumptionAddedListeners = new ArrayList<IConsumptionAdded>();
    private List<IConsumptionDeleted> _consumptionDeletedListeners = new ArrayList<IConsumptionDeleted>();


    public static Observer getSingleton() {
        if(_instance == null)
            _instance = new Observer();

        return _instance;
    }

    public void notifyPillsUpdated(Pill pill){
        for(IPillsUpdated observer : _pillsUpdatedArrayList){
            if(observer != null)
                observer.pillsUpdated(pill);
        }
    }

    public void registerPillsUpdatedObserver(IPillsUpdated observer){
        _pillsUpdatedArrayList.add(observer);
    }

    public interface IPillsUpdated{
        void pillsUpdated(Pill pill);
    }

    public void registerConsumptionDeletedObserver(IConsumptionDeleted observer){
        _consumptionDeletedListeners.add(observer);
    }

    public interface IConsumptionDeleted{
        void consumptionDeleted(Consumption consumption);
    }

    public interface IConsumptionAdded {
        public void consumptionAdded(Consumption consumption);
    }

    public void registerConsumptionAddedObserver(IConsumptionAdded listener) {
        _consumptionAddedListeners.add(listener);
    }

    public void notifyConsumptionAdded(Consumption consumption) {
        for (IConsumptionAdded listener : _consumptionAddedListeners) {
            if (listener != null)
                listener.consumptionAdded(consumption);
        }
    }

    public void notifyConsumptionDeleted(Consumption consumption){
        for(IConsumptionDeleted observer : _consumptionDeletedListeners){
            if(observer != null)
                observer.consumptionDeleted(consumption);
        }
    }
}
