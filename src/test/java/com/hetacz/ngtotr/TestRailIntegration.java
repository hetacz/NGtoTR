package com.hetacz.ngtotr;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.util.Arrays;

@Slf4j
public class TestRailIntegration implements IInvokedMethodListener {

    @Override
    public void afterInvocation(@NotNull IInvokedMethod method, ITestResult testResult) {
        if (method.isConfigurationMethod()) {
            return;
        }
        TestRailID testRailID = method
                .getTestMethod()
                .getConstructorOrMethod()
                .getMethod()
                .getAnnotation(TestRailID.class);
        if (testRailID == null) {
            log.warn("{}, has No TestRail ID given", method.getTestMethod().getQualifiedName());
            return;
        }
        String parameters = method.getTestMethod().isDataDriven()
                ? Arrays.toString(testResult.getParameters())
                : "empty.";
        if (testResult.getThrowable() instanceof SkipException) {
            testResult.setStatus(ITestResult.SKIP);
        }
        new PostResults().postResults(testRailID.value(), testResult, parameters);
    }
}
