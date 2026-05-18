package src.ro.uvt.fi.dp;

import java.io.*;
// Handles persistent storage of the entire Bank state using Java Serialization, the bank is written to / read from a single .ser file.
public class BankFileManager {
    private BankFileManager() {}
    public static void save(Bank bank, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {
            oos.writeObject(bank);
            Logger.getInstance().log("Bank state saved to: " + filePath);
        }
    }
    public static Bank load(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {
            Bank bank = (Bank) ois.readObject();
            Logger.getInstance().log("Bank state loaded from: " + filePath);
            return bank;
        }
    }
}
