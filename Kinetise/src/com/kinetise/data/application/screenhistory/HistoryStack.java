package com.kinetise.data.application.screenhistory;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Stack;

public class HistoryStack implements Serializable{

    private final static int MAX_STACK_SIZE = 10;

    @Expose private Stack<ApplicationState> mApplicationStateStack = new Stack<ApplicationState>();
    

    /**
     * zwraca szczytowy element ze stosu historii i zdejmuje ze stosu
     */
    public ApplicationState pop() {
        ApplicationState state = null;
        if (mApplicationStateStack.size() != 0)
            state = mApplicationStateStack.pop();

        return state;
    }

    /**
     * Zwraca szczytowy element ze stosu histori, bez zdejmowania go ze stosu
     */
    public ApplicationState peek() {
        return mApplicationStateStack.peek();
    }

    /**
     * dodaje element na szczycie stosu historii
     */
    public void push(ApplicationState item) {
        if (getSize() >= MAX_STACK_SIZE) {
            mApplicationStateStack.remove(1);
        }
        if (getSize() > 0) {
            String lastScreenId = mApplicationStateStack.peek().getScreenId();
            if (lastScreenId != null) {
                if (lastScreenId.equals(item.getScreenId())) return;
            }
        }
        mApplicationStateStack.push(item);
    }

    public int getSize() {
        return mApplicationStateStack.size();
    }

    public void clear() {
        mApplicationStateStack.clear();
    }
}
