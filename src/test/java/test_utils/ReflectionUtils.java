package test_utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.apache.commons.lang3.StringUtils.capitalize;


/**
 * Created by clara on 8/5/17.
 */
public class ReflectionUtils {
    
    
    public static boolean hasFieldOfType(Class c, String name, Class type) {
        try {
            Field f = c.getDeclaredField(name);  // This field must be declared in this class, not a superclass
            return f.getType().equals(type);
        } catch (NoSuchFieldException ne) {
            return false;
        }
    }
    
    
    public static boolean hasGetSet(Class c, String name, Class varType) {
        /** Check for presence of get and set methods for a variable of the given name
         * eg. if the variable is called name and is of type String, there should be a String getName() and a void setName(String n) */
    
        name = capitalize(name);   // StringUtils wooo~!  Turn name into Name or wingColor into WingColor
        
        // Check for getter
        try {
            Method m = c.getMethod("get" + name);  // Exception raised if doesn't exist, or takes any parameters
            if (! m.getReturnType().equals(varType)) { return false; }
        } catch (NoSuchMethodException ne) {
            return false;
        }
    
    
        // Check for setter
        try {
            Method m = c.getMethod("set" + name, varType);  // Exception raised if doesn't exist, or takes wrong parameters
            if (! m.getReturnType().equals(void.class)) { return false; }
        } catch (NoSuchMethodException ne) {
            return false;
        }
    
        
        return true;
    
    }
    
}
