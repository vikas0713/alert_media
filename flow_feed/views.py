# created by vikas
# Notify Sequoia hack 2016
# future imports
from __future__ import unicode_literals

# standard imports
import json

# django imports
from django.contrib.auth.models import User
from django.http import HttpResponse

# local imports
from flow_feed.models import Posts, Votes
from flow_feed.utilities.utils import get_posts_by_upvotes, get_posts_by_location, get_posts_by_profile, save_img, \
    get_address


# Method to show all popular posts, around posts and profile
def frontend_api(request):
    if request.POST:
        response_data = []
        latitude = request.POST.get("latitude", None)
        longitude = request.POST.get("longitude", None)
        user_id = request.POST.get("user_id", None)
        if latitude and longitude:
            posts_around_you = get_posts_by_location(latitude, longitude)
            response_data.append(posts_around_you)
        elif user_id:
            profile_posts = get_posts_by_profile(request.user)
            response_data.append(profile_posts)
        else:
            posts_by_upvotes = get_posts_by_upvotes()
            response_data.append(posts_by_upvotes)
        return HttpResponse(
            json.dumps({"response": response_data}),
            content_type="application/json"
        )
    else:
        return HttpResponse(
            json.dumps({"response": "bad request"}),
            content_type="application/json"
        )


def add_post(request):
    if request.POST:
        img_object = request.FILES
        a = {}
        file = img_object.getlist("image")
        # print file
        for i in file:
            a["name"] = i.name
            a["data"] = i.chunks()
        uploaded_img = open("/uploads/sample"+a["name"]+".jpg", "wb+")
        for chunk in a["data"]:
            # filename = "sample"+request.FILES['file_img'].name+".jpg"
            uploaded_img.write(chunk)

        img_url = save_img("/uploads/sample"+a["name"]+".jpg")
        if not img_url:
            return HttpResponse(
                json.dumps({"error": "error uploading image"}),
                content_type="application/json"
            )
        else:
            try:
                description = request.POST["description"]
                latitude = request.POST["latitude"]
                longitude = request.POST["longitude"]
                tags = request.POST["tags"]
                user_id = request.POST["user_id"]
            except KeyError as e:
                return HttpResponse(
                    json.dumps({"error": "request parameters are not correct"}),
                    content_type="application/json"
                )
            address = get_address(latitude, longitude)
            post_obj, created = Posts.objects.get_or_create(
                image_url=img_url,
                description=description,
                latitude=latitude,
                longitude=longitude,
                tags=tags,
                address=address
            )
            if created:
                # twitter_call
                return HttpResponse(
                    json.dumps({"msg": "success"}),
                    content_type="application/json"
                )
            else:
                return HttpResponse(
                    json.dumps({"msg": "request already registered"}),
                    content_type="application/json"
                )


def upvote_post(request):
    if request.POST:
        try:
            post_id = request.POST["post_id"]
            user_id = request.POST["user_id"]
        except:
            return HttpResponse(
                json.dumps({"error": "parameters not correct"}),
                content_type="application/json"
            )
        try:
            post_obj = Posts.objects.get(id=int(post_id))
        except:
            return HttpResponse(
                json.dumps({"error": "post doesn't exists"}),
                content_type="application/json"
            )
        try:
            user_obj = User.objects.get(id=int(user_id))
        except:
            return HttpResponse(
                json.dumps({"error": "user doesn't exists"}),
                content_type="application/json"
            )
        already_liked = post_obj.upvote.filter(
            liked_by__id=user_obj.id
        )
        if not already_liked:
            like_obj, created = Votes.objects.create()
            like_obj.liked_by = user_obj
            like_obj.save()
            post_obj.upvote.add(like_obj)
            return HttpResponse(
                json.dumps({"msg": "Success"}),
                content_type="application/json"
            )
        else:
            return HttpResponse(
                json.dumps({"msg": "already liked"}),
                content_type="application/json"
            )
