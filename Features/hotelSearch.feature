Feature: Hotel Search

  Scenario: Search hotels in Nairobi
    Given I am on Booking.com homepage for hotel search
    When I search hotels in "Nairobi" for 5 days for 4 people
    And Sort by top reviewed and apply filter for "Free Wifi"
    Then Hotel results should be displayed
