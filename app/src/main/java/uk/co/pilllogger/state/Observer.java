package uk.co.pilllogger.state;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.models.Pill;

/**
 * Created by alex on 15/12/2013.
 */
public class Observer {

    private static Observer _instance;
    private List<IPillsUpdated> _pillsUpdatedArrayList = new ArrayList<IPillsUpdated>();

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
}
