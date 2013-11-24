package org.alcibiade.asciiart.image.rasterize;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.alcibiade.asciiart.image.rasterize.ImageComparator;

/**
 *
 * @author Yannick Kirschhoffer <yannick.kirschhoffer@cgi.com>
 */
public class ShapesGenerator {

    public static void main(String... args) throws IOException {
        int sx = 3;
        int sy = 3;

        List<Character> validChars = new ArrayList<Character>();
        for (int c = 32; c < 127; c++) {
            validChars.add((char) c);
        }
        
        validChars.add('|');
        validChars.add('\\');

        ImageComparator comparator = new ImageComparator();

        for (int ix = 0; ix < (1 << (sx * sy)); ix++) {
            int imx = 200;
            int imy = 320;

            // Create blocks reference
            BufferedImage blocksImage = new BufferedImage(imx, imy, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = (Graphics2D) blocksImage.getGraphics();
            g2d.setBackground(Color.BLACK);
            g2d.setColor(Color.WHITE);
            g2d.clearRect(0, 0, imx, imy);

            for (int y = 0; y < sy; y++) {
                for (int x = 0; x < sx; x++) {
                    if ((ix & (1 << (x + sx * y))) != 0) {
                        g2d.fillRect(x * imx / sx, y * imy / sy, 1 + imx / sx, 1 + imy / sy);
                    }
                }
            }

            long bestDelta = Long.MAX_VALUE;
            int bestIndex = 0;

            for (int cix = 0; cix < validChars.size(); cix++) {
                String text = "" + validChars.get(cix);

                BufferedImage charImage = new BufferedImage(imx, imy, BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D letterg2d = (Graphics2D) charImage.getGraphics();
                letterg2d.setBackground(Color.WHITE);
                letterg2d.setColor(Color.BLACK);
                letterg2d.clearRect(0, 0, imx, imy);

                letterg2d.setFont(new Font("monospaced", Font.BOLD, imy));
                letterg2d.drawString(text, 0, imy * 5 / 6);

                long delta = comparator.difference(blocksImage, charImage);

                if (delta < bestDelta) {
                    bestDelta = delta;
                    bestIndex = cix;
                }

//                ImageIO.write(charImage, "PNG", new File("output-" + cix + ".png"));
            }
            char result = validChars.get(bestIndex);

            String resultString = (result == '\\' ? "\\\\" : "" + result);

            System.out.println(String.format("%d=%s", ix, resultString));
        }
    }

}