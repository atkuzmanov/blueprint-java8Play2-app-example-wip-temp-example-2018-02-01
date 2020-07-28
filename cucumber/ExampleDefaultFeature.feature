@ExampleAnnotation
Feature: Process a notification

  Background: Clean up
    Given that MongoDB is empty
    And that DynamoDB is empty

    # Happy path scenarios first.
  Scenario: Processing a notification
    Given a sqs notification is received
    When the notification has been processed
    # When all notifications have been processed
    Then a new record exists in MongoDB with the right information


