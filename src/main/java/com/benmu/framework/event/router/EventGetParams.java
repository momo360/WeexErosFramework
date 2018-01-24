package com.benmu.framework.event.router;

import android.content.Context;

import com.benmu.framework.extend.module.RouterModule;
import com.benmu.framework.manager.ManagerFactory;
import com.benmu.framework.manager.impl.RouterManager;
import com.benmu.framework.model.RouterModel;
import com.taobao.weex.bridge.JSCallback;

/**
 * Created by Carry on 2017/8/23.
 */

public class EventGetParams {
    public void getParams(Context context, JSCallback jscallback) {
        RouterManager routerManager = ManagerFactory.getManagerService(RouterManager.class);
        RouterModel routerModel = routerManager.getParams(context);
        if (jscallback != null) {
            jscallback.invoke(routerModel == null ? "" : routerModel.params);
        }
    }
}
