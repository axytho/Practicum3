/**
 * Superclass consisting of Items and links
 * 
 * @invar	Each disk item must have a properly spelled name.
 * 			| isValidName(getName())
 * @invar   Each disk item must have a valid creation time.
 *          | isValidCreationTime(getCreationTime())
 * @invar   Each disk item must have a valid modification time.
 *          | canHaveAsModificationTime(getModificationTime())
 * @invar   Each disk item must have a valid parent directory.
 *          | hasProperParentDirectory()
 * 
 * @author 	Frederik van Eecke en Jonas Bertels   
 *
 */
package filesystem;

import java.util.Date;

import be.kuleuven.cs.som.annotate.*;
import filesystem.exception.*;

/**
 * An abstract class of disk items.
 *
   
 * 
 */
public abstract class Item {

	/**
	 * Initialize a new root disk item with given name.
	 * 
	 * @param  	name
	 *         	The name of the new disk item.
	 * 
	 * @effect  The name of the disk item is set to the given name.
	 * 			If the given name is not valid, a default name is set.
	 *          | setName(name) 
	 *          
	 * @post 	The disk item is a root item
	 * 			| new.isRoot()
	 * 
	 * @note    This happens because the constructor of creation time initializes with Date() and the parent directory is set to null by default
	 * 
	 * @post	The new creation time of this disk item is initialized to some time during
	 *          constructor execution.
	 *          | (new.getCreationTime().getTime() >= System.currentTimeMillis()) &&
	 *          | (new.getCreationTime().getTime() <= (new System).currentTimeMillis())
	 * @post    The new disk item has no time of last modification.
	 *          | new.getModificationTime() == null
	 */
	@Model
	protected Item(String name) {
		setName(name);
	}

	/**
	 * Initialize a new Item with given parent directory, name and 
	 * writability.
	 *   
	 * @param  	parent
	 *         	The parent directory of the new disk item.
	 * @param  	name
	 *         	The name of the new disk item.  
	 *         
	 * @effect  The name of the disk item is set to the given name.
	 * 			If the given name is not valid, a default name is set.
	 *          | setName(name) 
	 * @effect 	The given directory is set as the parent 
	 *         	directory of this item.
	 *         	| setParentDirectory(parent)
	 * @effect 	This item is added to the items of the parent directory
	 *         	| parent.addAsItem(this)
	 * @post    The new creation time of this disk item is initialized to some time during
	 *          constructor execution.
	 *          | (new.getCreationTime().getTime() >= System.currentTimeMillis()) &&
	 *          | (new.getCreationTime().getTime() <= (new System).currentTimeMillis())
	 * @post    The new disk item has no time of last modification.
	 *          | new.getModificationTime() == null
	 * @post    The new disk item is not terminated.
	 *          | !new.isTerminated()
	 * @throws 	IllegalArgumentException
	 *         	The given parent directory is not effective
	 * @throws 	IllegalArgumentException
	 *         	The given valid name already exists in the effective and  parent directory
	 *          | parent != null && 
	 *         	|   isValidName(name) && parent.containsItemWithName(name)
	 * @throws 	IllegalArgumentException
	 *         	The given name is not valid and the default name already exists in 
	 *         	the effective parent directory
	 *          | parent != null && 
	 *         	|   !isValidName(name) && parent.containsItemWithName(getDefaultName())
	 */
	@Model
	protected Item(Directory parent, String name) 
			throws IllegalArgumentException, ItemNotWritableException {
		if (parent == null) 
			throw new IllegalArgumentException();
		if (parent.isWritable() && isValidName(name) && parent.containsItemWithName(name))
			throw new IllegalArgumentException();
		if (parent.isWritable() && !isValidName(name) && parent.containsItemWithName(getDefaultName()))
			throw new IllegalArgumentException();
		if (!parent.isWritable()) 
			throw new ItemNotWritableException(parent);
		
		setName(name);
		setParentDirectory(parent);
		try {
			parent.addAsItem(this);
		} catch (IllegalArgumentException e) {
			//cannot occur
			assert false;
		}
	}

	/**********************************************************
	 * delete/termination
	 **********************************************************/

	/**
	 * Variable registering whether or not this disk item has been terminated.
	 */
	protected boolean isTerminated = false;


	/**
	 * Check whether this disk item is already terminated.
	 */
	@Raw @Basic
	public boolean isTerminated() {
		return isTerminated;
	}


	/**
	 * Check whether this disk item can be terminated.
	 * 
	 * @return	True if the disk item is not yet terminated
	 * 			| !isTerminated()
	 * @note	This specification must be left open s.t. the subclasses can change it
	 */
	public boolean canBeTerminated(){
		return !isTerminated();
	}

	/**
	 * Terminate this disk item.
	 * 
	 * @post 	This disk item is terminated.
	 *       	| new.isTerminated()
	 * @effect 	If this disk item is not terminated and it is not a root, it is made a root
	 * 			| if (!isTerminated() && !isRoot())  
	 * 			| then makeRoot()
	 */
	public void terminate() {
		if(!isTerminated()){
			if(!isRoot()){
				try{
					makeRoot();
				}catch(ItemNotWritableException e){
					//should not happen since this item and its parent are writable
					assert false;
				}
			}
			this.isTerminated = true;
		}
	}




	/**********************************************************
	 * name - total programming
	 **********************************************************/

	/**
	 * Variable referencing the name of this disk item.
	 */
	private String name = null;

	/**
	 * Return the name of this disk item.
	 */
	@Raw @Basic 
	public String getName() {
		return name;
	}

	/**
	 * Check whether the given name is a legal name for a disk item.
	 * 
	 * @param  	name
	 *			The name to be checked
	 * @return	True if the given string is effective, not
	 * 			empty and consisting only of letters, digits, dots,
	 * 			hyphens and underscores; false otherwise.
	 * 			| result ==
	 * 			|	(name != null) && name.matches("[a-zA-Z_0-9.-]+")
	 */
	public static boolean isValidName(String name) {
		return (name != null && name.matches("[a-zA-Z_0-9.-]+"));
	}

	/**
	 * Set the name of this disk item to the given name.
	 *
	 * @param   name
	 * 			The new name for disk item file.
	 * @post    If the given name is valid, the name of
	 *          this disk item is set to the given name,
	 *          otherwise the name of the disk item is set to a valid name (the default).
	 *          | if (isValidName(name))
	 *          |      then new.getName().equals(name)
	 *          |      else new.getName().equals(getDefaultName())
	 */
	@Raw @Model
	protected void setName(String name) {
		if (isValidName(name)) {
			this.name = name;
		} else {
			this.name = getDefaultName();
		}
	}

	/**
	 * Return the name for a new disk item which is to be used when the
	 * given name is not valid.
	 *
	 * @return	A valid file name.
	 *         	| isValidName(result)
	 */
	@Model
	private static String getDefaultName() {
		return "new_Item";
	}

	/**
	 * Check whether the name of this disk item can be changed into the
	 * given name.
	 * 
	 * @return  True if this disk item is not terminated, the given 
	 *          name is a valid name for a disk item, the given name is different 
	 *          from the current name of this disk item
	 *          and the parent directory does not 
	 *          already contain an other item with the given name;
	 *          false otherwise.
	 *          | result == !isTerminated() && isValidName(name) && 
	 *          |			!getName().equals(name) && (!getParentDirectory().containsItemWithName(name) )
	 */
	public boolean canAcceptAsNewName(String name) {
		return !isTerminated() && isValidName(name) && !getName().equals(name) &&
				(!getParentDirectory().containsItemWithName(name));
	}	

	/**
	 * Set the name of this disk item to the given name.
	 *
	 * @param	name
	 * 			The new name for this disk item.
	 * @effect  If this disk item can accept the given name as
	 *          its name, the name of this disk item is set to
	 *          the given name.
	 *          Otherwise there is no change
	 *          | if (canAcceptAsNewName(name))
	 *          | then setName(name)
	 * @effect  If this disk item can accept the given name as
	 *          its new name, the modification time of this disk item is 
	 *          updated.
	 *          | if (canAcceptAsNewName(name))
	 *          | then setModificationTime()
	 * @effect  the order of the items in the parent
	 * 			directory is restored given the new name
	 * 			|	getParentDirectory().restoreOrderAfterNameChangeAt(getParentDirectory().getIndexOf(this))
	 * @throws 	IllegalStateException
	 * 			This disk item is already terminated
	 * 			| isTerminated()
	 * 
	 * @note	this item is overwritten in directory, so is not a root item
	 */
	public void changeName(String name) throws ItemNotWritableException, IllegalStateException {
		if (isTerminated()) throw new IllegalStateException("Disk item terminated!");
		if (canAcceptAsNewName(name)) {
			setName(name);
			setModificationTime();
			int currentIndexInParent = getParentDirectory().getIndexOf(this);
			getParentDirectory().restoreOrderAfterNameChangeAt(currentIndexInParent);
		}
	}

	/**
	 * Checks whether the name of this item is lexicographically 
	 * ordered after the given name, ignoring case.
	 * 
	 * @param 	name
	 *       	The name to compare with
	 * @return 	True if the given name is effective and the name of this item 
	 * 			comes strictly after the given name (ignoring case), 
	 *         	false otherwise.
	 *       	| result == (name != null) && (getName().compareToIgnoreCase(name) > 0)
	 */
	public boolean isOrderedAfter(String name) {
		return (name != null) && (getName().compareToIgnoreCase(name) > 0);
	}

	/**
	 * Checks whether the name of this item is lexicographically 
	 * ordered before the given name, ignoring case.
	 * 
	 * @param 	name
	 *       	The name to compare with
	 * @return 	True if the given name is effective and the name of this item 
	 * 			comes strictly before the given name (ignoring case), 
	 *         	false otherwise.
	 *       	| result == (name != null) && (getName().compareToIgnoreCase(name) < 0)
	 */
	public boolean isOrderedBefore(String name) {
		return (name != null) && (getName().compareToIgnoreCase(name) < 0);
	}

	/**
	 * Checks whether this item is ordered after the given other item
	 * according to the lexicographic ordering of their names,
	 * ignoring case.
	 * 
	 * @param 	other
	 *        	The item to compare with
	 * @return 	True if the given other item is effective, and the name
	 *         	of this item is lexicographically ordered after the name
	 *         	of the given other item (ignoring case),
	 *         	false otherwise.
	 *       	| result == (other != null) && 
	 *       	|           isOrderedAfter(other.getName())
	 */
	public boolean isOrderedAfter(@Raw Item other) {
		return (other != null) && isOrderedAfter(other.getName());
	}

	/**
	 * Checks whether this item is ordered before the given other item
	 * according to the lexicographic ordering of their names,
	 * ignoring case.
	 * 
	 * @param 	other
	 *        	The item to compare with
	 * @return 	True if the given other item is effective, and the name
	 *         	of this item is lexicographically ordered before the name
	 *         	of the given other item (ignoring case),
	 *         	false otherwise.
	 *       	| result == (other != null) && 
	 *       	|           isOrderedBefore(other.getName())
	 */
	public boolean isOrderedBefore(@Raw Item other) {
		return (other != null) && isOrderedBefore(other.getName());
	}


	/**********************************************************
	 * creationTime
	 **********************************************************/

	/**
	 * Variable referencing the time of creation.
	 */
	private final Date creationTime = new Date();

	/**
	 * Return the time at which this disk item was created.
	 */
	@Raw @Basic @Immutable 
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Check whether the given date is a valid creation time.
	 *
	 * @param  	date
	 *         	The date to check.
	 * @return 	True if and only if the given date is effective and not
	 * 			in the future.
	 *         	| result == 
	 *         	| 	(date != null) &&
	 *         	| 	(date.getTime() <= System.currentTimeMillis())
	 */
	public static boolean isValidCreationTime(Date date) {
		return 	(date!=null) &&
				(date.getTime()<=System.currentTimeMillis());
	}




	/**********************************************************
	 * modificationTime
	 **********************************************************/

	/**
	 * Variable referencing the time of the last modification,
	 * possibly null.
	 */
	private Date modificationTime = null;

	/**
	 * Return the time at which this disk item was last modified, that is
	 * at which the name or content was last changed. If this disk item has
	 * not yet been modified after construction, null is returned.
	 */
	@Raw @Basic
	public Date getModificationTime() {
		return modificationTime;
	}

	/**
	 * Check whether this disk item can have the given date as modification time.
	 *
	 * @param	date
	 * 			The date to check.
	 * @return 	True if and only if the given date is either not effective
	 * 			or if the given date lies between the creation time and the
	 * 			current time.
	 *         | result == (date == null) ||
	 *         | ( (date.getTime() >= getCreationTime().getTime()) &&
	 *         |   (date.getTime() <= System.currentTimeMillis())     )
	 */
	@Raw
	public boolean canHaveAsModificationTime(Date date) {
		return (date == null) ||
				( (date.getTime() >= getCreationTime().getTime()) &&
						(date.getTime() <= System.currentTimeMillis()) );
	}

	/**
	 * Set the modification time of this disk item to the current time.
	 *
	 * @post   The new modification time is effective.
	 *         | new.getModificationTime() != null
	 * @post   The new modification time lies between the system
	 *         time at the beginning of this method execution and
	 *         the system time at the end of method execution.
	 *         | (new.getModificationTime().getTime() >=
	 *         |                    System.currentTimeMillis()) &&
	 *         | (new.getModificationTime().getTime() <=
	 *         |                    (new System).currentTimeMillis())
	 */
	@Model @Raw
	protected void setModificationTime() {
		modificationTime = new Date();
	}

	/**
	 * Return whether this disk item and the given other disk item have an
	 * overlapping use period.
	 *
	 * @param 	other
	 *        	The other disk item to compare with.
	 * @return 	False if the other disk item is not effective
	 * 			False if the prime object does not have a modification time
	 * 			False if the other disk item is effective, but does not have a modification time
	 * 			otherwise, true if and only if the open time intervals of this disk item and
	 * 			the other disk item overlap
	 *        	| if (other == null) then result == false else
	 *        	| if ((getModificationTime() == null)||
	 *        	|       other.getModificationTime() == null)
	 *        	|    then result == false
	 *        	|    else 
	 *        	| result ==
	 *        	| ! (getCreationTime().before(other.getCreationTime()) && 
	 *        	|	 getModificationTime().before(other.getCreationTime()) ) &&
	 *        	| ! (other.getCreationTime().before(getCreationTime()) && 
	 *        	|	 other.getModificationTime().before(getCreationTime()) )
	 */
	public boolean hasOverlappingUsePeriod(Item other) {
		if (other == null) return false;
		if(getModificationTime() == null || other.getModificationTime() == null) return false;
		return ! (getCreationTime().before(other.getCreationTime()) && 
				getModificationTime().before(other.getCreationTime()) ) &&
				! (other.getCreationTime().before(getCreationTime()) && 
						other.getModificationTime().before(getCreationTime()) );
	}






	/**********************************************************
	 * parent directory
	 **********************************************************/	

	/**
	 * Variable referencing the directory (if any) to which this 
	 * disk item belongs.
	 */
	private Directory parentDirectory = null;


	/**
	 * Return the root item to which this item directly or indirectly
	 * belongs. In case this item is a root item, the item itself is 
	 * the result.
	 * 
	 * @return If this item is a root item, this item is returned;
	 *         Otherwise the root to which the parent item of this 
	 *         item belongs is returned.
	 *         | if (isRoot())
	 *         | then result == this
	 *         | else result == getParentDirectory().getRoot()
	 */
	public Item getRoot() {
		if (isRoot()) {
			return this;
		} else {
			return getParentDirectory().getRoot();
		}
	}

	/**
	 * Move this disk item to a given directory.
	 * 
	 * @param   target
	 *          The target directory.
	 * @effect  This disk item is
	 *          removed from its parent directory.
	 *          | then getParentDirectory().removeAsItem(this) 
	 * @effect  This disk item is added to the target directory.
	 *          | target.addAsItem(this)
	 * @effect  The modification time is updated.
	 *          | setModificationTime()
	 * @post    The given directory is registered as the parent directory 
	 *          of this item.
	 *          | new.getParentDirectory() == target
	 * @throws  IllegalArgumentException
	 *          The given target directory is not effective, or the parent
	 *          directory of this disk item is the given target directory,
	 *          or the target directory cannot have this item
	 *          | (target == null) || 
	 *          | (target == getParentDirectory()) ||
	 *          | (!target.canHaveAsItem(this))

	 * @throws	ItemNotWritableException(target)
	 * 			This target is effective, but not writable
	 * 			| (target != null) && !target.isWritable()
	 * @throws 	IllegalStateException
	 * 			This disk item is terminated
	 * 			| isTerminated()
	 */
	public void move(Directory target) 
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		if ( isTerminated()) 
			throw new IllegalStateException("Item is terminated!");
		if ( (target == null) || (getParentDirectory() == target) || !target.canHaveAsItem(this))
			throw new IllegalArgumentException();
		if (!target.isWritable())
			throw new ItemNotWritableException(target);

		try{
			getParentDirectory().removeAsItem(this);
			//our disk item becomes raw now
		}catch(IllegalArgumentException e){
			//this cannot happen because of the class invariants
			assert false;			
		}

		setParentDirectory(target); 
		try{
			target.addAsItem(this); //this is a raw item because it's not yet registered in the new parent
									//so the formal argument of addAsItem should be annotated @Raw
		}catch(IllegalArgumentException e){
			//this should not happen, because it can have this item
			assert false;
		}catch(ItemNotWritableException e){
			//this should not happen, because we checked it
			assert false;
		}
		setModificationTime();
	}


	/**
	 * Turns this disk item in a root disk item.
	 * 
	 * @post    The disk item is a root disk item.
	 *          | new.isRoot()
	 * @effect  This disk item is
	 *          removed from its parent directory.
	 *          | then getParentDirectory().removeAsItem(this)
	 * @effect  Its modification time changed

	 *          | then setModificationTime()         
	 * 
	 * @throws	ItemNotWritable(getParentDirectory())	
	 * 			This disk item's parent directory is not writable
	 * 			| !getParentDirectory().isWritable()
	 * @throws 	IllegalStateException
	 * 			This disk item is terminated
	 * 			| isTerminated()
	 */ 
	public void makeRoot() throws ItemNotWritableException {
		if ( isTerminated()) 
			throw new IllegalStateException("Item is terminated!");
		
		if(!getParentDirectory().isWritable())
			throw new ItemNotWritableException(getParentDirectory());

		Directory dir = getParentDirectory();
		setParentDirectory(null); 
		//this item is now in a raw state
		dir.removeAsItem(this);
		setModificationTime();
		
	}



	/** 
	 * Check whether this disk item (not a directory) has a proper parent directory as
	 * its parent directory.
	 * 
	 * @return  true if this disk item can have its registered parent directory 
	 * 			as its parent directory and 
	 * 			its registered parent directory has this item as a registered item.
	 *          | result == canHaveAsParentDirectory(getParentDirectory()) &&
	 *			|            (getParentDirectory().hasAsItem(this))
	 *	@note	This checker is split up in two parts, the consistency of the 
	 *			bidirectional relationship is added to the functionality of 
	 *			the internal state checker (canHaveAsParentDirectory())
	 */
	@Raw 
	public boolean hasProperParentDirectory() {
		return canHaveAsParentDirectory(getParentDirectory()) &&
				(getParentDirectory().hasAsItem(this));
	}
	
	/** 
	 * Check whether this disk item can have the given directory as
	 * its parent directory.
	 * 
	 * @param  	directory
	 *          The directory to check.
	 * @return  If this disk item is terminated, 
	 * 			true if the given directory is not effective, 
	 * 			false otherwise.
	 *          | if (this.isTerminated())
	 *          | then result == (directory == null)
	 * @return	If this disk item is not terminated,
	 * 				if the given directory is not effective,
	 * 				then true if this disk item is a root item or the parent of this item is writable, 
	 * 					 false otherwise
	 * 				else if the given directory is terminated, then false
	 * 					 if this disk item is the same as the given directory, then false
	 * 					 if this disk item is a direct or indirect parent of the given directory, then false
	 * 					 else true if the given directory is writable and it can have this item as an item
	 * 							and this item is a root or the parent directory of this item is writable,
	 * 						  false otherwise.
	 *			| if (!this.isTerminated())
	 *			| then if (directory == null)
	 *			|	   then result == (isRoot() || this.getParentDirectory().isWritable())
	 *			|	   else if (directory.isTerminated()) then result == false
	 *			|		 	if (directory == this) then result == false
	 *			|			if (this.isDirectOrIndirectParentOf(directory)) then result == false
	 *			|			else result == (directory.isWritable() && directory.canHaveAsItem(this) &&
	 *			|							(this.isRoot() || this.getParentDirectory().isWritable()) )
	 *	@note	This checker checks all conditions except the consistency of the bidirectional relationship
	 *			This checker can thus be used to check whether a disk item can accept a directory
	 *			as its new parent directory
	 */
	@Raw 
	public boolean canHaveAsParentDirectory(Directory directory) {
		if (this.isTerminated())
			return (directory == null);
		if (directory == null)
			return (this.isRoot() || this.getParentDirectory().isWritable());
		if (directory.isTerminated())
			return false;
		if (this.isDirectOrIndirectParentOf(directory))
			return false;
		else return (directory.isWritable() && directory.canHaveAsItem(this) &&
				(this.isRoot() || this.getParentDirectory().isWritable()) );
	}

	/**
	 * Check whether this item is a direct or indirect parent of to the given item.
	 * 
	 * @param 	other
	 *        	The disk item to check.
	 * @return 	If the given item is non-effective, then false
	 * 			| if (item == null) then result == false
	 * @return	If the given item is effective, 
	 * 			then true if this item is the direct or indirect parent of the given item,
	 * 				 false otherwise
	 *      	| if (item != null)
	 *      	| then result == (this == item.getParentDirectory() ||
	 *      	|				  isDirectOrIndirectParentOf(item.getParentDirectory() )
	 */
	@Raw
	public boolean isDirectOrIndirectParentOf(@Raw Item item) {
		if(item == null) return false;
		else return (this == item.getParentDirectory() || isDirectOrIndirectParentOf(item.getParentDirectory()));
	}

	/**
	 * Set the parent directory of this item to the given directory.
	 *
	 * @param  directory
	 *         The new parent directory for this item.
	 * @post   The parent directory of this item is set to the given 
	 *         directory.
	 *         | new.getParentDirectory() == directory
	 * @throws IllegalArgumentException
	 *         This item cannot have the given directory as its
	 *         parent directory.
	 *         | ! canHaveAsParentDirectory(parentDirectory)
	 * @throws 	IllegalStateException
	 * 			This disk item is terminated
	 * 			| isTerminated()
	 */
	@Raw @Model
	protected void setParentDirectory(Directory parentDirectory)
			throws IllegalArgumentException, IllegalStateException {
		if ( isTerminated()) 
			throw new IllegalStateException("Item is terminated!");
		if (!canHaveAsParentDirectory(parentDirectory)) {
			throw new IllegalArgumentException("Inappropriate item!");
		}
		this.parentDirectory = parentDirectory;
	}

	/**
	 * Return the parent directory (if any) to which this item
	 * applies.
	 */
	@Raw @Basic
	public Directory getParentDirectory() {
		return parentDirectory;
	}


}

