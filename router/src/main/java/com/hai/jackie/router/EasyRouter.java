package com.hai.jackie.router;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EasyRouter {

    private HashMap<String, String> mRouterMap;
    private Context mContext;
    private Bundle mBundle;
    private boolean mIsUseAnno = true;
    private static final String TAG = "EasyRouter";
    private static final String PAGE_NAME_NOT_AVAIRABLE = "This pageName is not available!";

    private static class EasyRouterHolder {
        private static EasyRouter sRouter = new EasyRouter();
    }

    public static EasyRouter getInstance() {
        return EasyRouterHolder.sRouter;
    }

    public void inject(Application application) {
        mRouterMap = new HashMap<>();
        if (mIsUseAnno) {
            getRouterMapFromAnno(application);
        }

    }

    public EasyRouter with(@NonNull Context context) {
        this.mContext = context;
        return this;
    }

    public void navigate(@NonNull String packageName) {
        if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(mRouterMap.get(packageName))) {
            Intent intent = new Intent();
            intent.setClassName(mContext.getPackageName(), mRouterMap.get(packageName));
            if (mBundle != null) {
                intent.putExtras(mBundle);
            }
            mContext.startActivity(intent);
        } else {
            Logger.e(TAG, PAGE_NAME_NOT_AVAIRABLE, StackTraceUtil.getStackTrace(), "pageName: " + packageName);
        }
    }

    /**
     * 编译时扫描所有packageName,然后返回路由表
     *
     * @param application
     */
    public void getRouterMapFromAnno(Application application) {
        List<String> moduleList = getModuleList(application);
        if (moduleList == null || moduleList.size() == 0) {
            return;
        }
        try {
            for (String moduleName : moduleList) {
                Class clazz = Class.forName(moduleName + ".factory.RouterFactory");
                Method method = clazz.getMethod("init");
                method.invoke(null);
                mRouterMap.putAll((HashMap<String, String>) clazz.getField("sHashMap").get(clazz));
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }


    }


    private List<String> getModuleList(Application application) {
        List<String> moduleList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = application.getAssets();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("module_info")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("modules");
            for (int i = 0; i < jsonArray.length(); i++) {
                moduleList.add(((JSONObject) jsonArray.get(i)).getString("packageName"));
            }
            return moduleList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public EasyRouter putString(String key, String value) {
        Log.i(TAG, "putString: ");
        if (mBundle == null) {
            mBundle = new Bundle();
            mBundle.putString(key, value);
        } else {
            mBundle.putString(key, value);
        }
        return this;
    }


}




