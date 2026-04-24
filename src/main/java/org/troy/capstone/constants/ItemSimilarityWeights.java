package org.troy.capstone.constants;

/**
 * The {@code ItemSimilarityWeights} enum defines the weights used for calculating item similarity based on different attributes. Each constant represents a specific attribute and its associated weight, which indicates the importance of that attribute in the similarity calculation.
 */
public enum ItemSimilarityWeights {
    /** The weight for the name attribute when calculating item similarity. */
    NAME(0.9f),
    /** The weight for the publisher attribute when calculating item similarity. */
    PUBLISHER(0.4f),
    /** The weight for the description attribute when calculating item similarity. */
    DESCRIPTION(0.8f),
    /** The weight for the category attribute when calculating item similarity. */
    CATEGORY(0.6f),
    /** The weight for the tags attribute when calculating item similarity. */
    TAGS(0.7f),
    /** The weight for the photo author attribute when calculating item similarity. */
    PHOTO_AUTHOR(0.3f),
    /** The weight for the price attribute when calculating item similarity. */
    PRICE(0.3f),
    /** The weight for the review score attribute when calculating item similarity. */
    REVIEW_SCORE(0.3f),
    /** The weight for the date added attribute when calculating item similarity. */
    DATE_ADDED(0.1f);

    /** The weight associated with the attribute for similarity calculation. */
    private final float weight;

    /** Constructor for the {@code ItemSimilarityWeights} enum.
     * @param weight The weight to associate with the attribute for similarity calculation.
     */
    ItemSimilarityWeights(float weight) {
        this.weight = weight;
    }

    /** Retrieves the weight associated with the attribute for similarity calculation.
     * @return The weight of the attribute for similarity calculation.
     */
    public float getWeight() {
        return weight;
    }

}
