//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sc.tmp_cw.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import com.dalong.marqueeview.R.styleable;

public class MarqueeView extends SurfaceView implements SurfaceHolder.Callback {
    public Context mContext;
    private float mTextSize;
    private int mTextColor;
    private boolean mIsRepeat;
    private int mStartPoint;
    private int mDirection;
    private int mSpeed;
    private SurfaceHolder holder;
    private TextPaint mTextPaint;
    private MarqueeViewThread mThread;
    private String margueeString;
    private int textWidth;
    private int textHeight;
    private int ShadowColor;
    public int currentX;
    public int sepX;
    public static final int ROLL_OVER = 100;
    Handler mHandler;
    OnMargueeListener mOnMargueeListener;

    public MarqueeView(Context context) {
        this(context, (AttributeSet)null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTextSize = 100.0F;
        this.mTextColor = -65536;
        this.textWidth = 0;
        this.textHeight = 0;
        this.ShadowColor = -16777216;
        this.currentX = 0;
        this.sepX = 5;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 100:
                        MarqueeView.this.stopScroll();
                        if (MarqueeView.this.mOnMargueeListener != null) {
                            MarqueeView.this.mOnMargueeListener.onRollOver();
                        }
                    default:
                }
            }
        };
        this.mContext = context;
        this.init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = this.getContext().obtainStyledAttributes(attrs, styleable.MarqueeView, defStyleAttr, 0);
        this.mTextColor = a.getColor(styleable.MarqueeView_textcolor, -65536);
        this.mTextSize = a.getDimension(styleable.MarqueeView_textSize, 48.0F);
        this.mIsRepeat = a.getBoolean(styleable.MarqueeView_isRepeat, false);
        this.mStartPoint = a.getInt(styleable.MarqueeView_startPoint, 0);
        this.mDirection = a.getInt(styleable.MarqueeView_direction, 0);
        this.mSpeed = a.getInt(styleable.MarqueeView_speed, 20);
        a.recycle();
        this.holder = this.getHolder();
        this.holder.addCallback(this);
        this.mTextPaint = new TextPaint();
        this.mTextPaint.setFlags(1);
        this.mTextPaint.setTextAlign(Align.LEFT);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(-3);
    }

    public void setText(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            this.measurementsText(msg);
        }

    }

    protected void measurementsText(String msg) {
        this.margueeString = msg;
        this.mTextPaint.setTextSize(this.mTextSize);
        this.mTextPaint.setColor(this.mTextColor);
        this.mTextPaint.setStrokeWidth(0.5F);
        this.mTextPaint.setFakeBoldText(true);
        this.textWidth = (int)this.mTextPaint.measureText(this.margueeString);
        Paint.FontMetrics fontMetrics = this.mTextPaint.getFontMetrics();
        this.textHeight = (int)fontMetrics.bottom;
        WindowManager wm = (WindowManager)this.mContext.getSystemService("window");
        int width = wm.getDefaultDisplay().getWidth();
        if (this.mStartPoint == 0) {
            this.currentX = 0;
        } else {
            this.currentX = width - this.getPaddingLeft() - this.getPaddingRight();
        }

    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (this.mThread != null) {
            this.mThread.isRun = true;
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.mThread != null) {
            this.mThread.isRun = false;
        }

    }

    public void startScroll() {
        if (this.mThread == null || !this.mThread.isRun) {
            this.mThread = new MarqueeViewThread(this.holder);
            this.mThread.start();
        }
    }

    public void stopScroll() {
        if (this.mThread != null) {
            this.mThread.isRun = false;
            this.mThread.interrupt();
        }

        this.mThread = null;
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    public void reset() {
        int contentWidth = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
        if (this.mStartPoint == 0) {
            this.currentX = 0;
        } else {
            this.currentX = contentWidth;
        }

    }

    public void setOnMargueeListener(OnMargueeListener mOnMargueeListener) {
        this.mOnMargueeListener = mOnMargueeListener;
    }

    public interface OnMargueeListener {
        void onRollOver();
    }

    class MarqueeViewThread extends Thread {
        private SurfaceHolder holder;
        public boolean isRun;

        public MarqueeViewThread(SurfaceHolder holder) {
            this.holder = holder;
            this.isRun = true;
        }

        public void onDraw() {
            try {
                synchronized(this.holder) {
                    if (TextUtils.isEmpty(MarqueeView.this.margueeString)) {
                        Thread.sleep(1000L);
                        return;
                    }

                    Canvas canvas = this.holder.lockCanvas();
                    int paddingLeft = MarqueeView.this.getPaddingLeft();
                    int paddingTop = MarqueeView.this.getPaddingTop();
                    int paddingRight = MarqueeView.this.getPaddingRight();
                    int paddingBottom = MarqueeView.this.getPaddingBottom();
                    int contentWidth = MarqueeView.this.getWidth() - paddingLeft - paddingRight;
                    int contentHeight = MarqueeView.this.getHeight() - paddingTop - paddingBottom;
                    int centeYLine = paddingTop + contentHeight / 2;
                    MarqueeView var10000;
                    if (MarqueeView.this.mDirection == 0) {
                        if (MarqueeView.this.currentX <= -MarqueeView.this.textWidth) {
                            if (!MarqueeView.this.mIsRepeat) {
                                MarqueeView.this.mHandler.sendEmptyMessage(100);
                            }

                            MarqueeView.this.currentX = contentWidth;
                        } else {
                            var10000 = MarqueeView.this;
                            var10000.currentX -= MarqueeView.this.sepX;
                        }
                    } else if (MarqueeView.this.currentX >= contentWidth) {
                        if (!MarqueeView.this.mIsRepeat) {
                            MarqueeView.this.mHandler.sendEmptyMessage(100);
                        }

                        MarqueeView.this.currentX = -MarqueeView.this.textWidth;
                    } else {
                        var10000 = MarqueeView.this;
                        var10000.currentX += MarqueeView.this.sepX;
                    }

                    if (canvas != null) {
                        canvas.drawColor(0, Mode.CLEAR);
                    }

                    canvas.drawText(MarqueeView.this.margueeString, (float)MarqueeView.this.currentX, (float)(centeYLine + MarqueeView.dip2px(MarqueeView.this.getContext(), (float)MarqueeView.this.textHeight) / 2), MarqueeView.this.mTextPaint);
                    this.holder.unlockCanvasAndPost(canvas);
                    int a = MarqueeView.this.textWidth / MarqueeView.this.margueeString.trim().length();
                    int b = a / MarqueeView.this.sepX;
                    int c = MarqueeView.this.mSpeed / b == 0 ? 1 : MarqueeView.this.mSpeed / b;
                    Thread.sleep((long)c);
                }
            } catch (Exception var15) {
                Exception e = var15;
                e.printStackTrace();
            }

        }

        public void run() {
            while(this.isRun) {
                this.onDraw();
            }

        }
    }
}
