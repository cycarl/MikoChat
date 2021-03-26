package com.xana.mikochat.factory.data.user;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xana.mikochat.factory.data.BaseDbRepository;
import com.xana.mikochat.factory.data.DataSource;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.model.db.User_Table;
import com.xana.mikochat.factory.persistence.Account;
import java.util.List;


public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource{

    @Override
    public void load(DataSource.SucceedCallback<List<User>> callback) {
        super.load(callback);

        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }


}
