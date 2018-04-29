package filesystem;

public class DirectoryLink extends Link {
	
	/**
	 * Initialize a new directory link
	 * @param	parent
	 * 			The parent directory
	 * @param	name
	 * 			The name of the directory link
	 * @param 	directory
	 * 			The directory to which we are linking
	 * @throws 	IllegalArgumentException
	 * 			Directory has been terminated
	 * 			| directory.isTerminated()
	 */

	public DirectoryLink(Directory parent,String name, Directory directory) throws IllegalArgumentException {
		super(parent, name, directory);
	}
	
	/**
	 * Check whether the given name is a legal name for a link to a directory.
	 * 
	 * @param  	name
	 *			The name to be checked
	 * @return	True if the given string is effective, not
	 * 			empty and consisting only of letters, digits, dots,
	 * 			hyphens and underscores; false otherwise.
	 * 			| result ==
	 * 			|	(name != null) && name.matches("[a-zA-Z_0-9-]+")
	 * 
	 * @note	Links to directories cannot contain '.'s in their name.
	 */
	@Override
	public boolean isValidName(String name) {
		return (name != null && name.matches("[a-zA-Z_0-9-]+"));
	}
	

}
