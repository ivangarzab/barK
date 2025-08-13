package com.ivangarzab.bark.sample.android.data

import com.ivangarzab.bark.Bark

/**
 * Sample service class to demonstrate auto-tag detection
 */
class UserService {
    fun performAction() {
        // Should show tag "UserService"
        Bark.i("UserService performing action")
        Bark.d("Action completed successfully")
    }
}