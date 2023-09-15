package com.eternalcode.plots.good.role;

public record Role(String name) {

    public static final Role NONE = new Role("__NONE__");

}
