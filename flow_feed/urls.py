"""alert_media URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.9/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
from django.conf.urls import url, include
from django.contrib import admin
from flow_feed.views import frontend_api, upload_post_image, upvote_post, add_post, login

urlpatterns = [
    url(r'^$', frontend_api),
    url(r'^upload_image$', upload_post_image),
    url(r'^upvote$', upvote_post),
    url(r'^add$', add_post),
    url(r'^login$', login),
]