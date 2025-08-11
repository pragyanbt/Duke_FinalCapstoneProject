// File: Rating.java
public class Rating implements Comparable<Rating> {
    private String item;
    private double value;

    public Rating(String item, double value) {
        this.item = item;
        this.value = value;
    }

    public String getItem() { return item; }
    public double getValue() { return value; }

    @Override
    public int compareTo(Rating other) {
        // natural order: ascending by value
        if (this.value < other.value) return -1;
        if (this.value > other.value) return 1;
        return 0;
    }

    @Override
    public String toString() {
        return "[" + item + ", " + value + "]";
    }
}

