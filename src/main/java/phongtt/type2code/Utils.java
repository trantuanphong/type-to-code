package phongtt.type2code;


import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

/**
 *
 * @author Phong
 */
public class Utils {
    
    public static String normalize(String s) {
        return s.replace("\r\n", "\n")
                .replace("\t", "    ");
    }

    public static void disableCopyPaste(JTextArea area) {

        InputMap im = area.getInputMap();
        ActionMap am = area.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "none");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), "none");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), "none");

        am.put("none", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {}
        });

        area.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return false;
            }
        });

        area.setComponentPopupMenu(null);
    }
}
