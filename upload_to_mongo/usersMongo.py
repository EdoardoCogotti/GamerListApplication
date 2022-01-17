from randomuser import RandomUser
import json
import pymongo
import time
import math
import random

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["GamerList"]
mycol = mydb["users"]
gog_users_filename = 'usernames_cleaned.json'

gog_username_list = None

with open(gog_users_filename, 'r', encoding='utf-8') as json_data:
  gog_username_list = json.load(json_data)
colSize = len(gog_username_list)

admin_indexes = random.sample( range ( 1 , 50 ), 7 )

n = math.ceil(colSize/5000)

print("itero " + str(n) +  " volte")
for i in range(0,n):

  # each request is max 5000 fake users
  user_list = RandomUser.generate_users(colSize)
  for j, user in enumerate(user_list):
      data= {}
      if gog_username_list:
        data['username'] = gog_username_list.pop()
        data['cell'] = user.get_cell()
        data['state'] = user.get_state()
        data['city'] = user.get_city()
        data['dob'] = user.get_dob() #[:10]
        data['email'] = user.get_email()
        data['first_name'] = user.get_first_name()
        data['last_name'] = user.get_last_name()
        data['gender'] = user.get_gender()
        data['pwd'] = user.get_password()
        data['reg'] = user.get_registered()
        data['salt'] = user.get_login_salt()
        data['sha256'] = user.get_login_sha256()
        data['role'] = (j in admin_indexes)

        mycol.insert_one(data)

  # to avoid Error 503 wait 1 minute between two requests
  time.sleep(60)