package src.ro.uvt.fi.dp;

import java.io.Serializable;
import java.util.Stack;

// Command pattern: being able to undo the last operation
public class TransactionHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Stack<Command> history = new Stack<>();

    public void execute(Command cmd) throws Exception {
        cmd.execute();
        history.push(cmd);
    }

    public void undoLast() throws Exception {
        if (!history.isEmpty()) {
            Command cmd = history.pop();
            Logger.getInstance().log("Undoing last operation...");
            cmd.undo();
        } else {
            Logger.getInstance().log("Nothing to undo.");
        }
    }

    public boolean hasHistory() {
        return !history.isEmpty();
    }
}
