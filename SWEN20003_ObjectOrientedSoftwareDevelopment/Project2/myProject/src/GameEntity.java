import bagel.*;

/**
 * Abstract class that provides a template for all game entities
 * @author Thomas Choi
 * @version 1.0
 */
public abstract class GameEntity {
    protected final static int REFRESH_RATE = 60;
    private Image currentImage;
    // Top left coordinates
    private double x, y;

    /**
     * Method that constructs a GameEntity
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public GameEntity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Method that renders the current image of the game entity
     */
    public void render() {
        currentImage.drawFromTopLeft(x, y);
    }

    // Getters and setters
    /**
     * Method that gets currentImage
     * @return Image This returns currentImage
     */
    public Image getCurrentImage() {
        return currentImage;
    }

    /**
     * Method that sets the currentImage
     * @param image The Image to be set
     */
    public void setCurrentImage(Image image) {
        this.currentImage = image;
    }

    /**
     * Method that gets x
     * @return double This returns x
     */
    public double getX() {
        return x;
    }

    /**
     * Method that gets y
     * @return double This returns y
     */
    public double getY() {
        return y;
    }

    /**
     * Method that sets x
     * @param x The x coordinate to be set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Method that sets y
     * @param y The y coordinate to be set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Method that gets x coordinate of the center
     * @return double This returns the x coordinate of the center
     */
    public double getCenterX() {
        return x + currentImage.getWidth() / 2.0;
    }

    /**
     * Method that gets y coordinate of the center
     * @return double This returns the y coordinate of the center
     */
    public double getCenterY() {
        return y + currentImage.getHeight() / 2.0;
    }
}
