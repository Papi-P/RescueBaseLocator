/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rescue.base.locator;

import java.util.concurrent.Executors;
import javax.swing.JOptionPane;

/**
 *
 * @author Daniel Allen
 */
public class InformationWindow{
    private String msg;
    private int msgType;
    private String title;
    public InformationWindow(String title, String message, int messageType) {
        this.msg = message;
        this.msgType = messageType;
        this.title = title;

    }
    public JOptionPane show(){
        JOptionPane jop = new JOptionPane();
        jop.setMessage(msg);
        jop.setMessageType(msgType);
        jop.createDialog(title).setVisible(true);
        return jop;
    }
}
