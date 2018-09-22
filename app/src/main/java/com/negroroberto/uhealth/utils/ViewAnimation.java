package com.negroroberto.uhealth.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;

public class ViewAnimation {
    private static final int SHORT_DURATION = 150;

    public static void SetViewVisibility(final Activity activity, final View view, final boolean visible, final boolean anim) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(anim) {
                    if (visible)
                        ViewAnimation.FadeIn(activity, view);
                    else
                        ViewAnimation.FadeOut(activity, view);
                } else {
                    if(visible) {
                        view.setAlpha(1f);
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setAlpha(0f);
                        view.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public static void SetViewSlide(final Activity activity, final View view, final boolean visible, final boolean anim) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(anim) {
                    if (visible)
                        ViewAnimation.SlideIn(activity, view);
                    else
                        ViewAnimation.SlideOut(activity, view);
                } else {
                    if(visible) {
                        view.setTranslationX(0);
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setTranslationX(activity.getWindow().getDecorView().getWidth());
                        view.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private static void SlideIn(final Activity activity, final View view) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Debug.Log(this, "Width: " + activity.getWindow().getDecorView().getWidth());

                if (view.getVisibility() == View.GONE)
                    view.setTranslationX(activity.getWindow().getDecorView().getWidth());

                view.setVisibility(View.VISIBLE);
                view.animate()
                        .translationX(0)
                        .setDuration(SHORT_DURATION)
                        .setListener(null);
            }
        });
    }

    private static void SlideOut(final Activity activity, final View view) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view.getVisibility() == View.GONE)
                    view.setTranslationX(view.getWidth());
                else {
                    view.setVisibility(View.VISIBLE);
                    view.animate()
                            .translationX(activity.getWindow().getDecorView().getWidth())
                            .setDuration(SHORT_DURATION)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    view.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
    }

    private static void FadeIn(final Activity activity, final View view) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view.getVisibility() == View.GONE)
                    view.setAlpha(0f);

                view.setVisibility(View.VISIBLE);
                view.animate()
                    .alpha(1f)
                    .setDuration(SHORT_DURATION)
                    .setListener(null);
            }
        });
    }

    private static void FadeOut(final Activity activity, final View view) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.animate()
                    .alpha(0f)
                    .setDuration(SHORT_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(View.GONE);
                        }
                    });
            }
        });
    }
}
