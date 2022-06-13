package com.hetacz.ngtotr;

import com.hetacz.ngtotr.testrail.APIClient;
import com.hetacz.ngtotr.testrail.APIException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.testng.ITestResult;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
class PostResults {

    private static final String STATUS_ID = "status_id";
    private static final String COMMENT = "comment";
    private static final String ELAPSED = "elapsed";
    private static final String WITH_PARAMETERS = " with parameters: ";
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private static String client; //!
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private static String user; //!
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private static String password; //!
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private static boolean isUsingRunCase;
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private static int runID; //!

    void postResults(int testRailId, @NotNull ITestResult result, String parameters) {
        if (runID != 0) {
            log.info(
                    "TestId [{}], run [{}], with parameters: {} & status: {}",
                    testRailId,
                    runID,
                    parameters,
                    result.getStatus()
            );
            post(testRailId, generateMetaData(result, parameters));
        } else {
            log.error("Run ID not set.");
        }
    }

    private Map<String, Object> generateMetaData(@NotNull ITestResult result, String parameters) {
        return switch (result.getStatus()) {
            case ITestResult.SUCCESS -> Map.of(
                    STATUS_ID, TestRailStatusID.PASS.getValue(),
                    COMMENT, TestRailStatusID.PASS.getMsg() + WITH_PARAMETERS + parameters,
                    ELAPSED, formatElapsed(result)
            );
            case ITestResult.FAILURE -> Map.of(
                    STATUS_ID,
                    TestRailStatusID.FAIL.getValue(),
                    COMMENT,
                    TestRailStatusID.FAIL.getMsg() + WITH_PARAMETERS + parameters + "\n" + result.getThrowable(),
                    ELAPSED,
                    formatElapsed(result)
            );
            case ITestResult.SKIP -> Map.of(
                    STATUS_ID, TestRailStatusID.RETEST.getValue(),
                    COMMENT, TestRailStatusID.RETEST.getMsg() + WITH_PARAMETERS
                            + parameters + "\n" + result.getThrowable() + "\n" + result.getSkipCausedBy(),
                    ELAPSED, formatElapsed(result)
            );
            default -> throw new IllegalStateException("Test ended with other status that Success, Failure or Skip.");
        };
    }

    private @NotNull String formatElapsed(@NotNull ITestResult result) {
        long elapsed = result.getEndMillis() - result.getStartMillis();
        long toMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsed);
        String minutes = toMinutes == 0 ? "" : toMinutes + "m";
        String seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed) - TimeUnit.MINUTES.toSeconds(toMinutes) + "s";
        return minutes + seconds;
    }

    private void post(int testRailId, Map<String, Object> data) {
        try {
            getTestRailAPIClient().sendPost("add_result_for_case/" + runID + "/" + testRailId, data);
        } catch (APIException | IOException e) {
            log.error("Error posting results to Testrail.");
            e.printStackTrace();
        }
    }

    private @NotNull APIClient getTestRailAPIClient() {
        APIClient apiClient = new APIClient(client);
        apiClient.setUser(user);
        apiClient.setPassword(password);
        return apiClient;
    }
}
