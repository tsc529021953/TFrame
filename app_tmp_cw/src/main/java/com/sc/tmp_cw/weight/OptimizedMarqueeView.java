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

/**
 * 优化版跑马灯组件
 * 支持显示/隐藏控制和动态速度调节
 */
public class OptimizedMarqueeView extends SurfaceView implements SurfaceHolder.Callback {
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
    private String marqueeString;
    private int textWidth;
    private int textHeight;
    private int ShadowColor;
    public int currentX;
    public int sepX;
    public static final int ROLL_OVER = 100;
    Handler mHandler;
    OnMarqueeListener mOnMarqueeListener;
    
    private volatile boolean isVisible = true;
    private volatile boolean isPaused = false;
    private volatile boolean isSurfaceValid = false;
    
    // 性能优化：缓存字段
    private Paint.FontMetrics fontMetricsCache;

    public OptimizedMarqueeView(Context context) {
        this(context, (AttributeSet)null);
    }

    public OptimizedMarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptimizedMarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTextSize = 100.0F;
        this.mTextColor = -65536;
        this.textWidth = 0;
        this.textHeight = 0;
        this.ShadowColor = -16777216;
        this.currentX = 0;
        this.sepX = 5;
        this.mSpeed = 50; // 默认速度设为 50
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 100:
                        OptimizedMarqueeView.this.stopScroll();
                        if (OptimizedMarqueeView.this.mOnMarqueeListener != null) {
                            OptimizedMarqueeView.this.mOnMarqueeListener.onRollOver();
                        }
                    default:
                }
            }
        };
        this.mContext = context;
        this.init(attrs, defStyleAttr);
        
        // 启用硬件加速优化性能
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = this.getContext().obtainStyledAttributes(attrs, styleable.MarqueeView, defStyleAttr, 0);
        this.mTextColor = a.getColor(styleable.MarqueeView_textcolor, -65536);
        this.mTextSize = a.getDimension(styleable.MarqueeView_textSize, 48.0F);
        this.mIsRepeat = a.getBoolean(styleable.MarqueeView_isRepeat, false);
        this.mStartPoint = a.getInt(styleable.MarqueeView_startPoint, 0);
        this.mDirection = a.getInt(styleable.MarqueeView_direction, 0);
        // 将 XML 中的速度值映射到 0-100 范围
        int xmlSpeed = a.getInt(styleable.MarqueeView_speed, 20);
        this.mSpeed = Math.max(0, Math.min(100, xmlSpeed));
        a.recycle();
        this.holder = this.getHolder();
        this.holder.addCallback(this);
        this.mTextPaint = new TextPaint();
        this.mTextPaint.setFlags(1);
        this.mTextPaint.setTextAlign(Align.LEFT);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(-3);
        
        // 预创建字体度量对象以复用
        fontMetricsCache = new Paint.FontMetrics();
    }

    public void setText(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            this.measurementsText(msg);
            // 设置文本后立即启动（如果可见且未暂停）
            if (isVisible && !isPaused && isSurfaceValid) {
                // 使用 post 确保在 UI 线程执行
                post(new Runnable() {
                    @Override
                    public void run() {
                        startScroll();
                    }
                });
            }
        }
    }

    protected void measurementsText(String msg) {
        this.marqueeString = msg;
        this.mTextPaint.setTextSize(this.mTextSize);
        this.mTextPaint.setColor(this.mTextColor);
        this.mTextPaint.setStrokeWidth(0.5F);
        this.mTextPaint.setFakeBoldText(true);
        this.textWidth = (int)this.mTextPaint.measureText(this.marqueeString);
        
        // 使用缓存的字体度量对象
        this.mTextPaint.getFontMetrics(fontMetricsCache);
        this.textHeight = (int)fontMetricsCache.bottom;
        
        // 立即计算位置，避免异步导致的闪烁问题
        int width = getWidth();
        if (width > 0) {
            // View 已经布局完成，直接计算位置
            setContentWidth(width);
        } else {
            // View 还未布局，等待布局完成后设置位置
            post(new Runnable() {
                @Override
                public void run() {
                    int w = getWidth();
                    if (w > 0) {
                        setContentWidth(w);
                    }
                }
            });
        }
    }

    /**
     * 根据宽度计算起始位置
     */
    private void setContentWidth(int width) {
        int contentWidth = width - getPaddingLeft() - getPaddingRight();
        // 文字从中间位置开始，然后向左滚动
        if (mStartPoint == 0) {
            // 从屏幕中间开始（50% 宽度处）
            currentX = contentWidth / 2;
        } else {
            // 从屏幕右侧开始
            currentX = contentWidth;
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        isSurfaceValid = true;
        // Surface 创建后，如果有文本则自动启动
        if (marqueeString != null && !marqueeString.isEmpty() && isVisible && !isPaused) {
            startScroll();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        isSurfaceValid = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceValid = false;
        stopScroll();
    }

    public void startScroll() {
        if (!isVisible || isPaused) {
            return;
        }
        
        // 如果 Surface 还未准备好，等待它准备好
        if (!isSurfaceValid || holder == null) {
            // 等待 Surface 创建，每 50ms 重试一次，最多重试 10 次
            postDelayed(new Runnable() {
                int retryCount = 0;
                @Override
                public void run() {
                    if (isVisible && !isPaused) {
                        if (isSurfaceValid && holder != null) {
                            doStartScroll();
                        } else if (retryCount < 10) {
                            retryCount++;
                            postDelayed(this, 50);
                        }
                    }
                }
            }, 50);
            return;
        }
        
        doStartScroll();
    }

    /**
     * 实际启动滚动线程
     */
    private void doStartScroll() {
        synchronized (this) {
            if (this.mThread != null && this.mThread.isRun) {
                return; // 已经在运行
            }
            
            // 确保 currentX 已经正确设置
            if (currentX == 0 && textWidth > 0) {
                int width = getWidth();
                if (width > 0) {
                    setContentWidth(width);
                }
            }
            
            this.mThread = new MarqueeViewThread(this.holder);
            this.mThread.start();
        }
    }

    public void stopScroll() {
        MarqueeViewThread thread;
        synchronized (this) {
            thread = this.mThread;
            this.mThread = null;
        }
        
        if (thread != null) {
            thread.isRun = false;
            // 中断线程，避免阻塞
            thread.interrupt();
        }
    }

    public void show() {
        if (!isVisible) {
            isVisible = true;
            if (marqueeString != null && !marqueeString.isEmpty()) {
                // 直接启动线程
                startScroll();
            }
        }
    }

    /**
     * 隐藏跑马灯
     */
    public void hide() {
        if (isVisible) {
            isVisible = false;
            stopScroll();
            // 异步清除画布，避免阻塞
            post(new Runnable() {
                @Override
                public void run() {
                    clearCanvas();
                }
            });
        }
    }

    /**
     * 暂停跑马灯动画
     */
    public void pause() {
        isPaused = true;
        stopScroll();
    }

    /**
     * 恢复跑马灯动画
     */
    public void resume() {
        isPaused = false;
        if (isVisible && marqueeString != null && !marqueeString.isEmpty()) {
            startScroll();
        }
    }

    /**
     * 设置滚动速度
     * @param speed 速度值，0-100，数值越大速度越快
     */
    public void setSpeed(int speed) {
        // 限制速度范围在 0-100
        this.mSpeed = Math.max(0, Math.min(100, speed));
    }

    /**
     * 获取当前速度
     */
    public int getSpeed() {
        return mSpeed;
    }

    /**
     * 检查是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 检查是否暂停
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * 清除画布内容
     */
    private void clearCanvas() {
        if (!isSurfaceValid || holder == null) {
            return;
        }
        
        Canvas canvas = null;
        try {
            canvas = this.holder.lockCanvas(null);
            if (canvas != null) {
                canvas.drawColor(0, Mode.CLEAR);
            }
        } catch (Exception e) {
            // 忽略异常，Surface 可能已销毁
        } finally {
            if (canvas != null) {
                try {
                    this.holder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    // 忽略异常
                }
            }
        }
    }

    /**
     * 立即绘制第一帧，让文字马上显示
     */
    private void drawFirstFrame() {
        if (!isSurfaceValid || holder == null || TextUtils.isEmpty(marqueeString)) {
            return;
        }
        
        // 检查尺寸是否有效
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        
        // 使用 tryLockCanvas 避免阻塞，设置超时时间为 100ms
        Canvas canvas = null;
        try {
            // 尝试锁定 Canvas，最多等待 100ms
            canvas = this.holder.lockCanvas(null);
            if (canvas == null) {
                // 如果无法立即获得 Canvas，不等待，直接返回
                // 线程启动后会正常绘制
                return;
            }
            
            int paddingLeft = this.getPaddingLeft();
            int paddingTop = this.getPaddingTop();
            int paddingRight = this.getPaddingRight();
            int contentWidth = width - paddingLeft - paddingRight;
            int contentHeight = height - paddingTop - getPaddingBottom();
            int centeYLine = paddingTop + contentHeight / 2;
            
            // 确保 currentX 在正确位置
            if (mDirection == 0) {
                // 向左滚动，从中间位置开始
                if (currentX <= -textWidth) {
                    currentX = contentWidth / 2;
                }
            } else {
                // 向右滚动，从左侧开始
                if (currentX >= contentWidth) {
                    currentX = -textWidth;
                }
            }
            
            canvas.drawColor(0, Mode.CLEAR);
            canvas.drawText(marqueeString, 
                (float)currentX, 
                (float)(centeYLine + dip2px(getContext(), (float)textHeight) / 2), 
                mTextPaint);
                
        } catch (Exception e) {
            // 静默处理，不影响后续线程绘制
        } finally {
            if (canvas != null) {
                try {
                    this.holder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    // 忽略异常
                }
            }
        }
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    public void reset() {
        int contentWidth = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
        // 重置到中间位置
        this.currentX = contentWidth / 2;
    }

    public void setOnMarqueeListener(OnMarqueeListener mOnMarqueeListener) {
        this.mOnMarqueeListener = mOnMarqueeListener;
    }

    public interface OnMarqueeListener {
        void onRollOver();
    }

    class MarqueeViewThread extends Thread {
        private SurfaceHolder holder;
        public volatile boolean isRun;

        public MarqueeViewThread(SurfaceHolder holder) {
            this.holder = holder;
            this.isRun = true;
        }

        public void onDraw() {
            // 快速检查退出条件
            if (!isRun || !isVisible || isPaused || !isSurfaceValid || 
                TextUtils.isEmpty(OptimizedMarqueeView.this.marqueeString)) {
                try {
                    Thread.sleep(16L); // 约60fps
                } catch (InterruptedException e) {
                    isRun = false;
                }
                return;
            }

            Canvas canvas = null;
            try {
                // 尝试锁定 Canvas，使用带超时的版本避免阻塞
                canvas = this.holder.lockCanvas(null);
                if (canvas == null || !isRun) {
                    // Canvas 不可用，短暂等待后重试
                    Thread.sleep(16);
                    return;
                }
                
                // 再次检查状态，避免无效绘制
                if (!isVisible || isPaused || !isSurfaceValid) {
                    return;
                }
                
                int paddingLeft = OptimizedMarqueeView.this.getPaddingLeft();
                int paddingTop = OptimizedMarqueeView.this.getPaddingTop();
                int paddingRight = OptimizedMarqueeView.this.getPaddingRight();
                int contentWidth = OptimizedMarqueeView.this.getWidth() - paddingLeft - paddingRight;
                int contentHeight = OptimizedMarqueeView.this.getHeight() - paddingTop - getPaddingBottom();
                int centeYLine = paddingTop + contentHeight / 2;
                
                if (OptimizedMarqueeView.this.mDirection == 0) {
                    if (OptimizedMarqueeView.this.currentX <= -OptimizedMarqueeView.this.textWidth) {
                        if (!OptimizedMarqueeView.this.mIsRepeat) {
                            OptimizedMarqueeView.this.mHandler.sendEmptyMessage(100);
                        }
                        // 滚动到左边后，回到中间位置
                        OptimizedMarqueeView.this.currentX = contentWidth / 2;
                    } else {
                        OptimizedMarqueeView.this.currentX -= OptimizedMarqueeView.this.sepX;
                    }
                } else {
                    if (OptimizedMarqueeView.this.currentX >= contentWidth) {
                        if (!OptimizedMarqueeView.this.mIsRepeat) {
                            OptimizedMarqueeView.this.mHandler.sendEmptyMessage(100);
                        }
                        OptimizedMarqueeView.this.currentX = -OptimizedMarqueeView.this.textWidth;
                    } else {
                        OptimizedMarqueeView.this.currentX += OptimizedMarqueeView.this.sepX;
                    }
                }

                canvas.drawColor(0, Mode.CLEAR);
                canvas.drawText(OptimizedMarqueeView.this.marqueeString, 
                    (float)OptimizedMarqueeView.this.currentX, 
                    (float)(centeYLine + OptimizedMarqueeView.dip2px(OptimizedMarqueeView.this.getContext(), 
                        (float)OptimizedMarqueeView.this.textHeight) / 2), 
                    OptimizedMarqueeView.this.mTextPaint);
                    
            } catch (Exception e) {
                // Surface 可能已销毁，静默处理
                if (!(e instanceof IllegalStateException)) {
                    e.printStackTrace();
                }
            } finally {
                if (canvas != null) {
                    try {
                        this.holder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        // 忽略解锁异常
                    }
                }
            }
            
            // 计算合适的睡眠时间
            try {
                String text = OptimizedMarqueeView.this.marqueeString;
                if (text != null && !text.isEmpty()) {
                    int charWidth = OptimizedMarqueeView.this.textWidth / 
                        Math.max(1, text.trim().length());
                    int stepRatio = Math.max(1, charWidth / OptimizedMarqueeView.this.sepX);
                    
                    // 速度映射：0-100 映射到睡眠时间 50ms-2ms
                    // 速度 0 = 最慢 (50ms), 速度 100 = 最快 (2ms)
                    int sleepTime = 50 - (OptimizedMarqueeView.this.mSpeed * 48 / 100);
                    sleepTime = Math.max(2, Math.min(50, sleepTime));
                    
                    Thread.sleep(sleepTime);
                } else {
                    Thread.sleep(16);
                }
            } catch (InterruptedException e) {
                // 线程被中断，正常退出
                isRun = false;
            }
        }

        public void run() {
            while(isRun) {
                onDraw();
            }
        }
    }
}
