/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gdclick;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author doets
 */
public class GdClick {

    static int x,  y, cx, cy ;
    static PointerInfo a;
    static Point b;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Robot r;
        r = null;
        try {
            r = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(GdClick.class.getName()).log(Level.SEVERE, null, ex);
        }

        //            r.mousePress(InputEvent.BUTTON1_MASK);

//            r.mouseRelease(InputEvent.BUTTON1_MASK);
        a = MouseInfo.getPointerInfo();
        b = a.getLocation();

        x = (int) b.getX();
        y = (int) b.getY();




        System.out.print(y + "  ");
        System.out.println(x);
        while (true) {//r.mouseMove(x, y - 50);
            b = a.getLocation();

            cx = (int) b.getX();
            cy = (int) b.getY();


            System.out.print("moven"+ x +"  "+y);

            r.mouseMove(x, y);

            r.mousePress(InputEvent.BUTTON1_MASK);

            r.mouseRelease(InputEvent.BUTTON1_MASK);
            System.out.print("moven naar c"+ cx +"  "+cy);
            r.mouseMove(cx, cy);
            System.out.print("nu wachten op 10 sec");

            r.delay(10000);




        }

    // TODO code application logic here



    }
}
