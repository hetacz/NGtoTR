package com.hetacz.ngtotr;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.File;
import java.text.MessageFormat;

/**
 * Use this class to change location of default config file,
 * pass or overwrite data from configs.
 * Take data from your Suite Files or other sources and
 * pass data directly to the config class
 * overwriting any previous configuration.
 * Static methods {@code init()} and @{code initCustomProperties(String filename)}
 * can let to load config from config file,
 * while their overloaded version also allow passing RunID directly.
 * Using {@code new Setup().Builder().initWithValues()} allows you to
 * overwrite any previous configurations.
 * Builder patter also support RunID initialization,
 * which can also be directly input with {@code initWithValues(int runID)}
 * There are also methods that allow changing runID and usingRunCase directly
 */
@Slf4j
public final class Setup {

    @Getter(AccessLevel.PACKAGE)
    private static String path =
            String.join(File.separator, "src", "test", "resources", "application.properties");
    private String client;
    private String user;
    private String password;
    private Boolean usingRunCase;
    private int runID;

    @Contract(pure = true)
    private Setup() {
        this.runID = 0;
    }

    /**
     * Change runID in a config class, set to 0 if not using.
     * @param runID TestRail Run ID
     * @see PostResults
     */
    public static void changeRunID(@Range(from = 0, to = Integer.MAX_VALUE) int runID) {
        PostResults.setRunID(runID);
    }

    /**
     * Choose if using Run and Case or Test based approach in test identification.
     * Changes setting directly in a config class.
     * <p>
     * If usingRunCase is TRUE (recommended)
     * Run and Case uses static Case number and requires naming test cases only once.
     * Only Run IDs change. Test is annotated only once.
     * </p><p>
     * Of usingRimCase is FALSE (not recommended)
     * Test approach uses unique name for every test.
     * Therefore every new test run renaming annotations in test methods is required.
     * </p>
     * @param usingRunCase set TRUE if using Run and Case approach, FALSE otherwise.
     * @see com.hetacz.ngtotr.PostResults
     */
    public static void changeUsingRunCase(boolean usingRunCase) {
        PostResults.setUsingRunCase(usingRunCase);
    }

    /**
     * Initialize data from default config file.
     * There is no key for runID in config file.
     */
    public static void init() {
        configureFromFile();
    }

    /**
     * Initialize data from default config file.
     * @param runID TestRail run ID
     */
    public static void init(int runID) {
        configureFromFile();
        PostResults.setRunID(runID);
    }

    /**
     * Initialize data from custom config file
     * @param filename filename location
     */
    public static void initCustomProperties(String filename) {
        changeDefaultConfigFile(filename);
        configureFromFile();
    }

    /**
     * Initialize data from custom config file
     * @param filename filename location
     * @param runID TestRail run ID
     */
    public static void initCustomProperties(String filename, @Range(from = 0, to = Integer.MAX_VALUE) int runID) {
        changeDefaultConfigFile(filename);
        configureFromFile();
        PostResults.setRunID(runID);
    }

    /**
     * Returns all configuration saved in configuration class.
     * @return formatted string with all the config data.
     * @see com.hetacz.ngtotr.PostResults
     */
    public static @NotNull String verifyConfiguration() {
        return MessageFormat.format(
                "Client: {0}\nUser: {1}\nPassword: {2}\nUserRunCase: {3}\nRunID: {4}",
                PostResults.getClient(),
                PostResults.getUser(),
                PostResults.getPassword(),
                PostResults.isUsingRunCase(),
                PostResults.getRunID()
        );
    }

    /**
     * Overwrite values from config file with data saved in an object.
     * Config file does not contain run ID.
     * Use after building settings object, with or without runID.
     */
    public void initWithValues() {
        configureFromInput();
    }

    /**
     * Overwrite values from config file with data saved in an object.
     * Config file does not contain run ID.
     * Use after building settings object, with or without runID.
     * RunID passed here overwrites any input from before.
     * @param runID TestRail run ID
     */
    public void initWithValues(@Range(from = 0, to = Integer.MAX_VALUE) int runID) {
        configureFromFile();
        PostResults.setRunID(runID);
    }

    private static void changeDefaultConfigFile(String filename) {
        if(Utils.isNullOrBlank(filename)) {
            log.error("Blank value was input as properties file filepath.");
            throw new IllegalArgumentException("Input is null or blank.");
        }
        if(Utils.isFile(filename)) {
            log.error("Provided path is not a valid file.");
            throw new IllegalArgumentException("Input does not represent a valid file.");
        }
        path = filename;
    }

    private static void configureFromFile() {
        PostResults.setClient(ConfigLoader.getInstance().getClient());
        PostResults.setUser(ConfigLoader.getInstance().getUser());
        PostResults.setPassword(ConfigLoader.getInstance().getPassword());
        PostResults.setUsingRunCase(ConfigLoader.getInstance().isUsingRunCase());
    }

    private void configureFromInput() {
        PostResults.setClient(Utils.isNullOrBlank(client) ? ConfigLoader.getInstance().getClient() : client);
        PostResults.setUser(Utils.isNullOrBlank(user) ? ConfigLoader.getInstance().getClient() : user);
        PostResults.setPassword(Utils.isNullOrBlank(password) ? ConfigLoader.getInstance().getClient() : password);
        PostResults.setUsingRunCase(usingRunCase == null ? ConfigLoader.getInstance().isUsingRunCase() : usingRunCase);
        PostResults.setRunID(this.runID);
    }

    /**
     * Builder class for overwriting any settings read from file.
     * Run ID can also be set from here as there is no key for it in properties file
     */
    public static class Builder {

        private String client;
        private String user;
        private String password;
        private Boolean usingRunCase;
        private int runID;

        /**
         * {@code runID} is 0 by default
         */
        @Contract(pure = true)
        public Builder() {
            this.runID = 0;
        }

        /**
         * Clients' TestRail URL address, e.g. Https://my-corp.testrail.io
         *
         * @param client URL address
         * @return this
         */
        public Builder withClient(String client) {
            nullCheck(client);
            this.client = client;
            return this;
        }

        /**
         * Testrail user
         *
         * @param user user
         * @return this
         */
        public Builder withUser(String user) {
            nullCheck(user);
            this.user = user;
            return this;
        }

        /**
         * Testrail password
         *
         * @param password password
         * @return this
         */
        public Builder withPassword(String password) {
            nullCheck(password);
            this.password = password;
            return this;
        }

        /**
         * Either uses Run ID and CaseID (TRUE — default), or only TestID to identify test case (FALSE).
         * Default case is preferred because it allows reusing test cases among runs.
         * Once created test case have immutable CaseID and creating new runs only require to change in runID.
         * So the tests can keep once given annotation forever, and runID can be changed on test configuration level.
         * Without this feature, one have to change test annotation between every run.
         * <<p>
         * If skipped default value will be used (TRUE — recommended)
         * </p>
         * @param usingRunCase Default TRUE, when using runID and caseID, FALSE, when using only testID (not recommended)
         * @return this
         */
        public Builder usingRunCase(boolean usingRunCase) {
            this.usingRunCase = usingRunCase;
            return this;
        }

        /**
         * Run ID — since {@code usingRunCase} is TRUE by default, it is good to change
         * this value to one corresponding the actual run.
         * It is set to 0 by default. If {@code usingRunCase} is set to FALSE
         * It is still a good practice to set Run ID to 0, even if it is not used.
         * <p>
         * Checks against negative number input.
         * </p>
         * @param runID Run ID from TestRail
         * @return this
         */
        public Builder withRunID(@Range(from = 0, to = Integer.MAX_VALUE) int runID) {
            this.runID = runID;
            return this;
        }

        /**
         * Builder method.
         * Prepares an object that facilitates overwriting data from the config file.
         * @return {@code Initialize} class instance
         */
        public Setup build() {
            Setup setup = new Setup();
            setup.client = client;
            setup.user = user;
            setup.password = password;
            setup.runID = runID;
            setup.usingRunCase = usingRunCase;
            return setup;
        }

        private void nullCheck(String value) {
            if (Utils.isNullOrBlank(value)) {
                log.error("Null or blank value passed.");
                throw new IllegalArgumentException("Value is null or blank");
            }
        }
    }
}
