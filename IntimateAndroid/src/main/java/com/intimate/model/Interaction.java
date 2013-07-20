package com.intimate.model;

/** Created by yurii_laguta on 20/07/13. */
public class Interaction {
    public static final int TYPE_IMAGE = 0;

    private int type = -1;

    public Interaction(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
