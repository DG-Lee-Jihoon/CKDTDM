/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server5;

public class ProcessData {

    String vt, bks, hang, mau, gio, act;

    public ProcessData(String recvMess) {
        int i;
        String temp = recvMess;

        try {
            i = temp.indexOf("|");
        } catch (Exception ex) {
            i = 0;
        }
        vt = temp.substring(0, i);
        i += 1;
        temp = temp.substring(i);

        try {
            i = temp.indexOf("|");
        } catch (Exception ex) {
            i = 0;
        }
        bks = temp.substring(0, i);
        i += 1;
        temp = temp.substring(i);

        try {
            i = temp.indexOf("|");
        } catch (Exception ex) {
            i = 0;
        }
        hang = temp.substring(0, i);
        i += 1;
        temp = temp.substring(i);

        try {
            i = temp.indexOf("|");
        } catch (Exception ex) {
            i = 0;
        }
        mau = temp.substring(0, i);
        i += 1;
        temp = temp.substring(i);

        try {
            i = temp.indexOf("|");
        } catch (Exception ex) {
            i = 0;
        }
        gio = temp.substring(0, i);
        i += 1;
        temp = temp.substring(i);

        act = temp;

    }

    public String getPos() {
        return vt;
    }

    public String getNum() {
        return bks.toUpperCase();
    }

    public String getType() {
        return hang.toUpperCase();
    }

    public String getColor() {
        return mau.toUpperCase();
    }

    public String getTime() {
        return gio.toUpperCase();
    }

    public String getAct() {
        return act.toUpperCase();
    }
}