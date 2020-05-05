package com.myapps.linkwidget.mainui;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.myapps.linkwidget.model.MFolder;
import com.myapps.linkwidget.R;
import com.myapps.linkwidget.model.MUrl;

public abstract class StorableEditor extends ActivityHandler {
    private StorableListHandler listHandler;
    protected MFolder currentView;

    private StorableEditor(StorableListHandler listHandler, MFolder currentView) {
        super(listHandler.c, R.layout.activity_main_storable_editor);
        this.listHandler = listHandler;
        this.currentView = currentView;
    }

    public abstract void save();
    public abstract boolean validate();

    public String getName() {
        return ((TextView) container.findViewById(R.id.nameEditor)).getText().toString();
    }

    public String getUrl() {
        return ((TextView) container.findViewById(R.id.urlEditor)).getText().toString();
    }
    public void setName(String content) {
        TextView text = container.findViewById(R.id.nameEditor);
        text.setVisibility(View.VISIBLE);
        text.setText(content);
    }
    public void setUrl(String content) {
        TextView url = container.findViewById(R.id.urlEditor);
        url.setVisibility(View.VISIBLE);
        url.setText(content);
    }

    public void setTitle(String s) {
        c.getTitleText().setText(s);
    }

    public void display() {
        super.display();
        container.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    save();
                    backPressed();
                }
            }
        });

        c.setBackButtonVisibile(true);
    }

    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        container.findViewById(R.id.nameEditor).requestFocus();
        showKeyboard();

    }
    public boolean backPressed() {
        c.setActivityHandler(listHandler);
        closeKeyBoard();
        return true;
    }

    public void beginEnterAnimation(final Runnable r) {
        container.setTranslationX(c.getMainContainer().getWidth());
        container.animate()
                .translationX(0)
                .setDuration(ANIM_DUR)
                .setListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) { }
                    public void onAnimationCancel(Animator animation) { }
                    public void onAnimationRepeat(Animator animation) { }
                    public void onAnimationEnd(Animator animation) {
                        r.run();
                    }
                })
                .start();
    }
    public void beginExitAnimation(final Runnable r) {
        container.bringToFront();
        container.animate()
                .translationX(c.getMainContainer().getWidth())
                .setDuration(ANIM_DUR)
                .setListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) { }
                    public void onAnimationCancel(Animator animation) { }
                    public void onAnimationRepeat(Animator animation) { }
                    public void onAnimationEnd(Animator animation) {
                        r.run();
                    }
                })
                .start();
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    private void closeKeyBoard() {
        View dummy = c.getCurrentFocus();

        if (dummy == null) dummy = new View(c);

       ((InputMethodManager) c.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(dummy.getWindowToken(),0);
    }

    private static abstract class UrlBase extends StorableEditor {
        private UrlBase(StorableListHandler listHandler, MFolder currentView, String name, String url, String title) {
            super(listHandler, currentView);
            setName(name);
            setUrl(url);
            setTitle(title);
        }

        public boolean validate() {
            TextInputEditText name = container.findViewById(R.id.nameEditor), url = container.findViewById(R.id.urlEditor);

            boolean error = false;
            if (getName().isEmpty()) {
                name.setError("Don't leave field empty");
                error = true;
            }else{
                name.setError(null);
            }

            if (getUrl().isEmpty()) {
                url.setError("Don't leave field empty");
                error = true;
            }else{
                url.setError(null);
            }

            return error;
        }
    }

    protected static class UrlCreator extends UrlBase {
        protected UrlCreator(StorableListHandler listHandler, MFolder currentView) {
            super(listHandler, currentView, "","","New URL");
        }

        public void save() {
            currentView.addStorable(new MUrl(getName(), getUrl()));
        }
    }

    protected static class UrlEdit extends UrlBase {
        private MUrl edit;
        protected UrlEdit(StorableListHandler listHandler, MUrl edit, MFolder currentView) {
            super(listHandler, currentView,edit.getName(), edit.getUrl(), "Edit URL");
            this.edit = edit;
        }

        public void save() {
            edit.setName(getName());
            edit.setUrl(getUrl());
        }
    }

    private static abstract class FolderBase extends StorableEditor {
        private FolderBase(StorableListHandler listHandler, MFolder currentView, String name, String title) {
            super(listHandler, currentView);
            setName(name);
            setTitle(title);
        }

        public boolean validate() {
            TextView name = container.findViewById(R.id.nameEditor);

            boolean error = false;
            if (getName().isEmpty()) {
                name.setError("Don't leave field empty");
                error = true;
            }else if (currentView.contains(getName())) {
                name.setError("Folder already exists");
                error = true;
            }else{
                name.setError(null);
            }


            return error;
        }
    }

    protected static class FolderCreator extends FolderBase {
        protected FolderCreator(StorableListHandler listHandler, MFolder currentView) {
            super(listHandler, currentView, "", "New Folder");
        }

        public void save() {
            currentView.addStorable(new MFolder(getName()));
        }
    }

    protected static class FolderEdit extends FolderBase {
        private MFolder edit;
        protected FolderEdit(StorableListHandler listHandler, MFolder edit, MFolder currentView) {
            super(listHandler, currentView, edit.getName(), "Edit Folder");
            this.edit = edit;
        }

        public void save() {
            edit.setName(getName());
        }
    }
}
