# serenityrail
plugin for Serenity-BDD and Testrail. Publish Serenity test reports to Testrail.

#Configuration
 * the plugin expects three properties specified in the serenity.properties file.
          testrail.url (testrail project url - ex. https://project1.testrail.net/)
          testrail.user (testrail username)
          testrail.pass (testrail password)
 * add the plugin to your plugin section :
      <plugin>
				<groupId>com.github.vvoicu</groupId>
				<artifactId>testrail-reporter-maven-plugin</artifactId>
				<version>0.0.1</version>
			</plugin>
      
 * on the scenario feature file add a tag for testrail test ID (it is usually specified as ex. 'T260')
      on your scenario add the tag to which test to report the results 
      (the issue tag is for the jira integration, that is supported and documented in serenity-bdd)
      [sample]
      @issue:EK-732 \n
      @testrail:T260 \n
      @done @detailed @bug @EK-732 \n
      Scenario: (1) Clicking 'ADD A NEW ADDRESS' in the Address Book page causes the page to auto-scroll to the top. \n
  	    Given I am signed in to the website as a customer \n
	      And I navigate to the Address Book page \n
	      When I click on 'ADD A NEW ADDRESS' \n
	      Then I should see the 'ADD ADDRESS' section \n
	      And clicking 'CANCEL' should close the 'ADD ADDRESS' form \n

#Local configuration
    If you will take the code directly from github, just run the "mvn install" command so the plugin will get set up in your local maven repository
    You will still need to make all the specified configurations 
