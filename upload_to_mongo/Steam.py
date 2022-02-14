import csv
import re
import string
import sys
import json
import pymongo
import datetime
import pandas as pd
import random
import time
import numpy as np


myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["GamerList"]
mycol = mydb["games"]
mycolReview = mydb["reviews"]

MAX_GAMES = 4500


#Load usernames
uf = open('usernames.json', encoding='utf8')
usernames = np.array(json.load(uf))
print(len(usernames))



    
def str_time_prop(start, end, time_format, prop):
    """Get a time at a proportion of a range of two formatted times.

    start and end should be strings specifying times formatted in the
    given format (strftime-style), giving an interval [start, end].
    prop specifies how a proportion of the interval to be taken after
    start.  The returned time will be in the specified format.
    """

    stime = time.mktime(time.strptime(start, time_format))
    etime = time.mktime(time.strptime(end, time_format))

    ptime = stime + prop * (etime - stime)

    return datetime.datetime.strptime(time.strftime(time_format, time.localtime(ptime)), '%d/%m/%Y')


def random_date(start, end, prop):
    return str_time_prop(start, end, '%d/%m/%Y', prop)





i=0
tot = 0
lastId = -1
lastPerc = -1

game = {}


f = open("./match/games.csv", encoding="utf-8", newline='')
reader = csv.reader(f)
rowG = next(reader)
reader = csv.DictReader(f, rowG)
bulkReviews = []
with open('./match/reviews.csv', encoding="utf-8", newline='') as mgf:
  matchReviewsFile = csv.reader(mgf)
  
  gId = None
  usernamesUsed = []
  usernamesToGet = []
  skip = False
  for rowR in matchReviewsFile:
    i+=1

    if i == 1:
      #matchReviewsFile.writerow(row)
      print(rowR)
      continue

    if i > MAX_GAMES:
      break

    id = int(rowR[0])
    #print(str(id) + "  -  "+str(gId))
    if gId != id:
      f = open("./match/games.csv", encoding="utf-8", newline='')
      reader = csv.reader(f)
      rowG = next(reader)
      reader = csv.DictReader(f, rowG)
      #print(rowG)
      while gId != id:
        rowG = next(reader)
        gId = int(re.findall(r'\d+', rowG['url'])[0])
        #print(str(id) + "  -  "+str(gId))
      print("Found "+str(gId)+"!!!")

      #same the previus game an reset the object
      
      if game:
        game["tot_reviews"] = len(game['reviews'])
        mycol.insert_one(game)
      game = {}
      usernamesUsed = []
      usernamesToGet = usernames.copy()
      

      #set the game filds
      #game["_id"] = gId
      game["url"] = rowG['url']
      game["store"] = "Steam"
      game["name"] = rowG['name']
      #if rowG["release_date"] == "2019":
      #  game["release_date"] = datetime.datetime.strptime(rowG["release_date"], "%y")
      #else:
      if rowG["release_date"] != "NaN":
        exCount = 0
        for fmt in ("%b %d, %Y", "%y", "%b %Y"):
          try:
            game["release_date"] = datetime.datetime.strptime(rowG["release_date"], fmt)
            break
          except ValueError:
            exCount+=1
            if exCount >= 3:
              raise ValueError('no valid date format found for ' + rowG["release_date"])
            pass
      else:
        game["release_date"] = random_date("1/1/2008", "1/08/2021", random.random())

      game['developer'] = rowG['developer']
      game['publisher'] = rowG['publisher']
      game['game_details'] = {
        "single_player": "Single-player" in rowG['game_details'],
        "multi_player": "Multiplayer" in rowG['game_details'],
        "coop": "Co-op," in rowG['game_details'],
        "controller_support": "Full it.unipi.gamerlist.controller support," in rowG['game_details'],
        "cloud_saves": "Cloud" in rowG['game_details'],
        "achievement": "Achievements" in rowG['game_details'],
      }
      game["languages"] = rowG["languages"].split(",")
      game["achievements"] = rowG["achievements"]
      if game["achievements"] == "NaN": 
        game["achievements"] = 0

      if game["achievements"] == "NaN":
        game["achievements"] = []
      game["genres"] = rowG["genre"].split(",")
      game["rating"] = rowG["mature_content"]
      if game["rating"] == "NaN":
        game["rating"] = None
        
      '''
      if rowG["all_reviews"] == "NaN":
        game["tot_reviews"] = 0
        game["all_reviews"] = None
      else:
        game["tot_reviews"] = int(rowG["all_reviews"][rowG["all_reviews"].find('(')+1 :  rowG["all_reviews"].find(')')].replace(",",""))
        game["all_reviews"] = rowG["all_reviews"]
      '''

      game["game_description"] = rowG["game_description"]
      if game["game_description"] == "NaN":
        game["game_description"] = ""

      game["minimum_requirements"] = rowG["minimum_requirements"]
      if game["minimum_requirements"] == "NaN":
        game["minimum_requirements"] = ""

      game["recommended_requirements"] = rowG["recommended_requirements"]
      if game["recommended_requirements"] == "NaN":
        game["recommended_requirements"] = ""

      game['reviews'] = []

    if skip :
      if id != 223330:
        continue
      else:
        skip = False

    
    if len(usernamesToGet) <= 100:
      continue

    #print(len(usernamesToGet))

    #make review object to insert into Mongo
    review = {}
    review["game_name"] = game["name"]
    randIndex = int(random.random()*len(usernamesToGet))
    review["username"] = usernamesToGet[randIndex]
    usernamesToGet = np.delete(usernamesToGet, randIndex)

    #st = time.time()
    #while review["username"] in usernamesUsed:
    #  review["username"] = random.choice(usernames)
    #print(str(i) + ": done in " + str(round(time.time() - st, 4)*1000) + " time...")
    
    game['reviews'].append({
      #"username":string"elbmek",
      "creation_date": random_date(game["release_date"].strftime("%d/%m/%Y"), "01/10/2021", random.random()),
      "helpful": int(random.random()*50),
      "positive": rowR[2] == "1",
    })

    usernamesUsed.append(review["username"])
    review["creation_date"] = game['reviews'][-1]["creation_date"]
    review["store"] = "Steam"
    review["helpful"] = game['reviews'][-1]["helpful"]
    review["positive"] = game['reviews'][-1]["positive"]
    review["content"] = rowR[1]

    game['reviews'][-1]["name"] = review["username"]

    #insert many reviews at once for performance reasons
    if len(bulkReviews) >= 1000:
      mycolReview.insert_many(bulkReviews)
      bulkReviews = []
    else:
      bulkReviews.append(review)
    


  if game:
    game["tot_reviews"] = len(game['reviews'])
    mycol.insert_one(game)

  if len(bulkReviews) >= 0:
      mycolReview.insert_many(bulkReviews)

























