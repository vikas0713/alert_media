# created by Vikas
# Notify Sequoia hack 2016
# future imports
from __future__ import unicode_literals

# standard imports

# django imports
from django.contrib.auth.models import User
from django.db import models

# local imports


class Votes(models.Model):
    liked_by = models.ForeignKey(User)
    time = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.liked_by


class Posts(models.Model):
    TAG = (
        ("garbage","garbage"),
        ("accident","accident"),
    )

    image_url = models.CharField(max_length=255, blank=True,null=True)
    description = models.TextField(blank=True, null=True)
    time = models.DateTimeField(auto_now_add=True)
    latitude = models.CharField(max_length=255, blank=True, null=True)
    longitude = models.CharField(max_length=255, blank=True, null=True)
    address = models.CharField(max_length=255, blank=True, null=True)
    tags = models.CharField(max_length=255, choices=TAG)
    user = models.ForeignKey(User, blank=True)
    up_vote = models.ManyToManyField(Votes, blank=True)

    @property
    def up_vote_counts(self):
        return self.up_vote.count()

    def __str__(self):
        return self.description
