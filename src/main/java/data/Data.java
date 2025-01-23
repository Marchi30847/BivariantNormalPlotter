package data;

public enum Data {

    ICON("src/resources/images/appIcon.png"),
    FONT("src/resources/fonts/Montserrat-Bold.ttf");

    private final String path;

    Data(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}