from django.test import TestCase
from rest_framework.authtoken.models import Token
from django.contrib.auth.models import User
from django.test import Client
from django.core.urlresolvers import reverse
from django.contrib.auth import authenticate
from django.contrib.auth.models import Group, Permission
from django.core.management import call_command
import json
from app.models import Parent, Teacher, Checkcode, Profile
from app.views import Sms
from teacher.views import information_complete_percent
from app.utils.algorithm import Tree, Node


# Create your tests here.
class TestApi(TestCase):
    def setUp(self):
        pass

    def tearDown(self):
        pass

    def test_token_key(self):
        # 测试token是否能正常创建
        user = User.objects.get(username="parent0")
        token = Token.objects.create(user=user)
        # print(token.key)
        self.assertTrue(isinstance(token.key, str))

    def test_teacher_list(self):
        client = Client()
        url = "/api/v1/teachers"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_teacher_detail(self):
        client = Client()
        url = "/api/v1/teachers/1"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_tag_list(self):
        client = Client()
        url = "/api/v1/tags"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_grade_list(self):
        client = Client()
        url = "/api/v1/grades"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_memberservice_list(self):
        client = Client()
        url = "/api/v1/memberservices"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_weeklytimeslot_list(self):
        client = Client()
        url = "/api/v1/weeklytimeslots"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_policy(self):
        client = Client()
        url = "/api/v1/policy"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_get_token_key(self):
        client = Client()
        request_url = "/api/v1/token-auth"
        # request_url = "/api/v1/subjects/"
        username = "parent1"
        password = "123123"
        user = authenticate(username=username, password=password)
        self.assertNotEqual(user, None)
        parent_user = User.objects.get(username=username)
        # self.assertEqual(parent_user.password, password)
        self.assertEqual(parent_user.is_active, 1)
        response = client.post(request_url, {"username": username, "password": password})
        # print(response.status_code)
        response.render()
        #print(response.content.decode())
        self.assertEqual(response.status_code, 200)

        client2 = Client()
        response2 = client2.post(request_url, {"username": username, "password": password})
        response2.render()
        # print(response2.content.decode())
        self.assertEqual(response.content, response2.content)

    def test_modify_student_name(self):
        token_client = Client()
        token_request_url = "/api/v1/token-auth"
        username = "parent1"
        password = "123123"
        response = token_client.post(token_request_url, {"username": username, "password": password})
        response.render()
        token = json.loads(response.content.decode())["token"]
        #print("get token:{token}".format(token=token))
        user = User.objects.get(username=username)
        parent = Parent.objects.get(user=user)
        user_token = Token.objects.get(user=user)
        self.assertEqual(user_token.key, token)

        call_command("add_groups_to_sample_users")

        # test 201
        client = Client()
        request_url = "/api/v1/parents/%d" % (parent.pk,)
        #print("the request_url is {request_url}".format(request_url=request_url))
        json_data = json.dumps({"student_name": "StudentNewName"})
        response = client.patch(request_url, content_type="application/json",
                                data=json_data,
                                **{"HTTP_AUTHORIZATION": " Token %s" % token})
        self.assertEqual(200, response.status_code)
        # print(response.status_code)
        response.render()
        # print(response.content)
        # print(response.content.decode())
        json_ret = json.loads(response.content.decode())
        # print(json_ret)
        # print(json_ret["done"])
        self.assertEqual(json_ret["done"], "true")
        # self.assertEqual(response.content.decode(), "{'done': 'false', 'reason': 'Student name already exits.'}")
        #print(response._headers)
        parent_after = Parent.objects.get(user=user)
        self.assertEqual(parent_after.student_name, "StudentNewName")

        # test 200
        parent_after.student_name = ""
        parent_after.save()
        response = client.patch(request_url, content_type="application/json",
                                data=json_data,
                                **{"HTTP_AUTHORIZATION": " Token %s" % token})
        self.assertEqual(200, response.status_code)
        response.render()
        self.assertEqual(response.content.decode(), '{"done":"true"}')
        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret["done"], "true")


class TestModels(TestCase):
    def setUp(self):
        call_command("build_groups_and_permissions")

    def tearDown(self):
        pass

    def test_new_teacher(self):
        new_teacher = Teacher.new_teacher()
        self.assertTrue(isinstance(new_teacher, User))

    def test_sms_verify(self):
        phone = "18922405996"
        sms_code = Sms().generateCheckcode(phone)
        self.assertTrue(Checkcode.verify_sms(phone, sms_code))
        self.assertFalse(Checkcode.verify_sms(phone, "error_code"))


class TestTeacherWeb(TestCase):
    def setUp(self):
        call_command("build_groups_and_permissions")

    def tearDown(self):
        pass

    def test_verify_sms_code(self):
        phone = "18922405996"
        sms_code = Sms().generateCheckcode(phone)
        client = Client()
        # 第一次
        response = client.post(reverse("teacher:verify-sms-code"),
                               {
                                   "phone": phone,
                                   "code": sms_code
                               })
        self.assertEqual(response.status_code, 200)
        # response.render()
        #print(response.content)
        self.assertEqual(json.loads(response.content.decode()),
                         {"result": True, "url": "/teacher/information/complete/"})
        # 第二次
        second_client = Client()
        response = second_client.post(reverse("teacher:verify-sms-code"),
                                      {
                                          "phone": phone,
                                          "code": sms_code
                                      })
        self.assertEqual(json.loads(response.content.decode()),
                         {"url": "/teacher/information/complete/", "result": True})
        #print(response.content)

        # 测试information_compelte_percent
        profile = Profile.objects.get(phone=phone)
        percent = information_complete_percent(profile.user)
        #print(percent)


class TestAlgorithm(TestCase):
    def test_tree_insert(self):
        tree = Tree()
        tree.root = Node("a")
        tree.insert_val("a", "b", "c")
        tree.insert_val("b", "d", "e")
        self.assertEqual(tree.get_val("d").val, "d")
        #print("test_tree_insert: {tree}".format(tree=tree.get_path("d")))
        #print("test_tree_insert: {tree}".format(tree=tree.get_path("d")))
        self.assertEqual(tree.get_path("d"), ["a", "b", "d"])
        self.assertEqual(tree.get_path("e"), ["a", "b", "e"])
        self.assertEqual(tree.get_path("c"), ["a", "c"])
        self.assertEqual(tree.get_path("b"), ["a", "b"])

