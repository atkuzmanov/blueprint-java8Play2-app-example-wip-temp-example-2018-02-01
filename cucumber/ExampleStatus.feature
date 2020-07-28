@ExampleStatus
Feature: Check the application's status

  Scenario: Hit the application's status endpoint
    When The the application's status is requested
    Then the status is OK
