from requests import Session
from requests.exceptions import ConnectionError, Timeout, TooManyRedirects
import json

url = 'https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest'

headers = {
  'Accepts': 'application/json',
  'X-CMC_PRO_API_KEY': '0db26e7d-db21-4140-8913-ea096678c684',
}

session = Session()
session.headers.update(headers)

def get_id(name):
	parameters = {
		'slug': name
	}
	try:
		response = session.get(url, params=parameters)
		data = json.loads(response.text)
		if data['status']['error_code'] == 0:
			for i in data['data']:
	  			var = i
			return var
	except (ConnectionError, Timeout, TooManyRedirects) as e:
		print(e)