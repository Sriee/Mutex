package com.aos.log;

import java.util.Collection;

public interface Logger {
    public void writeLog(Collection<String> entry);
    public void writeLog(String entry);
    public void writeEntry(String entry);
    public void writeClock(String entry);
}
