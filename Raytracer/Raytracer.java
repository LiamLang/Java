import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Raytracer
{
    static int xPixels = 1900;
    static int yPixels = 1080;
    
    static double eyeZ = 0d;
    
    static double[][] spheres;
    
    static BufferedImage image = new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB);
    static JFrame frame = new JFrame();
    
    static void populateSpheres()
    {
        // x, y, z, radius, RGB
        
        spheres = new double[][]
        {
            new double[] { 0d, -5d, -60d, 5d, 0x00FF00 },
            new double[] { 0d, 3d, -50d, 2d, 0x0000FF },
            new double[] { 5d, 5d, -110d, 8d, 0xFF0000 },
            new double[] { -2.5d, 1d, -55d, 1d, 0xFFFF00 },
            new double[] { 2d, 0.8d, -50d, 0.6d, 0xFF00FF },          
            new double[] { 1.2d, 0.4d, -49d, 0.4d, 0x00FFFF },
            new double[] { 2d, -2d, -50d, -1.2d, 0xFFFFFF },
        /*
            new double[] { -5d, -4d, -70d, 5d, 0xFF0000 },
            new double[] { 5d, -4d, -70d, 5d, 0x00FF00 },
            new double[] { 0d, 10d * Math.sin(Math.PI / 3) - 4d, -70d, 5d, 0x0000FF },
            new double[] { 10d, 10d * Math.sin(Math.PI / 3) - 4d, -70d, 5d, 0xFFFF00 },
            new double[] { 15d, -4d, -70d, 5d, 0xFF00FF },
            new double[] { -15d, -4d, -70d, 5d, 0x00FFFF },
            new double[] { -10d, 10d * Math.sin(Math.PI / 3) - 4d, -70d, 5d, 0xFFFFFF },
        */
        };
    }
    
    static void clearImage()
    {
        for(int i = 0; i < xPixels; i++)
        {
            for(int j = 0; j < yPixels; j++)
            {                
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
        frame.repaint();
    }
    
    static double dot(double[] u, double[] v)
    {
        return u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
    }
        
    static double[] normalize(double[] u)
    {
        double x = Math.pow(u[0] * u[0] + u[1] * u[1] + u[2] * u[2], 1d / 2d);
        return new double[] {u[0] / x, u[1] / x, u[2] / x};
    }
       
    static double[] getPrimaryRay(int xPixel, int yPixel)
    {
        double canvasX = - 1d + (2d / (double) xPixels * xPixel);
        double canvasY = ((double) yPixels / (double) xPixels) - (2d / (double) xPixels * yPixel);
        double canvasZ = eyeZ - 5d;
        
        return normalize(new double[] { canvasX, canvasY, canvasZ });
    }
    
    // intersects ? 1 : -1, distance to intersection, intersection point x, y, z, reflected ray x, y, z
    static double[] intersects(double[] origin, double[] ray, double[] sphere)
    {
        double[] res = intersects(ray, new double[] { sphere[0] - origin[0], sphere[1] - origin[1], sphere[2] - origin[2], sphere[3] });
        
        return res[0] < 0 ? new double[] { -1d } : new double[] { res[0], res[1], res[2] + origin[0], res[3] + origin[1], res[4] + origin[2], res[5], res[6], res[7] };
    }
    
    // intersects ? 1 : -1, distance to intersection, intersection point x, y, z, reflected ray x, y, z
    static double[] intersects(double[] ray, double[] sphere)
    {      
        double x1 = ray[0];
        double y1 = ray[1];
        double z1 = ray[2];
        
        double xc = sphere[0];
        double yc = sphere[1];
        double zc = sphere[2];
        
        double r = sphere[3];
        
        double a = x1 * x1 + y1 * y1 + z1 * z1;        
        double b = - 2d * (x1 * xc + y1 * yc + z1 * zc);        
        double c = xc * xc + yc * yc + zc * zc - r * r;
        
        double discrim = b * b - 4d * a * c;
        
        if (discrim <= 0) 
        {
            return new double[] { -1d };
        }
        
        double t = (- b - Math.pow(discrim, 1d / 2d)) / (2d * a);

        if (t < 0.001) 
        {
            return new double[] { -1d };
        }
        
        double xi = x1 * t;
        double yi = y1 * t;
        double zi = z1 * t;
        
        double[] normal = normalize(new double[] { xi - xc, yi - yc, zi - zc });
        
        double dotProd = dot(ray, normal);
        
        double xr = x1  - 2d * dotProd * normal[0];
        double yr = y1  - 2d * dotProd * normal[1];
        double zr = z1  - 2d * dotProd * normal[2];
        
        double[] reflected = normalize(new double[] { xr, yr, zr });
        
        return new double[] { 1, t, xi, yi, zi, reflected[0], reflected[1], reflected[2] };
    }
    
    // x, y, z, last sphere index
    static double[] trace(double[] origin, double[] ray, double sphereIndex, int depth)
    {   
        boolean intersected = false;
        double distance = Double.MAX_VALUE;        
        double[] newOrigin = { 0d, 0d, 0d };        
        double[] shadowRay = ray;       
        
        for (int i = 0; i < spheres.length; i++)
        {
            double[] intersection = intersects(origin, ray, spheres[i]);
            
            if (intersection[0] > 0)
            {
                intersected = true;
                
                if (intersection[1] < distance)
                {
                    distance = intersection[1];
                    newOrigin = new double[] { intersection[2], intersection[3], intersection[4] };
                    shadowRay = new double[] { intersection[5], intersection[6], intersection[7] };
                    sphereIndex = (double) i + 0.0001d;
                }
            }
        }        
        
        if (!intersected || depth == 100)
        {
            return new double[] { shadowRay[0], shadowRay[1], shadowRay[2], sphereIndex };
        }
        

        return trace(newOrigin, shadowRay, sphereIndex, depth + 1);
    }
    
    static int color(double[] tracedRay)
    {
        if (tracedRay[3] < 0d)
        {
            return 0;
        }
        
        int color = (int) spheres[(int) tracedRay[3]][4];

        if (dot(tracedRay, new double[] { 1d, 1d, 1d, }) <= 0)
        {        
            int r = color / 0x10000;
            int g = (color / 0x100) % 256;
            int b = color % 256;
            
            r /= 15;
            g /= 15;
            b /= 15;
            
            return r * 0x10000 + g * 0x100 + b;
        }
        
        return color;
    }
    
    static void draw()
    {        
        double[] origin = { 0d, 0d, eyeZ };
        
        for (int y = 0; y < yPixels; y++)
        {                
            for (int x = 0; x < xPixels; x++)
            {            
                double[] primaryRay = getPrimaryRay(x, y);
                
                double[] tracedRay = trace(origin, primaryRay, -1d, 0);
                
                int color = color(tracedRay);
                
                image.setRGB(x, y, 0xFF000000 + color);
            }
            
            frame.repaint();
        }
    }
    
    public static void main(String[] args)
    {        
        populateSpheres();
        
        setupFrame();
 
        while(true)
        {
            draw();
                        
            eyeZ -= 0.1d;
        }        
    }
}