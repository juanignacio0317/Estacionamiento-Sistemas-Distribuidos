
package estacionamiento.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class IconoAuto implements Icon {

    @Override
    public int getIconWidth() {
        return 32;
    }

    @Override
    public int getIconHeight() {
        return 32;
    }

    @Override
    public void paintIcon(
            Component componente,
            Graphics g,
            int x,
            int y
    ) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Carrocería roja.
        g2.setColor(new Color(210, 35, 35));

        g2.fill(new RoundRectangle2D.Double(
                x + 5, y + 9, 22, 16, 7, 7
        ));

        // Parte superior del auto.
        g2.fill(new RoundRectangle2D.Double(
                x + 9, y + 5, 14, 13, 5, 5
        ));

        // Ventanas.
        g2.setColor(new Color(190, 225, 240));

        g2.fill(new RoundRectangle2D.Double(
                x + 11, y + 7, 10, 8, 3, 3
        ));

        // Ruedas.
        g2.setColor(Color.DARK_GRAY);

        g2.fill(new Ellipse2D.Double(
                x + 4, y + 11, 5, 8
        ));

        g2.fill(new Ellipse2D.Double(
                x + 23, y + 11, 5, 8
        ));

        g2.fill(new Ellipse2D.Double(
                x + 4, y + 20, 5, 8
        ));

        g2.fill(new Ellipse2D.Double(
                x + 23, y + 20, 5, 8
        ));

        g2.dispose();
    }
}
