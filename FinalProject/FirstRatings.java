import edu.duke.*;
import java.util.*;
import org.apache.commons.csv.*;
import java.util.Date;

/**
 * Step 1 helper: load movies and raters, then answer the required questions.
 *
 * Files expected in your BlueJ project folder:
 *   - ratedmovies_short.csv / ratedmoviesfull.csv
 *   - ratings_short.csv / ratings.csv
 *
 * Uses Apache Commons CSV via edu.duke.FileResource.
 */
public class FirstRatings {

    // ---------- MOVIES ----------
    /** Return all Movie objects from the given CSV file. */
    public ArrayList<Movie> loadMovies(String filename) {
        ArrayList<Movie> movies = new ArrayList<>();
        FileResource fr = new FileResource(filename);
        CSVParser parser = fr.getCSVParser();

        for (CSVRecord rec : parser) {
            // Tolerate either "genre" or "genres" in header
            String id       = rec.get("id").trim();
            String title    = rec.get("title").trim();
            int year        = parseIntSafe(rec.get("year"));
            String country  = getOrEmpty(rec, "country");
            String genres   = getOrEmpty(rec, hasHeader(rec, "genres") ? "genres" : "genre");
            String director = getOrEmpty(rec, "director");
            int minutes     = parseIntSafe(getOrEmpty(rec, "minutes"));
            String poster   = getOrEmpty(rec, "poster");

            Movie m = new Movie(id, title, year, country, genres, director, minutes, poster);
            movies.add(m);
        }
        return movies;
    }

    /** Do all the required movie prints/calculations for Step 1. */
    public void testLoadMovies() {
        // 1) Load short set
        String file = "ratedmovies_short.csv";
        ArrayList<Movie> movies = loadMovies(file);

        // Print number of movies + each movie (then you can comment out the per-movie prints)
        System.out.println("Movies in " + file + ": " + movies.size());
        for (Movie m : movies) {
            System.out.println(m); // requires Movie.toString(); or replace with a custom print (see Movie below)
        }

        // 2) How many include Comedy?
        int numComedy = 0;
        for (Movie m : movies) {
            if (m.getGenres().toLowerCase().contains("comedy")) numComedy++;
        }
        System.out.println("Comedy movies: " + numComedy);

        // 3) How many > 150 minutes?
        int numLong = 0;
        for (Movie m : movies) {
            if (m.getMinutes() > 150) numLong++;
        }
        System.out.println("> 150 minutes: " + numLong);

        // 4) Maximum number of movies by any director & who they are
        // Some movies have multiple directors separated by commas
        HashMap<String,Integer> dirCount = new HashMap<>();
        for (Movie m : movies) {
            String[] dirs = m.getDirector().split(",");
            for (String d : dirs) {
                String name = d.trim();
                if (name.isEmpty()) continue;
                dirCount.put(name, dirCount.getOrDefault(name, 0) + 1);
            }
        }
        int maxMovies = 0;
        for (int c : dirCount.values()) {
            if (c > maxMovies) maxMovies = c;
        }
        ArrayList<String> topDirs = new ArrayList<>();
        for (Map.Entry<String,Integer> e : dirCount.entrySet()) {
            if (e.getValue() == maxMovies) topDirs.add(e.getKey());
        }
        System.out.println("Max movies by any director: " + maxMovies);
        System.out.println("Director(s) with that many: " + topDirs);

        // If you want to test the full set, uncomment:
        // ArrayList<Movie> allMovies = loadMovies("ratedmoviesfull.csv");
        // System.out.println("Movies in ratedmoviesfull.csv: " + allMovies.size()); // should be 3143
    }

    // ---------- RATERS ----------
    /**
     * Read ratings CSV and build one Rater per unique rater_id, each with all their ratings.
     * Returns an ArrayList<Rater>.
     */
    public ArrayList<Rater> loadRaters(String filename) {
        // rater_id,movie_id,rating,time
        HashMap<String, Rater> map = new HashMap<>();
        FileResource fr = new FileResource(filename);
        CSVParser parser = fr.getCSVParser();

        for (CSVRecord rec : parser) {
            String rid = rec.get("rater_id").trim();
            String mid = rec.get("movie_id").trim();
            double rating = parseDoubleSafe(rec.get("rating"));

            Rater r = map.get(rid);
            if (r == null) {
                r = new Rater(rid);
                map.put(rid, r);
            }
            r.addRating(mid, rating);
        }
        return new ArrayList<>(map.values());
    }

    /** Do all the required rater prints/calculations for Step 1. */
    public void testLoadRaters() {
        // 1) Load short set
        String file = "ratings_short.csv";
        ArrayList<Rater> raters = loadRaters(file);

        // Print total raters
        System.out.println("Total raters in " + file + ": " + raters.size());

        // Print each rater: ID, count, then each (movie, rating)
        for (Rater r : raters) {
            System.out.println("Rater " + r.getID() + " has " + r.numRatings() + " rating(s):");
            for (String item : r.getItemsRated()) {
                System.out.println("  " + item + " -> " + r.getRating(item));
            }
        }

        // 2) Number of ratings for a specific rater
        String targetRater = "2";
        int ratingsForTarget = 0;
        for (Rater r : raters) {
            if (r.getID().equals(targetRater)) {
                ratingsForTarget = r.numRatings();
                break;
            }
        }
        System.out.println("Rater " + targetRater + " has " + ratingsForTarget + " rating(s).");

        // 3) Max number of ratings by any rater; who has that max
        int maxRatings = 0;
        for (Rater r : raters) {
            if (r.numRatings() > maxRatings) maxRatings = r.numRatings();
        }
        ArrayList<String> withMax = new ArrayList<>();
        for (Rater r : raters) {
            if (r.numRatings() == maxRatings) withMax.add(r.getID());
        }
        System.out.println("Max ratings by any rater: " + maxRatings);
        System.out.println("Rater(s) with that many: " + withMax);

        // 4) Number of ratings a particular movie has
        String targetMovie = "1798709";
        int numRatingsForMovie = 0;
        for (Rater r : raters) {
            if (r.hasRating(targetMovie)) numRatingsForMovie++;
        }
        System.out.println("Movie " + targetMovie + " was rated by " + numRatingsForMovie + " rater(s).");

        // 5) Number of different movies rated by all raters
        HashSet<String> uniqueMovies = new HashSet<>();
        for (Date r : raters) {
            uniqueMovies.addAll(r.getItemsRated());
        }
        System.out.println("Different movies rated: " + uniqueMovies.size());

        // If you want to test the full set, uncomment:
        // ArrayList<Rater> allRaters = loadRaters("ratings.csv");
        // System.out.println("Total raters (ratings.csv): " + allRaters.size()); // should be 1048
    }

    // ---------- helpers ----------
    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }
    private double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0.0; }
    }
    private String getOrEmpty(CSVRecord rec, String col) {
        try { return rec.get(col).trim(); }
        catch (Exception e) { return ""; }
    }
    private boolean hasHeader(CSVRecord rec, String col) {
        try {
            rec.get(col);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
