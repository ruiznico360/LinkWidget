package com.myapps.linkwidget.mainui;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.myapps.linkwidget.model.MFolder;
import com.myapps.linkwidget.R;
import com.myapps.linkwidget.model.MStorable;
import com.myapps.linkwidget.model.MUrl;
import com.myapps.linkwidget.serialize.Storage;
import com.myapps.linkwidget.util.Util;

public class StorableListHandler extends ActivityHandler {
    private ListView mainList;
    private MAdapter adapter;
    private MFolder currentView;
    private Button add;
    protected Storage storage;

    public StorableListHandler(MainActivity c, Storage s) {
        super(c, R.layout.activity_main_storables_list);
        this.storage = s;
    }

    public void display() {
        super.display();

        if (mainList == null) {
            currentView = c.getCurrentWidgetFolder();
            generateList();
            setViewFolder(currentView);
        }else{
            updateActivity();
        }

        generateAddButton();
    }

    public void pause() {
        super.pause();
        c.getHeader().removeView(add);
    }

    private void generateList() {
        mainList = container.findViewById(R.id.mainList);
        adapter = new MAdapter();
        mainList.setAdapter(adapter);

        View emptyList = container.findViewById(R.id.empty);
        ((TextView)container.findViewById(R.id.text)).setText(Html.fromHtml("You have no links, Click the <b>" + c.getResources().getString(R.string.add_button) + "</b> button to add some."));
        mainList.setEmptyView(emptyList);
    }

    private void generateAddButton() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)c.getResources().getDimension(R.dimen.main_topbar_button),(int)c.getResources().getDimension(R.dimen.main_topbar_button));
        params.addRule(RelativeLayout.ALIGN_PARENT_END);

        add = new Button(c);
        add.setText(c.getResources().getString(R.string.add_button));
        c.getHeader().addView(add,params);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButtonPopup();
            }
        });
    }

    private void addButtonPopup() {
        final PopupMenu popup = new PopupMenu(c, add);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.acitivty_main_add_popup, popup.getMenu());

        popup.getMenu().findItem(R.id.folder).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                c.setActivityHandler(new StorableEditor.FolderCreator(StorableListHandler.this, currentView));
                return true;
            }
        });
        popup.getMenu().findItem(R.id.url).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                c.setActivityHandler(new StorableEditor.UrlCreator(StorableListHandler.this, currentView));
                return true;
            }
        });
        popup.show();
    }

    public boolean backPressed() {
        if (isCurrentBase()) {
            return false;
        }else{
            currentView = (currentView.getParent());
            updateActivity();
            return true;
        }
    }

    private boolean isCurrentBase() {
        return storage.getBase().equals(currentView);
    }

    public void updateActivity() {
        c.update();

        setViewFolder(currentView);
        adapter.update();
    }

    public void beginEnterAnimation(final Runnable end) {
        beginExitAnimation(end);
    }
    public void beginExitAnimation(final Runnable end) {
        final RelativeLayout ly = new RelativeLayout(c);
        ly.setBackgroundColor(Color.argb(10,0,0,0));
        container.addView(ly, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                end.run();
                container.removeView(ly);
            }
        }, ANIM_DUR);
    }

    public void setViewFolder(MFolder f) {
        c.getTitleText().setText(f.getName());

        if (isCurrentBase()) {
            c.setBackButtonVisibile(false);
        }else{
            c.setBackButtonVisibile(true);
        }
    }

    private class MAdapter extends BaseAdapter {
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.activity_main_storable, null, false);
            }

            TextView name = convertView.findViewById(R.id.name);
            TextView url = convertView.findViewById(R.id.url);
            ImageView webLogo = convertView.findViewById(R.id.weblogo);

            name.setText(currentView.getStorable(position).getName());

            final MStorable curr = currentView.getStorable(position);
            setupPopup(convertView, position);

            if (curr instanceof MFolder) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentView = ((MFolder)curr);
                        updateActivity();
                    }
                });
                url.setVisibility(View.GONE);
                webLogo.setImageResource(R.drawable.folder);
            }else{
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.launchUrl(MUrl.parse(((MUrl)curr).getUrl()), c);
                    }
                });
                url.setText(((MUrl)curr).getUrl());
                url.setVisibility(View.VISIBLE);
                webLogo.setImageResource(R.drawable.internet);
            }

            return convertView;
        }

        private void setupPopup(View convertView, final int position) {
            final MStorable curr = currentView.getStorable(position);
            final View menuDots = convertView.findViewById(R.id.menu_dots);

            menuDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popup = new PopupMenu(c, menuDots);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.activity_main_storable_menu_popup, popup.getMenu());

                    popup.getMenu().findItem(R.id.edit).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (curr instanceof MFolder) {
                                c.setActivityHandler(new StorableEditor.FolderEdit(StorableListHandler.this,(MFolder) curr, currentView));
                            }else{
                                c.setActivityHandler(new StorableEditor.UrlEdit(StorableListHandler.this,(MUrl) curr, currentView));
                            }
                            return false;
                        }
                    });

                    popup.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final int index = currentView.indexOf(curr);
                            curr.delete();
                            updateActivity();
                            popup.getMenu().close();

                            c.undoSnackbar = Snackbar.make(c.findViewById(R.id.snackbar), "Deleted", Snackbar.LENGTH_LONG);
                            c.undoSnackbar.setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    currentView.addStorable(curr, index);
                                    updateActivity();
                                }
                            });
                            c.undoSnackbar.show();

                            return false;
                        }
                    });

                    popup.getMenu().findItem(R.id.moveUp).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int newIndex = Math.max(0, position - 1);
                            currentView.removeStorable(curr);
                            currentView.addStorable(curr, newIndex);
                            updateActivity();
                            popup.getMenu().close();
                            return false;
                        }
                    });

                    popup.getMenu().findItem(R.id.moveDown).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            int newIndex = Math.min(currentView.getSize() - 1, position + 1);
                            currentView.removeStorable(curr);
                            currentView.addStorable(curr, newIndex);
                            popup.getMenu().close();
                            updateActivity();
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }

        public MStorable getItem(int position) { return currentView.getStorable(position); }
        public long getItemId(int position) { return position; }
        public int getCount() { return currentView.getSize(); }
        public void update() { notifyDataSetChanged(); }
    }
}
