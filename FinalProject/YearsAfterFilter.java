public class YearsAfterFilter implements Filter {
    private int year;
    public YearsAfterFilter(int year) {
        this.year = year;
    }
    public boolean satisfies(String id) {
        return MovieDatabase.getYear(id) >= year;
    }
}

