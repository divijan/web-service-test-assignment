## Service Description
The web service has a single `/find` endpoint
which accepts POST requests with JSON body of the following format:
```json
{
    "data": [2,4,65],
    "target": 5
}
```
where
- `data` is an array of integers between -10<sup>9</sup> and 10<sup>9</sup>, 2 to 10<sup>4</sup> elements long;
- `target` is an optional integer. If not provided, will be taken from service configuration. 
If not found in configuration either, service will return an error (see below)

### Successful Response  
- if such a pair of numbers in `data` was found that their sum equals to `target`, `200 OK` with a JSON body as follows:
  ```json
  {
    "indices": [1,2],
    "numbers": [5,10]
  }
  ```
  where
  - `indices` is an array of two integers, indices of said numbers in input `data` array
  - `numbers` is an array of two integers, actual elements of the `data` array pointed at by `indices`
- if no two numbers in `data` would amount to `target` when added, `204 No Content` with an empty body

### Error Response 
Error response bodies follow the same pattern:
```json
  {
    "type": "ValidationError",
    "message": "could not parse data array of ints and optional target int from request body"
  }
  ```
where
- `type` string field describes the type of an error; 
- `message` string field contains the specific error message. 

Error cases and corresponding response codes:
- If input constitutes a well-formed JSON with expected fields missing or the fields do not meet the constraints described above, 
the response status will be `400 Bad Request`;
- If neither input nor configuration contains a `target`, the response code will be `500 Internal Server Error`;
- If number of requests with correctly formed (valid or invalid) JSON payload per minute has exceeded maximum configured, 
the response code will be `429 Too Many Requests`.

## Running Locally in Docker
To create a docker image locally, in project root run
```shell
sbt Docker / publishLocal
```
To run the web service in a docker container, run 
```shell
docker run -p 9999:9000 web-service-test-assignment:1.0-SNAPSHOT
```
optionally substituting `9999` with the port on the host computer where the service needs to be available.