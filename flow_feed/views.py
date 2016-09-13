# created by vikas
# Notify Sequoia hack 2016
# future imports
from __future__ import unicode_literals

# standard imports
import json

# django imports
from django.core.files.base import ContentFile
from django.core.files.storage import default_storage
from django.http import HttpResponse

# local imports
from django.utils.encoding import smart_text
from alert_media.settings import MEDIA_ROOT
from flow_feed.models import Posts, Votes
# from flow_feed.utilities.facebook_handler import postMessage
from flow_feed.utilities.facebook_handler import postMessage
from flow_feed.utilities.tweeter_handler import post_tweet
from flow_feed.utilities.utils import get_posts_by_upvotes, get_posts_by_location, get_posts_by_profile, save_img, \
    get_address


# Method to show all popular posts, around posts and profile
from users.models import Profile


def frontend_api(request):
    params = request.body
    args = json.loads(params)
    latitude = args.get("latitude", None)
    longitude = args.get("longitude", None)
    user_id = args.get("user_id", None)
    if latitude and longitude:
        posts_around_you = get_posts_by_location(latitude, longitude)
        obj = {"posts": posts_around_you}
    elif user_id:
        profile_posts = get_posts_by_profile(user_id)
        obj = {"posts": profile_posts}
    else:
        posts_by_upvotes = get_posts_by_upvotes()
        obj = {"posts": posts_by_upvotes}
    return HttpResponse(
        json.dumps(obj),
        content_type="application/json"
    )



def add_post(request):
    params = request.body
    args = json.loads(params)
    description = args.get("description",None)
    latitude = args.get("latitude",None)
    longitude = args.get("longitude",None)
    tags = args.get("category",None)
    user_id = args.get("user_id",None)
    image_url = args.get("image_url",None)

    try:
        user = Profile.objects.get(id = user_id)
    except:
        return HttpResponse(
            json.dumps({"msg": "user doesn't exist","status":"500"}),
            content_type="application/json"
        )

    address = get_address(latitude, longitude)
    post_obj, created = Posts.objects.get_or_create(
        description=description,
        latitude=latitude,
        longitude=longitude,
        tags=tags,
        address=address,
        user = user,
        image_url = image_url
    )
    print created
    print "+=========post created============="
    if created:
        file_name = image_url.split("/")[-1]
        path = MEDIA_ROOT + "/images/"+file_name
        print path
        # twitter_call
        if post_tweet(description, path):
            if postMessage(description, path):
                return HttpResponse(
                    json.dumps({"msg": "success","status":"200"}),
                    content_type="application/json"
                )
            else:
                return HttpResponse(
                    json.dumps({"msg": "error with facebook","status":"500"}),
                    content_type="application/json"
                )

            # return HttpResponse(
            #     json.dumps({"msg": "success","status":"200"}),
            #     content_type="application/json"
            # )
        else:
            return HttpResponse(
                json.dumps({"msg": "error while tweeting","status":"500"}),
                content_type="application/json"
            )
    else:
        return HttpResponse(
            json.dumps({"msg": "request already registered","status":"200"}),
            content_type="application/json"
        )


def upvote_post(request):
    params = request.body
    args = json.loads(params)
    post_id = args.get("post_id",None)
    user_id = args.get("user_id",None)
    try:
        post_obj = Posts.objects.get(id=int(post_id))
    except:
        return HttpResponse(
            json.dumps({"msg": "post doesn't exists","status":"500"}),
            content_type="application/json"
        )
    try:

        user_obj = Profile.objects.get(id=int(user_id))
    except:
        return HttpResponse(
            json.dumps({"msg": "user doesn't exists","status":"500"}),
            content_type="application/json"
        )
    already_liked = post_obj.up_vote.filter(
        liked_by__id=user_obj.id
    )
    if not already_liked:
        like_obj = Votes.objects.create(liked_by=user_obj)
        post_obj.up_vote.add(like_obj)
        return HttpResponse(
            json.dumps({"msg": "Success","status":"200"}),
            content_type="application/json"
        )
    else:
        return HttpResponse(
            json.dumps({"msg": "Already UpVoted !!","status":"201"}),
            content_type="application/json"
        )

def upload_post_image(request):
    try:
        # session_id = request.POST["session_id"]
        # upload avatar image
        post_image = request.FILES.getlist("photo", None)
        avatar_file_name = smart_text((post_image[0].name).replace(" ", "_"))
        path = default_storage.save('images/' + avatar_file_name, ContentFile(post_image[0].read()))
        path = "http://"+request.META['HTTP_HOST']+"/site_media/media/"+path
        # size = 500, 500
        # image = Image.open(path)
        # if hasattr(image, '_getexif'):  # only present in JPEGs
        #     for orientation in ExifTags.TAGS.keys():
        #         if ExifTags.TAGS[orientation] == 'Orientation':
        #             break
        #     e = image._getexif()  # returns None if no EXIF data
        #     if e is not None:
        #         exif = dict(e.items())
        #         if orientation in exif:
        #             orientation = exif[orientation]
        #
        #             if orientation == 3:
        #                 image = image.transpose(Image.ROTATE_180)
        #             elif orientation == 6:
        #                 image = image.transpose(Image.ROTATE_270)
        #             elif orientation == 8:
        #                 image = image.transpose(Image.ROTATE_90)
        #
        # image.thumbnail(size, Image.ANTIALIAS)
        # image.save(path)
        #
        # upload_to_s3(user.guid, path, AVATAR_BUCKET_NAME, True)
        # user.is_avatar_uploaded = True
        # user.save()
        response_obj = {}
        response_obj["post_image_url"] = path
        response_obj["status"] = "200"
        return HttpResponse(content_type="application/json", content=json.dumps(response_obj))
    except Exception as e:
        return HttpResponse(
            json.dumps({"error": "error in saving images"}),
            content_type="application/json"
        )


def login(request):
    params = request.body
    args = json.loads(params)
    phone_no = args.get("mobile_number", None)
    if phone_no:
        obj, created = Profile.objects.get_or_create(
            contact= str(phone_no),
            username= str(phone_no)
        )
        return HttpResponse(
            json.dumps({"user_id": obj.id, "mobile_number": phone_no})
        )



