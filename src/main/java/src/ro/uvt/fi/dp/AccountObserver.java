package src.ro.uvt.fi.dp;

// Observer Pattern – interface that the GUI implements.
// Any class interested in account balance changes registers itself here.
public interface AccountObserver {
    void onAccountUpdated(Account account);
}
