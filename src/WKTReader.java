import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.Point;
import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.List;

public class WKTReader {

    /**
     * Transforms the input WKT-formatted String into Geometry object
     * <p>
     * GEOMETRYCOLLECTION (POINT (4 6), MULTIPOLYGON (((4 6, 7 10, 4 6), (1 2, 3 4, 1 2), (5 6, 7 8, 5 6)), ((4 6, 7 10, 4 6))))
     * GEOMETRYCOLLECTION (POINT (4 6), POLYGON ((4 6, 7 10, 4 6), (1 2, 3 4, 1 2), (5 6, 7 8, 5 6)))
     * GEOMETRYCOLLECTION (POINT (4 6), MULTIPOINT ((1 2), (5 6)))
     * MULTILINESTRING ((1 2, 3 4, 1 2), (5 6, 7 8, 5 6))
     */

    public Geometry read(String wktString) {

        return null;
    }

    private static Geometry startParse(String wktString) {

        String objectPart = wktString.substring(0, wktString.indexOf(' '));
        String paramPart = wktString.substring(wktString.indexOf(' ') + 1);

        while (!paramPart.isEmpty()) {

        }

        return null;
    }


    private static SGeometry parseAndPopulate(String wktString) {

        String objectPart = wktString.substring(0, wktString.indexOf(' '));
        String paramPart = wktString.substring(wktString.indexOf(' ') + 1);
        System.out.println("wjtString = " + wktString);
        System.out.println(objectPart + " PARAMPART = " + paramPart);

        if (objectPart.equals("GEOMETRYCOLLECTION")) {
            if (paramPart == "EMPTY") {
                return new SGeometry(paramPart, new GeometryCollection<>());
            }
            List<Geometry> elements = new ArrayList<>();
            elements.add(parseAndPopulate(paramPart.substring(1, paramPart.length() - 1)).geometry);
            return new SGeometry(paramPart, new GeometryCollection<>(elements));
        }
        if (objectPart.equals("POINT")) {
            if (paramPart == "EMPTY") {
                return new SGeometry(paramPart, new Point());
            }
            paramPart = paramPart.trim();
            int bracketOpenIndex = paramPart.indexOf('(')+1;
            int bracketCloseIndex = paramPart.indexOf(')');
            String temp = paramPart.substring(bracketOpenIndex, bracketCloseIndex);
            System.out.println("temp = " + temp);
            double x = Double.parseDouble(temp.split(" ")[0]);
            double y = Double.parseDouble(temp.split(" ")[1]);
          //  new SGeometry(paramPart.substring(bracketCloseIndex + 1), new Point(x, y));
            return new SGeometry(paramPart.substring(bracketCloseIndex + 1), new Point(x, y));

        }
        return null;
    }

    public static void main(String[] args) {
        String input = "GEOMETRYCOLLECTION (POINT (4 6), MULTIPOLYGON (((4 6, 7 10, 4 6), (1 2, 3 4, 1 2), (5 6, 7 8, 5 6)), ((4 6, 7 10, 4 6))))";
        String input2 = "GEOMETRYCOLLECTION (POINT (4 6))";
        Geometry geom = parseAndPopulate(input2).geometry;
        WKTWriter wktWriter = new WKTWriter();
        System.out.println("writee =  "+wktWriter.write(geom));
    }

}
