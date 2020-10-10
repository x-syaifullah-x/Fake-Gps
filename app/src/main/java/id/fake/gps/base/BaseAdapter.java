package id.fake.gps.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class BaseAdapter<Model, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected int listLayout;
    private ArrayList<Model> models;

    public abstract void bind(VH vh, Model model, int position);

    public BaseAdapter(int listLayout, ArrayList<Model> models) {
        this.listLayout = listLayout;
        this.models = models;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        bind(holder, this.models.get(position), position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        ArrayList<Model> arrayList = this.models;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public ArrayList<Model> getModels() {
        return this.models;
    }

    public void setModels(ArrayList<Model> models) {
        this.models = models;
        notifyDataSetChanged();
    }
}