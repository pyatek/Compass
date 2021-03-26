package com.pyatek.compass

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pyatek.compass.data.local.AppDatabase
import com.pyatek.compass.data.local.dao.CoordinatesDao
import com.pyatek.compass.utils.TestUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.equalTo
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CoordinatesRepositoryTest {

    private lateinit var coordinatesDao: CoordinatesDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        coordinatesDao = db.coordinatesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeCoordinateAndReadById() = runBlocking {
        //GIVEN
        val coordinate = TestUtil.createDbCoordinates()

        //WHEN
        val saveId = coordinatesDao.insertCoordinates(coordinate).toInt()
        val list = coordinatesDao.getHistoricCoordinateList().first()
        val coordinateById = list.find { coordinateItem -> coordinateItem.id == saveId }

        //THEN
        assertThat(coordinateById, Is(notNullValue()))
        assertThat(coordinateById?.startLat, Is(closeTo(20.0, 0.0)))
    }

    @Test
    @Throws(Exception::class)
    fun writeCoordinateAndDelete() = runBlocking {
        //GIVEN
        val coordinate = TestUtil.createDbCoordinates()

        //WHEN
        val saveId = coordinatesDao.insertCoordinates(coordinate).toInt()
        val listBeforeDelete = coordinatesDao.getHistoricCoordinateList().first()
        val coordinateById = listBeforeDelete.find { coordinateItem -> coordinateItem.id == saveId }
        coordinatesDao.deleteCoordinateFromHistory(saveId)
        val listAfterDelete = coordinatesDao.getHistoricCoordinateList().first()

        //THEN
        assertThat(listBeforeDelete.size, equalTo(1))
        assertThat(coordinateById, Is(notNullValue()))
        assertThat(coordinateById?.id, equalTo(saveId))
        assertThat(listAfterDelete.size, equalTo(0))
    }
}