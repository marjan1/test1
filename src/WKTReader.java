import com.sinergise.geometry.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WKTReader {

    private static final String EMPTY = "EMPTY";
    private static final String GEOMETRYCOLLECTION = "GEOMETRYCOLLECTION";
    private static final String LINESTRING = "LINESTRING";
    private static final String MULTILINESTRING = "MULTILINESTRING";
    private static final String MULTIPOINT = "MULTIPOINT";
    private static final String MULTIPOLYGON = "MULTIPOLYGON";
    private static final String POINT = "POINT";
    private static final String POLYGON = "POLYGON";
    private static final char OPEN_BRACKET = '(';
    private static final char CLOSE_BRACKET = ')';
    private static final String COMMA = ",";
    private static final String EMPTY_CHAR = " ";

    /**
     * Transforms the input WKT-formatted String into Geometry object
     * <p>
     * GEOMETRYCOLLECTION (POINT (4 6), MULTIPOLYGON (((4 6, 7 10, 4 6), (1 2, 3 4, 1 2), (5 6, 7 8, 5 6)), ((4 6, 7 10, 4 6))))
     * GEOMETRYCOLLECTION (POINT (4 6), POLYGON ((4 6, 7 10, 4 6), (1 2, 3 4, 1 2), (5 6, 7 8, 5 6)))
     * GEOMETRYCOLLECTION (POINT (4 6), MULTIPOINT ((1 2), (5 6)))
     * MULTILINESTRING ((1 2, 3 4, 1 2), (5 6, 7 8, 5 6))
     */

    private Geometry getGeometryObjectFromWKTString(String wktString) throws IOException {

        wktString = wktString.trim();
        String objectPart = wktString.substring(0, wktString.indexOf(EMPTY_CHAR));
        String paramPart = wktString.substring(wktString.indexOf(EMPTY_CHAR) + 1);

        switch (objectPart) {
            case GEOMETRYCOLLECTION: {
                if (paramPart.equals(EMPTY)) return new GeometryCollection<>();
                return createGeometryCollectionObject(paramPart);
            }
            case LINESTRING: {
                if (paramPart.equals(EMPTY)) return new LineString();
                return createLineStringObject(paramPart);
            }
            case MULTILINESTRING: {
                if (paramPart.equals(EMPTY)) return new MultiLineString();
                return createMultiLineStringObject(paramPart);
            }
            case MULTIPOINT: {
                if (paramPart.equals(EMPTY)) return new MultiPoint();
                return createMultiPointObject(paramPart);
            }
            case MULTIPOLYGON: {
                if (paramPart.equals(EMPTY)) return new MultiPolygon();
                return createMultiPolygonObject(paramPart);
            }
            case POINT: {
                if (paramPart.equals(EMPTY)) return new Point();
                return createPointObject(paramPart);
            }
            case POLYGON: {
                if (paramPart.equals(EMPTY)) return new Polygon();
                return createPolygonObject(paramPart);
            }
            default:
                throw new IOException("String is not in correct format");
        }
    }

    private Geometry createMultiLineStringObject(String paramPart) {
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] parts = paramPart.split(COMMA);
        List<LineString> lineStringsList = new ArrayList<>();
        for (String part : parts) {
            part = part.trim();
            lineStringsList.add(createLineStringObject(part));
        }
        return new MultiLineString(lineStringsList.toArray(new LineString[lineStringsList.size()]));
    }

    private Geometry createMultiPointObject(String paramPart) {
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] parts = paramPart.split(COMMA);
        List<Point> pointsList = new ArrayList<>();
        for (String part : parts) {
            part = part.trim();
            pointsList.add(createPointObject(part));
        }
        return new MultiPoint(pointsList.toArray(new Point[pointsList.size()]));
    }

    private Geometry createGeometryCollectionObject(String paramPart) throws IOException {
        List<Geometry> elements = new ArrayList<>();
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] stringElements = split(paramPart);
        for (String element : stringElements) {
            elements.add(getGeometryObjectFromWKTString(element));
        }
        return new GeometryCollection<>(elements);
    }

    private Geometry createMultiPolygonObject(String paramPart) {
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] stringElements = split(paramPart);
        Polygon[] polygons = new Polygon[stringElements.length];
        for (int i = 0; i < stringElements.length; i++) {
            polygons[i] = createPolygonObject(stringElements[i].trim());
        }

        return new MultiPolygon(polygons);
    }

    private Point createPointObject(String paramPart) {
        if (paramPart.charAt(0) == OPEN_BRACKET && paramPart.charAt(paramPart.length() - 1) == CLOSE_BRACKET) {
            paramPart = paramPart.substring(1, paramPart.length() - 1);
        }
        String[] stringElements = paramPart.split(EMPTY_CHAR);
        return new Point(Double.parseDouble(stringElements[0].trim()), Double.parseDouble(stringElements[1].trim()));
    }

    private  Polygon createPolygonObject(String paramPart) {
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

    private  LineString createLineStringObject(String paramPart) {
        paramPart = paramPart.trim();
        if (paramPart.charAt(0) == OPEN_BRACKET) {
            paramPart = paramPart.substring(1);
        }
        if (paramPart.charAt(paramPart.length() - 1) == CLOSE_BRACKET) {
            paramPart = paramPart.substring(0, paramPart.length() - 1);
        }
        String[] stringElements = paramPart.split(COMMA);
        List<Double> coords = new ArrayList<>();
        for (String element : stringElements) {
            element = element.trim();
            String[] temp = element.split(EMPTY_CHAR);
            coords.add(Double.parseDouble(temp[0]));
            coords.add(Double.parseDouble(temp[1]));
        }
        return new LineString(coords.stream().mapToDouble(Double::doubleValue).toArray());
    }

    private  String[] split(String string) {
        int lastIndex = 0;
        int level = 0;
        List<String> result = new LinkedList<>();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == OPEN_BRACKET) level++;
            if (string.charAt(i) == CLOSE_BRACKET) level--;
            if (string.charAt(i) == ',' && level == 0) {
                result.add(string.substring(lastIndex, i));
                lastIndex = i + 1;
            }
        }
        result.add(string.substring(lastIndex, string.length()));
        return result.toArray(new String[result.size()]);
    }




}
