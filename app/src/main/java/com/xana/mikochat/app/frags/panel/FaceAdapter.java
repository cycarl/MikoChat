package com.xana.mikochat.app.frags.panel;

import android.view.View;

import com.xana.mikochat.app.R;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.face.Face;

import java.util.List;

public class FaceAdapter extends RecyclerAdapter<Face.Bean> {

    public FaceAdapter(List<Face.Bean> beans, AdapterListener<Face.Bean> listener) {
        super(beans, listener);
    }

    @Override
    protected int getItemViewType(int position, Face.Bean bean) {
        return R.layout.cell_face;
    }

    @Override
    protected ViewHolder<Face.Bean> onCreateViewHolder(View root, int viewType) {
        return new FaceHolder(root);
    }
}
