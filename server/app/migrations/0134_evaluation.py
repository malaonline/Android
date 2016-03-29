# -*- coding: utf-8 -*-
# Generated by Django 1.9.1 on 2016-03-29 08:11
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0133_add_wx_policy'),
    ]

    operations = [
        migrations.CreateModel(
            name='Evaluation',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('start', models.DateTimeField()),
                ('end', models.DateTimeField()),
                ('status', models.CharField(choices=[('p', '待处理'), ('s', '已安排时间'), ('c', '已完成测评')], default='p', max_length=2)),
                ('order', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, to='app.Order')),
            ],
            options={
                'abstract': False,
            },
        ),
    ]
