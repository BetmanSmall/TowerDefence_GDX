package com.betmansmall.maps.xmls;

public class Image {
    public String width;
    public String height;
    public String source;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Image{");
        sb.append("width='").append(width).append('\'');
        sb.append(", height='").append(height).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
