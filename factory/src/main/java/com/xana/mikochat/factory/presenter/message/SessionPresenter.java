package com.xana.mikochat.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import com.xana.mikochat.factory.data.message.SessionDataSource;
import com.xana.mikochat.factory.data.message.SessionRepository;
import com.xana.mikochat.factory.model.db.Session;
import com.xana.mikochat.factory.presenter.BaseRecyclerPresenter;
import com.xana.mikochat.factory.presenter.BaseSourcePresenter;
import com.xana.mikochat.factory.utils.DiffUiDataCallback;

import java.util.List;

public class SessionPresenter extends
        BaseSourcePresenter<Session, Session, SessionDataSource, SessionContract.View>
        implements SessionContract.Presenter{

    public SessionPresenter(SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view = getView();
        if(view==null) return;
        List<Session> olds = view.getRecyclerAdapter().getItemList();

        DiffUiDataCallback<Session> callback = new DiffUiDataCallback<>(olds, sessions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        refreshData(result, sessions);


    }
}
