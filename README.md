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
If you want to read more about project:
All necessary information about this project here: -> https://prezi.com/p/rl-a9vtb6uwf
![image](![20945660](https://user-images.githubusercontent.com/79912262/141840872-bff2d454-4efa-4ac3-97df-5f233b63e35b.jpg))
## License
[MIT](https://choosealicense.com/licenses/mit/)
