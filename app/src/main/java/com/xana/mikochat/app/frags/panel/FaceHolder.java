package com.xana.mikochat.app.frags.panel;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.xana.mikochat.app.R;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.face.Face;

import butterknife.BindView;

public class FaceHolder extends RecyclerAdapter.ViewHolder<Face.Bean> {
    @BindView(R.id.im_face)
    ImageView mFace;

    public FaceHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(Face.Bean bean) {
        if(bean != null &&
                // drawable 资源
                ((bean.preview instanceof Integer)||
                        // zip包资源
                  (bean.preview instanceof String))){
            Glide.with(mFace.getContext())
                    .load(bean.preview)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888) // 设置编码，保证清晰度
                    .into(mFace);
        }

    }
}
