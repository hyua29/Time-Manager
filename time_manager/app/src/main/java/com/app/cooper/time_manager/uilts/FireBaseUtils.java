package com.app.cooper.time_manager.uilts;

import com.google.firebase.database.FirebaseDatabase;

/**
 * hold one firebaseDatabase instance
 */
public class FireBaseUtils {
    private static FirebaseDatabase firebaseDatabase;

    public static FirebaseDatabase getDatabase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        return firebaseDatabase;
    }

}
