# NetworkLogger.py
import requests
from requests import Session

session = Session()

def log_request(req):
    print(f"Request: {req.method} {req.url} \nHeaders: {req.headers} \nBody: {req.body}")

def log_response(resp):
    print(f"Response: {resp.status_code} \nHeaders: {resp.headers} \nContent: {resp.content}")

# Example of logging a GET request to Life360's API endpoint
req = requests.Request('GET', 'https://life360-api-endpoint.com')
prep = req.prepare()
log_request(prep)
resp = session.send(prep)
log_response(resp)
