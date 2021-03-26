package com.xana.mikochat.factory.presenter.group;

import android.support.v7.util.DiffUtil;

import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.factory.data.BaseDbRepository;
import com.xana.mikochat.factory.data.group.GroupsRepository;
import com.xana.mikochat.factory.data.helper.GroupHelper;
import com.xana.mikochat.factory.data.helper.UserHelper;
import com.xana.mikochat.factory.data.user.ContactDataSource;
import com.xana.mikochat.factory.data.user.ContactRepository;
import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.presenter.BaseSourcePresenter;
import com.xana.mikochat.factory.presenter.contact.ContactContract;
import com.xana.mikochat.factory.utils.DiffUiDataCallback;

import java.util.List;

public class GroupPresenter extends BaseSourcePresenter<Group, Group, GroupsRepository, GroupContract.View>
        implements GroupContract.Presenter{

    public GroupPresenter(GroupContract.View view) {
        super(new GroupsRepository(), view);
    }

    @Override
    public void start() {
        super.start();
        // 先加载本地数据，在网络请求刷新数据
        GroupHelper.refreshGroups();
    }

    // 运行到这里的时候是子线程
    @Override
    public void onDataLoaded(List<Group> groups) {
        //
        GroupContract.View view = getView();
        if(view!=null){
            RecyclerAdapter<Group> adapter = view.getRecyclerAdapter();
            List<Group> oldList = adapter.getItemList();
            // 进行数据对比
            DiffUtil.Callback callback = new DiffUiDataCallback<Group>(oldList, groups);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            // 调用基类方法进行基类刷新
            refreshData(result, groups);
        }
    }
}
