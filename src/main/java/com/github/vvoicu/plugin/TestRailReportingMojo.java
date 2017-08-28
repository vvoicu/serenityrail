package com.github.vvoicu.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

import com.github.vvoicu.testrail.APIClient;
import com.github.vvoicu.testrail.APIException;
import com.github.vvoicu.testrail.ResponderUtils;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomeLoader;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;


/**
 * Will publish the Serenity reports to TestRail. Each test that gets published should contain a tag (ex. @testrail:85 , where 85 is the test id in testrail) 
 * @author vvoicu
 *
 */
@Mojo(name = "publishreport")
public class TestRailReportingMojo extends AbstractMojo {

	private String reportFolderPath = "target" + File.separator + "site" + File.separator + "serenity" + File.separator;
	private String testRailBaseUrl = "";
	private String testRailUsername = "";
	private String testRailPassword = "";

	
	public void execute() {

		getLog().info("---- Serenity-bdd & TestRail Reporter ----");
		TestOutcomes outcomes = grabSerenityReportData(reportFolderPath);
		overwriteConfigDefaults();

//		SerenityUtils.printSerenityReport(outcomes);

		try {
			sendPostAddResultsToTestRail(outcomes);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (APIException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Will look in the default serenity reports folder and extract all the test
	 * results found.
	 * 
	 * @return
	 */
	public TestOutcomes grabSerenityReportData(String reportFolderPath) {

		getLog().info("Gathering Serenity reports data...");

		File reportFolder = new File(reportFolderPath);
		getLog().info("Reports absolute path: " + reportFolder.getAbsolutePath());

		TestOutcomes outcomes = null;

		try {
			outcomes = TestOutcomeLoader.testOutcomesIn(reportFolder);
		} catch (IOException e) {
			getLog().warn("Issue has been identified while trying to extract test outcomes");
			e.printStackTrace();
		}

		return outcomes;
	}

	/**
	 * Will extract the properties related to testrail (url, user, pass) that should be mentioned in the serenity.properties file.
	 */
	public void overwriteConfigDefaults() {
		getLog().info("Gathering global Testrail properties ");
		EnvironmentVariables variables = SystemEnvironmentVariables.createEnvironmentVariables();

		if (!variables.getProperty("testrail.url").isEmpty())
			testRailBaseUrl = variables.getProperty("testrail.url");
		if (!variables.getProperty("testrail.user").isEmpty())
			testRailUsername = variables.getProperty("testrail.user");
		if (!variables.getProperty("testrail.pass").isEmpty())
			testRailPassword = variables.getProperty("testrail.pass");

		getLog().info("BaseUrl: " + testRailBaseUrl);
		getLog().info("UserName: " + testRailUsername);
		getLog().info("UserPass: " + testRailPassword);
	}

	/**
	 * Will look at all tests that have the testrail tag and issue number and will
	 * create a request and send the results to the test rail test case.
	 * 
	 * @param outcomes
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws APIException
	 */
	@SuppressWarnings({ "rawtypes" })
	public void sendPostAddResultsToTestRail(TestOutcomes outcomes)
			throws MalformedURLException, IOException, APIException {

		getLog().info("Setting up TestRail Connection...");

		APIClient client = new APIClient(testRailBaseUrl);
		client.setUser(testRailUsername);
		client.setPassword(testRailPassword);

		for (final TestOutcome outcome : outcomes.getOutcomes()) {

			getLog().info("test lookup...");
			String testRailTestCaseTag = ResponderUtils.extractTestRailTagIssue(outcome);

			if (!testRailTestCaseTag.isEmpty()) {

				Map data = ResponderUtils.formRequestAddResult(outcome);
				getLog().info(testRailTestCaseTag + " TestCase reported.");
				client.sendPost("add_result/" + testRailTestCaseTag, data);
			}
		}
	}

}
