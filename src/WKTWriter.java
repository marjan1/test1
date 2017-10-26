import com.sinergise.geometry.*;

import java.util.Arrays;
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
    public String write(Geometry geom) {
        //TODO: Implement this
        if (geom.isEmpty()) {
            return geom.getClass().getSimpleName().toUpperCase() + " EMPTY";
        }

        if (geom instanceof MultiPolygon) {
            String returnValue = geom.getClass().getSimpleName().toUpperCase() + " (";
            for (int i = 0; i < ((MultiPolygon) geom).size(); i++) {
                returnValue = returnValue +"("+ getPolygonCoordinates(((MultiPolygon) geom).get(i)) + "), ";
            }
            return returnValue.subSequence(0, returnValue.length() - 2) + ")";
        }
        if (geom instanceof MultiPoint) {
            String returnValue = geom.getClass().getSimpleName().toUpperCase() + " (";
            for (int i = 0; i < ((MultiPoint) geom).size(); i++) {
                returnValue = returnValue + getPointCoordinates(((MultiPoint) geom).get(i)) + ", ";
            }
            return returnValue.subSequence(0, returnValue.length() - 2) + ")";
        }
        if (geom instanceof MultiLineString) {
            String returnValue = geom.getClass().getSimpleName().toUpperCase() + " (";
            for (int i = 0; i < ((MultiLineString) geom).size(); i++) {
                returnValue = returnValue + getLineStringCoordinates(((MultiLineString) geom).get(i)) + ", ";
            }
            return returnValue.subSequence(0, returnValue.length() - 2) + ")";
        }
        if (geom instanceof GeometryCollection) {
            Iterator it = ((GeometryCollection) geom).iterator();
            String returnValue = geom.getClass().getSimpleName().toUpperCase() + " (";
            while (it.hasNext()) {
                returnValue = returnValue + write((Geometry) it.next()) + ", ";
            }
            return returnValue.subSequence(0, returnValue.length() - 2) + ")";
        }
        if (geom instanceof LineString) {
            String returnValue = geom.getClass().getSimpleName().toUpperCase() + " ";
            return returnValue + getLineStringCoordinates((LineString) geom);
        }
        if (geom instanceof Point) {
            String returnValue = geom.getClass().getSimpleName().toUpperCase() + " ";
            returnValue = returnValue + getPointCoordinates((Point) geom);
            return returnValue;
        }
        if (geom instanceof Polygon) {
            String returnValue = geom.getClass().getSimpleName().toUpperCase() + " ";

            return returnValue + "(" + getPolygonCoordinates(((Polygon) geom)) + ")";
        }
        throw new UnsupportedOperationException("Parsing not good");
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


    public static void main(String[] args) {
        //  System.out.println(new WKTWriter().write(new LineString(new double[]{30, 10, 10, 30, 40, 40})));
        System.out.println(new WKTWriter()
                .write(new GeometryCollection(new Geometry[]{new Point(4, 6),
                        new MultiPolygon(new Polygon[]{
                        new Polygon(new LineString(new double[]{4, 6, 7, 10, 4, 6}),
                                new LineString[]{
                                        new LineString(new double[]{1, 2, 3, 4, 1, 2}),
                                        new LineString(new double[]{5, 6, 7, 8, 5, 6})}
                        ),
                                new Polygon(new LineString(new double[]{4, 6, 7, 10, 4, 6}), null)


                        })})));


        System.out.println(new WKTWriter()
                .write(new GeometryCollection(new Geometry[]{new Point(4, 6),
                        new Polygon(new LineString(new double[]{4, 6, 7, 10, 4, 6}),
                        new LineString[]{
                                new LineString(new double[]{1, 2, 3, 4, 1, 2}),
                                new LineString(new double[]{5, 6, 7, 8, 5, 6})}
                )})));

        System.out.println(new WKTWriter()
                .write(new GeometryCollection(new Geometry[]{new Point(4, 6),
                        new MultiPoint(
                                new Point[]{
                                        new Point(1, 2),
                                        new Point(5, 6)}
                        )})));

        System.out.println(new WKTWriter()
                .write(
                        new MultiLineString(
                                new LineString[]{
                                        new LineString(new double[]{1, 2, 3, 4, 1, 2}),
                                        new LineString(new double[]{5, 6, 7, 8, 5, 6})}
                        )));

//        System.out.println(new WKTWriter()
//                .write(new GeometryCollection(new Geometry[]{new Point(4, 6),
//                        new MultiLineString(
//                                new LineString[]{
//                                        new LineString(new double[]{1, 2, 3, 4, 1, 2}),
//                                        new LineString(new double[]{5, 6, 7, 8, 5, 6})}
//                        )})));
//        System.out.println(new WKTWriter().write(new GeometryCollection(new Geometry[]{new Point(4, 6), new LineString(new double[]{4, 6, 7, 10})})));
//
//        System.out.println(new WKTWriter().write(new GeometryCollection(new Geometry[]{new Point(), new LineString(new double[]{4, 6, 7, 10})})));
    }
}
