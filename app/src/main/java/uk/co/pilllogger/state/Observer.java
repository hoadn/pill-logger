package uk.co.pilllogger.state;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.pilllogger.fragments.ConsumptionInfoDialogFragment;
import uk.co.pilllogger.fragments.InfoDialogFragment;
import uk.co.pilllogger.fragments.PillInfoDialogFragment;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;

/**
 * Created by alex on 15/12/2013.
 */
public class Observer {

    private static final String TAG = "Observer";
    private static Observer _instance;
    private Map<PillInfoDialogFragment.PillInfoDialogListener, WeakReference<PillInfoDialogFragment.PillInfoDialogListener>> _pillInfoDialogListeners = new ConcurrentHashMap<PillInfoDialogFragment.PillInfoDialogListener, WeakReference<PillInfoDialogFragment.PillInfoDialogListener>>();

    public static Observer getSingleton() {
        if (_instance == null)
            _instance = new Observer();

        return _instance;
    }

    public void registerPillDialogObserver(PillInfoDialogFragment.PillInfoDialogListener listener) {
        _pillInfoDialogListeners.put(listener, new WeakReference<PillInfoDialogFragment.PillInfoDialogListener>(listener));
    }

    public void unregisterPillDialogObserver(PillInfoDialogFragment.PillInfoDialogListener observer) {
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

    public void notifyOnPillDialogAddConsumption(final Pill pill, final InfoDialogFragment dialog){
        notify(_pillInfoDialogListeners, new INotifiable<PillInfoDialogFragment.PillInfoDialogListener>() {
            @Override
            public void notify(PillInfoDialogFragment.PillInfoDialogListener observer) {
                observer.onDialogAddConsumption(pill, dialog);
            }
        });
    }

    public void notifyOnPillDialogDelete(final Pill pill, final InfoDialogFragment dialog){
        notify(_pillInfoDialogListeners, new INotifiable<PillInfoDialogFragment.PillInfoDialogListener>() {
            @Override
            public void notify(PillInfoDialogFragment.PillInfoDialogListener observer) {
                observer.onDialogDelete(pill, dialog);
            }
        });
    }

    public void notifyOnPillDialogFavourite(final Pill pill, final InfoDialogFragment dialog){
        notify(_pillInfoDialogListeners, new INotifiable<PillInfoDialogFragment.PillInfoDialogListener>() {
            @Override
            public void notify(PillInfoDialogFragment.PillInfoDialogListener observer) {
                observer.setDialogFavourite(pill, dialog);
            }
        });
    }

    public void notifyOnPillDialogChangePillColour(final Pill pill, final InfoDialogFragment dialog){
        notify(_pillInfoDialogListeners, new INotifiable<PillInfoDialogFragment.PillInfoDialogListener>() {
            @Override
            public void notify(PillInfoDialogFragment.PillInfoDialogListener observer) {
                observer.onDialogChangePillColour(pill, dialog);
            }
        });
    }

    public void notifyOnPillDialogChangeNameDosage(final Pill pill, final InfoDialogFragment dialog){
        notify(_pillInfoDialogListeners, new INotifiable<PillInfoDialogFragment.PillInfoDialogListener>() {
            @Override
            public void notify(PillInfoDialogFragment.PillInfoDialogListener observer) {
                observer.onDialogChangeNameDosage(pill, dialog);
            }
        });
    }

    private interface INotifiable<T> {
        void notify(T observer);
    }
}
