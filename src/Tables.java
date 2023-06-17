import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class Tables {
    // Positive X = translate right, Positive Y = translate down 
    public static final Map<Utils.Pair<Integer, Integer>, Point[]> NON_I_WALL_KICK_DATA = new HashMap<>() {{
        put(new Utils.Pair<>(0, 1), new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(-1, -1),
                new Point(0, 2),
                new Point(-1, 2)
        });
        put(new Utils.Pair<>(0, 2), new Point[]{
                new Point(0, 0),
                new Point(0, -1),
                new Point(1, -1),
                new Point(-1, -1),
                new Point(1, 0),
                new Point(-1, 0)
        });
        put(new Utils.Pair<>(0, 3), new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, -1),
                new Point(0, 2),
                new Point(1, 2)
        });
        put(new Utils.Pair<>(1, 0), new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, 1),
                new Point(0, -2),
                new Point(1, -2)
        });
        put(new Utils.Pair<>(1, 2), new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, 1),
                new Point(0, -2),
                new Point(1, -2)
        });
        put(new Utils.Pair<>(1, 3), new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, -2),
                new Point(1, 1),
                new Point(0, -2),
                new Point(0, -1)
        });
        put(new Utils.Pair<>(2, 0), new Point[]{
                new Point(0, 0),
                new Point(0, 1),
                new Point(-1, 1),
                new Point(1, 1),
                new Point(-1, 0),
                new Point(1, 0)
        });
        put(new Utils.Pair<>(2, 1), new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(-1, -1),
                new Point(0, 2),
                new Point(-1, 2)
        });
        put(new Utils.Pair<>(2, 3), new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, -1),
                new Point(0, 2),
                new Point(1, 2)
        });
        put(new Utils.Pair<>(3, 0), new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(-1, 1),
                new Point(0, -2),
                new Point(-1, -2)
        });
        put(new Utils.Pair<>(3, 1), new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(-1, -2),
                new Point(-1, -1),
                new Point(0, -2),
                new Point(0, -1)
        });
        put(new Utils.Pair<>(3, 2), new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(-1, 1),
                new Point(0, -2),
                new Point(-1, -2)
        });
    }};

    public static final Map<Utils.Pair<Integer, Integer>, Point[]> I_WALL_KICK_DATA = new HashMap<>() {{
        put(new Utils.Pair<>(0, 1), new Point[]{
                new Point(0, 0),
                new Point(-2, 0),
                new Point(1, 0),
                new Point(-2, 1),
                new Point(1, -2)
        });
        put(new Utils.Pair<>(0, 3), new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(2, 0),
                new Point(-1, -2),
                new Point(2, 1)
        });
        put(new Utils.Pair<>(1, 0), new Point[]{
                new Point(0, 0),
                new Point(2, 0),
                new Point(-1, 0),
                new Point(2, -1),
                new Point(-1, 2)
        });
        put(new Utils.Pair<>(1, 2), new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(2, 0),
                new Point(-1, -2),
                new Point(2, 1)
        });
        put(new Utils.Pair<>(2, 1), new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(-2, 0),
                new Point(1, 2),
                new Point(-2, -1)
        });
        put(new Utils.Pair<>(2, 3), new Point[]{
                new Point(0, 0),
                new Point(2, 0),
                new Point(-1, 0),
                new Point(2, -1),
                new Point(-1, 2)
        });
        put(new Utils.Pair<>(3, 0), new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(-2, 0),
                new Point(1, 2),
                new Point(-2, -1)
        });
        put(new Utils.Pair<>(3, 2), new Point[]{
                new Point(0, 0),
                new Point(-2, 0),
                new Point(1, 0),
                new Point(-2, 1),
                new Point(1, -2)
        });

    }};
}
