
# Comments

## Questions

### The Single Charge Rule

There are several ways to interpret the single charge rule. Is it done correctly? The current implementation would have to be confirmed with the product owners.

## How to use it?

Just open the Gradle project in IntelliJ and run the main method of the `CongestionCalculatorApplication` class. Then make 
a POST call to `http://localhost:8080/calculate-tax` with a JSON body looking something like this:

```json
{
    "vehicleType": "car",
    "taxEventTimes": [
        "2013-01-14 21:00:00",
        "2013-01-15 21:00:00",
        "2013-02-07 06:23:27",
        "2013-02-07 15:27:00",
        "2013-02-08 06:27:00",
        "2013-02-08 06:20:27",
        "2013-02-08 14:35:00",
        "2013-02-08 15:29:00",
        "2013-02-08 15:47:00",
        "2013-02-08 16:01:00",
        "2013-02-08 16:48:00",
        "2013-02-08 17:49:00",
        "2013-02-08 18:29:00",
        "2013-02-08 18:35:00",
        "2013-03-26 14:25:00",
        "2013-03-28 14:07:27"   
    ]
}
```

The response will contain teh vehicle type, the tax and a list of tax events confirming the date times sent in and explaining how their tax was calculated).

```
{
    "vehicleType": "CAR",
    "tax": 89,
    "taxEvents": [
        {
            "dateTime": "2013-01-14T21:00:00",
            "status": "TAX_FREE_TIME",
            "tax": 0
        },
        {
            "dateTime": "2013-02-07T06:23:27",
            "status": "TAXABLE",
            "tax": 8
        },
        ...
    ]
}
```

## The date-time format
I used the date format found on the post-it note in the request, but I would prefer to use the ISO format, e.g. `2013-02-08 15:27:00`. 

But it is dependent on if we can influence the format the callers of the service is using. Since they will have to adopt to the format of the response anyways it's likely that we can.

# Some of the bugs found

## Motorcycle vs Motorbike
The name and value of the Motorbike class didn't match the value in the list of exempt vehicle types. I selected to change
the Motorbike class.

## Year 113

The Date.getYear() method returns the current year minus 1900. This was then compared to the year given as input and the 
result was that July wasn't free from taxes. I added 1900 to the year before comparing.
