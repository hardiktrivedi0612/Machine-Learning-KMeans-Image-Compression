
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Hardik
 */
public class KMeans {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
            return;
        }
        try {
            BufferedImage originalImage = ImageIO.read(new File(args[0]));
            int k = Integer.parseInt(args[1]);
            BufferedImage kmeansJpg = kmeans_helper(originalImage, k);
            ImageIO.write(kmeansJpg, "jpg", new File(args[2]));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
        Graphics2D g = kmeansImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w, h, null);
        // Read rgb values from the image
        int[] rgb = new int[w * h];
        int count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rgb[count++] = kmeansImage.getRGB(i, j);
            }
        }
        // Call kmeans algorithm: update the rgb values
        kmeans(rgb, k);

        // Write the new rgb values to the image
        count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                kmeansImage.setRGB(i, j, rgb[count++]);
            }
        }
        return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k) {
        Random random = new Random();
        Color rgbColor = null;
        ArrayList<Color> pixelRGB = new ArrayList<>();
        for (int rgbVal : rgb) {
            rgbColor = new Color(rgbVal);
            pixelRGB.add(rgbColor);
        }

//        ArrayList<Color> clusters = new ArrayList<>();
        Color[] clusters = new Color[k];

        for (int i = 0; i < k; i++) {
            int randomPixel = random.nextInt(rgb.length);
//            clusters.add(pixelRGB.get(randomPixel));
            clusters[i] = pixelRGB.get(randomPixel);
        }

        boolean areClustersChanged = true;

        int[] pixelCluster = new int[rgb.length];
        
        int iterations = 1;
        
        while (areClustersChanged) {

            areClustersChanged = false;
            System.out.println("Iteration no = "+iterations++);

            //Assigning each of the pixels to a particular cluster based on the clusters
            for (int i = 0; i < pixelRGB.size(); i++) {

                //selecting the cluster with the least distance from the clusters
                Color pixel = pixelRGB.get(i);
                double[] distance = new double[k];
                for (int j = 0; j < k; j++) {
                    Color currentCluster = clusters[j];
                    int redDistance = (pixel.getRed() - currentCluster.getRed()) * (pixel.getRed() - currentCluster.getRed());
                    int greenDistance = (pixel.getGreen() - currentCluster.getGreen()) * (pixel.getGreen() - currentCluster.getGreen());
                    int blueDistance = (pixel.getBlue() - currentCluster.getBlue()) * (pixel.getBlue() - currentCluster.getBlue());

                    //distance of rgb of pixel i from rgb of cluster j 
                    distance[j] = Math.sqrt(redDistance + greenDistance + blueDistance);
                }

                //Selecting the least distance for the pixel and assigning to that cluster
                double min = Double.MAX_VALUE;
                int clusterIndex = -1;
                for (int j = 0; j < k; j++) {
                    if (distance[j] < min) {
                        min = distance[j];
                        clusterIndex = j;
                    }
                }

                //clusterIndex is the cluster number to which the pixel must be assigned to
                pixelCluster[i] = clusterIndex;
            }

            //Updating the clusters depending upon the pixels assigned to it
            for (int j = 0; j < k; j++) {

                //Getting the red, green and blue values for all the pixels
                int redTotal = 0, greenTotal = 0, blueTotal = 0, count = 0;
                for (int i = 0; i < pixelRGB.size(); i++) {
                    if (pixelCluster[i] == j) {
                        //pixel i belongs to cluster j
                        Color pixel = pixelRGB.get(i);
                        redTotal += pixel.getRed();
                        greenTotal += pixel.getGreen();
                        blueTotal += pixel.getBlue();
                        count++;
                    }
                }
                if (count == 0) {
                    continue;
                }
                int redAvg = redTotal / count;
                int greenAvg = greenTotal / count;
                int blueAvg = blueTotal / count;

                if ((int) redAvg != clusters[j].getRed() || (int) greenAvg != clusters[j].getGreen() || (int) blueAvg != clusters[j].getBlue()) {
                    areClustersChanged = true;
                    clusters[j] = new Color(redAvg, greenAvg, blueAvg);
                }
            }
        }

        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = clusters[pixelCluster[i]].getRGB();
        }
    }
}
