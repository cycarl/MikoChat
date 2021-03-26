package com.xana.mikochat.app.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.xana.mikochat.app.R;
import com.xana.mikochat.app.frags.search.SearchGroupFragment;
import com.xana.mikochat.app.frags.search.SearchUserFragment;
import com.xana.mikochat.common.app.Fragment;
import com.xana.mikochat.common.app.ToolbarActivity;

import org.w3c.dom.Text;

public class SearchActivity extends ToolbarActivity {
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final int TYPE_USER = 1; // 搜索人
    public static final int TYPE_GROUP = 2; // 搜索群
    //具体需要显示的类型
    private int type;

    private SearchFragment mSearchFragment;

    public static void show(Context context, int type){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }
    @Override
    protected boolean initArgs(Bundle bundle) {
        type = bundle.getInt(EXTRA_TYPE);
        return type==TYPE_USER||type==TYPE_GROUP;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 显示对应Fragment;
        Fragment fragment = type==TYPE_USER? new SearchUserFragment()
                : new SearchGroupFragment();
        mSearchFragment = (SearchFragment) fragment;

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 初始化菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if(searchView!=null){
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            // 添加搜索监听
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // 当点击了提交按钮的时候
                    search(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    // 当文件改变时候，不会即使搜索，只在为null的情况下进行搜索
                    if(TextUtils.isEmpty(s)){
                        search("");
                        return true;
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 搜索。。
     * @param query
     */
    private void search(String query) {
        if(mSearchFragment!=null) {
            mSearchFragment.search(query);
        }
    }

    /* 搜索的Fragment必须实现的接口 */
    public interface SearchFragment{
        void search(String query);
    }
}