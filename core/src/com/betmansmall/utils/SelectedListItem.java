package com.betmansmall.utils;

import com.badlogic.gdx.files.FileHandle;

public class SelectedListItem {
    public String string;
    public FileHandle file;

    public SelectedListItem(FileHandle fileHandle) {
        string = fileHandle.name();
        file = fileHandle;
    }

    public SelectedListItem(String string, FileHandle fileHandle) {
        this.string = string;
        this.file = fileHandle;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SelectedListItem{");
        sb.append("string='").append(string).append('\'');
        sb.append(", file=").append(file);
        sb.append('}');
        return sb.toString();
    }
}
