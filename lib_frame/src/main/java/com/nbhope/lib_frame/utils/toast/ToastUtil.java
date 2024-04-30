package com.nbhope.lib_frame.utils.toast;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.hjq.toast.Toaster;

public class ToastUtil {

    private ToastUtil() {
    }

    private static Toast sToast;


    public static Toast showS(CharSequence msg) {
        Toaster.cancel();
        Toaster.show(msg);

//        LayoutInflater inflater = (LayoutInflater) ContextProvider.get().getApplication().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.custom_toast, null);
//        TextView textView = view.findViewById(android.R.id.message);
//        textView.setText(msg);
//        return showToast(ContextProvider.get().getApplication(),view,Gravity.NO_GRAVITY,0);
        return null;

//        return showToastCustom(ContextProvider.get().getApplication(),msg,Toast.LENGTH_SHORT);
    }

    public static Toast showS(int resId) {
        Toaster.cancel();
        Toaster.show(resId);
//        return showToastShort(ContextProvider.get().getApplication(), resId);
        return null;
    }

//    public static Toast showL(CharSequence msg) {
//        return showToastLong(ContextProvider.get().getApplication(), msg);
//    }
//
//    public static Toast showL(int resId) {
//        return showToastLong(ContextProvider.get().getApplication(), resId);
//    }

    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param msg     显示的内容
     */
    public static Toast showToastShort(Context context, CharSequence msg) {
        return showToast(context, msg, Toast.LENGTH_SHORT);
    }


    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param resId   显示的资源ID
     */
    public static Toast showToastShort(Context context, int resId) {
        return showToast(context, resId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param msg     显示的内容
     */
    public static Toast showToastShortWithGravity(Context context, CharSequence msg, int gravity) {
        return showToast(context, msg, Toast.LENGTH_SHORT, gravity, 0, 0);
    }

    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param resId   显示的资源ID
     */
    public static Toast showToastShortWithGravity(Context context, int resId, int gravity) {
        return showToast(context, resId, Toast.LENGTH_SHORT, gravity, 0, 0);
    }

    /**
     * 显示时间长的Toast
     *
     * @param context 上下文
     * @param msg     显示的内容
     */
    public static Toast showToastLong(Context context, CharSequence msg) {
        return showToast(context, msg, Toast.LENGTH_LONG);
    }

    /**
     * 显示时间长的Toast
     *
     * @param context 上下文
     * @param resId   显示的资源ID
     */
    public static Toast showToastLong(Context context, int resId) {
        return showToast(context, resId, Toast.LENGTH_LONG);
    }

    /**
     * 显示时间长的Toast
     *
     * @param context 上下文
     * @param msg     显示的内容
     * @param gravity 要显示的文本重心
     */
    public static Toast showToastLongWithGravity(Context context, CharSequence msg, int gravity) {
        return showToast(context, msg, Toast.LENGTH_LONG, gravity, 0, 0);
    }

    /**
     * 显示时间长的Toast
     *
     * @param context 上下文
     * @param resId   显示的资源ID
     * @param gravity 要显示的文本重心
     */
    public static Toast showToastLongWithGravity(Context context, int resId, int gravity) {
        return showToast(context, resId, Toast.LENGTH_LONG, gravity, 0, 0);
    }

    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param resId   显示的资源ID
     * @param gravity 要显示的文本重心
     * @param bgResId 背景颜色
     */
    public static Toast showToastShortWithGravity(Context context, int resId, int gravity, int bgResId) {
        return showToast(context, resId, Toast.LENGTH_SHORT, gravity, 0, 0, bgResId);
    }

    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param text    显示的文字
     * @param gravity 要显示的文本重心
     * @param bgResId 背景颜色
     */
    public static Toast showToastShortWithGravity(Context context, final CharSequence text, int gravity, int bgResId) {
        return showToast(context, text, Toast.LENGTH_SHORT, gravity, 0, 0, bgResId);
    }


    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param resId    显示的资源ID
     * @param duration 时长
     */
    public static Toast showToast(Context context, int resId, int duration) {
        if (context == null) {
            return null;
        }
        String text = context.getString(resId);
        return showToast(context, text, duration);
    }

    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param text     要显示的toast内容
     * @param duration 时长
     */
    public static Toast showToast(final Context context, final CharSequence text, final int duration) {
        return showToast(context, text, duration, Gravity.NO_GRAVITY, 0, 0);
    }
    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param resId    显示的资源ID
     * @param duration 时长
     * @param gravity  要显示的文本重心
     * @param offsetX
     * @param offsetY
     */
    private static Toast showToast(final Context context, final int resId, final int duration, final int gravity, final int offsetX, final int offsetY) {
        String text = context.getString(resId);
        return showToast(context, text, duration, gravity, offsetX, offsetY);
    }

    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param resId    显示的资源ID
     * @param duration 时长
     * @param gravity  要显示的文本重心
     * @param offsetX
     * @param offsetY
     * @param bgResId  背景颜色
     */
    private static Toast showToast(final Context context, final int resId, final int duration, final int gravity, final int offsetX, final int offsetY, final int bgResId) {
        String text = context.getString(resId);
        return showToast(context, text, duration, gravity, offsetX, offsetY, bgResId);
    }


    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param text     要显示的toast内容
     * @param duration 时长
     * @param gravity  要显示的文本重心
     * @param offsetX
     * @param offsetY
     */
    public static Toast showToast(final Context context, final CharSequence text, final int duration, final int gravity, final int offsetX, final int offsetY) {
        return showToast(context, text, duration, gravity, offsetX, offsetY, 0);
    }


    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param text     要显示的toast内容
     * @param duration 时长
     * @param gravity  要显示的文本重心
     * @param offsetX
     * @param offsetY
     */
    private static Toast showToast(final Context context, final CharSequence text, final int duration, final int gravity, final int offsetX, final int offsetY, final int bgResId) {
        if (context == null) {
            return null;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            sToast = ToastCompat.makeText(context.getApplicationContext(), text, duration);
            if (gravity != Gravity.NO_GRAVITY) {
                sToast.setGravity(gravity, offsetX, offsetY);
            }
            if (bgResId != 0) {
                sToast.getView().setBackgroundResource(bgResId);
            }
            try {
                sToast.show();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return sToast;
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    sToast = ToastCompat.makeText(context.getApplicationContext(), text, duration);
                    if (gravity != Gravity.NO_GRAVITY) {
                        sToast.setGravity(gravity, offsetX, offsetY);
                    }
                    if (bgResId != 0) {
                        sToast.getView().setBackgroundResource(bgResId);
                    }
                    try {
                        sToast.show();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
        }
        return null;
    }

    /*private static Toast showToastCustom(final Context context, CharSequence text, int duration) {
        if (context == null) {
            return null;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            sToast = ToastCompat.makeText(context.getApplicationContext(),"", duration);
            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_toast, null);

            TextView textView = view.findViewById(android.R.id.message);
            textView.setText(text);
            sToast.setView(view);
            try {
                sToast.show();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return sToast;
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    sToast = ToastCompat.makeText(context.getApplicationContext(),"", duration);
                    LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.custom_toast, null);

                    TextView textView = view.findViewById(android.R.id.message);
                    textView.setText(text);
                    sToast.setView(view);
                    try {
                        sToast.show();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
        }
        return null;
    }*/

    public static Toast showToast(final Context context, final View view, final int gravity, final int bgResId) {
        return showToast(context, view, Toast.LENGTH_SHORT, gravity, 0, 0, bgResId);
    }

    public static Toast showToast(final Context context, final View view, final int gravity,final int offsetX, final int offsetY, final int bgResId) {
        return showToast(context, view, Toast.LENGTH_SHORT, gravity, offsetX, offsetY, bgResId);
    }

    private static Toast showToast(final Context context, final View view, final int duration, final int gravity, final int offsetX, final int offsetY, final int bgResId) {
        if (context == null || view == null) {
            return null;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            sToast = ToastCompat.makeText(context, "", duration);
            sToast.setView(view);
            if (gravity != Gravity.NO_GRAVITY) {
                sToast.setGravity(gravity, offsetX, offsetY);
            }
            if (bgResId != 0) {
                sToast.getView().setBackgroundResource(bgResId);
            }
            try {
                sToast.show();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return sToast;
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    sToast = ToastCompat.makeText(context.getApplicationContext(),"", duration);
                    sToast.setView(view);
                    if (gravity != Gravity.NO_GRAVITY) {
                        sToast.setGravity(gravity, offsetX, offsetY);
                    }
                    if (bgResId != 0) {
                        sToast.getView().setBackgroundResource(bgResId);
                    }
                    try {
                        sToast.show();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
        }
        return null;
    }

    public static void cancel() {
        if (sToast != null) {
            sToast.cancel();
        }
    }
}
