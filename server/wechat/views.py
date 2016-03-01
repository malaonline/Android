import  logging

# django modules
from django.shortcuts import render, redirect, get_object_or_404
from django.views.generic import View, TemplateView, ListView, DetailView
from django.db.models import Q
from django.utils import timezone


# local modules
from app import models
# Create your views here.


class TeachersView(ListView):
    model = models.Teacher
    context_object_name = 'teacher_list'
    template_name = 'wechat/teacher/teachers.html'

    def get_queryset(self):
        teacher_list = self.model.objects.filter(
            recommended_on_wechat=True
         ).filter(
            published=True
        )
        return teacher_list

class TeacherDetailView(DetailView):
    model = models.Teacher

