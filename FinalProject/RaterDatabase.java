import edu.duke.*;
import java.util.*;

import org.apache.commons.csv.*;

/**
 * Static database of Rater objects, keyed by rater_id.
 * CSV header expected: rater_id,movie_id,rating,time
 *
 * NOTE: Uses EfficientRater (course class) which implements the Rater interface.
 * Do NOT upload this to the course site unless your instructor asked you to;
 * the site already has RaterDatabase. It's fine for local BlueJ testing.
 */
public class RaterDatabase {
    private static HashMap<String, Rater> ourRaters = new HashMap<>();
    private static String currentSource = "";

    /** Clear everything (handy for testing). */
    public static void reset() {
        ourRaters.clear();
        currentSource = "";
    }

    /** Initialize from a CSV the first time (or when filename changes). */
    public static void initialize(String filename) {
        if (filename == null || filename.length() == 0) filename = "ratings.csv";
        if (!filename.equals(currentSource) || ourRaters.isEmpty()) {
            loadRatings(filename);
        }
    }

    /** Add ratings from a CSV to the database (does not clear existing). */
    public static void addRatings(String filename) {
        loadRatings(filename);
    }

    /** Get a single Rater by id (or null if not present). */
    public static Rater getRater(String id) {
        return ourRaters.get(id);
    }

    /** All raters as an ArrayList. */
    public static ArrayList<Rater> getRaters() {
        return new ArrayList<>(ourRaters.values());
    }

    /** Number of raters in the DB. */
    public static int size() {
        return ourRaters.size();
    }

    // ---- helpers ----
    private static void loadRatings(String filename) {
        currentSource = filename;
        FileResource fr = new FileResource(filename);
        CSVParser parser = fr.getCSVParser();

        for (CSVRecord rec : parser) {
            String rid = rec.get("rater_id").trim();
            String mid = rec.get("movie_id").trim();
            double rating = parseDoubleSafe(rec.get("rating"));
            addRaterRating(rid, mid, rating);
        }
    }

    private static void addRaterRating(String raterID, String movieID, double rating) {
        Rater r = ourRaters.get(raterID);
        if (r == null) {
            // ⬇⬇⬇ the important line: use EfficientRater, not Rater
            r = new EfficientRater(raterID);
            ourRaters.put(raterID, r);
        }
        r.addRating(movieID, rating);
    }

    private static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0.0; }
    }
}
