package com.sc.lib_frame.di;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import androidx.fragment.app.Fragment;
import com.sc.lib_frame.app.HopeBaseApp;
import dagger.android.AndroidInjector;
import dagger.android.HasAndroidInjector;
import dagger.internal.Beta;
import timber.log.Timber;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Injects core Android types.
 */
@Beta
public final class AndroidInjectionDelegate {
    private static final String TAG = "dagger.android";

    /**
     * Injects {@code activity} if an associated {@link AndroidInjector} implementation can be found,
     * otherwise throws an {@link IllegalArgumentException}.
     *
     * @throws RuntimeException if the {@link Application} doesn't implement {@link
     *                          HasAndroidInjector}.
     */
    public static void inject(Activity activity) {
        checkNotNull(activity, "activity");
        Application application = activity.getApplication();
        if (!(application instanceof HopeBaseApp)) {
            throw new RuntimeException(
                    String.format(
                            "%s does not a HopeBaseApp",
                            application.getClass().getCanonicalName()));
        }
        String className = activity.getComponentName().getClassName();
        AndroidInjector<Object> injector = ((HopeBaseApp) application).getInjector(className);
        if (injector == null) {
            throw new RuntimeException(
                    String.format(
                            "%s does not contain key %s",
                            application.getClass().getCanonicalName(),
                            activity.getPackageName()));
        }

        inject(activity, injector);
    }

    /**
     * Injects {@code fragment} if an associated {@link AndroidInjector} implementation can be found,
     * otherwise throws an {@link IllegalArgumentException}.
     *
     * <p>Uses the following algorithm to find the appropriate {@code AndroidInjector<Fragment>} to
     * use to inject {@code fragment}:
     *
     * <ol>
     *   <li>Walks the parent-fragment hierarchy to find the a fragment that implements {@link
     *       HasAndroidInjector}, and if none do
     *   <li>Uses the {@code fragment}'s {@link Fragment#getActivity() activity} if it implements
     *       {@link HasAndroidInjector}, and if not
     *   <li>Uses the {@link Application} if it implements {@link HasAndroidInjector}.
     * </ol>
     * <p>
     * If none of them implement {@link HasAndroidInjector}, a {@link IllegalArgumentException} is
     * thrown.
     *
     * @throws IllegalArgumentException if no parent fragment, activity, or application implements
     *                                  {@link HasAndroidInjector}.
     */
    public static void inject(Fragment fragment) {
        checkNotNull(fragment, "fragment");
        HasAndroidInjector hasAndroidInjector = findHasAndroidInjectorForFragment(fragment);
        Timber.d(String.format(
                "An injector for %s was found in %s",
                fragment.getClass().getCanonicalName(),
                hasAndroidInjector.getClass().getCanonicalName()));
//        if (Log.isLoggable(TAG, DEBUG)) {
//            Log.d(
//                    TAG,
//                    String.format(
//                            "An injector for %s was found in %s",
//                            fragment.getClass().getCanonicalName(),
//                            hasAndroidInjector.getClass().getCanonicalName()));
//        }

        inject(fragment, hasAndroidInjector.androidInjector());
    }

    private static HasAndroidInjector findHasAndroidInjectorForFragment(Fragment fragment) {
        Fragment parentFragment = fragment;
        while ((parentFragment = parentFragment.getParentFragment()) != null) {
            if (parentFragment instanceof HasAndroidInjector) {
                return (HasAndroidInjector) parentFragment;
            }
        }
        Activity activity = fragment.getActivity();
        if (activity instanceof HasAndroidInjector) {
            return (HasAndroidInjector) activity;
        }
        if (activity.getApplication() instanceof HasAndroidInjector) {
            return (HasAndroidInjector) activity.getApplication();
        }
        throw new IllegalArgumentException(
                String.format("No injector was found for %s", fragment.getClass().getCanonicalName()));
    }

    /**
     * Injects {@code service} if an associated {@link AndroidInjector} implementation can be found,
     * otherwise throws an {@link IllegalArgumentException}.
     *
     * @throws RuntimeException if the {@link Application} doesn't implement {@link
     *                          HasAndroidInjector}.
     */
    public static void inject(Service service) {
        checkNotNull(service, "service");
        Application application = service.getApplication();
        if (!(application instanceof HopeBaseApp)) {
            throw new RuntimeException(
                    String.format(
                            "%s does not a HopeBaseApp",
                            application.getClass().getCanonicalName()));
        }
        if (!(service instanceof InjectionGroup)) {
            throw new RuntimeException(
                    String.format(
                            "%s does not implement InjectionGroup",
                            service.getClass().getCanonicalName()));
        }
        String packageName = ((InjectionGroup) service).packageName();
        AndroidInjector<Object> injector = ((HopeBaseApp) application).getInjector(packageName);
        if (injector == null) {
            throw new RuntimeException(
                    String.format(
                            "%s does not contain key %s",
                            service.getClass().getCanonicalName(),
                            packageName));
        }
        inject(service, injector);
    }

    /**
     * Injects {@code broadcastReceiver} if an associated {@link AndroidInjector} implementation can
     * be found, otherwise throws an {@link IllegalArgumentException}.
     *
     * @throws RuntimeException if the {@link Application} from {@link
     *                          Context#getApplicationContext()} doesn't implement {@link HasAndroidInjector}.
     */
    public static void inject(BroadcastReceiver broadcastReceiver, Context context) {
        checkNotNull(broadcastReceiver, "broadcastReceiver");
        checkNotNull(context, "context");
        Application application = (Application) context.getApplicationContext();
        if (!(application instanceof HopeBaseApp)) {
            throw new RuntimeException(
                    String.format(
                            "%s does not a HopeBaseApp",
                            application.getClass().getCanonicalName()));
        }

        if (!(broadcastReceiver instanceof InjectionGroup)) {
            throw new RuntimeException(
                    String.format(
                            "%s does not implement InjectionGroup",
                            broadcastReceiver.getClass().getCanonicalName()));
        }
        String packageName = ((InjectionGroup) broadcastReceiver).packageName();
        AndroidInjector<Object> injector = ((HopeBaseApp) application).getInjector(packageName);
        if (injector == null) {
            throw new RuntimeException(
                    String.format(
                            "%s does not contain key %s",
                            broadcastReceiver.getClass().getCanonicalName(),
                            packageName));
        }
        inject(broadcastReceiver, injector);
    }

    /**
     * Injects {@code contentProvider} if an associated {@link AndroidInjector} implementation can be
     * found, otherwise throws an {@link IllegalArgumentException}.
     *
     * @throws RuntimeException if the {@link Application} doesn't implement {@link
     *                          HasAndroidInjector}.
     */
    public static void inject(ContentProvider contentProvider) {
        checkNotNull(contentProvider, "contentProvider");
        Context context = contentProvider.getContext();
        Application application = (Application) context.getApplicationContext();
        if (!(application instanceof HopeBaseApp)) {
            throw new RuntimeException(
                    String.format(
                            "%s does not a HopeBaseApp",
                            application.getClass().getCanonicalName()));
        }
        if (!(contentProvider instanceof InjectionGroup)) {
            throw new RuntimeException(
                    String.format(
                            "%s does not implement InjectionGroup",
                            contentProvider.getClass().getCanonicalName()));
        }
        String packageName = ((InjectionGroup) contentProvider).packageName();
        AndroidInjector<Object> injector = ((HopeBaseApp) application).getInjector(packageName);
        if (injector == null) {
            throw new RuntimeException(
                    String.format(
                            "%s does not contain key %s",
                            contentProvider.getClass().getCanonicalName(),
                            packageName));
        }
        inject(contentProvider, injector);
    }

    private static void inject(Object target, AndroidInjector<Object> injector) {
        checkNotNull(
                injector, "%s.androidInjector() returned null", injector.getClass());

        injector.inject(target);
    }

    private AndroidInjectionDelegate() {
    }
}
