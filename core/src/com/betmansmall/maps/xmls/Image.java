package com.betmansmall.maps.xmls;

import javax.xml.bind.annotation.XmlAttribute;

public class Image {
    private String width;
    private String height;
    private String source;

    @XmlAttribute
    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    @XmlAttribute
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @XmlAttribute
    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

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
