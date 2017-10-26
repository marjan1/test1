import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.Point;

public class WKTReader {

    /**
     * Transforms the input WKT-formatted String into Geometry object
     *
     * GEOMETRYCOLLECTION (POINT (4 6), MULTIPOLYGON (((4 6, 7 10, 4 6), (1 2, 3 4, 1 2), (5 6, 7 8, 5 6)), ((4 6, 7 10, 4 6))))
     GEOMETRYCOLLECTION (POINT (4 6), POLYGON ((4 6, 7 10, 4 6), (1 2, 3 4, 1 2), (5 6, 7 8, 5 6)))
     GEOMETRYCOLLECTION (POINT (4 6), MULTIPOINT ((1 2), (5 6)))
     MULTILINESTRING ((1 2, 3 4, 1 2), (5 6, 7 8, 5 6))
     */

    public Geometry read(String wktString) {

        return null;
    }

}
