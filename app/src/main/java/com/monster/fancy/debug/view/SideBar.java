package com.monster.fancy.debug.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.monster.fancy.debug.mago.R;

/**
 * Created by fancy on 2017/4/20.
 */
public class SideBar extends View {

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    public static String[] b = {"☆", "#", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    private int choose = -1;// chosen

    private Paint paint = new Paint();

    private TextView mTextDialog;

    public void setTextView(TextView mTextDialog) {

        this.mTextDialog = mTextDialog;

    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

    }

    public SideBar(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    public SideBar(Context context) {

        super(context);

    }

    //rewrite the method
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        // change the background color while get focused

        int height = getHeight();// 获取对应高度

        int width = getWidth(); // 获取对应宽度

        int singleHeight = height / b.length;// 获取每一个字母的高度

        for (int i = 0; i < b.length; i++) {

            if (!isInEditMode()) {
                paint.setColor(Color.parseColor("#838383"));
            }
            paint.setAntiAlias(true);
            paint.setTextSize(20);
            //while chosen
            if (i == choose) {
                paint.setColor(getResources().getColor(R.color.basicColorDark));
                paint.setFakeBoldText(true);
            }

            //x postion equals half of the width of the center character
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();// reset painter
        }
    }

    @SuppressWarnings("deprecation")

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// click y
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        //点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
        final int c = (int) (y / getHeight() * b.length);
        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(b[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(b[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    //opened method
    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {

        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;

    }


    //interface
    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }
}
