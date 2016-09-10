# created by Vikas
# Notify Sequoia hack 2016
# future imports
from __future__ import unicode_literals

# standard imports

# django imports


# local imports
from flow_feed.models import Posts


def get_posts_by_upvotes():
    data = []
    all_posts = Posts.objects.all().order_by("-up_vote_counts")
    for post in all_posts:
        post_obj = {
            "img_url": post.image_url,
            "description": post.description,
            "like_counts": post.up_vote_counts,
            "address": post.address,
        }
        data.append(post_obj)
    post = {"posts": data }
    pass


def get_posts_by_location(latitude, longitude):
    pass


def get_address(latitude, longitude):
    pass


def get_posts_by_profile(user):
    pass


def save_img(requested_file):
    pass