package uk.co.pilllogger.state;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;

/**
 * Created by alex on 15/12/2013.
 */
public class Observer {

    private static Observer _instance;
    private WeakHashMap<IPillsUpdated, WeakReference<IPillsUpdated>> _pillsUpdatedArrayList = new WeakHashMap<IPillsUpdated, WeakReference<IPillsUpdated>>();
    private WeakHashMap<IConsumptionAdded, WeakReference<IConsumptionAdded>> _consumptionAddedListeners = new WeakHashMap<IConsumptionAdded, WeakReference<IConsumptionAdded>>();
    private WeakHashMap<IConsumptionDeleted, WeakReference<IConsumptionDeleted>> _consumptionDeletedListeners = new WeakHashMap<IConsumptionDeleted, WeakReference<IConsumptionDeleted>>();

    public static Observer getSingleton() {
        if(_instance == null)
            _instance = new Observer();

        return _instance;
    }

    public void notifyPillsUpdated(Pill pill){
        List<WeakReference<IPillsUpdated>> deadrefs = new ArrayList<WeakReference<IPillsUpdated>>();

        for(WeakReference<IPillsUpdated> reference : _pillsUpdatedArrayList.values()){
            IPillsUpdated observer = reference.get();
            if(observer != null)
                observer.pillsUpdated(pill);
            else
                deadrefs.add(reference);
        }

        _pillsUpdatedArrayList.values().removeAll(deadrefs);
    }

    public void registerPillsUpdatedObserver(IPillsUpdated observer){
        _pillsUpdatedArrayList.put(observer, new WeakReference<IPillsUpdated>(observer));
    }

    public void unregisterPillsUpdatedObserver(IPillsUpdated observer) {
        _pillsUpdatedArrayList.remove(observer);
    }

    public void unregisterConsumptionDeletedObserver(IConsumptionDeleted observer){
        _consumptionDeletedListeners.remove(observer);
    }

    public void unregisterConsumptionAddedObserver(IConsumptionAdded observer){
        if(observer != null) {
            _consumptionAddedListeners.remove(observer);
        }
    }

    public interface IPillsUpdated{
        void pillsUpdated(Pill pill);
    }

    public void registerConsumptionDeletedObserver(IConsumptionDeleted observer){
        _consumptionDeletedListeners.put(observer, new WeakReference<IConsumptionDeleted>(observer));
    }

    public interface IConsumptionDeleted{
        void consumptionDeleted(Consumption consumption);
        void consumptionPillGroupDeleted(String group, int pillId);
    }

    public interface IConsumptionAdded {
        public void consumptionAdded(Consumption consumption);
    }

    public void registerConsumptionAddedObserver(IConsumptionAdded listener) {
        WeakReference<IConsumptionAdded> reference = new WeakReference<IConsumptionAdded>(listener);
        _consumptionAddedListeners.put(listener, reference);
    }

    public void notifyConsumptionAdded(Consumption consumption) {
        List<WeakReference<IConsumptionAdded>> deadrefs = new ArrayList<WeakReference<IConsumptionAdded>>();

        for (WeakReference<IConsumptionAdded> reference : _consumptionAddedListeners.values()) {
            IConsumptionAdded listener = reference.get();
            if (listener != null)
                listener.consumptionAdded(consumption);
            else
                deadrefs.add(reference);
        }

        _consumptionAddedListeners.values().removeAll(deadrefs);
    }

    public void notifyConsumptionDeleted(Consumption consumption){
        List<WeakReference<IConsumptionDeleted>> deadrefs = new ArrayList<WeakReference<IConsumptionDeleted>>();

        for(WeakReference<IConsumptionDeleted> reference : _consumptionDeletedListeners.values()){
            IConsumptionDeleted observer = reference.get();

            if(observer != null)
                observer.consumptionDeleted(consumption);
            else
                deadrefs.add(reference);
        }

        _consumptionDeletedListeners.values().removeAll(deadrefs);
    }

    public void notifyConsumptionPillGroupDeleted(String group, int pillId){
        List<WeakReference<IConsumptionDeleted>> deadrefs = new ArrayList<WeakReference<IConsumptionDeleted>>();

        for(WeakReference<IConsumptionDeleted> reference : _consumptionDeletedListeners.values()){
            IConsumptionDeleted observer = reference.get();

            if(observer != null)
                observer.consumptionPillGroupDeleted(group, pillId);
            else
                deadrefs.add(reference);
        }

        _consumptionDeletedListeners.values().removeAll(deadrefs);
    }
}
