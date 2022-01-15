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


myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["GamerList"]
mycol = mydb["games"]
mycolReview = mydb["reviews"]

MAX_GAMES = 4


#Load usernames
uf = open('usernames.json', encoding='utf8')
usernames = json.load(uf)



    
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
with open('./match/reviews.csv', encoding="utf-8", newline='') as mgf:
  matchReviewsFile = csv.reader(mgf)

  gId = None
  usernamesUsed = []
  for rowR in matchReviewsFile:
    i+=1

    if i == 1:
      #matchReviewsFile.writerow(row)
      print(rowR)
      continue

    if i > MAX_GAMES:
      #matchReviewsFile.writerow(row)
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
        mycol.insert_one(game)
      game = {}
      usernamesUsed = []

      #set the game filds
      #game["_id"] = gId
      game["url"] = rowG['url']
      game["store"] = "Steam"
      game["name"] = rowG['name']
      if rowG["release_date"] == "2019":
        game["release_date"] = datetime.datetime.strptime(rowG["release_date"], "%y")
      else:
        if rowG["release_date"] != "NaN":
          game["release_date"] = datetime.datetime.strptime(rowG["release_date"], "%b %d, %Y")
        else:
          game["release_date"] = random_date("1/1/2008", "1/08/2021", random.random())
      game['developer'] = rowG['developer']
      game['publisher'] = rowG['publisher']
      game['game_details'] = {
        "single_player": "Single-player" in rowG['game_details'],
        "multi_player": "Multiplayer" in rowG['game_details'],
        "coop": "Co-op," in rowG['game_details'],
        "controller_support": "Full controller support," in rowG['game_details'],
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
        
      if rowG["all_reviews"] == "NaN":
        game["tot_reviews"] = None
        game["all_reviews"] = None
      else:
        game["tot_reviews"] = int(rowG["all_reviews"][rowG["all_reviews"].find('(')+1 :  rowG["all_reviews"].find(')')].replace(",",""))
        game["all_reviews"] = rowG["all_reviews"]
      game["game_description"] = rowG["game_description"]
      game["minimum_requirements"] = rowG["minimum_requirements"]
      game["recommended_requirements"] = rowG["recommended_requirements"]
      game['reviews'] = []

    game['reviews'].append({
      #"username":string"elbmek",
      "creation_date": random_date(game["release_date"].strftime("%d/%m/%Y"), "01/10/2021", random.random()),
      "positive": rowR[2] == "1",
      "helpful": int(rowR[3]),
    })

    #make review object to insert into Mongo
    review = {}

    review["game_name"] = game["name"]
    review["username"] = random.choice(usernames)
    while review["username"] in usernamesUsed:
      review["username"] = random.choice(usernames)
    usernamesUsed.append(review["username"])
    review["creation_date"] = game['reviews'][-1]["creation_date"]
    review["helpful"] = game['reviews'][-1]["helpful"]
    review["positive"] = game['reviews'][-1]["positive"]
    review["content"] = rowR[1]

    mycolReview.insert_one(review)


  if game:
    mycol.insert_one(game)

























