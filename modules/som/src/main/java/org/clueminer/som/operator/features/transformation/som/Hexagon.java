package org.clueminer.som.operator.features.transformation.som;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Draw a hexagon
 *
 * @author Jan Motl
 */
public class Hexagon {

    private Graphics g;				// the canvas to draw
    private GeneralPath hexagon;	// draws one hexagon

    private int a; 					// height of the diagonal line  /
    private int b; 					// width of the diagonal line   /
    private int c; 					// height of the vertical line  |

    private boolean withOutline = true;	// draw the outline around the hexagon

	// create isometric hexagon:frame width and height, size of net horizontally and vertically
    // nice explanation is at: http://www.miniwizardstudios.com/iso.asp
    public Hexagon(Graphics g, int pixWidth, int pixHeight, int countX, int countY) {
        this.g = g;

        /* The old accurate 60 degrees...
         double sideLength=Math.min(pixHeight/(countY*3+1)*2, pixWidth/(countX*2+1)/Math.sin(Math.PI/3));
         c = (int)sideLength; 				// y dimension
         a = c/2; 							// y dimension, diagonal part (still integer)
         b = (int)(Math.sin(Math.PI/3)*c); 	// x dimension, diagonal part (60 degree)

         if ((pixHeight - (countY*(c+a)+a)) >= countY+1) a+=1;
         if ((pixHeight - (countY*(c+a)+a)) >= countY) c+=1;
         */
        // squeeze hexagons on the screen both to height and width (the hexagon map is ragged, hence + 1)
        int sideLength = Math.min(pixHeight / (countY * 3 + 1), pixWidth / (countX * 4 + 2));

        c = 2 * sideLength; 	// y dimension
        a = sideLength; 	// y dimension, diagonal part
        b = 2 * sideLength; 	// x dimension, diagonal part (63,435... degree)

        // maximize hexagons as possible (we don't necessary need the perfect isometric hexagons)
        if ((pixWidth - (countX * 2 * b + b)) >= countX * 2 + 1) {
            b += 1;
        }

        // create hexagon
        hexagon = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        hexagon.moveTo(0, a); 	// the coordinates are from top left corner. Start drawing from the top left of the hexagon...
        hexagon.lineTo(0, a + c); 	// ...and draw the hexagon counterclockwise
        hexagon.lineTo(b, c + 2 * a);
        hexagon.lineTo(2 * b, a + c);
        hexagon.lineTo(2 * b, a);
        hexagon.lineTo(b, 0);
        hexagon.moveTo(0, a);
        hexagon.lineTo(b, 0);	// we have to draw / line in the same direction as the previous / line to have the same pixel-wise line

        // don't draw the outline around the hexagon if the hexagon is too small
        if (c < 16) {
            withOutline = false;
        }
    }

    // center the matrix of hexagons
    public void allignToCenter(int pixWidth, int pixHeight, int countX, int countY) {
        g.translate((pixWidth - (countX * 2 * b + b)) / 2, (pixHeight - (countY * (c + a) + a)) / 2); // center the matrix
    }

    // draw hexagon
    private int[] drawHexagonBeginning(int positionX, int positionY, Color color) {
        g.setColor(color);

        positionX = positionX * (2 * b); // shift right
        if ((positionY % 2) == 1) {   // for even row...
            positionX += b;          // ... shift right bit more
        }
        positionY = positionY * (c + a); // shift down

        g.translate(positionX, positionY);  		//move to the demanded position
        ((Graphics2D) g).fill(hexagon);      		//fill with demanded color

        if (withOutline) { 								// draw dark outline around the hexagons
            Color customColor = new Color(10, 10, 10);	// instead of pure black use "almost black"
            g.setColor(customColor);
        }
        ((Graphics2D) g).draw(hexagon);

        int[] positions = {positionX, positionY};
        return positions; 	// return the present position in the graphic
    }

    public void drawHexagon(int positionX, int positionY, Color color) {
        int[] positions = drawHexagonBeginning(positionX, positionY, color);
        g.translate(-positions[0], -positions[1]); //reset the coordinate system
    }

    // helper function for histogram. It takes care that the bigger hit histogram hexagon is bellow the smaller ones
    private static int[] sortAndReturnIndexes(final double[] data) {
        // create indexes
        final Integer[] idx = new Integer[data.length];
        for (int i = 0; i < data.length; i++) {
            idx[i] = i;
        }

        // sort with a comparator
        Arrays.sort(idx, new Comparator<Integer>() {
            @Override
            public int compare(final Integer o1, final Integer o2) {
                return Double.compare(data[o2], data[o1]);
            }
        });

        // cast Integer -> int
        int[] index = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            index[i] = idx[i];
        }
        return index;
    }

    public void drawHexagonWithHistogram(int positionX, int positionY, Color color, double[] histogram, Color histogramColors[]) {
        int[] positions = drawHexagonBeginning(positionX, positionY, color);

        // sort data from the biggest cluster to the smallest cluster so all histograms will be visible ()
        int[] indexes = sortAndReturnIndexes(histogram);

        // Histogram
        for (int i = 0; i < histogram.length; i++) {
            int radius = (int) Math.ceil(histogram[indexes[i]] * c / 2); //scale (0..1) to (0..c) and calculate radius
            if (histogram[indexes[i]] > 0) {
                g.setColor(histogramColors[indexes[i]]); 						// plot a colorful circle
                g.fillArc(b - radius, a + c / 2 - radius, radius * 2, radius * 2, 0, 360);
                g.setColor(histogramColors[indexes[i]].darker()); 				// plot a darker outline to separate foreground from the background
                g.drawArc(b - radius, a + c / 2 - radius, radius * 2, radius * 2, 0, 360);
            }
        }

        g.translate(-positions[0], -positions[1]); //reset the coordinate system
    }

    public void drawHexagonWithLabel(int positionX, int positionY, Color color, String label) {
        int[] positions = drawHexagonBeginning(positionX, positionY, color);

        // Label
        if (label != null) {
            g.setColor(Color.BLACK);
            FontMetrics fm2 = g.getFontMetrics(g.getFont());
            double width2 = fm2.stringWidth(label);
            double height2 = fm2.getHeight();
            g.drawString(label, (int) (b - width2 / 2), (int) (a + c / 2 + height2 / 2));
        }

        g.translate(-positions[0], -positions[1]); //reset the coordinate system
    }

    //////////////////////////////////// following things are currently not in use ///////////////////////////////
    public void drawHexagonWithSOMValue(int positionX, int positionY, Color color, double somValue) {
        int[] positions = drawHexagonBeginning(positionX, positionY, color);

        // SOM value
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics(g.getFont());
        double width = fm.stringWidth(double2str(somValue));
        double height = fm.getHeight();
        g.drawString(double2str(somValue), (int) (b - width / 2), (int) (a + c / 2 + height / 2));

        g.translate(-positions[0], -positions[1]); //reset the coordinate system
    }

    // text formating
    public String double2str(double inValue) {
        DecimalFormat threeDec = new DecimalFormat("0.000");
        threeDec.setGroupingUsed(false);
        return threeDec.format(inValue);
    }

    // draw borders
    private int[] drawBoldBorder(int positionX, int positionY, boolean boldRight, boolean boldBottomRight, boolean boldBottomLeft) {
        // move to the correct position
        positionX = positionX * (2 * b); // shift right
        if ((positionY % 2) == 1) {   // for even row...
            positionX += b;          // ... shift right bit more
        }
        positionY = positionY * (c + a); // shift down

        g.translate(positionX, positionY); //move to the demanded position

        // set up drawing parameters
        g.setColor(Color.BLACK);
        int boldSize = 6;

        // calculate constants
        int dx = (boldSize / 2);
        int dy = (int) (Math.tan(Math.PI / 6) * boldSize / 2); //vertical
        int diy = (int) (dx / Math.cos(Math.PI / 6)); //diagonal y

        // right border
        if (boldRight) {
            int[] xPoints = {2 * b - dx, 2 * b, 2 * b + dx, 2 * b + dx, 2 * b, 2 * b - dx};
            int[] yPoints = {a - dy, a, a - dy, a + c + dy, a + c, a + c + dy};
            ((Graphics2D) g).fillPolygon(xPoints, yPoints, 6);
        }

        // bottom right border
        if (boldBottomRight) {
            int[] xPoints2 = {2 * b, 2 * b, 2 * b + dx, b, b, b - dx};
            int[] yPoints2 = {a + c - diy, a + c, a + c + dy, 2 * a + c + diy, 2 * a + c, 2 * a + c - dy};
            ((Graphics2D) g).fillPolygon(xPoints2, yPoints2, 6);
        }

        // bottom left border
        if (boldBottomLeft) {
            int[] xPoints3 = {-dx, 0, 0, b + dx, b, b};
            int[] yPoints3 = {a + c + dy, a + c, a + c - diy, 2 * a + c - dy, 2 * a + c, 2 * a + c + diy};
            ((Graphics2D) g).fillPolygon(xPoints3, yPoints3, 6);
        }

        int[] positions = {positionX, positionY}; //return the performed translations to allow reset of the translations
        return positions;
    }

    public void drawBoldBorders(int countX, int countY, Net network) {
        // plot borders
        for (int x = 0; x < countX; x++) {
            for (int y = 0; y < countY; y++) {
                // reset variables
                boolean boldRight = false;
                boolean boldBottomRight = false;
                boolean boldBottomLeft = false;

                // if neighboring nodes belong to different cluster draw bold border between them
                if (network.at(x, y).nRight != null && network.at(x, y).nRight.cluster != network.at(x, y).cluster) {
                    boldRight = true;
                }
                if (network.at(x, y).nBottomRight != null && network.at(x, y).nBottomRight.cluster != network.at(x, y).cluster) {
                    boldBottomRight = true;
                }
                if (network.at(x, y).nBottomLeft != null && network.at(x, y).nBottomLeft.cluster != network.at(x, y).cluster) {
                    boldBottomLeft = true;
                }

                int[] positions = drawBoldBorder(x, y, boldRight, boldBottomRight, boldBottomLeft); // draw the border
                g.translate(-positions[0], -positions[1]); //reset the coordinate system
            }
        }
    }

}
