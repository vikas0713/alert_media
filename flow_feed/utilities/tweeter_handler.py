import tweepy
import os
import sys


def get_api(cfg):
    auth = tweepy.OAuthHandler(cfg['consumer_key'], cfg['consumer_secret'])
    auth.set_access_token(cfg['access_token'], cfg['access_token_secret'])
    return tweepy.API(auth)


def main(messageToMain, photoPath):
    # Fill in the values noted in previous step here
    cfg = {
        "consumer_key": "IHFIlWW1BNz4Xb6FG3P4IrVd0",
        "consumer_secret": "bjgwQuFtioipprsXDIsLcwYEfj7jI9yDux95WoJm4HvCT0qili",
        "access_token": "774568366790934528-G4VzKGfgpWj0C8HhMRv9ZBxmGJdOqi1",
        "access_token_secret": "gJOmQxKvX2LatGkboMirMqOy5lPs54gL7lEBLTtvbZrBH"
    }

    api = get_api(cfg)
    # Yes, tweet is called 'status' rather confusing
    try:
        if not photoPath:
            tweet = messageToMain  # "Hello, world!"
            status = api.update_status(status=tweet)
        else:
            tweet = messageToMain
            photoLoc = photoPath
            # UpdateStatus of twitter called with the image file
            api.update_with_media(photoLoc, status=tweet)
    except Exception as ex:
        print ex
        print "===============TWEET================"



def post_tweet(writeMessage, photoLoc):
    ## This is the program to call post tweet
    if len(writeMessage) < 100:
        main(writeMessage, photoLoc)
        return True
    else:
        print "\n" + "Twitter messages can be less than 100 characters. Please enter proper Input" + "\n"
        return False
        # sys.exit()


## Twitter Handle - garbageNotifyie

## 100 char message -- twitter has character restriction of 116
tweetMessage = "The quick brown fox jumps over the lazy dog - The quick brown fox jumps over the lazy dog 1234567890"

pic = "/home/sashank/Documents/Daily_Activites/2016_09_September/2016_09_10/images/garbageCan.png"
# postTweet(tweetMessage,pic)
