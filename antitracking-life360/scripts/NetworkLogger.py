import requests
from requests import Session

session = Session()

# Logs request details
def log_request(req):
    print(f"Request: {req.method} {req.url} \nHeaders: {req.headers} \nBody: {req.body}")

# Logs response details
def log_response(resp):
    print(f"Response: {resp.status_code} \nHeaders: {resp.headers} \nContent: {resp.content}")

# Example API request (placeholder for testing)
url = 'https://jsonplaceholder.typicode.com/posts'
req = requests.Request('GET', url)
prep = req.prepare()

log_request(prep)

try:
    resp = session.send(prep)
    log_response(resp)
except requests.exceptions.RequestException as e:
    print('Request failed:', e)
