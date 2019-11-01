package io.sentry.android.core

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.sentry.core.Sentry
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SentryAndroidTest {
    private lateinit var context: Context

    @BeforeTest
    fun `set up`() {
        context = ApplicationProvider.getApplicationContext()
        Sentry.close()
    }

    @Test
    fun `when applicationId is defined, dsn in meta-data, SDK initializes`() {
        assertFalse(Sentry.isEnabled())

        val mockContext = createMockContext()
        val metaData = Bundle()
        mockMetaData(mockContext, metaData)

        metaData.putString(ManifestMetadataReader.DSN_KEY, "https://key@sentry.io/123")

        SentryAndroid.init(mockContext)

        assertTrue(Sentry.isEnabled())
    }

    private fun createMockContext(): Context {
        val mockContext = mock<Context> {
            on { applicationContext } doReturn context
        }
        whenever(mockContext.cacheDir).thenReturn(File("${File.separator}cache"))
        return mockContext
    }

    private fun mockMetaData(mockContext: Context, metaData: Bundle) {
        val mockPackageManager: PackageManager = mock()
        val mockApplicationInfo: ApplicationInfo = mock()

        whenever(mockContext.packageName).thenReturn("io.sentry.sample.test")
        whenever(mockContext.packageManager).thenReturn(mockPackageManager)
        whenever(mockPackageManager.getApplicationInfo(mockContext.packageName, PackageManager.GET_META_DATA)).thenReturn(mockApplicationInfo)

        mockApplicationInfo.metaData = metaData
    }
}