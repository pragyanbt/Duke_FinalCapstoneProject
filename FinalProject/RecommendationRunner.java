import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class RecommendationRunner implements Recommender {

    @Override
    public ArrayList<String> getItemsToRate() {
        MovieDatabase.initialize("ratedmoviesfull.csv");  // safe to call repeatedly
        ArrayList<String> pool = MovieDatabase.filterBy(new MinutesFilter(85, 180));
        if (pool == null || pool.size() < 15) {
            pool = MovieDatabase.filterBy(new TrueFilter()); // fallback
        }
        Collections.shuffle(pool, new Random());
        int n = Math.min(15, pool.size());
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < n; i++) items.add(pool.get(i));
        return items;
    }

    @Override
    public void printRecommendationsFor(String webRaterID) {
        MovieDatabase.initialize("ratedmoviesfull.csv");
        RaterDatabase.initialize("ratings.csv");

        FourthRatings fr = new FourthRatings();
        int numSimilarRaters = 20;
        int minimalRaters   = 5;

        ArrayList<Rating> recs = fr.getSimilarRatings(webRaterID, numSimilarRaters, minimalRaters);

        // Hide movies the user already rated
        Rater me = RaterDatabase.getRater(webRaterID);
        ArrayList<Rating> filtered = new ArrayList<>();
        for (Rating r : recs) {
            if (me == null || !me.hasRating(r.getItem())) filtered.add(r);
        }

        // Gentle fallback to get *something*
        if (filtered.isEmpty()) {
            recs = fr.getSimilarRatings(webRaterID, 50, 3);
            for (Rating r : recs) {
                if (me == null || !me.hasRating(r.getItem())) filtered.add(r);
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("<h2>No recommendations yet</h2>"
              + "<p>Try rating a few more movies and refresh the page.</p>");
            return;
        }

        int limit = Math.min(15, filtered.size());

        StringBuilder sb = new StringBuilder();
        sb.append("<style>")
          .append("body{font-family:Arial,Helvetica,sans-serif;margin:24px;background:#0b1020;color:#eef1f7}")
          .append("h1{font-size:24px;margin:0 0 8px}")
          .append("p.sub{opacity:.8;margin:0 0 16px}")
          .append("table{border-collapse:collapse;width:100%}")
          .append("th,td{padding:12px 10px;border-bottom:1px solid rgba(255,255,255,.08);vertical-align:top}")
          .append("th{position:sticky;top:0;background:#0b1020;text-align:left;font-size:12px;letter-spacing:.06em;text-transform:uppercase;opacity:.8}")
          .append("tr:hover{background:rgba(255,255,255,.04)}")
          .append(".poster{width:70px;height:105px;object-fit:cover;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,.25)}")
          .append(".title{font-weight:700;font-size:16px;margin-bottom:4px}")
          .append(".meta{opacity:.8;font-size:13px}")
          .append(".badge{display:inline-block;padding:2px 8px;border:1px solid rgba(255,255,255,.2);border-radius:999px;font-size:12px;margin-right:6px;opacity:.9}")
          .append("@media (max-width:640px){td:nth-child(1){display:none}}")
          .append("</style>");

        sb.append("<h1>Recommended for You</h1>")
          .append("<p class='sub'>Based on your ratings and similar users.</p>")
          .append("<table>")
          .append("<thead><tr><th>#</th><th>Movie</th><th>Details</th><th>Score</th></tr></thead><tbody>");

        for (int i = 0; i < limit; i++) {
            Rating r = filtered.get(i);
            String id        = r.getItem();
            String poster    = MovieDatabase.getPoster(id);
            String year      = "" + MovieDatabase.getYear(id);
            String mins      = "" + MovieDatabase.getMinutes(id) + " min";
            String title     = MovieDatabase.getTitle(id);
            String genres    = MovieDatabase.getGenres(id);
            String directors = MovieDatabase.getDirector(id);
            String country   = MovieDatabase.getCountry(id);

            String imgTag = (poster != null && poster.length() > 0 && !"N/A".equals(poster))
              ? "<img class='poster' src='" + poster + "' alt='poster'/>"
              : "<div class='poster' style='background:#1a2240;display:flex;align-items:center;justify-content:center;"
                + "border:1px solid rgba(255,255,255,.1)'>N/A</div>";

            sb.append("<tr>");
            sb.append("<td>").append(i + 1).append("</td>");
            sb.append("<td style='display:flex;gap:12px;align-items:flex-start'>")
              .append(imgTag)
              .append("<div>")
              .append("<div class='title'>").append(escape(title)).append(" (").append(escape(year)).append(")</div>")
              .append("<div class='meta'>").append(escape(genres)).append("</div>")
              .append("</div></td>");
            sb.append("<td>")
              .append("<span class='badge'>").append(escape(mins)).append("</span>")
              .append("<span class='badge'>").append(escape(country)).append("</span>")
              .append("<div class='meta'>Directed by ").append(escape(directors)).append("</div>")
              .append("</td>");
            sb.append("<td>").append(String.format(Locale.US, "%.2f", r.getValue())).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");

        System.out.println(sb.toString());
    }

    // Local test harness: choose a valid rater and print HTML to console.
    public static void main(String[] args) {
        MovieDatabase.initialize("ratedmoviesfull.csv");
        RaterDatabase.initialize("ratings.csv");

        // Prefer an argument if provided; otherwise pick the most-active rater.
        String raterID = (args != null && args.length > 0) ? args[0] : null;
        if (raterID == null || RaterDatabase.getRater(raterID) == null) {
            String best = null; int max = -1;
            for (Rater r : RaterDatabase.getRaters()) {
                int n = r.numRatings();
                if (n > max) { max = n; best = r.getID(); }
            }
            raterID = best;
        }

        RecommendationRunner rr = new RecommendationRunner();
        // (Optional) see which items we’d display to rate:
        // System.out.println(rr.getItemsToRate());

        rr.printRecommendationsFor(raterID);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
