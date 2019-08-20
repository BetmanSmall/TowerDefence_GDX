package com.betmansmall.util;

import com.badlogic.gdx.files.FileHandle;

public class FileListItem {
    String string;
    FileHandle file;

    public FileListItem(FileHandle fileHandle) {
        file = fileHandle;
    }

    public FileListItem(String string, FileHandle fileHandle) {
        this.string = string;
        this.file = fileHandle;
    }

    @Override
    public String toString() {
        return file.path();
    }
}
