import java.util.*;

/**
 * Core recommendation engine for the capstone.
 * Depends on: RaterDatabase, MovieDatabase, Filter, Rating, Rater
 */
public class FourthRatings {

    public FourthRatings() { }

    // ------------- AVERAGE RATINGS -------------
    private double getAverageByID(String movieID, int minimalRaters) {
        int count = 0;
        double total = 0.0;

        for (Rater r : RaterDatabase.getRaters()) {
            if (r.hasRating(movieID)) {
                count++;
                total += r.getRating(movieID);
            }
        }
        if (count < minimalRaters) return 0.0;
        return total / count;
    }

    public ArrayList<Rating> getAverageRatings(int minimalRaters) {
        ArrayList<Rating> out = new ArrayList<>();
        for (String id : MovieDatabase.getMovies()) {
            double avg = getAverageByID(id, minimalRaters);
            if (avg > 0.0) {
                out.add(new Rating(id, avg));
            }
        }
        Collections.sort(out); // Rating compares by value ascending
        return out;
    }

    public ArrayList<Rating> getAverageRatingsByFilter(int minimalRaters, Filter filterCriteria) {
        ArrayList<Rating> out = new ArrayList<>();
        for (String id : MovieDatabase.filterBy(filterCriteria)) {
            double avg = getAverageByID(id, minimalRaters);
            if (avg > 0.0) out.add(new Rating(id, avg));
        }
        Collections.sort(out);
        return out;
    }

    // ------------- SIMILARITY (USER-BASED) -------------
    /** Dot product of two raters using ratings normalized by subtracting 5. */
    private double dotProduct(Rater a, Rater b) {
        double sum = 0.0;
        for (String item : a.getItemsRated()) {
            if (b.hasRating(item)) {
                double an = a.getRating(item) - 5.0;
                double bn = b.getRating(item) - 5.0;
                sum += an * bn;
            }
        }
        return sum;
    }

    /**
     * Return similarities with all other raters as (raterID, similarity),
     * sorted descending by similarity. Negative similarities are dropped.
     */
    private ArrayList<Rating> getSimilarities(String raterID) {
        ArrayList<Rating> sims = new ArrayList<>();
        Rater me = RaterDatabase.getRater(raterID);
        if (me == null) return sims;

        for (Rater r : RaterDatabase.getRaters()) {
            if (r.getID().equals(raterID)) continue;
            double sim = dotProduct(me, r);
            if (sim > 0) {
                sims.add(new Rating(r.getID(), sim));
            }
        }

        // Sort descending by value
        Collections.sort(sims, Collections.reverseOrder());
        return sims;
    }

    // ------------- WEIGHTED RECOMMENDATIONS -------------
    public ArrayList<Rating> getSimilarRatings(String raterID, int numSimilarRaters, int minimalRaters) {
        return getSimilarRatingsByFilter(raterID, numSimilarRaters, minimalRaters, new TrueFilter());
    }

    public ArrayList<Rating> getSimilarRatingsByFilter(String raterID, int numSimilarRaters, int minimalRaters, Filter filterCriteria) {
        ArrayList<Rating> similar = getSimilarities(raterID);
        int topN = Math.min(numSimilarRaters, similar.size());

        ArrayList<Rating> recommendations = new ArrayList<>();
        for (String movieID : MovieDatabase.filterBy(filterCriteria)) {
            double weightedSum = 0.0;
            double sumWeights  = 0.0;
            int ratersCounted  = 0;

            for (int k = 0; k < topN; k++) {
                Rating sim = similar.get(k);
                String otherID = sim.getItem();     // stores raterID here
                double weight  = sim.getValue();    // similarity weight

                Rater r = RaterDatabase.getRater(otherID);
                if (r != null && r.hasRating(movieID)) {
                    ratersCounted++;
                    weightedSum += weight * r.getRating(movieID);  // weight × raw rating
                    sumWeights  += Math.abs(weight);
                }
            }

            if (ratersCounted >= minimalRaters && sumWeights > 0) {
                double weightedAvg = weightedSum / sumWeights;
                recommendations.add(new Rating(movieID, weightedAvg));
            }
        }

        // Sort high -> low
        Collections.sort(recommendations, Collections.reverseOrder());
        return recommendations;
    }
}

