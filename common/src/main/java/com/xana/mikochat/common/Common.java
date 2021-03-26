package com.xana.mikochat.common;

public interface Common {
    /**
     * 配置信息，常量
     */
    interface Constance{
        String REGEX_MOBILE = "^1[34578][0-9]{9}$";
        String API_URL = "http://60.205.204.182:8080/MikochatApi/api/";
    }

    String IMAGE = "https://mikochat.oss-ap-northeast-1.aliyuncs.com/portrait/202103/ekidora_0.jfif";
    // 图片压缩最大尺寸 860KB
    long MAX_UPLOAD_IMAGE_SIZE = 860*1024;

    String IMAGE_URL = "https://mikochat.oss-ap-northeast-1.aliyuncs.com/img/bg/pixiv_%03d.jpg";
}
