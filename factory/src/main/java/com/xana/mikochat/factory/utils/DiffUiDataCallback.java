package com.xana.mikochat.factory.utils;

import android.support.v7.util.DiffUtil;

import java.util.List;

public class DiffUiDataCallback<T extends DiffUiDataCallback.UiDataDiffer<T>> extends DiffUtil.Callback {
    private List<T> oldList, newList;

    public DiffUiDataCallback(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    // 两个类是否相同 比如Id相等的User
    @Override
    public boolean areItemsTheSame(int oldItemPos, int newItemPos) {
        T oldItem = oldList.get(oldItemPos);
        T newItem = newList.get(newItemPos);
        return newItem.isSame(oldItem);
    }
    // 在经过相等判断后 进一步判断是否有数据修改
    // 比如 同一个用户的两个不同实例，其中的name字段不同
    @Override
    public boolean areContentsTheSame(int oldItemPos, int newItemPos) {
        T oldItem = oldList.get(oldItemPos);
        T newItem = newList.get(newItemPos);
        return newItem.isUiContentSame(oldItem);
    }

    public interface UiDataDiffer<T>{
        boolean isSame(T old);
        boolean isUiContentSame(T old);
    }
}
