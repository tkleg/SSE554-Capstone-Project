package org.troy.capstone.constants;

public enum URL {
    UNSPLASH_ATTRIBUTION("https://unsplash.com/?utm_source=sse554_capstone&utm_medium=referral"),
    DEFAULT_AUTHOR_URL("https://unsplash.com/@kalenemsley?utm_source=sse554_capstone&utm_medium=referral"),
    DEFAULT_AUTHOR_NAME("Kalen Emsley"),
    DEFAULT_IMAGE_URL("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w4NzQxNTN8MHwxfHNlYXJjaHwxfHxtb3VudGFpbnN8ZW58MHx8fHwxNzcxMDM1NzQwfDA&ixlib=rb-4.1.0&q=80&w=1080?utm_source=sse554_capstone&utm_medium=referral");

    private final String url;

    URL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
