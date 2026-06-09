package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAppDatabaseClassLoads() {
    val databaseClass = Class.forName("com.example.db.AppDatabase")
    assertNotNull(databaseClass)
    println("AppDatabase class loaded successfully!")

    try {
      val implClass = Class.forName("com.example.db.AppDatabase_Impl")
      assertNotNull(implClass)
      println("AppDatabase_Impl class loaded successfully!")
    } catch (e: ClassNotFoundException) {
      fail("AppDatabase_Impl could not be found! Room compilation failed to generate class implementation.")
    }
  }
}
