package test_utils;

import java.io.File;
import java.util.UUID;

/**
 * Created by Clara on 8/2/17.
 * Collection of utility methods for working with files
 */

public class FileUtils {
    
    public static void deleteFile(String filename) {
        
        /* Delete a file from the file system.  */
        
        File file = new File(filename);
        
        try {
            boolean deleted = file.delete();       // Ignoring delete result
            if (!deleted) {
                // Delete temp file
                System.out.println("Tried to delete a temporary file used for testing, but was not able to. "+
                        "\nPlease delete the file " + filename);
                
            }
        } catch (Exception e) {
    
            System.out.println("Tried to delete the temp file but an error occurred: " + e.getMessage() +
                    "\nPlease delete the file " + filename);
    
        }
    }

    public static String uniqueFilename(String prefix) {
        
        String tempMsg = "temporary_file_for_testing_please_delete";
        
        String tempPrefix = (prefix == null) ? tempMsg : tempMsg + "_" + prefix;
        
        return tempPrefix + "_" + UUID.randomUUID().toString() + ".data";
    }
    
    
    
}
