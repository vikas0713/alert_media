from django.contrib import admin

# Register your models here.
from flow_feed.models import Posts, Votes

admin.site.register(Posts)
admin.site.register(Votes)