package com.tjbaobao.ledhelper.utils;

import com.tjbaobao.framework.util.ConstantUtil;
import com.tjbaobao.framework.util.FileUtil;

/**
 * Created by TJbaobao on 2017/12/4.
 */

public class AppConstantUtil extends ConstantUtil {

    public static String getAppConfigPath()
    {
        String path = getFilePath()+"config/";
        FileUtil.createFolder(path);
        return path ;
    }
}
