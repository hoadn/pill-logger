package uk.co.pilllogger.state;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import uk.co.pilllogger.dialogs.ConsumptionInfoDialog;
import uk.co.pilllogger.dialogs.InfoDialog;
import uk.co.pilllogger.dialogs.PillInfoDialog;
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
    private WeakHashMap<ConsumptionInfoDialog.ConsumptionInfoDialogListener, WeakReference<ConsumptionInfoDialog.ConsumptionInfoDialogListener>> _consumptionInfoDialogListeners = new WeakHashMap<ConsumptionInfoDialog.ConsumptionInfoDialogListener, WeakReference<ConsumptionInfoDialog.ConsumptionInfoDialogListener>>();
    private WeakHashMap<PillInfoDialog.PillInfoDialogListener, WeakReference<PillInfoDialog.PillInfoDialogListener>> _pillInfoDialogListeners = new WeakHashMap<PillInfoDialog.PillInfoDialogListener, WeakReference<PillInfoDialog.PillInfoDialogListener>>();

    public static Observer getSingleton() {
        if (_instance == null)
            _instance = new Observer();

        return _instance;
    }

    public void notifyPillsUpdated(Pill pill) {
        List<WeakReference<IPillsUpdated>> deadrefs = new ArrayList<WeakReference<IPillsUpdated>>();

        List<WeakReference<IPillsUpdated>> tempObservers = new ArrayList<WeakReference<IPillsUpdated>>(_pillsUpdatedArrayList.values());

        Logger.d(TAG, "notifyPillsUpdated");

        for (WeakReference<IPillsUpdated> reference : tempObservers) {
            IPillsUpdated observer = reference.get();
            if (observer != null)
                observer.pillsUpdated(pill);
            else
                deadrefs.add(reference);
        }
        _pillsUpdatedArrayList.values().removeAll(deadrefs);
    }

    public void registerPillsUpdatedObserver(IPillsUpdated observer) {
        _pillsUpdatedArrayList.put(observer, new WeakReference<IPillsUpdated>(observer));
    }

    public void unregisterPillsUpdatedObserver(IPillsUpdated observer) {
        _pillsUpdatedArrayList.remove(observer);
    }

    public void registerFeaturePurchasedObserver(IFeaturePurchased observer) {
        _featuredPurchasedListeners.put(observer, new WeakReference<IFeaturePurchased>(observer));
    }

    public void unregisterFeaturePurchasedObserver(IFeaturePurchased observer) {
        _featuredPurchasedListeners.remove(observer);
    }

    public void unregisterConsumptionDeletedObserver(IConsumptionDeleted observer) {
        _consumptionDeletedListeners.remove(observer);
    }

    public void unregisterConsumptionAddedObserver(IConsumptionAdded observer) {
        if (observer != null) {
            _consumptionAddedListeners.remove(observer);
        }
    }

    public void unregisterPillsLoadedObserver(IPillsLoaded observer) {
        if (observer != null) {
            _pillsLoadedListeners.remove(observer);
        }
    }

    public void registerConsumptionDeletedObserver(IConsumptionDeleted observer) {
        _consumptionDeletedListeners.put(observer, new WeakReference<IConsumptionDeleted>(observer));
    }

    public void registerConsumptionAddedObserver(IConsumptionAdded listener) {
        WeakReference<IConsumptionAdded> reference = new WeakReference<IConsumptionAdded>(listener);
        _consumptionAddedListeners.put(listener, reference);
    }

    public void registerPillsLoadedObserver(IPillsLoaded listener) {
        WeakReference<IPillsLoaded> reference = new WeakReference<IPillsLoaded>(listener);
        _pillsLoadedListeners.put(listener, reference);
    }

    public void registerConsumptionDialogObserver(ConsumptionInfoDialog.ConsumptionInfoDialogListener listener) {
        _consumptionInfoDialogListeners.put(listener, new WeakReference<ConsumptionInfoDialog.ConsumptionInfoDialogListener>(listener));
    }

    public void unregisterConsumptionDialogObserver(ConsumptionInfoDialog.ConsumptionInfoDialogListener observer) {
        if (observer != null) {
            _consumptionInfoDialogListeners.remove(observer);
        }
    }

    public void registerPillDialogObserver(PillInfoDialog.PillInfoDialogListener listener) {
        _pillInfoDialogListeners.put(listener, new WeakReference<PillInfoDialog.PillInfoDialogListener>(listener));
    }

    public void unregisterPillDialogObserver(PillInfoDialog.PillInfoDialogListener observer) {
        if (observer != null) {
            _pillInfoDialogListeners.remove(observer);
        }
    }

    public <T> void notify(Map<T, WeakReference<T>> collection, INotifiable<T> notifier) {
        List<WeakReference<T>> deadrefs = new ArrayList<WeakReference<T>>();

        Logger.d(TAG, "Timing: Going to notify " + collection.size() + " listeners");

        for (WeakReference<T> reference : collection.values()) {
            T observer = reference.get();

            if (observer != null) {
                notifier.notify(observer);
            } else {
                deadrefs.add(reference);
            }
        }

        collection.values().removeAll(deadrefs);
    }

    public void notifyConsumptionAdded(final Consumption consumption) {
        notify(_consumptionAddedListeners, new INotifiable<IConsumptionAdded>() {
            @Override
            public void notify(IConsumptionAdded observer) {
                observer.consumptionAdded(consumption);
            }
        });
    }

    public void notifyFeaturePurchased(final FeatureType featureType) {
        notify(_featuredPurchasedListeners, new INotifiable<IFeaturePurchased>() {
            @Override
            public void notify(IFeaturePurchased observer) {
                observer.featurePurchased(featureType);
            }
        });
    }


    public void notifyConsumptionDeleted(final Consumption consumption) {
        notify(_consumptionDeletedListeners, new INotifiable<IConsumptionDeleted>() {
            @Override
            public void notify(IConsumptionDeleted observer) {
                observer.consumptionDeleted(consumption);
            }
        });
    }

    public void notifyConsumptionPillGroupDeleted(final String group, final int pillId) {
        notify(_consumptionDeletedListeners, new INotifiable<IConsumptionDeleted>() {
            @Override
            public void notify(IConsumptionDeleted observer) {
                observer.consumptionPillGroupDeleted(group, pillId);
            }
        });
    }

    public void notifyPillsLoaded(final List<Pill> pills) {
        notify(_pillsLoadedListeners, new INotifiable<IPillsLoaded>() {
            @Override
            public void notify(IPillsLoaded observer) {
                observer.pillsLoaded(pills);
            }
        });
    }

    public void notifyOnConsumptionDialogTakeAgain(final Consumption consumption, final InfoDialog dialog) {
        notify(_consumptionInfoDialogListeners, new INotifiable<ConsumptionInfoDialog.ConsumptionInfoDialogListener>() {
            @Override
            public void notify(ConsumptionInfoDialog.ConsumptionInfoDialogListener observer) {
                observer.onDialogTakeAgain(consumption, dialog);
            }
        });
    }

    public void notifyOnConsumptionDialogIncrease(final Consumption consumption, final InfoDialog dialog) {
        notify(_consumptionInfoDialogListeners, new INotifiable<ConsumptionInfoDialog.ConsumptionInfoDialogListener>() {
            @Override
            public void notify(ConsumptionInfoDialog.ConsumptionInfoDialogListener observer) {
                observer.onDialogIncrease(consumption, dialog);
            }
        });
    }

    public void notifyOnConsumptionDialogDecrease(final Consumption consumption, final InfoDialog dialog) {
        notify(_consumptionInfoDialogListeners, new INotifiable<ConsumptionInfoDialog.ConsumptionInfoDialogListener>() {
            @Override
            public void notify(ConsumptionInfoDialog.ConsumptionInfoDialogListener observer) {
                observer.onDialogDecrease(consumption, dialog);
            }
        });
    }

    public void notifyOnConsumptionDialogDelete(final Consumption consumption, final InfoDialog dialog) {
        notify(_consumptionInfoDialogListeners, new INotifiable<ConsumptionInfoDialog.ConsumptionInfoDialogListener>() {
            @Override
            public void notify(ConsumptionInfoDialog.ConsumptionInfoDialogListener observer) {
                observer.onDialogDelete(consumption, dialog);
            }
        });
    }

    public void notifyOnPillDialogAddConsumption(final Pill pill, final InfoDialog dialog){
        notify(_pillInfoDialogListeners, new INotifiable<PillInfoDialog.PillInfoDialogListener>() {
            @Override
            public void notify(PillInfoDialog.PillInfoDialogListener observer) {
                observer.onDialogAddConsumption(pill, dialog);
            }
        });
    }

    public void notifyOnPillDialogDelete(final Pill pill, final InfoDialog dialog){
        notify(_pillInfoDialogListeners, new INotifiable<PillInfoDialog.PillInfoDialogListener>() {
            @Override
            public void notify(PillInfoDialog.PillInfoDialogListener observer) {
                observer.onDialogDelete(pill, dialog);
            }
        });
    }

    public void notifyOnPillDialogFavourite(final Pill pill, final InfoDialog dialog){
        notify(_pillInfoDialogListeners, new INotifiable<PillInfoDialog.PillInfoDialogListener>() {
            @Override
            public void notify(PillInfoDialog.PillInfoDialogListener observer) {
                observer.setDialogFavourite(pill, dialog);
            }
        });
    }

    public void notifyOnPillDialogChangePillColour(final Pill pill, final InfoDialog dialog){
        notify(_pillInfoDialogListeners, new INotifiable<PillInfoDialog.PillInfoDialogListener>() {
            @Override
            public void notify(PillInfoDialog.PillInfoDialogListener observer) {
                observer.onDialogChangePillColour(pill, dialog);
            }
        });
    }

    public void notifyOnPillDialogChangeNameDosage(final Pill pill, final InfoDialog dialog){
        notify(_pillInfoDialogListeners, new INotifiable<PillInfoDialog.PillInfoDialogListener>() {
            @Override
            public void notify(PillInfoDialog.PillInfoDialogListener observer) {
                observer.onDialogChangeNameDosage(pill, dialog);
            }
        });
    }

    public interface IPillsUpdated {
        void pillsUpdated(Pill pill);
    }

    public interface IConsumptionDeleted {
        void consumptionDeleted(Consumption consumption);

        void consumptionPillGroupDeleted(String group, int pillId);
    }

    public interface IConsumptionAdded {
        public void consumptionAdded(Consumption consumption);
    }

    public interface IPillsLoaded {
        void pillsLoaded(List<Pill> pills);
    }

    public interface IFeaturePurchased {
        void featurePurchased(FeatureType featureType);
    }

    private interface INotifiable<T> {
        void notify(T observer);
    }
}
