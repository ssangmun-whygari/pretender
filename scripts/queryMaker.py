# -*- coding: utf-8 -*-

# Sample Python code for youtube.commentThreads.list
# See instructions for running these code samples locally:
# https://developers.google.com/explorer-help/code-samples#python

import os
import googleapiclient.discovery
import json
import time
from datetime import datetime, timedelta
import random

class Options:
  PREFIX = None
  START_INDEX = None
  END_INDEX = None

def main():

  prompt = input("이미 json 파일을 생성하였습니까?(Y / N)")
  if (prompt.strip() == "Y" or prompt.strip() == "y"):
    file_name = input("json 파일의 이름을 입력해주세요 : ") or "comments.json"
    with open(file_name, "r", encoding='utf-8') as file:
      data = json.loads(file.read())
  else:
    data = make_request()
  make_query(data)
  
def make_query(data: dict):
  # responses[id] = {
  #   "parent" : {"parent_id" : None,
  #               "id" : id,
  #               "content" : content,
  #               "post_date": topLevelComment["snippet"].get("publishedAt"),
  #               "correct_date" : topLevelComment["snippet"].get("updatedAt")
  #             }, 
  #   "children" : []
  # }
  print("아이디를 입력합니다.")
  Options.PREFIX = input("---PREFIX : ") or "test"
  Options.START_INDEX = int(input("---START_INDEX : ").strip()) or 1
  Options.END_INDEX = int(input("---END_INDEX : ").strip()) or 10
  print(f"id는 {Options.PREFIX}{Options.START_INDEX}@test.com부터 {Options.PREFIX}{Options.END_INDEX}@test.com중에서 랜덤으로 입력됩니다...")
  print("10% 확률로 is_deleted = 'Y'입니다.")

  print("post_date 순서대로 삽입하기 위해 인덱스를 정렬하고 있습니다...")
  start_time = time.time()
  all_replys = []
  for values in data.values():
    all_replys.append(values["parent"])
    for child in values["children"]:
      all_replys.append(child)
  all_replys.sort(key = lambda x: datetime.fromisoformat(x["post_date"]))
  for id, reply in enumerate(all_replys):
     reply["no"] = id + 1 # 1부터 시작
  end_time = time.time()
  print(f"{end_time - start_time:.2f} 초가 걸렸습니다.")
  print(f"댓글의 개수는 {len(all_replys)}개입니다.")
  print("data의 일부 value를 출력합니다 : ")
  print(list(data.values())[:10]) # 10개 출력

  print("===========================쿼리 작성 시작===========================")
  base_query = "insert into reviews(no, members_id, parent_no, content, post_date, correct_date, is_deleted, media_id, media_type, stars) \n"
  select_queries = []
  for key in data.keys():
    query = ""
    random_id = f"{Options.PREFIX}{random.randint(Options.START_INDEX, Options.END_INDEX)}@test.com"
    # 부모 댓글
    parent = data[key]["parent"]
    query += f"\tselect {parent["no"]}, '{random_id}', 0, \n"
    content = parent["content"].replace(";", "").replace("'", "") #MySQL에서는 ;가 포함되어 있으면 구문의 종료로 인식하여 에러가 남. 마찬가지로 ' 도 제거거
    query += f"\t\t'{content}', \n"
    post_date = datetime.fromisoformat(parent['post_date'])
    query += f"\t\t'{post_date.strftime('%Y-%m-%d %H:%M:%S')}', \n"
    correct_date = ""
    if parent.get("correct_date") == None:
      query += f"\t\tnull, \n"
    elif abs(post_date - datetime.fromisoformat(parent["correct_date"])) <= timedelta(minutes=1):
      query += f"\t\tnull, \n"
    else:
      correct_date = datetime.fromisoformat(parent["correct_date"]).strftime('%Y-%m-%d %H:%M:%S')
      query += f"\t\t'{correct_date}', \n"
    is_deleted = 'N'
    if (random.randint(1, 10) == 1):
       is_deleted = 'Y'
    query += f"\t\t'{is_deleted}', \n"
    query += "\t\t5092, 'tv', " # 무한도전, tv
    stars = (random.sample(['0.0', '0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0'], 1))[0]
    query += f"{stars}\n"
    select_queries.append(query)

    # 자식 댓글
    children = data[key]["children"]
    for child in children:
      query = ""
      random_id = f"{Options.PREFIX}{random.randint(Options.START_INDEX, Options.END_INDEX)}@test.com"
      query += f"\tselect {child["no"]}, '{random_id}', {parent["no"]}, \n"
      content = child["content"].replace(";", "").replace("'", "") #MySQL에서는 ;가 포함되어 있으면 구문의 종료로 인식하여 에러가 남. 마찬가지로 ' 도 제거거
      query += f"\t\t'{content}', \n"
      post_date = datetime.fromisoformat(child['post_date'])
      query += f"\t\t'{post_date.strftime('%Y-%m-%d %H:%M:%S')}', \n"
      correct_date = ""
      if child.get("correct_date") == None:
        query += f"\t\tnull, \n"
      elif abs(post_date - datetime.fromisoformat(child["correct_date"])) <= timedelta(minutes=1):
        query += f"\t\tnull, \n"
      else:
        correct_date = datetime.fromisoformat(parent["correct_date"]).strftime('%Y-%m-%d %H:%M:%S')
        query += f"\t\t'{correct_date}', \n"
      is_deleted = 'N'
      if (random.randint(1, 10) == 1):
       is_deleted = 'Y'
      query += f"\t\t'{is_deleted}', \n"
      query += f"\t\t5092, 'tv', " # 무한도전, tv
      query += "null\n" # 대댓글은 별점 못매김
      select_queries.append(query)

  base_query += " UNION ALL ".join(select_queries)
  base_query += ";"

  print("===========================쿼리 작성 완료===========================")

  print("review likes 데이터를 임의로 생성합니다")
  make_review_likes_query(data)

  with open("query(reviews).sql", 'w', encoding='utf-8') as file:
    file.write(base_query)
  print("===========================파일 쓰기 완료===========================")
  
def make_review_likes_query(all_parent: dict):
  # responses[id] = {
  #   "parent" : {"parent_id" : None,
  #               "id" : id,
  #               "content" : content,
  #               "post_date": topLevelComment["snippet"].get("publishedAt"),
  #               "correct_date" : topLevelComment["snippet"].get("updatedAt")
  #             }, 
  #   "children" : []
  # }
  
  # 대댓글이 있는 부모 댓글에서 50%, 그렇지 않은 댓글에서 10%를 뽑는다.
  media_id = 5092
  media_type = "tv"

  replys_with_children = list(filter(lambda x : len(x["children"]) > 0, all_parent.values()))
  replys_without_children = list(filter(lambda x : len(x["children"]) == 0, all_parent.values()))
  sampled_replys_with_children = random.sample(replys_with_children, len(replys_with_children) // 2)
  sampled_replys_without_children = random.sample(replys_with_children, len(replys_without_children) // 10)

  base_query = "insert ignore into review_likes(media_id, media_type, reviews_no, members_id) \n"
  select_queries = []
  member_id_list = [f"{Options.PREFIX}{index}@test.com" for index in range(Options.START_INDEX, Options.END_INDEX + 1)]
  # 대댓글이 있는 부모 댓글이 그렇지 않은 댓글보다 좀 더 많은 좋아요를 받는다.
  for reply in sampled_replys_with_children:
    parent_likes = random.randint(
      int((Options.END_INDEX - Options.START_INDEX + 1) / 1.3), 
      (Options.END_INDEX - Options.START_INDEX + 1))
    for member_id in random.sample(member_id_list, parent_likes):
      query = ""
      reviews_no = reply["parent"]["no"]
      query += f"\tselect {media_id}, '{media_type}', {reviews_no}, '{member_id}'\n"
      select_queries.append(query)
    # 자식 댓글은 부모 댓글 이하의 좋아요를 받는다.
    for child in reply["children"]:
      for member_id in random.sample(member_id_list, random.randint(0, parent_likes)):
        query = ""
        reviews_no = child["no"]
        query += f"\tselect {media_id}, '{media_type}', {reviews_no}, '{member_id}'\n"
        select_queries.append(query)
  for reply in sampled_replys_without_children:
    likes = random.randint(1, int((Options.END_INDEX - Options.START_INDEX + 1) / 1.3))
    for member_id in random.sample(member_id_list, likes):
      query = ""
      reviews_no = reply["parent"]["no"]
      query += f"\tselect {media_id}, '{media_type}', {reviews_no}, '{member_id}'\n"
      select_queries.append(query)
  base_query += " UNION ALL ".join(select_queries)
  base_query += ";"

  # 파일 쓰기
  with open("query(review_likes).sql", 'w', encoding='utf-8') as file:
    file.write(base_query)


def make_request() -> dict:
  # Disable OAuthlib's HTTPS verification when running locally.
  # *DO NOT* leave this option enabled in production.
  os.environ["OAUTHLIB_INSECURE_TRANSPORT"] = "1"

  api_service_name = "youtube"
  api_version = "v3"
  DEVELOPER_KEY = "my_sceret_key"

  youtube = googleapiclient.discovery.build(
      api_service_name, api_version, developerKey = DEVELOPER_KEY)

  topLevelCommentsNumber = 0
  totalCommentsNumber = 0
  responses = {}
  # "id" : {parent : {}, children : [{}, {}]}
  nextPageToken = ""
  isFinalRequest = False

  # topLevelComment 정보 채우기
  while (True):
    if (isFinalRequest == True):
        print("topLevelComment 요청 종료")
        print(f"topLevelCommentsNumber : {topLevelCommentsNumber}")
        break
    
    print("요청 중...")
    print(f"topLevelCommentsNumber : {topLevelCommentsNumber}")
    request = youtube.commentThreads().list(
        part="snippet,replies",
        videoId="PnBxrNMJzeo",
        maxResults=100,
        pageToken=nextPageToken
    )
    response = request.execute()

    nextPageToken = response.get("nextPageToken")
    print(f"nextPageToken : {nextPageToken}")
    if (nextPageToken == None):
        isFinalRequest = True

    for item in response['items']:
        topLevelComment = item["snippet"]['topLevelComment']
        id = topLevelComment["id"]
        content = topLevelComment["snippet"]["textOriginal"]
        responses[id] = {
              "parent" : {"parent_id" : None,
                          "id" : id,
                          "content" : content,
                          "post_date": topLevelComment["snippet"].get("publishedAt"),
                          "correct_date" : topLevelComment["snippet"].get("updatedAt")
                        }, 
              "children" : []
          }
        topLevelCommentsNumber += 1
  
  totalCommentsNumber = topLevelCommentsNumber
  print("=============대댓글 요청 시작...=============")
  parentReplyNumber = 0
  parentWithChildReplyNumber = 0
  
  for key in responses.keys():
    isFinalRequest = False
    nextPageToken = None
    parentReplyNumber += 1
    while True:
      if isFinalRequest == True:
        break
      
      request = youtube.comments().list(
        part="id,snippet",
        parentId=key,
        pageToken=nextPageToken
      )
      response = request.execute()
      if (len(response["items"]) != 0):
        print(f"{len(response["items"])}개의 댓글 처리 중...")
      if (parentReplyNumber % 10 == 0):
        print(f"============={parentReplyNumber}개의 부모 댓글을 처리하고 있습니다")
      
      for item in response["items"]:
        child = responses[key]["children"]
        child.append({
          "parent_id":  key,
          "id": item["id"],
          "content": item["snippet"]["textOriginal"],
          "post_date": item["snippet"].get("publishedAt"),
          "correct_date": item["snippet"].get("updatedAt")
        })
        totalCommentsNumber += 1

      nextPageToken = response.get("nextPageToken")
      if nextPageToken == None:
        isFinalRequest = True
        if (len(response["items"]) != 0):
          parentWithChildReplyNumber += 1

  print(f"전체 부모 댓글 수 : {parentReplyNumber}, 댓글이 달린 부모 댓글 수 : {parentWithChildReplyNumber}")
  print("=============모든 댓글 저장 완료=============")
  
  # 파일에 저장
  with open("comments.json", 'w', encoding='utf-8') as file:
     file.write(json.dumps(responses, ensure_ascii=False))
  return responses

if __name__ == "__main__":
  main()