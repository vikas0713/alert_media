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