package at.yawk.snap;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardSaver {
    private ClipboardSaver() {
    }
    
    public static void saveToClipboard(String url) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), new ClipboardOwner() {
            @Override
            public void lostOwnership(Clipboard arg0, Transferable arg1) {
            }
        });
    }
}
