package br.com.bb.cerberus.model;

public class Movie {
    private String rank, votes, year, runtimeMin, metascore;
    private String title, genre, description, director, actors;
    private String rating, revenueMillions;

    public String getRank() {
        return rank;
    }

    public String getVotes() {
        return votes;
    }

    public String getYear() {
        return year;
    }

    public String getRuntimeMin() {
        return runtimeMin;
    }

    public String getMetascore() {
        return metascore;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public String getDirector() {
        return director;
    }

    public String getActors() {
        return actors;
    }

    public String getRating() {
        return rating;
    }

    public String getRevenueMillions() {
        return revenueMillions;
    }

    public Movie(String rank,
            String title,
            String genre,
            String description,
            String director,
            String actors,
            String year,
            String runtime,
            String rating,
            String votes,
            String revenue,
            String metascore) {
        this.rank = rank;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.director = director;
        this.actors = actors;
        this.year = year;
        this.runtimeMin = runtime;
        this.metascore = metascore;
        this.rating = rating;
        this.votes = votes;
        this.revenueMillions = revenue;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
                rank,
                title,
                genre,
                description,
                director,
                actors,
                year,
                runtimeMin,
                metascore,
                rating,
                votes,
                revenueMillions);
    }

    public void printMovie() {
        System.out.println(this.toString());
    }

    public boolean isOfGenre(String genre) {
        return this.genre.contains(genre);
    }

    public boolean isHorrorMovie() {
        return this.genre.contains("Horror");
    }
}
