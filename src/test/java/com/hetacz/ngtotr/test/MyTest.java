package com.hetacz.ngtotr.test;

import com.hetacz.ngtotr.Setup;
import com.hetacz.ngtotr.TestRailID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.*;

/**
 * R415 // T - task, C - case. By default we use R with C values.
 */
@Slf4j
public class MyTest {

    @BeforeMethod
    public void before() {
        log.info("Hello!");
    }

    @TestRailID
    @Test(description="First Test. T37876. C30722.")
    public void firstTest() {
        boolean value = true;
        Assert.assertThat(value, equalTo(true));
    }

    @TestRailID
    @Test(description="Another Test. T37877. C30723.")
    public void anotherTest() {
        boolean value = false;
        Assert.assertThat(value, equalTo(true));
    }

    @TestRailID(30738)
    @Test
    public void skipTest() {
        throw new SkipException("Test skipped programmatically.");
    }

    @TestRailID(30724)
    @Test(description = "Try Data Provider. T37879. C30724", dataProvider = "myData")
    public void tryDataProvider(Object o) {
        Assert.assertThat(o, is(not(null)));
    }

    @DataProvider(name = "myData")
    public Object[] myData() {
        return new Object[]{1, 2, 3};
    }

    @Test
    public void x() {
        new Setup.Builder().build().initWithValues();
    }
}
