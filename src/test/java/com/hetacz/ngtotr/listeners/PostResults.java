package com.hetacz.ngtotr.listeners;

import com.hetacz.ngtotr.listeners.testrail.APIClient;
import com.hetacz.ngtotr.listeners.testrail.APIException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.testng.ITestResult;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PostResults {

    @Getter
    private static final String CLIENT = ""; //!
    @Getter
    private static final String USER = ""; //!
    @Getter
    private static final String PASSWORD = ""; //!
    private static final int RUN_ID = 1111111111; //!
    private static final String STATUS_ID = "status_id";
    private static final String COMMENT = "comment";
    private static final String ELAPSED = "elapsed";
    private static final String WITH_PARAMETERS = " with parameters: ";

    public void postResults(int testRailId, @NotNull ITestResult result, String parameters) {
        if (RUN_ID != 0) {
            log.info(
                    "TestId [{}], run [{}], with parameters: {} & status: {}",
                    testRailId,
                    RUN_ID,
                    parameters,
                    result.getStatus()
            );
            post(testRailId, generateMetaData(result, parameters));
        } else {
            log.error("Run ID not set.");
        }
    }

    public void init(/***/) {
        // read from file if no args given, throw on reinitialization attempt
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
            getTestRailAPIClient().sendPost("add_result_for_case/" + RUN_ID + "/" + testRailId, data);
        } catch (APIException | IOException e) {
            log.error("Error posting results to Testrail.");
            e.printStackTrace();
        }
    }

    private @NotNull APIClient getTestRailAPIClient() {
        APIClient client = new APIClient(CLIENT);
        client.setUser(USER);
        client.setPassword(PASSWORD);
        return client;
    }
}
