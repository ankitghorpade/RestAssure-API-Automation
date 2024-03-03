Gradle Installation

For Ubuntu,Mac and Windows use the below link to setup Gradle.

    https://gradle.org/install/

Running the Workflow

    Goto root directory of the project.

    To run all the testcases use the below command in the commandline.
        gradle cleanTest test --info

     //specific test method
      gradle cleanTest  test --tests com.secpod.testCases.DirectoryChecks.makeDir

     //specific test method, use wildcard for packages
     includeTestsMatching "*SomeTest.someSpecificFeature"

     //specific test class
     gradle cleanTest  test --tests com.secpod.testCases.DirectoryChecks

     //specific test class, wildcard for packages
     includeTestsMatching "*.SomeTest"

     //all classes in package, recursively
     includeTestsMatching "com.gradle.tooling.*"

     //all integration tests, by naming convention
      includeTestsMatching "*IntegTest"

     //only ui tests from integration tests, by some naming convention
     includeTestsMatching "*IntegTest*ui"

Report

    To check the reports goto below mentioned path in the root directory of the project.
        build/reports/tests/test/index.html


   
