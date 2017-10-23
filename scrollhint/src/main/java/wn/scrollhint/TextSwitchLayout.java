package wn.scrollhint;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

/**
 * author : wn
 * e-mail : memory_cjj@163.com
 * time   : 2017-07-01
 */

public class TextSwitchLayout extends TextSwitcher implements Runnable {
    private int mIndex = -1;
    private Runnable mRunnable;
    private List<String> mDatas = new ArrayList<>();
    Handler handler = new Handler();
    private long mCrtDelayed;

    public TextSwitchLayout(Context context) {
        this(context, null);

    }

    public TextSwitchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        for (int i = 0; i < 5; i++) {
            mDatas.add("data " + i);
        }

    }


    public void start() {
        if (!isRun()) {
            mRunnable = this;
            handler.post(mRunnable);
        }
    }


    public void stop() {
        if (isRun()) {
            handler.removeCallbacks(mRunnable);
            mRunnable = null;
        }
    }


    @Override
    public void run() {
        if (mRunnable == null)
            return;
        mIndex = next();
        setText(mDatas.get(mIndex));
        handler.postDelayed(mRunnable, mCrtDelayed);
    }

    private int next() {
        int flag = ++mIndex ;
        if (flag > mDatas.size() - 1) {
            flag -= mDatas.size();
        }
        return flag;
    }


    public boolean isRun() {
        return mRunnable != null;
    }

    public void setScrollDelayed(long delayed) {
        mCrtDelayed =delayed;
    }


}