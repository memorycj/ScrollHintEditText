package wn.scrollhint;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * author : wn
 * e-mail : memory_cjj@163.com
 * time   : 2017-10-19
 */


public class ScrollHintEditText extends FrameLayout implements ViewSwitcher.ViewFactory {

    private TextSwitchLayout mScrHintLayout;
    private EditText mEdtTxv;
    private boolean autoStartAnim;

    private int mHintTxvColor;
    private float mHintTxvSize;
    private static final int DEFAULT_HINT_TXV_SIZE = 15;
    private static final int DEFAULT_HINT_TXV_COLOR = Color.GRAY;
    private static final int DEFT_HINT_TXV_SCROLL_DELAYED = 4000;
    private int mHintScollDelayed;
    private String mHintDefaultText;
    private int mEdtBackground;

    public ScrollHintEditText(@NonNull Context context) {
        this(context, null);
    }

    public ScrollHintEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollHintEditText(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollHintEditText);
        int animIn = typedArray.getResourceId(R.styleable.ScrollHintEditText_animIn, 0);
        int animOut = typedArray.getResourceId(R.styleable.ScrollHintEditText_animOut, 0);
        int editLedtDrawable = typedArray.getResourceId(R.styleable.ScrollHintEditText_editLeftDrawable, 0);
        autoStartAnim = typedArray.getBoolean(R.styleable.ScrollHintEditText_autoStartAnim, false);
        mHintTxvColor = typedArray.getInt(R.styleable.ScrollHintEditText_hintTxvColor, DEFAULT_HINT_TXV_COLOR);
        mHintTxvSize = typedArray.getDimensionPixelSize(R.styleable.ScrollHintEditText_hintTxvSize, DEFAULT_HINT_TXV_SIZE);
        mHintScollDelayed = typedArray.getColor(R.styleable.ScrollHintEditText_hintScrollDelayed, DEFT_HINT_TXV_SCROLL_DELAYED);
        mHintDefaultText = typedArray.getString(R.styleable.ScrollHintEditText_hintDefaultText);
        mEdtBackground = typedArray.getResourceId(R.styleable.ScrollHintEditText_editBackground, 0);
        typedArray.recycle();
        inflateView();
        if (animIn > 0) {
            mScrHintLayout.setInAnimation(AnimationUtils.loadAnimation(context, animIn));
        }
        if (animOut > 0) {
            mScrHintLayout.setOutAnimation(AnimationUtils.loadAnimation(context, animOut));
        }
        setEdtLeftDrawable(editLedtDrawable);

        mEdtTxv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEdtTxv.getEditableText().length() > 0 && mEdtTxv.getVisibility() == VISIBLE) {
                    mScrHintLayout.setVisibility(INVISIBLE);
                    mScrHintLayout.stop();
                } else {
                    if (mScrHintLayout.getVisibility() == INVISIBLE) {
                        mScrHintLayout.setVisibility(VISIBLE);
                        mScrHintLayout.start();
                    }
                }
            }
        });
    }


    public void setEdtLeftDrawable(int editLedtDrawable) {

        if (editLedtDrawable > 0) {
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = getResources().getDrawable(editLedtDrawable, getContext().getTheme());
            } else {
                drawable = getResources().getDrawable(editLedtDrawable);
            }
            mEdtTxv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
        refreshHintPaddingLeft();

    }

    private void refreshHintPaddingLeft() {
        int pdLeftt = mEdtTxv.getCompoundDrawables().length > 0 && mEdtTxv.getCompoundDrawables()[0] != null ? mEdtTxv.getCompoundDrawables()[0].getIntrinsicWidth() + mEdtTxv.getPaddingLeft() : mEdtTxv.getPaddingLeft();
        mScrHintLayout.setPadding(pdLeftt, 0, 0, 0);
    }

    private void inflateView() {
        mEdtTxv = new EditText(getContext());
        if (mEdtBackground <= 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mEdtTxv.setBackground(null);
            } else {
                mEdtTxv.setBackgroundDrawable(null);
            }
        else {
            mEdtTxv.setBackgroundResource(mEdtBackground);
        }
        addView(mEdtTxv);
        mScrHintLayout = new TextSwitchLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        mScrHintLayout.setLayoutParams(layoutParams);
        addView(mScrHintLayout);
        mScrHintLayout.setScrollDelayed(mHintScollDelayed);
        mScrHintLayout.setFactory(this);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (autoStartAnim && !mScrHintLayout.isRun()) {
            mScrHintLayout.start();
        }
    }

    @Override
    public View makeView() {
        TextView tv = new TextView(getContext());
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setMaxLines(1);
        tv.setTextColor(mHintTxvColor);
        tv.setText(TextUtils.isEmpty(mHintDefaultText) && !autoStartAnim ? "" : mHintDefaultText);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHintTxvSize);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        tv.setLayoutParams(params);
        return tv;
    }

    public String getText() {
        String content = "";
        if (mEdtTxv.getEditableText().length() > 0) {
            content = mEdtTxv.getText().toString();
        } else {
            content = ((EditText) mScrHintLayout.getCurrentView()).getText().toString();
        }
        return content;
    }


}
