from flask import Flask, request, jsonify, request, render_template
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime, timedelta
from functools import wraps
from bs4 import BeautifulSoup
import requests
from transformers import pipeline
import jwt
from parsing import getUrls

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://postgres:Aizhan1212@localhost/PythonAssignment'
app.config['SECRET_KEY'] = 'thisismyflasksecretkey'
db = SQLAlchemy(app)
token_save = ""


class Users(db.Model):
	__tablename__ = 'users'
	id = db.Column('id', db.Integer, primary_key = True)
	login = db.Column('login', db.Unicode)
	password = db.Column('password', db.Unicode)
	token = db.Column('token', db.Unicode)
	def __init__(self, id, login, password, token):
		self.login = login
		self.password = password
		self.token = token

class Store(db.Model):
	__tablename__ = 'store'
	id = db.Column('id', db.Integer, primary_key = True)
	articles = db.Column('articles', db.Unicode)
	summaries = db.Column('summaries', db.Unicode)
	def __init__(self, id, articles, summaries):
		self.articles = articles
		self.summaries = summaries
		
def token_required(f):
	@wraps(f)
	def decorated(*args, **kwargs):
		find_token = request.args.get('token')
		if not find_token:
			return '''<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
		integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous"> <h1 class="text-warning"> Token is missing </h1>''', 403
		real = Users.query.filter_by(token = find_token).first()
		if real is None:
			return '''<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
		integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous"> <h1 class="text-danger"> Could not verify the token </h1>'''
		return f(*args, **kwargs)
	return decorated

@app.route('/')
def home():
	return render_template('home.html')

@app.route('/token')
def token():
	return '<h3>This is current token:<h3>' + token_save


@app.route('/protected')
@token_required
def protected():
	return '''
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
		integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">

	<div class="container d-flex align-items-center justify-content-center" style="height: 600px">
		<div class="row text-center">
			<div>
				<h1 class="text-success" class="font-size: 60px"> Success </h1>
				<p class="text-success" style="font-size: 40px"> Token is correct </p>
				<lottie-player src="https://assets5.lottiefiles.com/packages/lf20_4qldwfx4.json"  background="transparent"  speed="1"  style="width: 300px; height: 300px;" autoplay></lottie-player>
			</div>
		</div>
	</div>
	<script src="https://unpkg.com/@lottiefiles/lottie-player@latest/dist/lottie-player.js"></script>

	'''


@app.route('/login', methods=['GET', 'POST'])
def login():
	if request.method == 'POST':
		usernamedata = Users.query.filter_by(login = request.form['username'], password = request.form['password']).first()
		if usernamedata is not None: 
			token = jwt.encode({'user': usernamedata.login, 'exp':datetime.utcnow() + timedelta(minutes=15)}, app.config['SECRET_KEY'])
			global token_save 
			token_save = token
			print(token)
			update_token = Users.query.filter_by(id = usernamedata.id).first()
			token2 = token
			update_token.token = token2
			db.session.commit()
			return render_template('coin.html')
		else:
			error = 'Invalid login or password!'
			return render_template('login.html', error = error)
	return render_template('login.html')


@app.route('/registration', methods=['GET', 'POST'])
def registration():
	if request.method == 'POST':
		usernamedata = Users.query.filter_by(login = request.form['username']).first()
		user = request.form['username']
		passw = request.form['password']
		passw2 = request.form['password2']
		if user == "" or passw == "" or passw2 == "":
		 	error = 'Please fill all blanks'
		 	return render_template('registration.html', error = error)	
		if passw != passw2:
		 	error = 'Password mismatch'
		 	return render_template('registration.html', error = error)
		if usernamedata is None: 
			info = Users(int, user, passw, "")
			db.session.add(info)
			db.session.commit()
			success = 'Successfully registered!'
			return render_template('registration.html', success = success)
		else:
			error = 'This user already exists'
			return render_template('registration.html', error = error)
	return render_template('registration.html')	


@app.route('/coin', methods=['GET', 'POST'])
def coin():
	if request.method == 'POST':
		summarizer = pipeline("summarization")
		bitcoin_name = request.form['coin']
		lst = getUrls(bitcoin_name)
		if lst == 'Error':
			error = 'Invalid cryptocurrency name!'
			return render_template('coin.html', error=error)
		TOTAL_ARTICLES = ""
		TOTAL_SUMMARY = ""
		cnt = 1
		for i in lst:
			strin = []
			cookies = dict(BCPermissionLevel='PERSONAL')
			try:
				req = requests.get(i, headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36'}, cookies=cookies)
			except:
				continue
			soup = BeautifulSoup(req.content, 'html.parser')
			tmp = soup.find_all('p')
			for news in tmp:
				strin.append(news.get_text())
			ARTICLE = ' '.join(strin)
			ARTICLE = ARTICLE.replace('.', '.<eos>')
			ARTICLE = ARTICLE.replace('?', '?<eos>')
			ARTICLE = ARTICLE.replace('!', '!<eos>')
			max_chunk = 500
			sentences = ARTICLE.split('<eos>')
			current_chunk = 0 
			chunks = []
			for sentence in sentences:
					if len(chunks) == current_chunk + 1: 
						if len(chunks[current_chunk]) + len(sentence.split(' ')) <= max_chunk:
								chunks[current_chunk].extend(sentence.split(' '))
						else:
								current_chunk += 1
								chunks.append(sentence.split(' '))
					else:
	   					chunks.append(sentence.split(' '))
			for chunk_id in range(len(chunks)):
					chunks[chunk_id] = ' '.join(chunks[chunk_id])
			TOTAL_ARTICLES += '<h1>' + 'ARTICLE ' + str(cnt) + '</h1>' + '<p>' + ARTICLE + '</p>' + '</br>'
			summary = summarizer(chunks, max_length=120, min_length=30, do_sample=False)
			text = ' '.join([summ['summary_text'] for summ in summary])
			TOTAL_SUMMARY += '<h1>' + 'SUMMARY ' + str(cnt) + '</h1>' + '<p>' + text + '</p>' + '</br>'
			cnt = cnt + 1 
	 
		info = Store(int, TOTAL_ARTICLES, TOTAL_SUMMARY)
		db.session.add(info)
		db.session.commit()
		str_answer = Store.query.filter_by(summaries = TOTAL_SUMMARY).first()
		return '''
				<!DOCTYPE html>
				<html lang="en">	
				<head>
					<meta charset="UTF-8">
					<meta name="viewport" content="width=device-width, initial-scale=1.0">
					<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
					integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
					<title>Coin</title>
				</head>
				<body>
					<div style="width: 400px; padding: 10px;">
						<form method="POST" action="coin">
							<div class="input-group mb-3">
								<input name="coin" type="text" class="form-control" placeholder="Coin name">
								<button class="btn btn-success" type="submit" value="Check" >Check</button>
							</div>
						</form>
					</div>
					<div >
						<div class="row">
							<div class="col">
								{}
							</div>
							<div class="col">
								{}
							</div>
						</div>
					</div>
				</body>
				</html>'''.format(str_answer.articles, str_answer.summaries)
	else:
		return render_template('coin.html')

if __name__ == "__main__":
	app.run(debug=True)

