/**
 * 
 */
package uk.co.pilllogger.models;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.state.Observer;

/**
 * @author alex
 *
 */
public class Pill implements Serializable, Observer.IConsumptionAdded, Observer.IConsumptionDeleted {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _id;
	private String _name = "";
    private String _units = "mg";
	private int _size;
    private int _colour = R.color.pill_default_color;
    private boolean _favourite = false;

    private List<Consumption> _consumptions = new ArrayList<Consumption>();

    public Pill() {
        Observer.getSingleton().registerConsumptionAddedObserver(this);
        Observer.getSingleton().registerConsumptionDeletedObserver(this);
    }

    public Pill(CharSequence name, int size) {
        this();
        _name = String.valueOf(name);
        _size = size;
    }

    public Pill(String name, int size, String units) {
        this(name, size);
        _units = units;
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
	public int getSize() {
		return _size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
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
        return _units;
    }

    public void setUnits(String _units) {
        this._units = _units;
    }

    public List<Consumption> getConsumptions() {
        return _consumptions;
    }

    public Consumption getLatestConsumption(){

        if(_consumptions.isEmpty())
            return null;

        Consumption latest = _consumptions.get(0);
        for(Consumption c : _consumptions){
            if(c.getDate().getTime() > latest.getDate().getTime())
                latest = c;
        }

        return latest;
    }

    public int getTotalSize(int hours){
        return getTotalQuantity(hours) * getSize();
    }

    public int getTotalQuantity(int hours){
        Date back = DateTime.now().plusHours(hours * -1).toDate();
        Date currentDate = new Date();

        int total = 0;

        for (Consumption consumption : _consumptions) {
            Date consumptionDate = consumption.getDate();
            if (consumptionDate.compareTo(back) >= 0 && consumptionDate.compareTo(currentDate) <= 0) {
                total += (consumption.getQuantity());
            }
        }

        return total;
    }

    @Override
	public String toString(){
		return getName() + '(' + getSize() + ')';
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

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
        result = 31 * result + _size;
        result = 31 * result + _colour;
        result = 31 * result + (_favourite ? 1 : 0);
        return result;
    }

    @Override
    public void consumptionAdded(Consumption consumption) {
        if (consumption != null) {
            if (consumption.getPillId() == _id && !_consumptions.contains(consumption)) {
                _consumptions.add(consumption);
            }
        }
    }

    @Override
    public void consumptionDeleted(Consumption consumption) {
        if (consumption != null) {
            if (consumption.getPillId() == _id && _consumptions.contains(consumption)) {
                _consumptions.remove(consumption);
            }
        }
    }
}
