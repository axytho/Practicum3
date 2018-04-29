package filesystem;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * A class of links
 * 
 * @invar	links must refer to a valid File or Directory
 * 			| isValidItem(link)
 * 
 * 
 * 
 * @author Frederik van Eecke en Jonas Bertels
 *
 */

public class Link extends Item{
	
	/**
	 * Initialize a link with a given name, and Item
	 * 
	 * @param	name (total programming, so no error if wrong, or null, or weird name, only set to default by setName())
	 * 			| The name of the link
	 * @param	item (defensive programming: must be a valid directory, which has not been terminated)
	 * 			| The Disk Item to which the link refers
	 * 
	 * @effect	The new link is a disk item with a given name and a true writabillity (a link is always writable)
	 * 			| super(name, true)
	 * 
	 * @effect	the linked Item is set to the given item
	 * 			| setLink(item)
	 * 
	 * @post	The link is Valid
	 * 			| new.isValid() == true
	 * 
	 * @throws	IllegalArgumentException
	 * 			The given item has been terminated
	 * 			| item.isTerminated()
	 */
	
	public Link(Directory dir, String name, DiskItem item)	throws IllegalArgumentException {
		super(dir, name);
		if (item.isTerminated())	{
			dir.removeAsItem(this);
			throw new IllegalArgumentException("Item is terminated!");
		}
		setLink(item);
	}
	
	/**
	 * The item to which the file refers
	 */
	private Item link = null;
	
	/**
	 * Returns the Item to which the link refers
	 */
	@Basic @Raw
	protected Item getLink()	{
		return this.link;
	}

	
	/**
	 * Sets the link of the Link to the given item (should only be used for constructor)
	 * 
	 * @param	item
	 *			the item to which our link is going to refer
	 * @post	Our link refers to the new item
	 * 			| new.getLink() = item
	 */
	@Raw @Model
	private void setLink(Item item)	{
		this.link = item;
	}
	// changeLink does not exist.
	
	

	
	/**
	 * Return whether the current link is valid
	 * 
	 * @return	True if and only if the item to which it refers has not been terminated
	 * 			| !getLink().isTerminated()
	 */
	@Raw
	public boolean isValid()	{
		return !getLink().isTerminated();
	}
	
    /**
     * Get the total disk usage of this link
     * 
     * @return	The link does not take up space
     * 			| result == 0
     */
    
    public int getTotalDiskUsage() {
    	return 0;
    }

	

}
