# Vessel API

API that reads a csv file with raw data from vessels, cleans the data, 
perform statistics and makes the data available via 4 endpoints  

## Table of Contents

- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
    - [Clone the Repository](#clone-the-repository)
    - [Add CSV file](#add-csv-file)
    - [Build the Project](#build-the-project)
    - [Run the Application](#run-the-application)
- [API Endpoints](#api-endpoints)
- [Notes](#notes)
- [Assumptions](#assumptions)

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) 8 or higher installed.
- Apache Maven installed (for building the project).

## Getting Started

Follow these steps to set up and run the project locally.

### Clone the Repository

```bash
git clone https://github.com/GiatrGio/vessel_api.git
cd vessel_api
```

### Add CSV file
Add data CSV file in a location you like and then specify the file's path in application.properties file

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

### Run all Tests
```bash
mvn test
```

The application will be accessible at `http://localhost:8080`.

Swagger UI will be accessible at http://localhost:8080/swagger-ui.html

## API Endpoints

### /api/vessel/speed-difference endpoint

- Returns speed difference between the vessel’s actual speed over ground and the speed over ground that was proposed by the system, at each waypoint

### /api/vessel/invalid-data-report

- Returns problems identified in the original data, sorted by frequency of occurrence, descending.

### /api/vessel/data

- Returns raw and calculated data for a vessel between two dates. If a value was invalid in the original file it is returned as null.

### /api/vessel/compliance
Compares the speed deviation(actual vs proposed speed) of two vessels over all the valid timestamps. The vessel with the smaller speed deviation is the more compliant vessel with the system’s suggestions.


## Notes

For storing the data I am using in memory cache. The first time a call will be made, the data will be loaded and then remain in cache for the rest of the calls.

I didn't have time to filter the data for outliers but the implementation would be like this: While loading the 
data into objects, for each column we calculate the mean and standard deviation. 
Then we iterate over all the values of the column and check if the value is considered an outlier using mean and standard deviation.

I didn't have time to implement the tests in csvLoaderServiceTest.

The amount of data that is returned from a couple of endpoints can be big and thus make the Swagger page not responsive. 
To solve that we could add some kind of server side pagination depending on the business case. 
For this project, the problem can be solved by using something like Postman.

## Assumptions

During the data filtering if a value is invalid (for instance power is negative) I mark power as invalid 
but I still consider the rest of values valid. 

In the compliance report endpoint, I return both vessels. The first one is the one with the lowest speed deviation 
and thus the one that complies better.

In the return all data endpoint, I made the assumption that the returned data should not have invalid values. 
So I replace invalid values with null. 

