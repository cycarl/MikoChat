package com.xana.mikochat.factory.presenter.group;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.xana.mikochat.common.Common;
import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.R;
import com.xana.mikochat.factory.data.DataSource;
import com.xana.mikochat.factory.data.helper.GroupHelper;
import com.xana.mikochat.factory.data.helper.UserHelper;
import com.xana.mikochat.factory.model.api.group.GroupCreateModel;
import com.xana.mikochat.factory.model.card.GroupCard;
import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.model.db.view.UserSimple;
import com.xana.mikochat.factory.net.UploadHelper;
import com.xana.mikochat.factory.presenter.BaseRecyclerPresenter;


import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GroupCreatePresenter extends
        BaseRecyclerPresenter<GroupCreateContract.ViewModel, GroupCreateContract.View>
    implements GroupCreateContract.Presenter,
        DataSource.Callback<GroupCard>{

    public GroupCreatePresenter(GroupCreateContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        Factory.runOnAsync(loader);
    }

    @Override
    public void create(String name, String desc, String picUrl) {
        GroupCreateContract.View view = getView();
        // 判断参数
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(desc)||TextUtils.isEmpty(picUrl)||userIds.size()==0) {
            view.showError(R.string.label_group_create_invalid);
            return;
        }
        view.showLoading();

        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                // 上传图片
                // 请求接口
                // 处理回调
                String url = uploadPic(picUrl);
                GroupCreateModel model = new GroupCreateModel(name, desc, url, userIds);
                GroupHelper.create(model, GroupCreatePresenter.this);
            }
        });
    }

    private String uploadPic(String picUrl) {
        String url = UploadHelper.uploadPortrait(picUrl);
        if(TextUtils.isEmpty(url)) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    if(getView()!=null){
                        getView().showError(R.string.image_upload_error);
                    }
                }
            });
            url = Common.IMAGE;
        }
        return url;
    }


    private Set<String> userIds = new HashSet<>();
    @Override
    public void changeSelect(GroupCreateContract.ViewModel model, boolean isSelected) {
        if(isSelected)
            userIds.add(model.user.getId());
        else
            userIds.remove(model.user.getId());

    }

    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            List<UserSimple> userSimples = UserHelper.getSimpleContacts();
            List<GroupCreateContract.ViewModel> models = new LinkedList<>();
            for (UserSimple userSimple : userSimples) {
                GroupCreateContract.ViewModel model = new GroupCreateContract.ViewModel();
                model.user = userSimple;
                models.add(model);
            }
            refreshData(models);
        }
    };

    @Override
    public void onDataLoaded(GroupCard data) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                if(getView()!=null){
                    getView().onCreateSuccess();
                }
            }
        });
    }

    @Override
    public void onDataNotAvailable(int str) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                if(getView()!=null){
                    getView().showError(str);
                }
            }
        });
    }
}
