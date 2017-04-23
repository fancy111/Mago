package com.monster.fancy.debug.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by fancy on 2017/4/20.
 */
public class SearchEditText extends EditText {

    private Drawable mLeft, mTop, mRight, mBottom;
    private Rect mBounds;

    //construct
    public SearchEditText(Context context) {
        super(context);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setDrawable();
        //add text changed listener
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });
    }

    private void setDrawable() {
        if (length() == 0) {
            setCompoundDrawables(mLeft, mTop, null, mBottom);
        } else {
            setCompoundDrawables(mLeft, mTop, mRight, mBottom);
        }
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (mLeft == null) {
            this.mLeft = left;
        }
        if (mTop == null) {
            this.mTop = top;
        }
        if (mRight == null) {
            this.mRight = right;
        }
        if (mBottom == null) {
            this.mBottom = bottom;
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }

    // input event handler
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRight != null && event.getAction() == MotionEvent.ACTION_UP) {
            this.mBounds = mRight.getBounds();
            mRight.getIntrinsicWidth();
            int eventX = (int) event.getX();
            int width = mBounds.width();
            int right = getRight();
            int left = getLeft();
            if (eventX > (right - width * 2 - left)) {
                setText("");
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.mLeft = null;
        this.mTop = null;
        this.mRight = null;
        this.mBottom = null;
        this.mBounds = null;
    }
}
