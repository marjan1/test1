import com.sinergise.geometry.*;

import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.util.Iterator;

public class WKTWriter {

    /**
     * Transforms the input Geometry object into WKT-formatted String. e.g.
     * <pre><code>
     * new WKTWriter().write(new LineString(new double[]{30, 10, 10, 30, 40, 40}));
     * //returns "LINESTRING (30 10, 10 30, 40 40)"
     * </code></pre>
     * <p>
     * write(new Point(30, 10)) returns "POINT (30 10)"
     * <p>
     * write(new LineString()) returns "LINESTRING EMPTY"
     * <p>
     * write(new GeometryCollection<Geometry>(new Geometry[]{new Point(4,6), new LineString(new double[] {4,6,7,10})}))
     * returns "GEOMETRYCOLLECTION (POINT (4 6), LINESTRING (4 6, 7 10))"
     */
    public String write(Geometry geom) throws IOException {

        if (geom.isEmpty()) {
            return geom.getClass().getSimpleName().toUpperCase() + " EMPTY";
        }

        if (geom instanceof MultiPolygon) {
            return getMultiPolygonString(geom);
        }
        if (geom instanceof MultiPoint) {
            return getMultiPointString(geom);
        }
        if (geom instanceof MultiLineString) {
            return getMultiString(geom);
        }
        if (geom instanceof GeometryCollection) {
            return getGeometryCollectionString(geom);
        }
        if (geom instanceof LineString) {
            return concat(geom, getLineStringCoordinates((LineString) geom));
        }
        if (geom instanceof Point) {
            return concat(geom, getPointCoordinates((Point) geom));

        }
        if (geom instanceof Polygon) {
            return concat(geom, "(", getPolygonCoordinates(((Polygon) geom)), ")");
        }
        throw new IOException("Parsing not good");
    }

    private String getGeometryCollectionString(Geometry geom)throws IOException {
        Iterator it = ((GeometryCollection) geom).iterator();
        String returnValue = geom.getClass().getSimpleName().toUpperCase() + " (";
        while (it.hasNext()) {
            returnValue = returnValue + write((Geometry) it.next()) + ", ";
        }
        return returnValue.subSequence(0, returnValue.length() - 2) + ")";
    }

    private String getMultiString(Geometry geom) {
        String returnValue = geom.getClass().getSimpleName().toUpperCase() + " (";
        for (int i = 0; i < ((MultiLineString) geom).size(); i++) {
            returnValue = returnValue + getLineStringCoordinates(((MultiLineString) geom).get(i)) + ", ";
        }
        return returnValue.subSequence(0, returnValue.length() - 2) + ")";
    }

    private String getMultiPointString(Geometry geom) {
        String returnValue = geom.getClass().getSimpleName().toUpperCase() + " (";
        for (int i = 0; i < ((MultiPoint) geom).size(); i++) {
            returnValue = returnValue + getPointCoordinates(((MultiPoint) geom).get(i)) + ", ";
        }
        return returnValue.subSequence(0, returnValue.length() - 2) + ")";
    }

    private String getMultiPolygonString(Geometry geom) {
        String returnValue = geom.getClass().getSimpleName().toUpperCase() + " (";
        for (int i = 0; i < ((MultiPolygon) geom).size(); i++) {
            returnValue = returnValue + "(" + getPolygonCoordinates(((MultiPolygon) geom).get(i)) + "), ";
        }
        return returnValue.subSequence(0, returnValue.length() - 2) + ")";
    }

    private String concat(Geometry geometry, String... parts) {

        return geometry.getClass().getSimpleName().toUpperCase() + " " + String.join("", parts);
    }

    private String getPolygonCoordinates(Polygon geom) {
        String returnValue = getLineStringCoordinates(geom.getOuter());
        if (geom.getNumHoles() > 0) {
            for (int i = 0; i < geom.getNumHoles(); i++) {
                returnValue = returnValue + ", " + getLineStringCoordinates(geom.getHole(i));
            }
        }
        return returnValue;
    }

    private String getPointCoordinates(Point geom) {
        return "(" + (int) geom.getX() + " " + (int) geom.getY() + ")";
    }

    private String getLineStringCoordinates(LineString geom) {
        String returnValue = "(";
        for (int i = 0; i < geom.getNumCoords(); i++) {
            returnValue = returnValue + (int) geom.getX(i) + " ";
            returnValue = returnValue + (int) geom.getY(i) + ", ";
        }
        return returnValue.subSequence(0, returnValue.length() - 2) + ")";
    }

}
