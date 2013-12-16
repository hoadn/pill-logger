package uk.co.pilllogger.state;

import java.util.ArrayList;
import java.util.List;

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

    public void notifyPillsUpdated(){
        for(IPillsUpdated observer : _pillsUpdatedArrayList){
            if(observer != null)
                observer.pillsUpdated();
        }
    }

    public void registerPillsUpdatedObserver(IPillsUpdated observer){
        _pillsUpdatedArrayList.add(observer);
    }

    public interface IPillsUpdated{
        void pillsUpdated();
    }
}
