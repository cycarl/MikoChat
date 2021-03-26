package com.xana.mikochat.factory.presenter.contact;

import android.support.v7.util.DiffUtil;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.factory.data.helper.UserHelper;
import com.xana.mikochat.factory.data.user.ContactDataSource;
import com.xana.mikochat.factory.data.user.ContactRepository;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.presenter.BaseSourcePresenter;
import com.xana.mikochat.factory.utils.DiffUiDataCallback;
import java.util.List;

public class ContactPresenter extends BaseSourcePresenter<User, User, ContactDataSource, ContactContract.View>
        implements ContactContract.Presenter{

    public ContactPresenter(ContactContract.View view) {
        super(new ContactRepository(), view);
    }

    @Override
    public void start() {
        super.start();
        // 先加载本地联系人数据，在网络请求刷新数据
        UserHelper.refreshContacts();

        // TODO 问题:
        // 1.关注后虽然保存数据库，但是没有刷新联系人
        // 2.如果刷新数据库，或者从网络刷新，最终刷新的结果是全局刷新
        // 3.本地刷新和网络刷新，在添加到界面的时候会有可能冲突；导致数据显示异常异常
        // 4.如何识别已经在数据库中有这样的数据了？做到精准刷新...

    }

    // 运行到这里的时候是子线程
    @Override
    public void onDataLoaded(List<User> users) {
        //
        ContactContract.View view = getView();
        if(view!=null){
            RecyclerAdapter<User> adapter = view.getRecyclerAdapter();
            List<User> oldList = adapter.getItemList();
            // 进行数据对比
            DiffUtil.Callback callback = new DiffUiDataCallback<User>(oldList, users);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            // 调用基类方法进行基类刷新
            refreshData(result, users);
        }
    }
}
