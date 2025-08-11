import java.util.*;

/** Local-only helper. Do NOT upload if the course site already has EfficientRater. */
public class EfficientRater implements Rater {
    private String myID;
    private HashMap<String, Double> myRatings;

    public EfficientRater(String id) {
        myID = id;
        myRatings = new HashMap<>();
    }

    public void addRating(String item, double rating) { myRatings.put(item, rating); }
    public boolean hasRating(String item) { return myRatings.containsKey(item); }
    public String getID() { return myID; }
    public double getRating(String item) { return myRatings.getOrDefault(item, -1.0); }
    public int numRatings() { return myRatings.size(); }
    public ArrayList<String> getItemsRated() { return new ArrayList<>(myRatings.keySet()); }
}
