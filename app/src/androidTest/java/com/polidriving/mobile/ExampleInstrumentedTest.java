package com.polidriving.mobile;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import android.content.Context;
import org.junit.Test;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.polidriving.mobile", appContext.getPackageName());
    }
}