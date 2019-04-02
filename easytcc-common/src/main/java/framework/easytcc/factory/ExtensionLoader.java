package framework.easytcc.factory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public abstract class ExtensionLoader {

	public static <T>Set<T> getExtensions(Class<T> superClass) {
		Set<T> set = new HashSet<T>();
		Iterator<T> iterator = ServiceLoader.load(superClass).iterator();
    	while(iterator.hasNext()) {
    		set.add(iterator.next());
    	}
    	return set;
	}
	/**
	 * return if implementer only 1 
	 * @param superClass
	 * @return
	 * @throws IllegalStateException	if implementer not equal to 1
	 */
	public static <T>T getExtension(Class<T> superClass) throws IllegalStateException{
    	T extension = getExtension(superClass, null);
    	if(extension == null) {
    		throw new IllegalStateException(superClass + " implementer not found");
    	}
    	return extension;
	}
	/**
	 * return if implementer only 1 
	 * @param superClass
	 * @param defaultImpl
	 * @return
	 * @throws IllegalStateException	if implementer more than 1
	 */
	public static <T>T getExtension(Class<T> superClass,T defaultImpl) throws IllegalStateException{
		Set<T> extensions = getExtensions(superClass);
		if(extensions.isEmpty()) {
			return defaultImpl;
		}
		if(extensions.size() > 1) {
			throw new IllegalStateException(superClass + " implementer has more than 1,only 1 can be used");
		}
		return extensions.iterator().next();
	}
	
}
