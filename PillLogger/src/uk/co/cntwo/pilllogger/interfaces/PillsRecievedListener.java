package uk.co.cntwo.pilllogger.interfaces;

import java.util.List;

import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 21/10/13.
 */
public interface PillsRecievedListener {

    public void pillsRecieved(List<Pill> pills);
}
