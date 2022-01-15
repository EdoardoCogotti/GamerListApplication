import json
import pymongo
import datetime

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["GamerList"]
mycol = mydb["games"]
reviewCol = mydb["reviews"]

MAX_ITER = 3
 
# Opening JSON file
f = open('games_GOG.json', encoding='utf8')
 
# returns JSON object as
# a dictionary
data = json.load(f)

game_details_attr = ["single_player", "multi_player", "coop","controller_support","cloud_saves", "achievement"]
game_reviews_attr_to_rem = ['count', 'reviews_count', 'verified_owner', 'title', 'content']

attr_to_remove = ['price', 'overlay', 'leaderboard'] 
attr_to_add = ['id','store','tot_reviews', 'game_details']
#attr_to_rename = {'name':'username'} 

id = 2028850+1

# Iterating through the json
# list
tot_reviews=-1

for i, element in enumerate(data):
    if i>= MAX_ITER:
        break
    print(element["name"])
        #remove
    for attr in attr_to_remove:
        del element[attr] 
    tot_reviews = len(element['reviews'])
    for i in range(tot_reviews):
        element['reviews'][i]['creation_date'] = datetime.datetime.strptime(str(element['reviews'][i]['creation_date']), "%B %d, %Y")
        
        #add the review to the review collection in MongoDB
        review = {}
        review["game_name"] = element["name"] 
        review["username"]  = element['reviews'][i]["name"]
        review["creation_date"]  = element['reviews'][i]["creation_date"]
        review["store"] = "GOG"
        review["content"]  = element['reviews'][i]["content"]
        review["rating"]  = int(element['reviews'][i]["rating"])
        review["title"]  = element['reviews'][i]["title"]
        reviewCol.insert_one(review)
        

        for attr in game_reviews_attr_to_rem:
            #print(element['reviews'][i]['creation_date'])
            del element['reviews'][i][attr] 


    simpLanguages = []
    for language in element["languages"]:
        simpLanguages.append(language["name"])
    element["languages"] = simpLanguages

    element["achievements"] = len(element["achievements"])

    #for old_key, new_key in attr_to_rename.items():
        #element[new_key] = element.pop(old_key)

    # add
    #element["_id"]=id
    id += 1
    element["store"]="GOG"
    element["tot_reviews"]=tot_reviews

    #to date 
    element['release_date'] = datetime.datetime.strptime( element['release_date'], "%Y-%m-%dT%H:%M:%S%z")

    #move in game_details
    element["game_details"] = {}
    for attr in game_details_attr:
        element["game_details"][attr] = element.pop(attr)

    #print(element) 
    #txt = input("pausa")

    mycol.insert_one(element)
    #break
 
# Closing file
f.close()