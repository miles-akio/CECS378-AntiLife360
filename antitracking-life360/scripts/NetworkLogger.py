import requests
from requests import Session

session = Session()

def log_request(req):
    print(f"Request: {req.method} {req.url} \nHeaders: {req.headers} \nBody: {req.body}")

def log_response(resp):
    print(f"Response: {resp.status_code} \nHeaders: {resp.headers} \nContent: {resp.content}")

# Example of logging a GET request to a placeholder API endpoint
url = 'https://jsonplaceholder.typicode.com/posts'  # Placeholder API for testing
req = requests.Request('GET', url)
prep = req.prepare()

# Log the request
log_request(prep)

# Send the request and log the response
try:
    resp = session.send(prep)
    log_response(resp)
except requests.exceptions.RequestException as e:
    print('Request failed:', e)
