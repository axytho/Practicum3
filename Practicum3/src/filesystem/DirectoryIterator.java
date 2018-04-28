package filesystem;

/**
 *  A directory-iterator is an iterator specifically aimed at
 *  returning items belonging to a directory.
 * 
 * 	A non-empty directory-iterator has a current item, which is
 * 	returned by the inspector getCurrentItem(). This inspector
 *	keeps on returning the same element, until the directory-iterator
 * 	is advanced or reset.
 * 
 * 	Upon creation, a directory-iterator will be initialized with
 * 	all the elements in its directory. If the contents of that
 * 	directory is not empty, its first element will become the
 * 	current element for the iterator.
 * 
 * @author 	Tommy Messelis
 * @version	2.0 - 2015
 */

public interface DirectoryIterator {

	/**
	 * Return the number of remaining disk items to be
	 * returned by this directory-iterator, including
	 * the current item.
	 * 
	 * @return	The resulting number cannot be negative.
	 * 			| result >= 0 
	 */
	int getNbRemainingItems();
	
	/**
	 * Return the current disk item of this directory-iterator.
	 * 
	 * @return	The current item
	 * @throws	IndexOutOfBoundsException
	 * 			This directory-iterator has no current item.
	 * 			| getNbRemainingItems() == 0
	 */
	DiskItem getCurrentItem() throws IndexOutOfBoundsException;
	
	/**
	 * Advance the current item of this directory-iterator to the
	 * next disk item. 
	 * 
	 * @pre		This directory-iterator must still have some remaining items.
	 * 			| getNbRemainingItems() > 0
	 * @post	The number of remaining disk items is decremented
	 * 			by 1.
	 * 			| new.getNbRemainingItems() == getNbRemainingItems() - 1
	 */
	void advance();
	
	/**
	 * Reset this directory-iterator to its first item.
	 */
	void reset();

}
