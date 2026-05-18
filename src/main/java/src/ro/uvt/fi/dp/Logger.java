package src.ro.uvt.fi.dp;

import java.io.Serializable;

// Singleton logger
class Logger implements Serializable {
    private static final long serialVersionUID = 1L;
    private static transient Logger instance;

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) instance = new Logger();
        return instance;
    }

    public void log(String message) {
        System.out.println("LOG: " + message);
    }
}
