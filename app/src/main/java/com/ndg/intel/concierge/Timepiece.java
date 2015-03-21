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
        STEEL,
        LEATHER
    }

    enum PriceRange {
        LOW,
        LOW_MEDIUM,
        MEDIUM,
        MEDIUM_HIGH,
        HIGH
    }

    enum Collection {
        NONE,
        CARRERA,
        AQUARACER,
        MONACO,
        FORMULA1
    }

    enum Gender {
        MALE,
        FEMALE
    }

    private Gender mGender;
    private Shape mShape;
    private Type mType;
    private Strap mStrap;
    private int mImageId;
    private PriceRange mPrice;
    private Collection mCollection;

    public Timepiece(Collection collection, Gender gender, Type type, Shape shape, Strap strap,
                     PriceRange price, int imageId) {
        mCollection = collection;
        mGender = gender;
        mType = type;
        mShape = shape;
        mStrap = strap;
        mPrice = price;
        mImageId = imageId;
    }

    public Collection getCollection() { return mCollection; }
    public Gender getGender() { return mGender; }
    public Shape getShape() { return mShape; }
    public Type getType() { return mType; }
    public Strap getStrap() {
        return mStrap;
    }
    public PriceRange getPrice() { return mPrice; }
    public int getImageId() { return mImageId; }

    public boolean match(Timepiece watch) {

        // Two products match if
        // "gender" AND at least three of the following five parameters match:
        // Collection, Price, Shape, Type, Strap
        if (mGender != watch.getGender()) return false;

        int match = 0;
        if (mPrice.ordinal() < watch.getPrice().ordinal()) match++;
        if (mCollection == watch.getCollection()) match++;
        if (mType == watch.getType()) match++;
        if (mShape == watch.getShape()) match++;
        if (mStrap == watch.getStrap()) match++;

        return (match > 3);
    }

}
