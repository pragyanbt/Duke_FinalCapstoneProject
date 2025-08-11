
public class Movie {
    private String id;
    private String title;
    private int year;
    private String country;
    private String genres;
    private String director;
    private int minutes;
    private String poster;

    public Movie(String id, String title, int year, String country, String genres,
                 String director, int minutes, String poster) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.country = country;
        this.genres = genres;
        this.director = director;
        this.minutes = minutes;
        this.poster = poster;
    }

    public String getID()       { return id; }
    public String getTitle()    { return title; }
    public int    getYear()     { return year; }
    public String getCountry()  { return country; }
    public String getGenres()   { return genres; }
    public String getDirector() { return director; }
    public int    getMinutes()  { return minutes; }
    public String getPoster()   { return poster; }
}

