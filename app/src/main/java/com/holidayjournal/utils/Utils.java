package com.holidayjournal.utils;

import com.google.firebase.auth.FirebaseAuth;

public final class Utils {

    public static String getUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

}
