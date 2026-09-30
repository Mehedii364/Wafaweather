package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.AppLanguage
import com.example.domain.model.TemperatureUnit
import com.example.domain.model.WeatherCondition
import com.example.domain.model.WeatherConditionType
import com.example.domain.util.BanglaHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context verifies Wafa Weather`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Wafa Weather", appName)
  }

  @Test
  fun `bangla digits conversion is accurate`() {
    val converted = BanglaHelper.toBanglaDigits("1234567890")
    assertEquals("১২৩৪৫৬৭৮৯০", converted)
  }

  @Test
  fun `temperature formatting works for celsius and fahrenheit in bangla`() {
    val tempC = 33.0
    val formattedBn = BanglaHelper.formatTemp(tempC, TemperatureUnit.CELSIUS, AppLanguage.BANGLA)
    assertEquals("৩৩°C", formattedBn)

    val formattedEn = BanglaHelper.formatTemp(tempC, TemperatureUnit.CELSIUS, AppLanguage.ENGLISH)
    assertEquals("33°C", formattedEn)
  }

  @Test
  fun `wmo code maps to correct weather condition`() {
    val clearDay = WeatherCondition.fromWmoCode(0, true)
    assertEquals(WeatherConditionType.CLEAR_SUNNY, clearDay.type)
    assertEquals("পরিষ্কার আকাশ", clearDay.titleBn)

    val thunder = WeatherCondition.fromWmoCode(95, true)
    assertEquals(WeatherConditionType.THUNDERSTORM, thunder.type)
    assertEquals("বজ্রঝড়", thunder.titleBn)
  }
}
