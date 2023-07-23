## Service Description
The web service can be reached at port 9000. It has a single /find endpoint
which accepts POST requests with JSON body of the following format:
```
{
    "data": [2,4,65],
    "target": 5
}
```
where
- `data` is an array of integers between -10<sup>9</sup> and 10<sup>9</sup>, 2 to 10<sup>4</sup> elements long;
- `target` is an optional integer. If not provided, will be taken from service configuration. 
If not found in configuration either, service will return an error

In a successful case the endpoint returns either a 
- 200 OK with a JSON body consisting of 
  - `indices` - an array of two integers, indices of first pair of numbers in input `data` array that make `target` when added
  - `numbers` - an array of two integers, actual elements of the array pointed at by `indices`
- 204 No Content with an empty body if no two numbers in `data` added amount to `target`

If input does not constitute a well-formed JSON with the expected fields or the fields do not meet the constraints described above, 
the response will be 400 Bad Request with a JSON body containing an object with a single `message` string field describing the problem with input.
If neither input nor configuration contains a target, the response will be 500 Internal Server Error with JSON body containing an object with a single `message` string.