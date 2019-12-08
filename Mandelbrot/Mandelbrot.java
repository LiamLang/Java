
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.complex.Complex;

@SuppressWarnings("serial")
public class Mandelbrot extends JPanel {

    private BufferedImage canvas;

    static int w = 1000;
    static int h = 1000;
    
    static Double iterations = 1000.0;
    
    static int zoom = 10;
    
    static Double brightness = 2.5;
    static Double multiplier = 1.0;

    public Mandelbrot(int width, int height, Double pixel, Double left,
            Double top, Double iterations) {

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Double r = left + pixel * x;
                Double i = top - pixel * y;
                Complex c = new Complex(r, i);

                Double large = 0.0;

                Complex z = new Complex(0.0, 0.0);

                for (int k = 1; k <= iterations; k++) {
                    z = z.pow(2.0);
                    if (z.isNaN()) {
                        z = new Complex(0.0, 0.0);
                    }
                    z = z.add(c);
                    Double q = z.abs();
                    if (q >= 2.0) {
                        large = k + 10 - Math.log(q);
                        break;
                    }
                }
                if (large > 0) {
                    int l = (int) (large * brightness);
                    String hex = String.format("FF%02X%02X%02X", l / 3, l / 2, 1);
                    int h = (int) Long.parseLong(hex, 16);
                    canvas.setRGB(x, y, h);

                } else {
                    canvas.setRGB(x, y, 0xFF000000);
                }
            }
            if ((y + 1) % (h / 100) == 0) {
                System.out.println(Integer.toString((y + 1) * 100 / h) + "%");
            }
        }
        repaint();
    }

    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }

    static boolean next = false;
    static Double pixel = (double) 4 / w;
    static Double left  = -2.0;
    static Double top   = 2.0 * h / w;

    public static void main(String[] args) {

        final JFrame frame = new JFrame("Mandelbrot");

        while (true) {

            next = false;

            final Mandelbrot panel = new Mandelbrot(w, h, pixel, left, top,
                    iterations);

            frame.add(panel);
            frame.setVisible(true);
            frame.setSize(w, h);
            frame.setPreferredSize(new Dimension(w, h));
            frame.setMinimumSize(new Dimension(w, h));
            frame.pack();
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    System.out.println(e.getX() + ", " + e.getY());

                    frame.remove(panel);

                    left = left + pixel * e.getX() - w / (2 * zoom) * pixel;
                    top = top - pixel * e.getY() + h / (2 * zoom) * pixel;
                    pixel = pixel / zoom;
                    iterations = iterations * multiplier;
                    next = true;
                }
            });

            while (!next) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }

        }
    }
}
