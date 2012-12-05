/**
 * 
 */
package uk.co.cntwo.pilllogger.models;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author alex
 *
 */
public class Pill implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UUID _id;
	private String _name;
	private int _size;

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

	public UUID getId() {
		return _id;
	}
	

	public Pill() {
		_id = UUID.randomUUID();
	}

	@Override
	public String toString(){
		return getName() + '(' + getSize() + ')';
	}
}
