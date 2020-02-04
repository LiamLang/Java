import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Bifurcation  
{    
    static int    xDim   = 1900;
    static int    yDim   = 900;
    
    static double minR = 2.99d;
    static double maxR = 4d;
    
    static double rStep = (maxR - minR)/ xDim;
    
    static double r  = minR;
    static double xn = 0.5d;
        
    static int[] hits;  
    static int precision = 1000;
    
    static boolean[] buffer;
    static int bufferSize = 1000;
    static int bufferPos;
    static boolean bufferFull = false;
    
    static double xScale = maxR - minR;
    static double yScale = 1;
    
    static BufferedImage image = new BufferedImage(xDim, yDim, BufferedImage.TYPE_INT_ARGB);
    static JFrame frame = new JFrame();
    
    static void clearImage()
    {
        for(int i = 0; i < xDim; i++)
        {
            for(int j = 0; j < yDim; j++)
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
    
    static void drawPoint(double x, double y)
    {   
        int imageX = (int) (xDim / xScale * (x - minR));
        int imageY = yDim - (int) (yDim / yScale * y) - 1;
                        
        image.setRGB(imageX, imageY, 0xFF000000);
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
    
    static void clearBuffer()
    {
        for(int i = 0; i < bufferSize; i++)
        {
            buffer[i] = false;
        }
    }
    
    static boolean hit()
    {
        int p = (int) (precision * xn);
             
        hits[p]++;
        
        return (hits[p] > 1);
    }
    
    static void map()
    {
        double xn_1 = r * xn * (1d - xn);
        xn = xn_1;
    }
    
    static double[] converge()
    {
        clearHits();
        clearBuffer();
        bufferPos = 0;
        bufferFull = false;
        
        xn = 0.5d;
        
        while (!hasConverged())
        {              
            map();
            
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
            map();
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
                results[resultNum] = ((double) i) / precision;
                resultNum++;
            }
        }
        
        return results;
    }
    
    static void plotForR()
    {
        double[] convergences = converge();
        
        for (double c : convergences)
        {
            drawPoint(r, c);
        }
    }
    
    public static void main(String[] args) throws InterruptedException 
    {                
        setupFrame();
        
        hits = new int[precision];
        buffer = new boolean[bufferSize];
        
        while (r < maxR)
        {
            plotForR();
            r += rStep;
            
            frame.repaint();
        }
    }
}