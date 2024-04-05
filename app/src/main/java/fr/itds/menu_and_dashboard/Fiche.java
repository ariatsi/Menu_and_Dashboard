package fr.itds.menu_and_dashboard;

public class Fiche {
    private String id;
    private String userId;
    private String date;
    private String title;
    private String description;

    // Constructeur
    public Fiche(String id, String userId, String date, String title, String description) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.title = title;
        this.description = description;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
