package com.storerepublic.wifaapp.utills;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public final class AnimationUtils {

    public static void slideDown(final View view) {
        view.setTranslationY(0);
        view.animate()
                .translationY(-view.getHeight())
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // superfluous restoration
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public static void slideUp(final View view) {
        view.setVisibility(View.VISIBLE);

        if (view.getHeight() > 0) {
            slideUpNow(view);
        } else {
            // wait till height is measured
            view.post(() -> slideUpNow(view));
        }
    }

    private static void slideUpNow(final View view) {
        view.setTranslationY(-view.getHeight());
        view.animate()
                .translationY(0)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                });
    }

}