import facebook
import json

def main(messageToMain,photoPath):
    try:
        configParam = {
            "page_id"      : "1792180177720250",
            "access_token" : "EAAQLcHRWXPgBAEKEFzOf3VNBvo8wzTnKoaIWIkEbvwJ1A2PRJ0sJFzZCZAJ1P3aFVanmaZBBhyZAuZBfc0qk22bGe9AzW8P4vfih1LMx7DiElkJBWVIZC3BLimsChiiSIaSlxTIrAogJvWzXdZCKNzQyJYWSvnbaJikSaZCsOaDiGQZDZD"
        }

        api = facebook.GraphAPI(configParam['access_token'])
        msg = messageToMain
        if not photoPath:
            api.put_object(configParam['page_id'], "feed", message=msg)
        else:
            jtags = json.dumps([ {'x':'1', 'y':'1', 'tag_uid':'100013351456266','tag_text':'Alexa Muncip'}])
            pic = photoPath
            photo = open(pic, "rb")
            posted_image_id = api.put_photo(image=photo, message=msg, privacy = json.dumps({'value': 'EVERYONE'})   ) # , tags = jtags
            photo.close()
            return True
    except:
        return None


def postMessage(writeMessage,photoLoc):
    return main(writeMessage,photoLoc)

## Below is an example to send photo
msgString = "Bob is exquisitely demanding. But it is with him that I learned this essential truth: Nothing is impossible.\n \
And this: Because nothing is impossible, you have to dream big dreams; the bigger, the better.\n \
So many people along the way, whatever it is you aspire to do, will tell you it can’t be done. But all it takes is imagination.\n \
You dream. You plan. You reach.\n \
There will be obstacles. There will be doubters. There will be mistakes.\n \
But with hard work, with belief, with confidence and trust in yourself and those around you, there are no limits.\n \
Perseverance, determination, commitment, and courage – those things are real. The desire for redemption drives you. And the will to succeed – it’s everything. That’s why, on the pool deck in Beijing in the summer of 2008, there were sometimes no words, only screams.\n \n\
Because, believe it, dreams really can come true.\n \n\
–  Micheal Phelps\n "

pic = "/home/sashank/Documents/Daily_Activites/2016_08_August/2016_08_11/clipart/michael-phelps-rio-olympics-gold-medal.jpg"
#pic = "/home/sashank/Documents/Daily_Activites/2016_08_August/2016_08_11/clipart/samudra-mandhan.jpg"
postMessage(msgString,pic)

## Facebook URL:: https://www.facebook.com/Alertmedia123-1792180177720250