import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

class Complex
{
    double r;
    double i;
    
    public Complex(double _r, double _i)
    {
        this.r = _r;
        this.i = _i;
    }
    
    public Complex square()
    {
        return new Complex(Math.pow(r, 2d) - Math.pow(i, 2d), 2d * r * i);
    }
    
    public void add(Complex other)
    {
        r += other.r;
        i += other.i;
    }
    
    public double abs()
    {
        return Math.pow(Math.pow(r, 2d) + Math.pow(i, 2d), 1d / 2d);
    }
}

public class MandelbrotBifurcation  
{    
    static int    dim    = 800;
    static double scale  = 2;
    static double center = (double) dim / 2d;
    static double resolution = scale * 2d / ((double) dim);
    
    static int iterations = 500;
    
    static double x;
    static double xn;
    
    static int[] hits;  
    static int precision = 2000;
    
    static boolean[] buffer;
    static int bufferSize = 1000;
    static int bufferPos;
    static boolean bufferFull = false;
    
    static int p    = 0;
    static int pMax = 300000;
        
    static double[] ax = new double[pMax];
    static double[] ay = new double[pMax];    
    static double[] az = new double[pMax];   
    
    static double[][] local = {{1d, 0d, 0d}, {0d, 1d, 0d}, {0d, 0d, 1d}};
    static double[][] world = {{1d, 0d, 0d}, {0d, 1d, 0d}, {0d, 0d, 1d}};
    
    static BufferedImage image = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
    static JFrame frame = new JFrame();
    
    static void clearImage()
    {
        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {                
                image.setRGB(i, j, 0xFFFFFFFF);
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
        //double[] x = { 1d, -0.01d, -0.01d };
        //double[] z = { 0.01d, -0.01d, 1d };
        //double[] y = { 0.01d, 1d, 0.01d };
        double[] y = { 0d, 1d, 0.025d };
        double[] z = { 0d, -0.025d, 1d };
        //x = normalize(x);
        y = normalize(y);
        z = normalize(z);        
        //double[][] m = {x, y, z};
        double[][] m = { cross(y, z), y, z };
        return m;
    }
    
    static void rotateWorld()
    {
        double[][] transform = transformationMatrix(rotationMatrix(), local);
        double[][] rotated = { transform(transform, world[0]), transform(transform, world[1]), transform(transform, world[2]) };
        world = rotated;
    }
    
    static void drawPoint(double x, double y)
    {   
        int imageX = (int) (center + (center / scale * x));
        int imageY = (int) (center + (center / scale * y));
                        
        if (imageX < 0)
        {
            imageX = 0;
        }
        else if (imageX >= dim)
        {
            imageX = dim;
        }
        
        if (imageY < 0)
        {
            imageY = 0;
        }
        else if (imageY >= dim)
        {
            imageY = dim;
        }
        
        image.setRGB(imageX, imageY, 0xFF000000);
    }
    
    static void paint()
    {        
        double[][] transform  = transformationMatrix(world, local);

        for (int i = 0; i < p; i++)
        {
            double[] point = {ax[i], ay[i], az[i]};
            double[] transformed = transform(transform, point);
            
            drawPoint(transformed[0], transformed[1]);
        }
        
        frame.repaint();
    }
    
    static boolean hasConverged()
    {
        if (!bufferFull)
        {
            return false;
        }
        
        for (boolean b : buffer)
        {
            if (!b)
            {
                return false;
            }
        }
        
        return true;
    }
    
    static void clearHits()
    {
        for(int i = 0; i < precision; i++)
        {
            hits[i] = 0;
        }
    }
        
    static boolean hit()
    {
        int p = (int) (precision * xn / 4d) + (precision / 2);
        
        if (p < 0)
        {
            p = 0;
        }
        else if (p >= precision)
        {
            p = precision - 1;
        }
        
        hits[p]++;
        
        return (hits[p] > 1);
    }
    
    static void iterate()
    {
        xn = Math.pow(xn, 2d) + x;
    }
    
    static double[] converge()
    {
        clearHits();
        buffer = new boolean[bufferSize];
        bufferPos = 0;
        bufferFull = false;
        
        xn = x;
        
        while (!hasConverged())
        {              
            iterate();
            
            buffer[bufferPos] = hit();
            bufferPos++;
            
            if (bufferPos == bufferSize)
            {
                bufferPos = 0;
                bufferFull = true;
            }
        }
        
        clearHits();
        
        for (int i = 0; i < bufferSize; i++)
        {
            iterate();
            hit();
        }
        
        int numResults = 0;
        for (int i : hits)
        {
            if (i > 0)
            {
                numResults++;
            }
        }
        
        double[] results = new double[numResults];
        
        int resultNum = 0;
        
        for (int i = 0; i < precision; i++)
        {
            if (hits[i] > 0)
            {                
                results[resultNum] = ((double) i - ((double) precision / 2d)) / (double) precision * 4d;
                resultNum++;;
            }
        }
        
        return results;
    }
    
    static boolean isInSet(Complex c)
    {
        Complex p = c;
        
        for (int i = 0; i < iterations; i++)
        {
            p = p.square();
            p.add(c);
            
            if (p.abs() > 2d)
            {
                return false;
            }          
        }
        
        return true;
    }
        
    static void populateBifurcationPoints()
    {
        x = -2d;
                
        while (x < 2d)
        {
            if (isInSet(new Complex(x, 0)))
            {
                double[] convergences = converge();
        
                for (double c : convergences)
                {
                    ax[p] = x;
                    az[p] = c;
                
                    p++;
                }
            }
            
            x += resolution;
        }
    }
    
    static void populateSetPoints()
    {
        double x = -2d;
        double y = -2d;
                
        while (x < 2d)
        {
            y = -2d;
            
            while (y < 2d)
            {
                if (isInSet(new Complex(x, y)))
                {
                    ax[p] = x;
                    ay[p] = y;
                    
                    p++;
                }
                
                y += resolution;
            }
            x += resolution;
        }
    }
    
    public static void main(String[] args) throws InterruptedException 
    {                
        setupFrame();
        
        hits = new int[precision];
        
        populateSetPoints();
        populateBifurcationPoints();
        
        System.out.println("p = " + Integer.toString(p));
        
        while(true)
        {
            rotateWorld();
            clearImage();
            paint();
            
            Thread.sleep(5);
        }
    }
}