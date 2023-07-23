package net.artux.pda.ui.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

public class TypeWriterTextView extends androidx.appcompat.widget.AppCompatTextView {
    private CharSequence mText;
    private int mIndex;
    private boolean effect = true;
    private long mDelay = 20; // in ms default
    private AnimationListener listener;

    public TypeWriterTextView(Context context) {
        super(context);
        setOnClickListener(v -> setEffect(false));
    }

    public TypeWriterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(v -> setEffect(false));
    }

    public void setListener(AnimationListener listener) {
        this.listener = listener;
    }

    public void setmText(CharSequence mText) {
        if (effect)
            animateText(mText);
        else
            super.setText(mText);
    }

    public void setEffect(boolean effect) {
        this.effect = effect;
        if (!this.effect) {
            mHandler.removeCallbacks(characterAdder);
            if (!getText().equals(mText)) {
                setText(mText);
                if (listener != null)
                    listener.onAnimationEnd();
            }
        }
    }

    public void animateText(CharSequence txt) {
        mText = txt;
        mIndex = 0;
        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long m) {
        mDelay = m;
    }

    private final Handler mHandler = new Handler();
    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (mIndex != mText.length() + 1)
                mHandler.postDelayed(characterAdder, mDelay);
            else if (listener != null)
                listener.onAnimationEnd();
        }
    };

    public interface AnimationListener {
        void onAnimationEnd();
    }
}