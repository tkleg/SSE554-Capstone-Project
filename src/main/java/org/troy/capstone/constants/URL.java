package org.troy.capstone.constants;

/**
 * Enum for commonly used URLs in the application, such as attribution links and default image URLs. This helps avoid hardcoding strings throughout the codebase and reduces risk of typos.
 */
public enum URL {
    /** URL for Unsplash attribution, used to give credit to the image source. */
    UNSPLASH_ATTRIBUTION("https://unsplash.com/?utm_source=sse554_capstone&utm_medium=referral"),
    /** URL for the page of the author of the default image, used for attribution purposes. */
    DEFAULT_AUTHOR_URL("https://unsplash.com/@kalenemsley?utm_source=sse554_capstone&utm_medium=referral"),
    /** Name of the author of the default image, used for attribution purposes. */
    DEFAULT_AUTHOR_NAME("Kalen Emsley"),
    /** URL for the default image used when an item does not have its own image, sourced from Unsplash and chosen to be a generic mountain landscape that fits well with the theme of the application. */
    DEFAULT_IMAGE_URL("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w4NzQxNTN8MHwxfHNlYXJjaHwxfHxtb3VudGFpbnN8ZW58MHx8fHwxNzcxMDM1NzQwfDA&ixlib=rb-4.1.0&q=80&w=1080?utm_source=sse554_capstone&utm_medium=referral");

    /** The string value associated with this enum constant, used for URLs and attribution information. */
    private final String url;

    /** Constructs a {@code URL} enum constant with the specified string value.
     * @param url The string value to associate with this enum constant, typically used for URLs and attribution information.
    */
    URL(String url) {
        this.url = url;
    }

    /** Getter for the string value associated with this enum constant.
     * @return The string value that can be used for URLs and attribution information.
    */
    public String getUrl() {
        return url;
    }

}
