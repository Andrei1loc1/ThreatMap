import requests
url = "http://localhost:8080/api/ingest"
data = {"message": "Jun 1 12:34:56 server sshd[1234]: Failed password for root from 192.168.1.100"}
headers = {"Content-Type": "application/json"}
response = requests.post(url, json=data, headers=headers)
print(response.status_code, response.text)