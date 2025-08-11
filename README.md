# Duke_FinalCapstoneProject
Built a Java movie recommender for my Duke (Coursera) capstone. Parsed ~3K films and ~1K user ratings (CSV), modeled raters, and used user-based collaborative filtering (dot product) to generate top-N picks. Integrated a web runner so users rate 10–20 movies and see a styled HTML table of recommendations. Stack: Java, Commons CSV, BlueJ. 

Movie Recommender — Duke (Coursera) Capstone
Personalized movie recommendations using user-based collaborative filtering. Users rate 10–20 movies, then see top picks in a clean HTML table.

Features
Load ~3K movies and ~1K ratings from CSV.

Similarity via normalized dot product; weighted top-N recommendations.

Filter support (e.g., minutes, year, genre).

Web integration through RecommendationRunner (prints a full HTML page).

BlueJ-friendly; runs locally or on the course site.

Project Structure (key classes)
FirstRatings — CSV loaders + exploratory stats.

FourthRatings — averages, similarities, and recommendations.

RecommendationRunner — implements Recommender (getItemsToRate, printRecommendationsFor).

RaterDatabase — static store of raters (uses EfficientRater).

Rating, Rater (interface), EfficientRater (impl).

Filters: Filter (interface), TrueFilter, MinutesFilter, (optional: YearsAfterFilter, etc.).

MovieDatabase (static lookups), Movie (POJO for local tests).

Course site upload: You typically submit only your compiled .class files for
FourthRatings, RecommendationRunner, and any custom filters you used.

Data
Place CSVs in your project folder:

ratedmoviesfull.csv (movies)

ratings.csv (ratings)

Quick Start (BlueJ)
Compile all files.

Right-click RecommendationRunner → void main(String[] args) → OK

It prints an HTML page to the terminal. Copy into preview.html to view, or use the file output variant below.
