package ie.atu.sw;

/**
 * Class representing a definition of a word in the dictionary, that is its type and definition text.
 */
public class Definition {
    /**
     * The type of the definition, e.g. noun, verb, etc.
     */
    private String type;
    /**
     * The definition text.
     */
    private String text;

    /**
     * Constructs a Definition object with the given type and text.
     *
     * @param type the type of the definition (noun, verb, etc.)
     * @param text the text of the definition
     */
    public Definition(String type, String text) {
        this.type = type;
        this.text = text;
    }

    /**
     * Returns the type of the definition.
     *
     * @return the type of the definition
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the text of the definition.
     *
     * @return the text of the definition
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the type of the definition.
     *
     * @param type the new type of the definition
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the text of the definition.
     *
     * @param text the new text of the definition
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns a string representation of the Definition object.
     *
     * @return a string representation of the Definition object
     */
    @Override
    public String toString() {
        return "Definition{" +
                "type='" + type + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
