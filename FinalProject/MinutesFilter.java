public class MinutesFilter implements Filter {
    private int min, max;
    public MinutesFilter(int min, int max) { this.min = min; this.max = max; }
    public boolean satisfies(String id) {
        int m = MovieDatabase.getMinutes(id);
        return m >= min && m <= max;
    }
}

