Feature: Cruise Search

  Scenario: Search cruises in London
    Given I am on Booking.com homepage for cruise search
    When I search cruises in "London"
    And For the next 5 days
    And Apply filter of "Boat tours & cruises"
    Then Cruise results should be displayed
