# created by Vikas
# Notify Sequoia hack 2016
# future imports
from __future__ import unicode_literals

# standard imports
import json
import time
import urllib
import math
import cloudinary as cloudinary
from cloudinary import uploader
# django imports


# local imports
from flow_feed.models import Posts

google_api_address = "http://maps.googleapis.com/maps/api/"
CLOUDINARY_CLOUD_NAME = "joyage"
CLOUDINARY_ACCESS_KEY = "819733926495133"
CLOUDINARY_SECRET_KEY = "JCXpREDEqTzBbvM6rn0BH-1Chio"


def get_posts_by_upvotes():
    data = []
    all_posts = Posts.objects.all()
    for post in all_posts:
        post_obj = {
            "image_url": post.image_url or "",
            "description": post.description,
            "location": post.address,
            "post_id": post.id,
            "like_count": post.up_vote_counts
        }
        data.append(post_obj)
    return data


def get_posts_by_location(latitude, longitude):
    posts = []
    min_lat, max_lat, min_long, max_long = min_max_lat_long(latitude, longitude, 5)
    post = Posts.objects.filter(
        latitude__gte=min_lat,
        longitude__gte=min_long,
        latitude__lte=max_lat,
        longitude__lte=max_long
    )[:10]
    for each_post in post:
        post_obj = {
            "image_url": each_post.image_url or "",
            "description": each_post.description,
            "location": each_post.address,
            "post_id": each_post.id,
            "like_count": each_post.up_vote_counts
        }
        posts.append(post_obj)
    return posts


def get_address(latitude, longitude):
    url = google_api_address + "geocode/json?latlng=" + str(latitude)+","+str(longitude) + "&sensor=true"
    data = json.loads(urllib.urlopen(url).read())
    try:
        address = str(data['results'][0]['formatted_address'])
        # address = str(data['results'][1]['formatted_address']).split(",")
    except:

        return "1, Residency Rd, Srinivas Nagar, Shanthala Nagar, Ashok Nagar, Bengaluru, Karnataka 560025"
    else:
        return address


# area around current lat lng
def min_max_lat_long(lat, lng, distance):
    min_lat = float(lat) - (0.009 * distance)
    max_lat = float(lat) + (0.009 * distance)
    min_long = round((float(lng) - ((0.009 * distance) / math.cos(float(lat) * math.pi / 180))), 5)
    max_long = round((float(lng) + ((0.009 * distance) / math.cos(float(lat) * math.pi / 180))), 5)
    print min_lat, max_lat, min_long, max_long
    return str(min_lat), str(max_lat), str(min_long), str(max_long)


def get_posts_by_profile(user_id):
    posts = []
    all_user_posts = Posts.objects.filter(
        user__id=user_id
    )
    for post in all_user_posts:
        post_obj = {
            "image_url": post.image_url or "",
            "description": post.description,
            "location": post.address,
            "post_id": post.id,
            "like_count": post.up_vote_counts
        }
        posts.append(post_obj)
    return posts


def save_img(requested_file):
    cloudinary.config(
        cloud_name=CLOUDINARY_CLOUD_NAME,
        api_key=CLOUDINARY_ACCESS_KEY,
        api_secret=CLOUDINARY_SECRET_KEY
    )
    response_data = uploader.upload(requested_file)
    return response_data
