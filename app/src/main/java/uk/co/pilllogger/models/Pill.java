/**
 * 
 */
package uk.co.pilllogger.models;

import android.content.Context;

import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.events.CreatedConsumptionEvent;
import uk.co.pilllogger.events.DeletedConsumptionEvent;
import uk.co.pilllogger.events.DeletedConsumptionGroupEvent;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.state.State;

/**
 * @author alex
 *
 */
public class Pill implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private static final String TAG = "Pill";
    private int _id;
	private String _name = "";
    private String _units = "mg";
	private float _size;
    private int _colour = R.color.pill_default_color;
    private boolean _favourite = false;
    private Consumption _latest = null;
    private Consumption _first = null;

    private List<Consumption> _consumptions = new ArrayList<Consumption>();

    public Pill() {
        State.getSingleton().getBus().register(this);
    }

    public Pill(CharSequence name, float size) {
        this();
        _name = String.valueOf(name);
        _size = size;
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * @return the size
	 */
	public float getSize() {
		return _size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(float size) {
		_size = size;
	}

	public int getId() {
		return _id;
	}

    public void setId(int id) {
        _id = id;
    }

    public int getColour() {
        return _colour;
    }

    public void setColour(int colour) {
        _colour = colour;
    }

    public boolean isFavourite() {
        return _favourite;
    }

    public void setFavourite(boolean favourite) {
        _favourite = favourite;
    }

    public String getUnits() {
        return _size > 0 ? _units : "";
    }

    public void setUnits(String _units) {
        this._units = _units;
    }

    public int getSortOrder(){return _id;}

    public List<Consumption> getConsumptions() {
        return _consumptions;
    }

    public Consumption getLatestConsumption(Context context){
        if(_consumptions.isEmpty()) {
            if(ConsumptionRepository.getSingleton(context).isCachedForPill(getId())) {
                List<Consumption> consumptions = ConsumptionRepository.getSingleton(context).getForPill(this);
                if (consumptions == null || consumptions.size() == 0)
                    return null;

                _consumptions.addAll(consumptions);
            }
            else{
                return null;
            }
        }

        if(_latest == null) {
            updateLatestFirst();
        }
        return _latest;
    }

    public Consumption getFirstConsumption(){
        if(_consumptions.isEmpty())
            return null;

        if(_first == null){
            updateLatestFirst();
        }

        return _first;
    }

    private void updateLatestFirst(){
        if(_consumptions.isEmpty())
            return;

        _latest = _consumptions.get(0);
        _first = _consumptions.get(0);

        if(_latest == null || _first == null)
            return;

        for (Consumption c : _consumptions) {
            long time = c.getDate().getTime();
            Date latestDate = _latest.getDate();
            Date firstDate = _first.getDate();

            if(latestDate == null || firstDate == null){
                Logger.e(TAG, "Latest or First date is null");
                continue;
            }

            long latestTime = latestDate.getTime();
            long firstTime = firstDate.getTime();

            if (time > latestTime)
                _latest = c;
            if(time < firstTime)
                _first = c;
        }
    }

    public float getTotalSize(int hours){
        return getTotalQuantity(hours) * getSize();
    }

    public int getTotalQuantity(int hours){
        Date back = DateTime.now().plusHours(hours * -1).toDate();
        Date currentDate = new Date();

        int total = 0;

        for (Consumption consumption : _consumptions) {
            Date consumptionDate = consumption.getDate();
            if (hours < 0 || consumptionDate.compareTo(back) >= 0 && consumptionDate.compareTo(currentDate) <= 0) {
                total += (consumption.getQuantity());
            }
        }

        return total;
    }

    public String getFormattedSize(){
        if(_size <= 0)
            return "";
        return NumberHelper.getNiceFloatString(_size);
    }

    @Override
	public String toString(){
		return getName() + '(' + getSize() + ')';
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        Pill pill = (Pill) o;

        if (_favourite != pill._favourite) return false;
        if (_id != pill._id) return false;
        if (_size != pill._size) return false;
        if (_colour !=  pill._colour) return false;
        if (!_name.equals(pill._name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id;
        result = 31 * result + _name.hashCode();
        result = 31 * result + _units.hashCode();
        result = 31 * result + (_size != +0.0f ? Float.floatToIntBits(_size) : 0);
        result = 31 * result + _colour;
        result = 31 * result + (_favourite ? 1 : 0);
        return result;
    }

    @Subscribe
    public void consumptionAdded(CreatedConsumptionEvent event) {
        Consumption consumption = event.getConsumption();
        if (consumption != null) {
            if (consumption.getPillId() == _id && !_consumptions.contains(consumption)) {
                _consumptions.add(consumption);

                if(_latest == null || consumption.getDate().getTime() > _latest.getDate().getTime())
                    _latest = consumption;
            }
        }
    }

    @Subscribe
    public void consumptionDeleted(DeletedConsumptionEvent event) {
        Consumption consumption = event.getConsumption();
        if (consumption != null) {
            if (consumption.getPillId() == _id && _consumptions.contains(consumption)) {
                _consumptions.remove(consumption);

                if(_latest != null && consumption.getId() == _latest.getId())
                    _latest = null;
            }
        }
    }

    @Subscribe
    public void consumptionPillGroupDeleted(DeletedConsumptionGroupEvent event) {
        List<Consumption> toRemove = new ArrayList<Consumption>();

        if(event.getGroup() == null)
            return;

        for(Consumption c : _consumptions){
            String consumptionGroup = c.getGroup();
            if(consumptionGroup == null)
                continue;

            if(c.getGroup().equals(event.getGroup()) && c.getPillId() == event.getPillId())
                toRemove.add(c);

            if(_latest != null && c.getId() == _latest.getId())
                _latest = null;
        }

        _consumptions.removeAll(toRemove);
    }

    public float getDailyAverage(){
        return getDailyAverage(0);
    }

    public float getDailyAverage(int days){
        Consumption first = getFirstConsumption();
        if(first == null)
            return 0;

        DateTime firstDt = new DateTime(first.getDate());
        DateTime now = new DateTime();

        int totalDays = Days.daysBetween(firstDt.withTimeAtStartOfDay(), now.withTimeAtStartOfDay()).getDays();

        if(days == 0)
            days = totalDays;

        if(days > totalDays)
            days = totalDays;

        int hours = days * 24;
        if(totalDays == 0)
            hours = 24;

        int total = getTotalQuantity(hours);

        if(days == 0)
            return total;

        return total / (float)days;
    }
}
