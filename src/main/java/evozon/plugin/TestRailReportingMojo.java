package evozon.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

import evozon.testrail.APIClient;
import evozon.testrail.APIException;
import evozon.testrail.ResponderUtils;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomeLoader;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;

@Mojo(name = "railer")
public class TestRailReportingMojo extends AbstractMojo {
	// @parameter expression="${project.build.directory}"

	private String reportFolderPath = "target" + File.separator + "site" + File.separator + "serenity" + File.separator;
	private String testRailBaseUrl = "";
	private String testRailUsername = "";
	private String testRailPassword = "";
	// private String testRailBaseUrl = "https://matchesfashion1.testrail.net/";
	// private String testRailUsername = "voicu.vac@evozon.com";
	// private String testRailPassword = "Matches123";

	public void execute() {

		getLog().info("---- Serenity-bdd & TestRail Reporter ----");
		TestOutcomes outcomes = grabSerenityReportData();
		overwriteConfigDefaults();

		// printSerenityReport(outcomes);

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

	public void overwriteConfigDefaults() {
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
	 * Will look in the default serenity reports folder and extract all the test
	 * results found.
	 * 
	 * @return
	 */
	public TestOutcomes grabSerenityReportData() {

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
	 * Print data extracted from the serenity reports
	 * 
	 * @param outcomes
	 */
	public void printSerenityReport(TestOutcomes outcomes) {

		getLog().info("Printing Serenity Outcomes...");
		for (final TestOutcome outcome : outcomes.getOutcomes()) {
			System.out.println("-----------------------------");
			System.out.println(outcome.getCompleteName());
			System.out.println(outcome.getTestCaseName());
			System.out.println(outcome.getResult());
			System.out.println(outcome.getDurationInSeconds());
			System.out.println(outcome.getDataTable());
			System.out.println(outcome.getIssueKeys());
			System.out.println(outcome.getTags());
			System.out.println(outcome.getDescription());
			System.out.println(outcome.getIssues());
			System.out.println(outcome.getFailureDetails().getCompleteErrorMessage());
			System.out.println(outcome.getFailureDetails().getConciseErrorMessage());

		}
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
	@SuppressWarnings({ "rawtypes"})
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
//				JSONObject response = (JSONObject) client.sendPost("add_result/" + testRailTestCaseTag, data);

				// System.out.println("TestRail response: " + response.toString());
			}
		}
	}

}
