from types import new_class
from requests import Session
import json
from flask import Flask

from getId import get_id

app = Flask(__name__)
session = Session()

def getUrls(name):
	get_url_news = 'https://api.coinmarketcap.com/content/v3/news?coins=%s'
	coin_id = get_id(name.lower())
	url = get_url_news %coin_id
	response = session.get(url)
	data = json.loads(response.text)
	if data['status']['error_code'] == '500':
		return 'Error'
	leng = (len(data['data']))
	thislist = []
	for i in range(leng):
		thislist.append(data['data'][i]['meta']['sourceUrl'])
	return thislist

if __name__ == '__main__':
     app.run(debug=True)