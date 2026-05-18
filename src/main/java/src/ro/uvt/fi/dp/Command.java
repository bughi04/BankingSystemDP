// Interface for the command pattern
package src.ro.uvt.fi.dp;

public interface Command {
    void execute() throws Exception;
    void undo() throws Exception;
}