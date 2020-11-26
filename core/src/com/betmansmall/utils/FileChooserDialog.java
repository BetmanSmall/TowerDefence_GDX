package com.betmansmall.utils;
import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.widget.VisDialog;

public class FileChooserDialog extends VisDialog {
    public interface ResultListener {
        boolean result(boolean success, FileHandle result);
    }

    private final Skin skin;
    private boolean fileNameEnabled;
    private boolean newFolderEnabled;
    private final TextField fileNameInput;
    private final Label fileNameLabel;
    private final TextButton newFolderButton;
    private final FileHandle baseDir;
    private final Label fileListLabel;
    private final List<SelectedListItem> fileList;

    private FileHandle currentDir;
    protected String result;

    protected ResultListener resultListener;
    private Stage stage;

    private final TextButton ok;
    private final TextButton cancel;
    private boolean directoryBrowsingEnabled = true;

    private static final Comparator<SelectedListItem> dirListComparator = new Comparator<SelectedListItem>() {
        @Override
        public int compare(SelectedListItem file1, SelectedListItem file2) {
            if (file1.file.isDirectory() && !file2.file.isDirectory()) {
                return -1;
            }
            if (file1.file.isDirectory() && file2.file.isDirectory()) {
                return 0;
            }
            if (!file1.file.isDirectory() && !file2.file.isDirectory()) {
                return 0;
            }
            return 1;
        }
    };
    private FileFilter filter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return true;
        }
    };

    public FileChooserDialog(String title, final Skin skin, FileHandle baseDir) {
        super(title);
        this.skin = skin;
        this.baseDir = baseDir;

        final Table content = getContentTable();
        content.top().left();

        fileListLabel = new Label("fileListLabel", skin);
        fileListLabel.setAlignment(Align.left);

        fileList = new List<SelectedListItem>(skin);
        fileList.getSelection().setProgrammaticChangeEvents(false);

        fileNameInput = new TextField("", skin);
        fileNameLabel = new Label("File name:", skin);
        fileNameInput.setTextFieldListener(new TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                result = textField.getText();
            }
        });

        newFolderButton = new TextButton("New Folder", skin);
        newFolderButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (newFolderButton.isChecked()) {
                    newFolderButton.setChecked(false);
                    Gdx.app.log("FileChooser::FileChooser()", "-- new folder");
//                    new NewFileDialog("New Folder", skin) {
//                        @Override
//                        protected void result(Object object) {
//                            final boolean success = (Boolean) object;
//                            if (success) {
//                                final FileHandle newFolder = new FileHandle(currentDir.path() + "/" + getResult());
//                                newFolder.mkdirs();
//                                changeDirectory(currentDir);
//                            }
//                        };
//                    }.show(stage);
                }
            }
        });

        ok = new TextButton("Ok", skin);
        button(ok, true);

        cancel = new TextButton("Cancel", skin);
        button(cancel, false);
        key(Keys.ENTER, true);
        key(Keys.ESCAPE, false);

        fileList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final SelectedListItem selected = fileList.getSelected();
                if (!selected.file.isDirectory()) {
                    result = selected.file.name();
                    fileNameInput.setText(result);
                }
            }
        });
    }

    private void changeDirectory(FileHandle directory) {
        currentDir = directory;
        fileListLabel.setText(currentDir.path());
        final Array<SelectedListItem> items = new Array<SelectedListItem>();
        final FileHandle[] list = directory.list(filter);
        for (final FileHandle handle : list) {
            items.add(new SelectedListItem(handle));
        }
        items.sort(dirListComparator);
        if (directory.file().getParentFile() != null) {
            items.insert(0, new SelectedListItem("..", directory.parent()));
        }
        fileList.setSelected(null);
        fileList.setItems(items);
        fileList.setSelectedIndex(0);
        result = fileList.getSelected().string;
    }

    public FileHandle getResult() {
        String path = currentDir.path() + "/";
        if (result != null && result.length() > 0) {
            path += result;
        }
        return new FileHandle(path);
    }

    @Override
    public VisDialog show(Stage _stage, Action action) {
        this.stage = _stage;
        final Table content = getContentTable();
        content.add(fileListLabel).top().left().expandX().fillX().row();
        content.add(new ScrollPane(fileList, skin)).fill().expand().row(); //.size(300, 150)
//        if (fileNameEnabled) {
            content.add(fileNameLabel).fillX().expandX().row();
            content.add(fileNameInput).fillX().expandX().row();
            stage.setKeyboardFocus(fileNameInput);
//        }
//        if (newFolderEnabled) {
            content.add(newFolderButton).fillX().expandX().row();
//        }
//        if(directoryBrowsingEnabled){
            fileList.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    final SelectedListItem selected = fileList.getSelected();
                    if (selected.file.isDirectory()) {
                        changeDirectory(selected.file);
                    }
                }
            });
//        }
        changeDirectory(baseDir);
        return super.show(stage, action);
    }

//    public static FileChooser createDialog(final FileHandle path) {
//
//    }

    public static FileChooserDialog createSaveDialog(String title, final Skin skin, final FileHandle path) {
        final FileChooserDialog save = new FileChooserDialog(title, skin, path) {
            @Override
            protected void result(Object object) {
                if (resultListener == null) {
                    return;
                }
                final boolean success = (Boolean) object;
                if (!resultListener.result(success, getResult())) {
                    this.cancel();
                }
            }
        }.setFileNameEnabled(true).setNewFolderEnabled(true).setOkButtonText("Save").setCancelButtonText("saveCancel");
        return save;
    }

    public static FileChooserDialog createLoadDialog(String title, final Skin skin, final FileHandle path) {
        final FileChooserDialog load = new FileChooserDialog(title, skin, path) {
            @Override
            protected void result(Object object) {
                if (resultListener == null) {
                    return;
                }
                final boolean success = (Boolean) object;
                resultListener.result(success, getResult());
            }
        }.setNewFolderEnabled(false).setFileNameEnabled(false).setOkButtonText("Load").setCancelButtonText("loadCancel");
        return load;
    }

    public static FileChooserDialog createPickDialog(String title, final Skin skin, final FileHandle path) {
        final FileChooserDialog pick = new FileChooserDialog(title, skin, path) {
            @Override
            protected void result(Object object) {
                if (resultListener == null) {
                    return;
                }
                final boolean success = (Boolean) object;
                resultListener.result(success, getResult());
            }
        }.setOkButtonText("Select").setCancelButtonText("pickCancel");
        return pick;
    }
    public FileChooserDialog setFilter(FileFilter filter) {
        this.filter = filter;
        return this;
    }
    public FileChooserDialog setOkButtonText(String text) {
        this.ok.setText(text);
        return this;
    }
    public FileChooserDialog setCancelButtonText(String text) {
        this.cancel.setText(text);
        return this;
    }
    public FileChooserDialog setFileNameEnabled(boolean fileNameEnabled) {
        this.fileNameEnabled = fileNameEnabled;
        return this;
    }
    public FileChooserDialog setNewFolderEnabled(boolean newFolderEnabled) {
        this.newFolderEnabled = newFolderEnabled;
        return this;
    }
    public FileChooserDialog setResultListener(ResultListener result) {
        this.resultListener = result;
        return this;
    }
    public FileChooserDialog disableDirectoryBrowsing() {
        this.directoryBrowsingEnabled = false;
        return this;
    }
}
