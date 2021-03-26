package com.xana.mikochat.factory.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.utils.HashUtil;

import java.io.File;
import java.util.Date;

/**
 * 上传工具类, 用于上传各种文件到阿里OSS
 */
public class UploadHelper {
    private static final String TAG = UploadHelper.class.getSimpleName();
    public static final String ENDPOINT = "http://oss-ap-northeast-1.aliyuncs.com";
    private static final String BUCKET_NAME = "mikochat";

    private static OSS getClient() {
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                "LTAI4G8Un1oZUbanTvdHk8tU", "LbzSy1Yq05KGBBEsf6FQAljMHrA7nf");
        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }

    /**
     * 上传的最终方法，成功返回则一个路径
     * @param objKey 上传上去后，在服务器上的独立的KEY
     * @param path   需要上传的文件的路径
     * @return 存储的地址
     */
    private static String upload(String objKey, String path){
        // 构造一个上传请求
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME,
                objKey, path);

        try {
            // 初始化上传的Client
            OSS client = getClient();
            // 开始同步上传
            PutObjectResult result = client.putObject(request);
            // 得到一个外网可访问的地址
            String url = client.presignPublicObjectURL(BUCKET_NAME, objKey);
            // 格式打印输出
            Log.d(TAG, String.format("PublicObjectURL:%s", url));
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            // 如果有异常则返回空
            return null;
        }
    }
    public static String uploadImage(String path){
        String key = getImageObjKey(path);
        return upload(key, path);
    }
    public static String uploadPortrait(String path){
        String key = getPortraitObjKey(path);
        return upload(key, path);
    }
    public static String uploadAudio(String path){
        String key = getAudioObjKey(path);
        return upload(key, path);
    }

    /**
     * 分月存储
     * @return
     */
    private static String getDateString(){
        return DateFormat.format("yyyyMM", new Date()).toString();
    }
    private static String getImageObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("image/%s/%s.jpg", dateString, fileMd5);
    }
    private static String getPortraitObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }
    private static String getAudioObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3", dateString, fileMd5);
    }

}
