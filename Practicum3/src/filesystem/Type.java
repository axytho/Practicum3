package filesystem;

import be.kuleuven.cs.som.annotate.*;

/**
 * 	An enumeration of file types.
 *  In its current definition, the class only distinguishes between
 *  text, pdf and java file types. With each file type, an extension is associated
 *    
 * @invar	Each type must have a properly spelled extension.
 * 			| isValidExtension(getExtension())
 * 
 * @author Jonas, Frederik
 *    
 */
@Value
public enum Type {
	
	TEXT("txt"), PDF("pdf"), JAVA("java");
	
	/**
	 * Initialize a new type with given extension.
	 * 
	 * @param extension
	 *        The extension of the new type.
	 * @post  The extension of this new type is set to the given
	 *        extension.
	 *        | new.getExtension().equals(extension)
	 */
	private Type(String extension){
		this.extension = extension;
	}
	
	/**
	 * Return the extension of this type.
	 */
	@Raw @Basic @Immutable
	public String getExtension(){
		return this.extension;
	}
	
    /**
     * Check whether the given extension is a legal extension
     * for a file type.
     *
     * @return	True if the given string is effective, if it is not
     * 			empty and if it only consists of non-capital letters;
     *           false otherwise.
     * 			| result ==
     * 			|	(extension != null) && extension.matches("[a-z]+")
     */
	public static boolean isValidExtension(String extension){
      return extension != null && extension.matches("[a-z]+");
	}
	
	/**
     * Variable registering the extension of this type.
     */
	private final String extension;
	
	
}
