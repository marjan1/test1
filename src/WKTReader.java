import com.sinergise.geometry.*;
import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

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
        if (wktString.isEmpty()) {
            return null;
        }
        wktString = wktString.trim();
        String objectPart = wktString.substring(0, wktString.indexOf(' '));
        String paramPart = wktString.substring(wktString.indexOf(' ') + 1);
        System.out.println("wjtString = " + wktString);
        System.out.println(objectPart + " PARAMPART = " + paramPart);

        if (objectPart.equals("GEOMETRYCOLLECTION")) {
            if (paramPart == "EMPTY") {
                return new SGeometry(paramPart, new GeometryCollection<>());
            }
            List<Geometry> elements = new ArrayList<>();
            while (!paramPart.isEmpty()) {
                SGeometry temp = parseAndPopulate(paramPart.substring(1, paramPart.length()));
                if (temp != null) {
                    elements.add(temp.geometry);
                    paramPart = temp.text;
                }
            }
            return new SGeometry(paramPart, new GeometryCollection<>(elements));
        }
        if (objectPart.equals("POINT")) {
            if (paramPart == "EMPTY") {
                return new SGeometry(paramPart, new LineString());
            }

            paramPart = paramPart.trim();
            int bracketOpenIndex = paramPart.indexOf('(') + 1;
            int bracketCloseIndex = paramPart.indexOf(')');
            String temp = paramPart.substring(bracketOpenIndex, bracketCloseIndex);
            temp.replace(',', ' ');
            System.out.println("temp = " + temp);
            double x = Double.parseDouble(temp.split(" ")[0]);
            double y = Double.parseDouble(temp.split(" ")[1]);
            return new SGeometry(paramPart.substring(bracketCloseIndex + 1), new Point(x, y));
        }
        if (objectPart.equals("LINESTRING")) {
            if (paramPart == "EMPTY") {
                return new SGeometry(paramPart, new Point());
            }
            paramPart = paramPart.trim();
            int bracketOpenIndex = paramPart.indexOf('(') + 1;
            int bracketCloseIndex = paramPart.indexOf(')');
            String temp = paramPart.substring(bracketOpenIndex, bracketCloseIndex);
            System.out.println("temp = " + temp);
            String[] parts = temp.split(" ");
            double[] coords = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].contains(",")) {
                    coords[i] = Double.parseDouble(parts[i].substring(0, parts[i].length() - 1));
                }
            }
            return new SGeometry(paramPart.substring(bracketCloseIndex + 1), new LineString(coords));

        }
        return null;
    }

    String[] geomObjectNames = {"GEOMETRYCOLLECTION", "POINT", "LINESTRING", "POLYGON", "MULTIPOLYGON", "MULTIPOINT", "MULTILINESTRING"};

    private static List<String> getparsedElements(String paramPart) {
        List<String> elements = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(paramPart);
        while (stringTokenizer.hasMoreTokens()) {
            String element = stringTokenizer.nextToken();
            String nextPart = stringTokenizer.nextToken();
            if (stringTokenizer.nextToken().equals("EMPTY")) {
                element = element + nextPart;
            } else if (stringTokenizer.nextToken().equals("EMPTY,")) {
                element = element + nextPart.substring(0, nextPart.length() - 1);
            } else {
                while (!nextPart.contains(")") || !nextPart.contains("),") && stringTokenizer.hasMoreTokens()) {
                    element = element + nextPart;
                    nextPart = stringTokenizer.nextToken();
                }
            }

            elements.add(element);
        }
        return elements;
    }

    static String[] split(String string) {
        int lastIndex = 0;
        int level = 0;
        List<String> result = new LinkedList<>();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '(') level++;
            if (string.charAt(i) == ')') level--;
            if (string.charAt(i) == ',' && level == 0) {
                result.add(string.substring(lastIndex, i));
                lastIndex = i + 1;
            }
        }
        result.add(string.substring(lastIndex, string.length()));
        return result.toArray(new String[result.size()]);
    }

    private static Geometry parseAndPopulate2(String wktString) {

        wktString = wktString.trim();
        String objectPart = wktString.substring(0, wktString.indexOf(' '));
        String paramPart = wktString.substring(wktString.indexOf(' ') + 1);

        if (objectPart.equals("GEOMETRYCOLLECTION")) {
            if (paramPart == "EMPTY") {
                return new GeometryCollection<>();
            }
            return createGeometryCollectionObject(paramPart);
        }
        if (objectPart.equals("LINESTRING")) {
            if (paramPart == "EMPTY") {
                return new LineString();
            }
            return createLineStringObject(paramPart);
        }

        if (objectPart.equals("MULTILINESTRING")) {
            if (paramPart == "EMPTY") {
                return new MultiLineString();
            }
            return createMultiLineStringObject(paramPart);

        }

        if (objectPart.equals("MULTIPOINT")) {
            if (paramPart == "EMPTY") {
                return new MultiPoint();
            }
            return creayeMultiPointObject(paramPart);
        }

        if (objectPart.equals("MULTIPOLYGON")) {
            if (paramPart == "EMPTY") {
                return new MultiPolygon();
            }
            return createMultiPointObject(paramPart);
        }
        if (objectPart.equals("POINT")) {
            if (paramPart == "EMPTY") {
                return new Point();
            }
            return createPointObject(paramPart);
        }

        if (objectPart.equals("POLYGON")) {
            if (paramPart == "EMPTY") {
                return new Polygon();
            }
            return createPolygonObject(paramPart);
        }


        return null;
    }

    private static Geometry createMultiLineStringObject(String paramPart) {
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] parts = paramPart.split(",");
        List<LineString> lineStringsList = new ArrayList<>();
        for (String part : parts) {
            part = part.trim();
            lineStringsList.add(createLineStringObject(part));
        }
        return new MultiLineString(lineStringsList.toArray(new LineString[lineStringsList.size()]));
    }

    private static Geometry creayeMultiPointObject(String paramPart) {
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] parts = paramPart.split(",");
        List<Point> pointsList = new ArrayList<>();
        for (String part : parts) {
            part = part.trim();
            pointsList.add(createPointObject(part));
        }
        return new MultiPoint(pointsList.toArray(new Point[pointsList.size()]));
    }

    private static Geometry createGeometryCollectionObject(String paramPart) {
        List<Geometry> elements = new ArrayList<>();
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] stringElements = split(paramPart);
        for (String element : stringElements) {
            elements.add(parseAndPopulate2(element));
        }
        return new GeometryCollection<>(elements);
    }

    private static Geometry createMultiPointObject(String paramPart) {
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] stringElements = split(paramPart);
        Polygon[] polygons = new Polygon[stringElements.length];
        for (int i = 0; i < stringElements.length; i++) {
            polygons[i] = createPolygonObject(stringElements[i].trim());
        }

        return new MultiPolygon(polygons);
    }

    private static Point createPointObject(String paramPart) {
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] stringElements = paramPart.split(" ");
        double x = Double.parseDouble(stringElements[0].trim());
        double y = Double.parseDouble(stringElements[1].trim());
        return new Point(x, y);
    }

    private static Polygon createPolygonObject(String paramPart) {
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] stringElements = split(paramPart);

        LineString outer = createLineStringObject(stringElements[0]);
        LineString[] holes = null;

        if (stringElements.length > 1) {
            holes = new LineString[stringElements.length - 1];
            for (int i = 1; i < stringElements.length; i++) {
                holes[i - 1] = createLineStringObject(stringElements[i]);
            }
        }
        return new Polygon(outer, holes);
    }

    private static LineString createLineStringObject(String paramPart) {
        paramPart = paramPart.trim();
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] stringElements = paramPart.split(",");
        List<Double> coords = new ArrayList<>();
        for (String element : stringElements) {
            element = element.trim();
            String[] temp = element.split(" ");
            double x = Double.parseDouble(temp[0]);
            double y = Double.parseDouble(temp[1]);
            coords.add(x);
            coords.add(y);
        }
        return new LineString(coords.stream().mapToDouble(Double::doubleValue).toArray());
    }

    public static void main(String[] args) {
        String input = "GEOMETRYCOLLECTION (POINT (4 6), MULTIPOLYGON (((4 6, 7 10, 4 6), (1 2, 3 4, 1 2), (5 6, 7 8, 5 6)), ((4 6, 7 10, 4 6))))";
        String input2 = "GEOMETRYCOLLECTION (POINT (4 6), LINESTRING (4 6, 7 10), MULTIPOINT ((10 40), (40 30), (20 20), (30 10)), " +
                "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), " +
                "(20 30, 35 35, 30 20, 20 30)), "
                + "MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)), " +
                "((20 35, 10 30, 10 10, 30 5, 45 20, 20 35), " +
                "(30 20, 20 15, 20 25, 30 20))), " +
                "MULTIPOINT ((10 40), (40 30), (20 20), (30 10)), "
                + "MULTIPOINT (10 40, 40 30, 20 20, 30 10), "
                + "MULTILINESTRING ((10 10, 20 20, 10 40), " +
                "(40 40, 30 30, 40 20, 30 10)))";
        Geometry geom = parseAndPopulate2(input2);
        WKTWriter wktWriter = new WKTWriter();
        System.out.println("writee =  " + wktWriter.write(geom));
    }

}
