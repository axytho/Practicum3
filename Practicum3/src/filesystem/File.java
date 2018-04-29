package filesystem;

import filesystem.exception.*;
import be.kuleuven.cs.som.annotate.*;

/**
 * A class of files.
 *
 * @invar	Each file must have a valid size.
 * 			| isValidSize(getSize())
 * @invar   Each file must have a valid type.
 *          | isValidType(getType())
 * 
 * @author 	Frederik van Eecke en Jonas Bertels     
 */
public class File extends DiskItem{

    /**********************************************************
     * Constructors
     **********************************************************/



    /**
     * Initialize a new writable, empty file with given name in the given directory
     *
     * @param  name
     *         The name of the new file.
     * @param  type
     *         The type of the new file.
     * @param  dir
     * 		   The parent directory
     * 
     * @effect This new file is initialized with the given name
     *         and the given type, the new file is empty
     *         and writable and sits in dir
     *         | this(name,type,0,true)
     */
    public File(String name, Type type, Directory dir) {
        this(dir, name,type,0,true);
    }
   
    /**
     * Initialize a new file with given parent directory, name,
     * size and writability.
     *
     * @param  	parent
     *         	The parent directory of the new file.       
     * @param  	name
     *         	The name of the new file.
     * @param  	size
     *         	The size of the new file.
     * @param  	writable
     *         	The writability of the new file.
     * @param  	type
     *         	The type of the new file. 
     * 
     * @pre		type is effective
     * 			|type != null
     * @effect 	The new file is a disk item with the given
     *         	parent, name and writability.
     *         	| super(parent,name,writable)
     * @effect 	The new file has the given size
     *         	| setSize(size)
     * @post   	The type of this new file is set to the given type.
     *         	|new.getType() == type        
     */
    public File(Directory parent, String name, Type type, int size, boolean writable)
    		throws IllegalArgumentException, ItemNotWritableException {
    	super(parent,name,writable);
    	setSize(size);
    	this.type=type;
    }

    /**
     * Initialize a new writable, empty file with given parent directory
     * and name.
     *
     * @param  parent
     *         The parent directory of the new file.
     * @param  name
     *         The name of the new file.
     * @param  type
     *         The type of the new file.        
     * 
     * @effect This new file is initialized with the given name
     *         and the given parent directory, type, 
     *         the new file is empty and writable.
     *         | this(parent,name,type,0,true)
     */
    public File(Directory parent, String name, Type type)
    		throws IllegalArgumentException, ItemNotWritableException {
    	this(parent,name,type,0,true);
    }    
    
   /**
	* Return a textual representation of this file.
	* 
	* @return  The name of this file followed by a dot
	*          followed by the extension representing the
	*          type of this file.
	*          | result.equals(getName()+"."+getType().getExtension())
	*/    
    public String toString(){
    	  return getName()+"."+getType().getExtension();
    }
    
	
    /**********************************************************
	 * delete/termination
	 **********************************************************/
    
    
    /**
	 * Check whether this disk item can be terminated.
	 * 
	 * @return	True if the disk item is not yet terminated, is writable and it is either a root or
	 * 			its parent directory is writable
	 * 			| result == !isTerminated() && isWritable() && (isRoot() || getParentDirectory().isWritable())
	 * @note	This specification can now be closed
	 */
    @Override
    public boolean canBeTerminated(){
    	// no additional implementation required
		return super.canBeTerminated();
	}
    
    
    /**********************************************************
     * type
     **********************************************************/
    
    /**
	 * Variable referencing the type of this file.					
	 */
    private final Type type;
    
    /**
     * Return whether the given type is a valid type for a file.
     *
     * @param  type
     *         The type to check.
     * @return True if and only if the given type is effective.
     *         | result == (type != null)
     */
    @Raw
    public static boolean isValidType(Type type){
    	  return type != null;
    }
    
    /**
     * Return the type of this file.
     */ 
    @Raw @Basic @Immutable
    public Type getType(){
    	  return type;
    }

    
    
    
    /**********************************************************
     * size - nominal programming
     **********************************************************/
    
    /**
     * Variable registering the size of this file (in bytes).
     */
    private int size = 0;
    
    /**
     * Variable registering the maximum size of any file (in bytes).
     */
    private static final int maximumSize = Integer.MAX_VALUE;


    /**
     * Return the size of this file (in bytes).
     */
    @Raw @Basic 
    public int getSize() {
        return size;
    }
    
    /**
     * Set the size of this file to the given size.
     *
     * @param  size
     *         The new size for this file.
     * @pre    The given size must be legal.
     *         | isValidSize(size)
     * @post   The given size is registered as the size of this file.
     *         | new.getSize() == size
     */
    @Raw @Model 
    private void setSize(int size) {
        this.size = size;
    }
   
    /**
     * Return the maximum file size.
     */
    @Basic @Immutable
    public static int getMaximumSize() {
        return maximumSize;
    }

    /**
     * Check whether the given size is a valid size for a file.
     *
     * @param  size
     *         The size to check.
     * @return True if and only if the given size is positive and does not
     *         exceed the maximum size.
     *         | result == ((size >= 0) && (size <= getMaximumSize()))
     */
    public static boolean isValidSize(int size) {
        return ((size >= 0) && (size <= getMaximumSize()));
    }

    /**
     * Increases the size of this file with the given delta.
     *
     * @param   delta
     *          The amount of bytes by which the size of this file
     *          must be increased.
     * @pre     The given delta must be strictly positive.
     *          | delta > 0
     * @effect  The size of this file is increased with the given delta.
     *          | changeSize(delta)
     */
    public void enlarge(int delta) throws ItemNotWritableException {
        changeSize(delta);
    }

    /**
     * Decreases the size of this file with the given delta.
     *
     * @param   delta
     *          The amount of bytes by which the size of this file
     *          must be decreased.
     * @pre     The given delta must be strictly positive.
     *          | delta > 0
     * @effect  The size of this file is decreased with the given delta.
     *          | changeSize(-delta)
     */
    public void shorten(int delta) throws ItemNotWritableException {
        changeSize(-delta);
    }

    /**
     * Change the size of this file with the given delta.
     *
     * @param  delta
     *         The amount of bytes by which the size of this file
     *         must be increased or decreased.
     * @pre    The given delta must not be 0
     *         | delta != 0
     * @effect The size of this file is adapted with the given delta.
     *         | setSize(getSize()+delta)
     * @effect The modification time is updated.
     *         | setModificationTime()
     * @throws FileNotWritableException(this)
     *         This file is not writable.
     *         | ! isWritable()
     */
    @Model 
    private void changeSize(int delta) throws ItemNotWritableException{
        if (isWritable()) {
            setSize(getSize()+delta);
            setModificationTime();            
        }else{
        	throw new ItemNotWritableException(this);
        }
    }
    
    /**
     * Get the total disk usage of this file
     * 
     * @return	The size of the file
     * 			| getSize()
     */
    
    public int getTotalDiskUsage() {
    	return getSize();
    }
    
	/**
	 * Returns a full path of the item
	 * 
	 * @return	a string build by the names of all super directories and subdirectories, seperated by slash and ending with the type extension
	 * 			| getParentDirectory().getAbsolutePath().concat("/".concat(this.getName())).concat(".").concat(getType().getExtension())
	 * 			
	 */
    @Override
	public String getAbsolutePath() {
		return  getParentDirectory().getAbsolutePath().concat("/".concat(this.getName())).concat(".").concat(getType().getExtension());
	}
    
}