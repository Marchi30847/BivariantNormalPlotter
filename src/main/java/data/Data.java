package data;

public enum Data {

    ICON("java/resources/images/appIcon.png"),
    FONT("java/resources/fonts/Montserrat-Bold.ttf");

    private final String path;

    Data(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}