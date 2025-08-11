
import java.util.*;

public interface Rater {
    /** Add or replace the rating for a movie id. */
    public void addRating(String item, double rating);

    /** True if this rater has a rating recorded for the movie id. */
    public boolean hasRating(String item);

    /** The rater’s unique ID (string). */
    public String getID();

    /** Get the rating for a movie id, or -1.0 if not rated. */
    public double getRating(String item);

    /** Total number of ratings this rater has made. */
    public int numRatings();

    /** List of all movie ids this rater has rated. */
    public ArrayList<String> getItemsRated();
}

