# Final-Project-Python

This project was created as the Final Project for the Advanced Programming 1 course. 
It is a website with login and registration forms and an input form where you enter any cryptocurrency name you want, after which the algorithm will parse all related news and blogs from coinmarketcap.com(https://coinmarketcap.com/) and will summarize each paragraph separately. Then it will show them on the page.

### Installation
Copy from source
```bash
git clone https://github.com/Assylken/Final-Project-Python.git
```

### Usage

```
from flask import Flask, request, jsonify, request, render_template
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime, timedelta
from functools import wraps
from bs4 import BeautifulSoup
import requests
from transformers import pipeline
import jwt
```

### Examples
(/login, /register) - Login and Registration pages. Store in PostgreSQL db. 
![alt text](![image](https://user-images.githubusercontent.com/79912262/141839589-bcbd6219-eec9-43d9-ba69-53135bcf4bbf.png))
![image](![image](https://user-images.githubusercontent.com/79912262/141839842-dda17baf-6a55-4e91-97c2-259a438e7cfd.png))
(/protected) - On this page you can check the validation of your token
![image](![image](https://user-images.githubusercontent.com/79912262/141839842-dda17baf-6a55-4e91-97c2-259a438e7cfd.png))
![image](https://user-images.githubusercontent.com/79912262/138473334-88d2cb9d-20bd-4a95-be9e-354d20ce0ab9.png)

## License
[MIT](https://choosealicense.com/licenses/mit/)
