package uk.co.pilllogger.models;

import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alex on 05/06/2014
 * in uk.co.pilllogger.models.
 */
public class ExportSettings {
    private Set<Pill> _selectedPills = new HashSet<Pill>();

    MutableDateTime _startDate = new MutableDateTime();
    MutableDateTime _endDate = new MutableDateTime();
    LocalTime _startTime = new LocalTime();
    LocalTime _endTime = new LocalTime();

    public Set<Pill> getSelectedPills() {
        return _selectedPills;
    }

    public void setSelectedPills(Set<Pill> selectedPills) {
        _selectedPills = selectedPills;
    }

    public MutableDateTime getStartDate() {
        return _startDate;
    }

    public void setStartDate(MutableDateTime startDate) {
        _startDate = startDate;
    }

    public MutableDateTime getEndDate() {
        return _endDate;
    }

    public void setEndDate(MutableDateTime endDate) {
        _endDate = endDate;
    }

    public LocalTime getStartTime() {
        return _startTime;
    }

    public void setStartTime(LocalTime startTime) {
        _startTime = startTime;
    }

    public LocalTime getEndTime() {
        return _endTime;
    }

    public void setEndTime(LocalTime endTime) {
        _endTime = endTime;
    }
}
