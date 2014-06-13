package uk.co.pilllogger.state;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;

/**
 * Created by alex on 15/12/2013.
 */
public class Observer {

    private static final String TAG = "Observer";
    private static Observer _instance;
    private WeakHashMap<IPillsUpdated, WeakReference<IPillsUpdated>> _pillsUpdatedArrayList = new WeakHashMap<IPillsUpdated, WeakReference<IPillsUpdated>>();
    private WeakHashMap<IConsumptionAdded, WeakReference<IConsumptionAdded>> _consumptionAddedListeners = new WeakHashMap<IConsumptionAdded, WeakReference<IConsumptionAdded>>();
    private WeakHashMap<IConsumptionDeleted, WeakReference<IConsumptionDeleted>> _consumptionDeletedListeners = new WeakHashMap<IConsumptionDeleted, WeakReference<IConsumptionDeleted>>();
    private WeakHashMap<IPillsLoaded, WeakReference<IPillsLoaded>> _pillsLoadedListeners = new WeakHashMap<IPillsLoaded, WeakReference<IPillsLoaded>>();
    private WeakHashMap<IFeaturePurchased, WeakReference<IFeaturePurchased>> _featuredPurchasedListeners = new WeakHashMap<IFeaturePurchased, WeakReference<IFeaturePurchased>>();

    public static Observer getSingleton() {
        if(_instance == null)
            _instance = new Observer();

        return _instance;
    }

    public void notifyPillsUpdated(Pill pill){
        List<WeakReference<IPillsUpdated>> deadrefs = new ArrayList<WeakReference<IPillsUpdated>>();

        List<WeakReference<IPillsUpdated>> tempObservers = new ArrayList<WeakReference<IPillsUpdated>>(_pillsUpdatedArrayList.values());

        Logger.d(TAG, "notifyPillsUpdated");

        for(WeakReference<IPillsUpdated> reference : tempObservers){
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

    public void registerFeaturePurchasedObserver(IFeaturePurchased observer){
        _featuredPurchasedListeners.put(observer, new WeakReference<IFeaturePurchased>(observer));
    }

    public void unregisterFeaturePurchasedObserver(IFeaturePurchased observer){
        _featuredPurchasedListeners.remove(observer);
    }

    public void unregisterConsumptionDeletedObserver(IConsumptionDeleted observer){
        _consumptionDeletedListeners.remove(observer);
    }

    public void unregisterConsumptionAddedObserver(IConsumptionAdded observer){
        if(observer != null) {
            _consumptionAddedListeners.remove(observer);
        }
    }

    public void unregisterPillsLoadedObserver(IPillsLoaded observer){
        if(observer != null){
            _pillsLoadedListeners.remove(observer);
        }
    }

    public void registerConsumptionDeletedObserver(IConsumptionDeleted observer){
        _consumptionDeletedListeners.put(observer, new WeakReference<IConsumptionDeleted>(observer));
    }

    public void registerConsumptionAddedObserver(IConsumptionAdded listener) {
        WeakReference<IConsumptionAdded> reference = new WeakReference<IConsumptionAdded>(listener);
        _consumptionAddedListeners.put(listener, reference);
    }

    public void registerPillsLoadedObserver(IPillsLoaded listener){
        WeakReference<IPillsLoaded> reference = new WeakReference<IPillsLoaded>(listener);
        _pillsLoadedListeners.put(listener, reference);
    }

    public void notifyConsumptionAdded(Consumption consumption) {
        List<WeakReference<IConsumptionAdded>> deadrefs = new ArrayList<WeakReference<IConsumptionAdded>>();

        Logger.d(TAG, "About to notify " + _consumptionAddedListeners.size() + " listeners of new consumption");

        for (WeakReference<IConsumptionAdded> reference : _consumptionAddedListeners.values()) {
            IConsumptionAdded listener = reference.get();
            if (listener != null)
                listener.consumptionAdded(consumption);
            else
                deadrefs.add(reference);
        }

        _consumptionAddedListeners.values().removeAll(deadrefs);
    }

    public void notifyFeaturePurchased(FeatureType featureType) {
        List<WeakReference<IFeaturePurchased>> deadrefs = new ArrayList<WeakReference<IFeaturePurchased>>();

        Logger.d(TAG, "About to notify " + _featuredPurchasedListeners.size() + " listeners of new purchase");

        for (WeakReference<IFeaturePurchased> reference : _featuredPurchasedListeners.values()) {
            IFeaturePurchased listener = reference.get();
            if (listener != null)
                listener.featurePurchased(featureType);
            else
                deadrefs.add(reference);
        }

        _featuredPurchasedListeners.values().removeAll(deadrefs);
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

    public void notifyPillsLoaded(List<Pill> pills){
        List<WeakReference<IPillsLoaded>> deadrefs = new ArrayList<WeakReference<IPillsLoaded>>();

        Logger.d(TAG, "Timing: Going to notify " + _pillsLoadedListeners.size() + " pillsLoaded listeners");
        for(WeakReference<IPillsLoaded> reference : _pillsLoadedListeners.values()){
            IPillsLoaded observer = reference.get();

            if(observer != null)
                observer.pillsLoaded(pills);
            else
                deadrefs.add(reference);
        }

        _pillsLoadedListeners.values().removeAll(deadrefs);
    }

    public interface IPillsUpdated{
        void pillsUpdated(Pill pill);
    }

    public interface IConsumptionDeleted{
        void consumptionDeleted(Consumption consumption);
        void consumptionPillGroupDeleted(String group, int pillId);
    }

    public interface IConsumptionAdded {
        public void consumptionAdded(Consumption consumption);
    }

    public interface IPillsLoaded{
        void pillsLoaded(List<Pill> pills);
    }
    public interface IFeaturePurchased {
        void featurePurchased(FeatureType featureType);
    }

}
