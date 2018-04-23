package filesystem;



import be.kuleuven.cs.som.annotate.*;
import filesystem.exception.*;

/**
 * An abstract class of disk items.
 *
 * 
 * @author 	Frederik van Eecke en Jonas Bertels      
 * 
 */
public abstract class DiskItem extends Item {

	/**
	 * Initialize a new root disk item with given name and writability.
	 * 
	 * @param  	name
	 *         	The name of the new disk item.
	 * @param  	writable
	 *         	The writability of the new disk item.
	 * 
	 * @effect  A new item is initialized with a given name
	 * 			| super(name)
	 * @effect	The writability is set to the given flag
	 * 			| setWritable(writable)
	 */
	@Model
	protected DiskItem(String name, boolean writable) {
		super(name);
		setWritable(writable);
	}

	/**
	 * Initialize a new disk item with given parent directory, name and 
	 * writability.
	 *   
	 * @param  	parent
	 *         	The parent directory of the new disk item.
	 * @param  	name
	 *         	The name of the new disk item.  
	 * @param  	writable
	 *         	The writability of the new disk item.
	 *         

	 * @effect	The writability is set to the given flag
	 * 			| setWritable(writable)
	 * @effect 	A new Item with a given parent directory and name is initialized
	 * 			| super(parent, name)
	 * @post    The new disk item is not terminated.
	 *          | !new.isTerminated()

	 * @throws 	ItemNotWritableException(parent)
	 *         	The given parent directory is effective, but not writable.
	 *         	| parent != null && !parent.isWritable()

	 */
	@Model
	protected DiskItem(Directory parent, String name, boolean writable) 
			throws IllegalArgumentException, ItemNotWritableException {
		super(parent, name);
	
		if (parent != null && !parent.isWritable()) {
			throw new ItemNotWritableException(parent);
		}
		setWritable(writable);
		try {
			parent.addAsItem(this);
		} catch (ItemNotWritableException e) {
			//cannot occur
			assert false;
		} catch (IllegalArgumentException e) {
			//cannot occur
			assert false;
		}

	}

	/**********************************************************
	 * delete/termination
	 **********************************************************/




	/**
	 * Check whether this disk item can be terminated.
	 * 
	 * @return	True if the disk item is not yet terminated, is writable and it is either a root or
	 * 			its parent directory is writable
	 * 			| if (isTerminated() || !isWritable() || (!isRoot() && !getParentDirectory().isWritable()))
	 * 			| then result == false
	 * @note	This specification must be left open s.t. the subclasses can change it
	 */
	@Override
	public boolean canBeTerminated(){
		return !isTerminated() && isWritable() && (isRoot() || getParentDirectory().isWritable());
	}
	/**
	 * Terminate this disk item.
	 * 
	 * @post 	This disk item is terminated.
	 *       	| new.isTerminated()
	 * @effect 	If this disk item is not terminated and it is not a root, it is made a root
	 * 			| if (!isTerminated() && !isRoot())  
	 * 			| then makeRoot()
	 * @throws 	IllegalStateException
	 * 		   	This disk item is not yet terminated and it can not be terminated.
	 * 		   	| !isTerminated() && !canBeTerminated()
	 */
	@Override
	public void terminate() throws IllegalStateException {
		if(!isTerminated()){
			if (!canBeTerminated()) {
				throw new IllegalStateException("This item cannot be terminated");
			}
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
	 * Check whether the name of this disk item can be changed into the
	 * given name.
	 * 
	 * @return  True if this disk item is not terminated, the given 
	 *          name is a valid name for a disk item, this disk item
	 *          is writable, the given name is different from the current name of this disk item
	 *          and either this item is a root item or the parent directory does not 
	 *          already contain an other item with the given name;
	 *          false otherwise.
	 *          | result == !isTerminated() && isWritable() && isValidName(name) && 
	 *          |			!getName().equals(name) && ( isRoot() || !getParentDirectory().containsDiskItemWithName(name) )
	 */
	@Override
	public boolean canAcceptAsNewName(String name) {
		return !isTerminated() && isWritable() && isValidName(name) && !getName().equals(name) &&
				(isRoot() || !getParentDirectory().containsItemWithName(name));
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
	 * @effect  If this disk item is not a root item, the order of the items in the parent
	 * 			directory is restored given the new name
	 * 			| if (!isRoot()) then 
	 * 			|	getParentDirectory().restoreOrderAfterNameChangeAt(getParentDirectory().getIndexOf(this))
	 * @throws  ItemNotWritableException(this)
	 *          This disk item is not writable.
	 *          | !isWritable()
	 * @throws 	IllegalStateException
	 * 			This disk item is already terminated
	 * 			| isTerminated()
	 */
	@Override
	public void changeName(String name) throws ItemNotWritableException, IllegalStateException {
		if (isTerminated()) throw new IllegalStateException("Disk item terminated!");
		if (!isWritable()) throw new ItemNotWritableException(this);
		if (canAcceptAsNewName(name)) {
			setName(name);
			setModificationTime();
			if(!isRoot()){
				int currentIndexInParent = getParentDirectory().getIndexOf(this);
				getParentDirectory().restoreOrderAfterNameChangeAt(currentIndexInParent);
			}
		}
	}





	/**********************************************************
	 * writable
	 **********************************************************/

	/**
	 * Variable registering whether or not this disk item is writable.
	 */
	private boolean isWritable = true;

	/**
	 * Check whether this disk item is writable.
	 */
	@Raw @Basic
	public boolean isWritable() {
		return isWritable;
	}

	/**
	 * Set the writability of this disk item to the given writability.
	 *
	 * @param isWritable
	 *        The new writability
	 * @post  The given writability is registered as the new writability
	 *        for this disk item.
	 *        | new.isWritable() == isWritable
	 */
	@Raw 
	public void setWritable(boolean isWritable) {
		this.isWritable = isWritable;
	}




	/**********************************************************
	 * parent directory
	 **********************************************************/	






	/**
	 * Move this disk item to a given directory.
	 * 
	 * @param   target
	 *          The target directory.
	 * @effect  If this disk item is not a root, this disk item is
	 *          removed from its parent directory.
	 *          | if (!isRoot())
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
	 * @throws	ItemNotWritableException(this)
	 * 			This disk item is not writable
	 * 			| !isWritable()
	 * @throws	ItemNotWritableException(target)
	 * 			This target is effective, but not writable
	 * 			| (target != null) && !target.isWritable()
	 * @throws 	IllegalStateException
	 * 			This disk item is terminated
	 * 			| isTerminated()
	 */
	@Override
	public void move(Directory target) 
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		if ( isTerminated()) 
			throw new IllegalStateException("Diskitem is terminated!");
		if ( (target == null) || (getParentDirectory() == target) || !target.canHaveAsItem(this))
			throw new IllegalArgumentException();
		if (!isWritable())
			throw new ItemNotWritableException(this);
		if (!target.isWritable())
			throw new ItemNotWritableException(target);

		if (!isRoot()) {
			try{
				getParentDirectory().removeAsItem(this);
				//our disk item becomes raw now
			}catch(IllegalArgumentException e){
				//this cannot happen because of the class invariants
				assert false;
			}
		}
		setParentDirectory(target); 
		try{
			target.addAsItem(this); //this is a raw item because it's not yet registered in the new parent
									//so the formal argument of assAsItem should be annotated @Raw
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
	 * @effect  If this disk item is not a root, this disk item is
	 *          removed from its parent directory.
	 *          | if (!isRoot())
	 *          | then getParentDirectory().removeAsItem(this)
	 * @effect  If this disk item is not a root, its modification time changed
	 * 			| if (!isRoot())
	 *          | then setModificationTime()         
	 * 
	 * @throws	ItemNotWritableException(this)
	 * 			This disk item is not a root and it is not writable
	 * 			| !isRoot() && !isWritable()
	 * @throws	DiskItemNotWritable(getParentDirectory())	
	 * 			This disk item is not a root and its parent directory is not writable
	 * 			| !isRoot() && !getParentDirectory().isWritable()
	 * @throws 	IllegalStateException
	 * 			This disk item is terminated
	 * 			| isTerminated()
	 */ 
	@Override
	public void makeRoot() throws ItemNotWritableException {
		if ( isTerminated()) 
			throw new IllegalStateException("Diskitem is terminated!");
		if (!isRoot()) {
			if (!isWritable()) 
				throw new ItemNotWritableException(this);
			if(!getParentDirectory().isWritable())
				throw new ItemNotWritableException(getParentDirectory());

			Directory dir = getParentDirectory();
			setParentDirectory(null); 
			//this item is now in a raw state
			dir.removeAsItem(this);
			setModificationTime();
		}
	}




}