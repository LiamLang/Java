import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Attractor2  
{    
    static double x;
    static double y;
    static double z;
    
    static double rho   = 28d;
    static double sigma = 10d;
    static double beta  = 8d/3d;
    
    static double dt = 0.00025d;
    
    static int p    = 0;
    static int pMax = 60000;
        
    static int stepsPerPaint = 250;
    
    static boolean bufferFull = false;
    
    static double[] ax = new double[pMax];
    static double[] ay = new double[pMax];    
    static double[] az = new double[pMax];   
    
    static double[][] local = {{1d, 0d, 0d}, {0d, 1d, 0d}, {0d, 0d, 1d}};
    static double[][] world = {{1d, 0d, 0d}, {0d, 0d, -1d}, {0d, 1d, 0d}};
    
    static int    dim    = 800;
    static double scale  = 60;
    static double center = (double) dim / 2d;
    
    static BufferedImage image = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
    static JFrame frame = new JFrame();
    
    static void clearImage()
    {
        for(int i = 0; i < dim; i++) {
            for(int j = 0; j < dim; j++) {                
                image.setRGB(i, j, 0xFF000000);
            }
        }   
    }

    static void setupFrame()
    {
        clearImage();
        
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();     
  	frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    static double[] normalize(double[] u)
    {
        double x = Math.pow(Math.pow(u[0], 2d) + Math.pow(u[1], 2d) + Math.pow(u[2], 2d), 1d / 2d);
        double[] v = {u[0] / x, u[1] / x, u[2] / x};   
        return v;
    }
    
    static double dot(double[] u, double[] v)
    {
        return u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
    }
    
    static double[] cross(double[] u, double[] v)
    {
        double[] w =
        {
            u[1] * v[2] - u[2] * v[1],
            u[2] * v[0] - u[0] * v[2],
            u[0] * v[1] - u[1] * v[0]
        };       
        
        return normalize(w);
    }
    
    static double[][] transformationMatrix(double[][]m, double[][]n)
    {
        double[][] o =
        {{
            dot(m[0], n[0]),
            dot(m[0], n[1]),
            dot(m[0], n[2])
        }, {
            dot(m[1], n[0]),
            dot(m[1], n[1]),
            dot(m[1], n[2])
        }, {
            dot(m[2], n[0]),
            dot(m[2], n[1]),
            dot(m[2], n[2])
        }}; 
        
        return o;
    }
    
    static double[] transform(double[][] transformation, double[] vector)
    {
        double[] transformed = 
        {
            vector[0] * transformation[0][0] + vector[1] * transformation[0][1] + vector[2] * transformation[0][2],
            vector[0] * transformation[1][0] + vector[1] * transformation[1][1] + vector[2] * transformation[1][2],
            vector[0] * transformation[2][0] + vector[1] * transformation[2][1] + vector[2] * transformation[2][2]
        };
        
        return transformed;
    }
    
    static double[][] rotationMatrix()
    {
        double[] x = {1d, -0.03d, 0d};
        double[] y = {0.03d, 1d, 0d};
        x = normalize(x);
        y = normalize(y);        
        double[][] m = {x, y, cross(x, y)};      
        return m;
    }
    
    static void rotateWorld()
    {
        double[][] transform = transformationMatrix(rotationMatrix(), local);
        double[][] rotated = { transform(transform, world[0]), transform(transform, world[1]), transform(transform, world[2]) };
        world = rotated;
    }
    
    static void paint()
    {        
        double[][] transform  = transformationMatrix(world, local);
                
        for (int i = 0; i < (bufferFull ? pMax : p); i++)
        {
            double[] point = {ax[i], ay[i], az[i]};
            double[] transformed = transform(transform, point);
            
            int age = p > i ? p - i : p + pMax - i;
            
            drawPoint(transformed[0], transformed[1], age);
        }
    }
        
    static void drawPoint(double x, double y, int age)
    {
        int imageX = (int) (center + (center / scale * x));
        int imageY = (int) (center + (center / scale * y));
        
        int brightness = 255 - (int) (255d * age / pMax);
        
        image.setRGB(imageX, imageY, 0xFF000000 + 0x100 * brightness);
    }
            
    static void attractorStep()
    {
        x += sigma * (y - x) * dt;
        y += (x * (rho - z) - y ) * dt;
        z += ((x * y) - (beta * z)) * dt;
        
        ax[p] = x;
        ay[p] = y;
        az[p] = z;
        
        p++;
    }
    
    public static void main(String[] args) throws InterruptedException 
    {                
        setupFrame();
        
        Random random = new Random();
        x = random.nextDouble();
        y = random.nextDouble();
        z = random.nextDouble();
        
        while(true)
        {
            if (p <= pMax - stepsPerPaint)
            {
                for (int i = 0; i < stepsPerPaint; i++)
                {
                    attractorStep();
                }
            } else
            {
                bufferFull = true;
                p = 0;
            }
            
            rotateWorld();
            clearImage();           
            paint();
            frame.repaint();
        }
    }
}