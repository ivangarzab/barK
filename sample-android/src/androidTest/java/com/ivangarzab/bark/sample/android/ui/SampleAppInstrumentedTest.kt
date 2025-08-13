package com.ivangarzab.bark.sample.android.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.trainers.AndroidTestLogTrainer
import com.ivangarzab.bark.trainers.ColoredUnitTestTrainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Simple instrumented test for the sample app
 *
 * Just exercises basic app functionality to see where barK logs appear
 * during instrumented test runs.
 */
@RunWith(AndroidJUnit4::class)
class SampleAppInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SampleActivity::class.java)

    @Test
    fun justLaunchAppAndLog() {
        // Setup barK for this test
        Bark.releaseAllTrainers()
        Bark.apply {
            train(AndroidTestLogTrainer(volume = Level.VERBOSE))
            tag("Instrumented Test")
        }

        // Do some logging from the test
        Bark.i("=== Instrumented test is running ===")
        Bark.d("SampleActivity should have launched")
        Bark.v("This is a verbose message from instrumented test")
        Bark.w("This is a warning from instrumented test")
        Bark.e("This is an error from instrumented test")

        // Just wait a moment for the app to settle
        Thread.sleep(2000)

        Bark.i("=== Test completed ===")

        // The app's own barK logging should have happened during launch.
    }
}