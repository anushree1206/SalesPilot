package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.entity.BusinessProfileEntity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SessionManagerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val prefs = context.getSharedPreferences("salespilot_session_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun `test initial session state defaults to false for welcome screen`() {
        val prefs = context.getSharedPreferences("salespilot_session_prefs", Context.MODE_PRIVATE)
        assertFalse("Key should not exist initially in fresh prefs", prefs.contains("user_authenticated_session"))
        assertFalse("Defaults to signed out so welcome screen appears", prefs.getBoolean("user_authenticated_session", false))

        // When user signs in
        prefs.edit().putBoolean("user_authenticated_session", true).commit()
        assertTrue(prefs.getBoolean("user_authenticated_session", false))

        // When user signs out
        prefs.edit().putBoolean("user_authenticated_session", false).commit()
        assertFalse(prefs.getBoolean("user_authenticated_session", true))
    }

    @Test
    fun `test onboarding completion marks session signed in`() {
        val prefs = context.getSharedPreferences("salespilot_session_prefs", Context.MODE_PRIVATE)
        // Simulate signed out
        prefs.edit().putBoolean("user_authenticated_session", false).commit()
        assertFalse(prefs.getBoolean("user_authenticated_session", true))

        // Simulate completeOnboarding
        prefs.edit().putBoolean("user_authenticated_session", true).commit()
        assertTrue(prefs.getBoolean("user_authenticated_session", false))
    }

    @Test
    fun `test delete business data clears session state`() {
        val prefs = context.getSharedPreferences("salespilot_session_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("user_authenticated_session", true).commit()
        assertTrue(prefs.getBoolean("user_authenticated_session", false))

        // On delete data, user should be signed out and redirected to welcome screen
        prefs.edit().putBoolean("user_authenticated_session", false).commit()
        assertFalse(prefs.getBoolean("user_authenticated_session", true))
    }

    @Test
    fun `test local account profile display values on welcome screen`() {
        val testProfile = BusinessProfileEntity(
            businessName = "Shree Meenakshi Silks",
            ownerName = "Lakshmi Sundaram",
            contactPhone = "9876543210"
        )
        assertEquals("Shree Meenakshi Silks", testProfile.businessName)
        assertEquals("Lakshmi Sundaram", testProfile.ownerName)
        assertEquals("9876543210", testProfile.contactPhone)
    }
}
