package uk.co.cntwo.pilllogger.interfaces;

import java.util.List;

import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 21/10/13.
 */
public interface PillsReceivedListener {

    public void pillsReceived(List<Pill> pills);
}
