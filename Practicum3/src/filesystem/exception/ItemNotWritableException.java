
package filesystem.exception;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;
import filesystem.Item;

/**
 * A class for signaling illegal attempts to change a disk item
 * due to writability restrictions.
 */
public class ItemNotWritableException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5764443045080909433L;

	/**
	 * Variable referencing the disk item to which change was denied.
	 */
	private final Item item;

	/**
	 * Initialize this new not writable exception involving the
	 * given disk item.
	 * 
	 * @param	item
	 * 			The disk item for the new not writable exception.
	 * @post	The disk item involved in the new not writable exception
	 * 			is set to the given disk item.
	 * 			| new.getItem() == item
	 */
	@Raw
	public ItemNotWritableException(Item item) {
		this.item = item;
	}
	
	/**
	 * Return the disk item involved in this not writable exception.
	 */
	@Raw @Basic
	public Item getItem() {
		return item;
	}
	
}
