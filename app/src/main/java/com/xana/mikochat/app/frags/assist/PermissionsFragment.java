package com.xana.mikochat.app.frags.assist;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xana.mikochat.app.LaunchActivity;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.frags.media.GalleryFragment;
import com.xana.mikochat.app.helper.PermissionCallback;
import com.xana.mikochat.common.app.Application;
import com.xana.mikochat.common.app.Fragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 权限申请
 */
public class PermissionsFragment extends BottomSheetDialogFragment
implements EasyPermissions.PermissionCallbacks, View.OnClickListener{


    private static final String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    private static final int RC = 0X0100;

    private static PermissionCallback mCallback;


    public PermissionsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_premissions, container, false);
        root.findViewById(R.id.btn_submit).setOnClickListener(this);
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        refreshState(getView());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new GalleryFragment.TransStatusBarBottomSheetDialog(getContext());
    }

    /**
     * 刷新图片状态
     * @param root
     */
    private void refreshState(View root){
        if (root==null) return;
        Context context = getContext();
        root.findViewById(R.id.im_state_permission_network)
                .setVisibility(haveNetwork(context)?View.VISIBLE:View.GONE);
        root.findViewById(R.id.im_state_permission_read)
                .setVisibility(haveRead(context)?View.VISIBLE:View.GONE);
        root.findViewById(R.id.im_state_permission_write)
                .setVisibility(haveWrite(context)?View.VISIBLE:View.GONE);
        root.findViewById(R.id.im_state_permission_record_audio)
                .setVisibility(haveRecordAudio(context)?View.VISIBLE:View.GONE);

    }

    /**
     * 判断是否拥有所需权限
     * @param context
     * @return
     */
    private static boolean haveNetwork(Context context){
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }
    private static boolean haveRead(Context context){
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }
    private static boolean haveWrite(Context context){
        String[] perms = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }
    private static boolean haveRecordAudio(Context context){
        String[] perms = new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static void show(FragmentManager manager){
        new PermissionsFragment()
                .show(manager, PermissionsFragment.class.getName());
    }

    /**
     * 检查所有权限
     * @param context
     * @param manager
     * @return
     */

    public static boolean haveAll(Context context, FragmentManager manager){
        mCallback = (PermissionCallback) context;
        boolean flag = haveNetwork(context)
                &&haveRead(context)
                &&haveWrite(context)
                &&haveRecordAudio(context);
        if(!flag)
            show(manager);
        return flag;
    }

    /**
     * 授权按钮点击
     * @param view
     */
    @Override
    public void onClick(View view) {
        requestPrem();
    }

    @AfterPermissionGranted(RC)
    private void requestPrem(){
        if(EasyPermissions.hasPermissions(getContext(), PERMISSIONS)){
            Application.showToast(R.string.label_permission_ok);
            refreshState(getView());
            dismiss();
            mCallback.onSuccess();
        }else {
            EasyPermissions.requestPermissions(this, getString(R.string.title_assist_permissions), RC, PERMISSIONS);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            /* 如果授权失败 打开设置界面 */
            new AppSettingsDialog
                    .Builder(this)
                    .build()
                    .show();
        }
    }

    /**
     * 权限申请的时候的回调，把权限申请交给EasyPermissions处理
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}