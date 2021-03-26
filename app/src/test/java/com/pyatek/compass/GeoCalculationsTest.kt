package com.pyatek.compass

import com.pyatek.compass.helpers.GeoCalculationHelper
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.equalTo
import org.hamcrest.core.Is

@RunWith(JUnit4::class)
class GeoCalculationsTest {

    @Test
    fun calculateDestinationBetweenTwoPointsTest() {
        val destination = GeoCalculationHelper.calculateDestinationDistance(20.0, 20.0, 0.0, 0.0)
        assertThat(destination, Is(closeTo(destination, 0.5)))
    }

    @Test
    fun calculateBearingBetweenTwoPointsTest() {
        val bearing = GeoCalculationHelper.calculateBearingBetweenDestinations(20.0, 20.0, 0.0, 0.0)
        assertThat(bearing, equalTo(226))
    }
}