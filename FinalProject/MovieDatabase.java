
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Minimal standalone MovieDatabase for BlueJ/local testing.
 * Place ratedmoviesfull.csv in the project folder.
 *
 * Expected columns (header, case-insensitive):
 * id,title,year,country,genres,director,minutes,poster
 * (If your CSV uses "genre" instead of "genres", that's handled too.)
 */
public class MovieDatabase {
    private static HashMap<String, Movie> ourMovies = new HashMap<>();
    private static String currentSource = "";

    /** Load (or reload) movies from a CSV file the first time this is called
     *  for that file path. Safe to call multiple times. */
    public static void initialize(String moviefile) {
        if (moviefile == null) moviefile = "ratedmoviesfull.csv";
        if (!moviefile.equals(currentSource) || ourMovies.isEmpty()) {
            loadMovies(moviefile);
        }
    }

    public static int size() {
        return ourMovies.size();
    }

    public static boolean containsID(String id) {
        return ourMovies.containsKey(id);
    }

    public static ArrayList<String> getMovies() {
        return new ArrayList<>(ourMovies.keySet());
    }

    // ---- Field getters (course-compatible) ----
    public static String getTitle(String id)    { Movie m = ourMovies.get(id); return (m==null)?"":m.getTitle(); }
    public static int    getYear(String id)     { Movie m = ourMovies.get(id); return (m==null)?0:m.getYear(); }
    public static String getCountry(String id)  { Movie m = ourMovies.get(id); return (m==null)?"":m.getCountry(); }
    public static String getGenres(String id)   { Movie m = ourMovies.get(id); return (m==null)?"":m.getGenres(); }
    public static String getDirector(String id) { Movie m = ourMovies.get(id); return (m==null)?"":m.getDirector(); }
    public static int    getMinutes(String id)  { Movie m = ourMovies.get(id); return (m==null)?0:m.getMinutes(); }
    public static String getPoster(String id)   { Movie m = ourMovies.get(id); return (m==null)?"":m.getPoster(); }

    /** Return IDs whose movie rows satisfy the given filter */
    public static ArrayList<String> filterBy(Filter f) {
        ArrayList<String> out = new ArrayList<>();
        for (String id : ourMovies.keySet()) {
            if (f.satisfies(id)) out.add(id);
        }
        return out;
    }

    // ---- CSV loading ----
    private static void loadMovies(String filename) {
        ourMovies.clear();
        currentSource = filename;
        Path p = Paths.get(filename);

        if (!Files.exists(p)) {
            // Try project-relative path as fallback (BlueJ sometimes runs from a different CWD)
            p = Paths.get(System.getProperty("user.dir"), filename);
            if (!Files.exists(p)) {
                throw new RuntimeException("CSV not found: " + filename + " (tried " + p.toString() + ")");
            }
        }

        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String header = br.readLine();
            if (header == null) return;

            String[] cols = splitCsvLine(header).toArray(new String[0]);
            HashMap<String,Integer> idx = new HashMap<>();
            for (int i = 0; i < cols.length; i++) idx.put(cols[i].trim().toLowerCase(), i);

            // Helper to read fields by name (case-insensitive)
            java.util.function.Function<String,Integer> pos = name -> idx.getOrDefault(name.toLowerCase(), -1);

            int iId       = pos.apply("id");
            int iTitle    = pos.apply("title");
            int iYear     = pos.apply("year");
            int iCountry  = pos.apply("country");
            int iGenres   = pos.apply("genres");
            if (iGenres < 0) iGenres = pos.apply("genre"); // tolerate "genre"
            int iDirector = pos.apply("director");
            int iMinutes  = pos.apply("minutes");
            int iPoster   = pos.apply("poster");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                ArrayList<String> parts = splitCsvLine(line);

                String id      = get(parts, iId);
                if (id.isEmpty()) continue;

                String title   = get(parts, iTitle);
                int year       = parseIntSafe(get(parts, iYear), 0);
                String country = get(parts, iCountry);
                String genres  = get(parts, iGenres);
                String director= get(parts, iDirector);
                int minutes    = parseIntSafe(get(parts, iMinutes), 0);
                String poster  = get(parts, iPoster);

                Movie m = new Movie(id, title, year, country, genres, director, minutes, poster);
                ourMovies.put(id, m);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error reading " + filename + ": " + ex.getMessage(), ex);
        }
    }

    // Robust CSV splitter: handles quotes, commas, and escaped quotes ("")
    private static ArrayList<String> splitCsvLine(String line) {
        ArrayList<String> out = new ArrayList<>();
        if (line == null) return out;

        // Strip UTF-8 BOM if present
        if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
            line = line.substring(1);
        }

        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote ("")
                    cur.append('"');
                    i++; // skip next
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        // Trim outer quotes and whitespace
        for (int i = 0; i < out.size(); i++) {
            String s = out.get(i).trim();
            if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
                s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
            }
            out.set(i, s);
        }
        return out;
    }

    private static String get(ArrayList<String> parts, int idx) {
        if (idx < 0 || idx >= parts.size()) return "";
        return parts.get(idx).trim();
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return def; }
    }
}

