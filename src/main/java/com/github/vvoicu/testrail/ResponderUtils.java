package com.github.vvoicu.testrail;

import java.util.HashMap;
import java.util.Map;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;

public class ResponderUtils {

	/**
	 * Transform the Serenity status message into an int status code (as testrail expects it)
	 * @param status
	 * @return
	 */
	public static int convertStatusToInt(String status) {
		if (status.contains("SUCCESS"))
			return 1;
		if (status.contains("FAILURE"))
			return 5;
		return 3;
	}

	
	/**
	 * Form a map of key:values that will be sent via the request body in json format to testrail.
	 * @param outcome
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map formRequestAddResult(TestOutcome outcome) {

		Map data = new HashMap();
		data.put("status_id", convertStatusToInt(String.valueOf(outcome.getResult())));
		data.put("comment", outcome.getCompleteName() + " \n " + outcome.getTestSteps());
		data.put("version", String.valueOf(outcome.getDurationInSeconds()));
		data.put("elapsed", String.valueOf((int) Math.round(outcome.getDurationInSeconds())) + "s");
		data.put("defects", outcome.getFailureDetails().getCompleteErrorMessage());
		data.put("assignedto_id", "");
		
		return data;
	}
	
	
	/**
	 * Will extract the tag with the key = testrail and will sanitize the id (ex. from T260 to 260)
	 * In the URL it is expected a numeric id.
	 * @param outcome
	 * @return
	 */
	public static String extractTestRailTagIssue(TestOutcome outcome) {
		for (TestTag itemNow : outcome.getTags()) {
			if(itemNow.getType().contains("testrail"))
				return itemNow.getName().replaceAll("T", "");
		}

		return "";
	}
}
