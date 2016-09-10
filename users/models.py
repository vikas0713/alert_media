from __future__ import unicode_literals
from django.contrib.auth.models import UserManager, AbstractUser

from django.db import models

# Create your models here.

# user model override
class Profile(AbstractUser):
    avatar = models.CharField(max_length=500, blank=True, null=True)
    contact = models.CharField( max_length=255, unique=True)
    objects = UserManager()

    USERNAME_FIELD = 'username'

    class Meta:
        db_table = "profile"

    # def get_full_name(self):    # get user's full name
    #     return self.first_name + self.last_name

    def __unicode__(self):
        return self.username