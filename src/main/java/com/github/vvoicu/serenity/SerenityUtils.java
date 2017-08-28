package com.github.vvoicu.serenity;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;

public class SerenityUtils {

	/**
	 * Print data extracted from the serenity reports
	 * 
	 * @param outcomes
	 */
	public static void printSerenityReport(TestOutcomes outcomes) {

//		getLog().info("Printing Serenity Outcomes...");
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
}
