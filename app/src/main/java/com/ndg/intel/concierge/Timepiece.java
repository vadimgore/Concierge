package com.ndg.intel.concierge;

public class Timepiece {

    enum Shape {
        ROUND,
        SQUARE
    }
    enum Type {
        CHRONOGRAPH,
        ANALOGWATCH
    }
    enum Strap {
        Steel,
        Leather
    }

    enum PriceRange {
        LOW,
        MEDIUM,
        HIGH
    }

    enum Collection {
        None,
        Football,
        Basketball,
        Golf,
        Formula_1,
        Diving
    }

    private Shape mShape;
    private Type mType;
    private Strap mStrap;
    private int mImageId;
    private PriceRange mPrice;
    private Collection mCollection;

    public Timepiece(Collection collection, Type type, Shape shape, Strap strap, PriceRange price, int imageId) {
        mCollection = collection;
        mType = type;
        mShape = shape;
        mStrap = strap;
        mPrice = price;
        mImageId = imageId;
    }

    public Collection getCollection() { return mCollection; }
    public Shape getShape() { return mShape; }
    public Type getType() { return mType; }
    public Strap getStrap() {
        return mStrap;
    }
    public PriceRange getPrice() { return mPrice; }
    public int getImageId() { return mImageId; }

    public boolean match(Timepiece watch) {

        // Two products match if
        // "price range" AND at least two of the following four parameters match: Collection, Shape, Type, Strap
        if (mPrice.ordinal() > watch.getPrice().ordinal()) return false;
        int match = 0;
        if (mCollection == watch.getCollection()) match++;
        if (mType == watch.getType()) match++;
        if (mShape == watch.getShape()) match++;
        if (mStrap == watch.getStrap()) match++;

        return (match>2);
    }

}
