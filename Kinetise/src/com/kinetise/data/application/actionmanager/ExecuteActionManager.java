package com.kinetise.data.application.actionmanager;

import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.helpers.asynccaller.AsyncCaller;
import com.kinetise.helpers.threading.AGAsyncTask;
import com.kinetise.helpers.threading.ThreadPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for executing all applications action(functions)
 */
public class ExecuteActionManager {
    public static int actionsCount = 1;
    public static final Object FUNCTION_SYNCHRONIZER = new Object();
    private static List<AGAsyncTask> mDelayedActions = new ArrayList<>();

    public static Object executeMultiAction(final MultiActionDataDesc multiActionDataDesc) {
        Object result = null;
        for (ActionDataDesc actionDataDesc : multiActionDataDesc.getActions()) {
            result = executeAction(actionDataDesc);
        }
        return result;
    }

    public static void executeMultiActionDelayed(final MultiActionDataDesc multiActionDataDesc, final long delay) {
        actionsCount++;
        AGAsyncTask r = new AGAsyncTask() {
            @Override
            public void run() {
                if (mIsCanceled)
                    return;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                AsyncCaller.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsCanceled) {
                            return;
                        }
                        actionsCount--;
                        if(actionsCount<0) actionsCount=0;
                        executeMultiAction(multiActionDataDesc);
                    }
                });
            }
        };
        mDelayedActions.add(r);
        ThreadPool.getInstance().execute(r);
    }

    /**
     * Obtains proper functions based on action descriptor and calls their execute method
     *
     * @param actionDataDesc Action descriptor
     */
    public static Object executeAction(ActionDataDesc actionDataDesc) {
        if (actionDataDesc == null)
            return null;

        Object result = null;

        for (AbstractFunctionDataDesc funcDataDesc : actionDataDesc.getFunctions()) {
            IFunctionCommand command = funcDataDesc.getFunction();
            result = command.execute(result);
        }

        return result;
    }

    /**
     * Cancel all delayed actions
     */
    public static void cancelDelayedActions() {
        actionsCount=0;
        for (AGAsyncTask r : mDelayedActions) {
            r.cancel();
        }
        mDelayedActions.clear();
    }
}