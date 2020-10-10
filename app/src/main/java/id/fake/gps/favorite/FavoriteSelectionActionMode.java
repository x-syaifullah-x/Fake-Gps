package id.fake.gps.favorite;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.selection.SelectionTracker;

import java.util.ArrayList;

import id.fake.gps.R;
import id.fake.gps.dialog.DialogEditDataFragment;
import id.fake.gps.utils.selection.Details;

import static id.fake.gps.db.local.DatabaseContract.CONTENT_URI_FAVORITE;

public class FavoriteSelectionActionMode implements ActionMode.Callback {

    private ArrayList<FavoriteEntity> databaseEntities;
    private SelectionTracker<Long> selectionTracker;
    private Context context;

    public FavoriteSelectionActionMode(ArrayList<FavoriteEntity> databaseEntities, Context context, SelectionTracker<Long> selectionTracker) {
        this.databaseEntities = databaseEntities;
        this.context = context;
        this.selectionTracker = selectionTracker;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.favorite_menu_action_mode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delete_data_selection:
                StringBuilder message = new StringBuilder();
                for (Object id : selectionTracker.getSelection()) {
                    message.append(databaseEntities.get(Integer.parseInt(id.toString())).getAddress()).append(" ...\n\n");
                }
                new AlertDialog.Builder(this.context)
                        .setTitle("Delete Data Favorite")
                        .setMessage("Are you sure you want to delete ?\n\n" + message)
                        .setCancelable(false)
                        .setPositiveButton("delete", (dialog, which) -> {
                            for (Object id : selectionTracker.getSelection()) {
                                context.getContentResolver().delete(CONTENT_URI_FAVORITE, "id=?", new String[]{String.valueOf(databaseEntities.get(Integer.parseInt(id.toString())).getId())});
                            }
                            actionMode.finish();
                        })
                        .setNegativeButton("cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create().show();
                return true;
            case R.id.select_all:
                if (selectionTracker.getSelection().size() == databaseEntities.size()) {
                    for (int i = 0; i < databaseEntities.size(); i++) {
                        selectionTracker.deselect((long) i);
                    }
                } else {
                    for (int i = 0; i < databaseEntities.size(); i++) {
                        if (!selectionTracker.getSelection().contains((long) i)) {
                            selectionTracker.select((long) i);
                        }
                    }
                }
                return true;
            case R.id.edit_data_selection:
                FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                DialogFragment dialogAddFragment = new DialogEditDataFragment();
                Bundle bundle = new Bundle();

                for (int i = 0; i < databaseEntities.size(); i++) {
                    if (selectionTracker.isSelected((long) i)) {
                        bundle.putParcelable("data", databaseEntities.get(i));
                        dialogAddFragment.setArguments(bundle);
                        dialogAddFragment.show(fragmentTransaction, "test");
                        actionMode.finish();
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        FavoriteActivity.actionMode = null;
        selectionTracker.clearSelection();
        Details.isLongClick = true;
    }
}
