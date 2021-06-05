
package com.eversec.database.sdb.util.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskFilefilter implements FileFilter {

    // @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return false;
        }
        Pattern p = Pattern.compile(".*.csv$");
        Matcher m = p.matcher(file.getName());
        return m.matches();
    }

}
