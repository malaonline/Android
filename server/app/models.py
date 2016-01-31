import uuid
import datetime

from django.contrib.auth.models import User
from django.db import models
from django.contrib.auth.hashers import make_password
from django.contrib.auth.models import Group
from django.contrib.auth import authenticate
from django.apps import apps
from django.utils import timezone

from app.utils.algorithm import Tree, Node

from app.utils import random_string, classproperty


class BaseModel(models.Model):
    class Meta:
        abstract = True


class Policy(BaseModel):
    content = models.TextField()
    updated_at = models.DateTimeField(auto_now=True)


class Region(BaseModel):
    """
    Province, City & District
    """
    name = models.CharField(max_length=50)
    superset = models.ForeignKey('Region', blank=True, null=True, default=None,
                                 on_delete=models.SET_NULL)
    admin_level = models.PositiveIntegerField()
    leaf = models.BooleanField()
    weekly_time_slots = models.ManyToManyField('WeeklyTimeSlot')
    opened = models.BooleanField(default=False)

    def __str__(self):
        return '%s (%d)' % (self.name, self.admin_level)

    def full_name(self, sep='-'):
        full_name = self.name
        upper = self.superset
        while upper:
            full_name = upper.name + sep + full_name
            upper = upper.superset
        return full_name

    def make_dict(self):
        _dict = {}
        _region = self
        while _region:
            if _region.admin_level == 1:
                _dict['province'] = _region
            elif _region.admin_level == 2:
                _dict['city'] = _region
            elif _region.admin_level == 3:
                _dict['district'] = _region
            _region = _region.superset
        return _dict


class School(BaseModel):
    name = models.CharField(max_length=100)
    address = models.CharField(max_length=200)
    thumbnail = models.ImageField(upload_to='schools', null=True, blank=True)
    region = models.ForeignKey(Region, limit_choices_to={'opened': True})
    center = models.BooleanField()
    longitude = models.IntegerField()
    latitude = models.IntegerField()
    opened = models.BooleanField(default=False)

    def __str__(self):
        return '%s%s %s' % (self.region, self.name, 'C' if self.center else '')


class Subject(BaseModel):
    ENGLISH = None
    name = models.CharField(max_length=10, unique=True)

    def __str__(self):
        return self.name

    @classmethod
    def get_english(cls):
        if not cls.ENGLISH:
            cls.ENGLISH = Subject.objects.get(name='英语')
        return cls.ENGLISH


class Tag(BaseModel):
    name = models.CharField(max_length=20, unique=True)

    def __str__(self):
        return self.name


class Grade(BaseModel):
    name = models.CharField(max_length=10, unique=True)
    superset = models.ForeignKey('Grade', blank=True, null=True, default=None,
                                 on_delete=models.SET_NULL,
                                 related_name='subset')
    leaf = models.BooleanField()

    def __str__(self):
        return self.name

    @property
    def subjects(self):
        Ability = apps.get_model('app', 'Ability')
        ans = Ability.objects.filter(grade=self)
        for one in ans:
            yield one.subject

    @staticmethod
    def get_all_grades():
        """
        获得所有的grade名称
        """
        return [[item.name for item in item.subset.all()]
                for item in Grade.objects.filter(superset=None)]


class Ability(BaseModel):
    grade = models.ForeignKey(Grade)
    subject = models.ForeignKey(Subject)

    class Meta:
        unique_together = ('grade', 'subject')

    def __str__(self):
        return '%s, %s' % (self.grade, self.subject)


class Level(BaseModel):
    name = models.CharField(max_length=20, unique=True)

    def __str__(self):
        return self.name


class Price(BaseModel):
    region = models.ForeignKey(Region, limit_choices_to={'opened': True})
    ability = models.ForeignKey(Ability, default=1)
    level = models.ForeignKey(Level)
    price = models.PositiveIntegerField()

    class Meta:
        unique_together = ('region', 'ability', 'level')

    def __str__(self):
        return '%s,%s,%s => %d' % (
                self.region, self.ability, self.level, self.price)

    @property
    def grade(self):
        return self.ability.grade

    @property
    def subject(self):
        return self.ability.subject


class Profile(BaseModel):
    """
    For extending the system class: User
    """
    MALE = 'm'
    FEMALE = 'f'
    UNKNOWN = 'u'
    GENDER_CHOICES = (
        (FEMALE, '女'),
        (MALE, '男'),
        (UNKNOWN, '未知'),
    )

    user = models.OneToOneField(User)
    phone = models.CharField(max_length=20, default='', db_index=True)
    # deprecated: use django group instead
    # role = models.ForeignKey(Role, null=True, blank=True,
    #                          on_delete=models.SET_NULL)
    gender = models.CharField(max_length=1,
                              choices=GENDER_CHOICES,
                              default=UNKNOWN,
                              )
    avatar = models.ImageField(null=True, blank=True, upload_to='avatars')

    def __str__(self):
        return '%s (%s)' % (self.user, self.gender)

    def mask_phone(self):
        return "{before}****{after}".format(
                before=self.phone[:3], after=self.phone[-4:])

    def avatar_url(self):
        return self.avatar and self.avatar.url or ''


class Teacher(BaseModel):
    DEGREE_CHOICES = (
        ('h', '高中'),
        ('s', '专科'),
        ('b', '本科'),
        ('p', '研究生'),
    )
    TO_CHOOSE = 1
    NOT_CHOSEN = 2
    TO_INTERVIEW = 3
    INTERVIEW_OK = 4
    INTERVIEW_FAIL = 5
    STATUS_CHOICES = (
        (TO_CHOOSE, '待处理'),
        (NOT_CHOSEN, '初选淘汰'),
        (TO_INTERVIEW, '邀约面试'),
        (INTERVIEW_OK, '面试通过'),
        (INTERVIEW_FAIL, '面试失败'),
    )
    user = models.OneToOneField(User)
    name = models.CharField(max_length=200)
    degree = models.CharField(max_length=2,
                              choices=DEGREE_CHOICES,
                              )
    published = models.BooleanField(default=False)
    fulltime = models.BooleanField(default=True)
    teaching_age = models.PositiveIntegerField(default=0)
    level = models.ForeignKey(Level, null=True, blank=True,
                              on_delete=models.SET_NULL)

    experience = models.PositiveSmallIntegerField(null=True, blank=True)
    profession = models.PositiveSmallIntegerField(null=True, blank=True)
    interaction = models.PositiveSmallIntegerField(null=True, blank=True)
    video = models.FileField(null=True, blank=True, upload_to='video')
    audio = models.FileField(null=True, blank=True, upload_to='audio')

    tags = models.ManyToManyField(Tag)
    schools = models.ManyToManyField(School)
    weekly_time_slots = models.ManyToManyField('WeeklyTimeSlot')
    abilities = models.ManyToManyField('Ability')

    region = models.ForeignKey(Region, null=True, blank=True,
                               limit_choices_to={'opened': True})
    status = models.IntegerField(default=1, choices=STATUS_CHOICES)

    def __str__(self):
        return '%s %s %s' % (self.name, 'F' if self.fulltime else '',
                             'Unpublished' if not self.published else '')

    def avatar(self):
        if not hasattr(self.user, 'profile'):
            return None
        return self.user.profile.avatar or None

    def gender(self):
        if not hasattr(self.user, 'profile'):
            return None
        return self.user.profile.gender

    def subject(self):
        abilities = self.abilities.all()
        if not abilities:
            return None
        return abilities[0].subject

    def grades(self):
        abilities = self.abilities.all()
        return [ability.grade for ability in abilities]

    def grades_shortname(self):
        grades = self.grades()
        grades = list(set(x.superset if x.superset else x for x in grades))
        sort_dict = {'小学': 1, '初中': 2, '高中': 3}
        grades = sorted(grades, key=lambda x: sort_dict.get(x.name, 4))
        if len(grades) == 0:
            return ''
        if len(grades) == 1:
            return grades[0].name
        else:
            return ''.join(x.name[0] for x in grades)

    def prices(self):
        abilities = self.abilities.all()
        return Price.objects.filter(
                level=self.level, region=self.region,
                ability__in=abilities)

    def min_price(self):
        prices = list(self.prices())
        if not prices:
            return None
        return min(x.price for x in prices)

    def max_price(self):
        prices = list(self.prices())
        if not prices:
            return None
        return max(x.price for x in prices)

    def is_english_teacher(self):
        subject = self.subject()
        ENGLISH = Subject.get_english()
        return subject and (subject.id == ENGLISH.id)

    def audio_url(self):
        return self.audio and self.audio.url or ''

    def video_url(self):
        return self.video and self.video.url or ''

    def cert_verified_count(self):
        Certificate = apps.get_model('app', 'Certificate')
        if self.is_english_teacher():
            cert_types = [Certificate.ID_HELD, Certificate.ACADEMIC,
                          Certificate.TEACHING, Certificate.OTHER]
        else:
            cert_types = [Certificate.ID_HELD, Certificate.ACADEMIC,
                          Certificate.TEACHING, Certificate.ENGLISH,
                          Certificate.OTHER]
        return Certificate.objects.filter(
                teacher=self, verified=True, type__in=cert_types).distinct(
                        'type').count()

    # 获得当前审核进度
    def get_progress(self):
        if self.status in [self.TO_CHOOSE, self.NOT_CHOSEN]:
            return 1
        if self.status in [self.TO_INTERVIEW, self.INTERVIEW_FAIL]:
            return 2
        if self.status in [self.INTERVIEW_OK]:
            return 3

    # 建立审核信息
    def build_progress_info(self):
        tree = Tree()
        tree.root = Node(1)
        tree.insert_val(1, 3, 2)
        tree.insert_val(3, 4)
        tree.insert_val(4, 5)
        tree.insert_val(5, 7, 6)
        tree.insert_val(7, 8)
        tree.insert_val(8, 9)
        status_2_node = {
            self.TO_CHOOSE: 1,
            self.NOT_CHOSEN: 2,
            self.TO_INTERVIEW: 5,
            self.INTERVIEW_FAIL: 6,
            self.INTERVIEW_OK: 9
        }
        # return tree.get_path(status_2_node.get(self.INTERVIEW_OK, 1))
        # return range(1,10)
        return tree.get_path(status_2_node.get(self.status, 1))

    def safe_get_account(self):
        # 获得账户,如果没有账户就创建一个
        try:
            account = self.account
        except AttributeError:
            # 新建一个账户
            account = Account(teacher=self, balance=0)
            account.save()
        return account

    # 新建一个空白老师用户
    @staticmethod
    def new_teacher()->User:
        # 新建用户
        username = random_string()[:30]
        salt = random_string()[:5]
        password = "malalaoshi"
        user = User(username=username)
        user.email = ""
        user.password = make_password(password, salt)
        user.save()
        # 创建老师身份
        profile = Profile(user=user, phone="")
        profile.save()
        teacher = Teacher(user=user)
        teacher.save()
        teacher_group = Group.objects.get(name="老师")
        user.groups.add(teacher_group)
        # 集体保存
        user.save()
        profile.save()
        teacher.save()
        ret_user = authenticate(username=username, password=password)
        return ret_user


class Highscore(BaseModel):
    """
    提分榜
    """
    teacher = models.ForeignKey(Teacher)
    name = models.CharField(max_length=200)
    increased_scores = models.IntegerField(default=0)
    school_name = models.CharField(max_length=300)
    admitted_to = models.CharField(max_length=300)

    def __str__(self):
        return '%s %s (%s => %s)' % (
                self.name, self.increased_scores, self.school_name,
                self.admitted_to)


class Achievement(BaseModel):
    """
    特殊成果
    """
    teacher = models.ForeignKey(Teacher)
    title = models.CharField(max_length=30)
    img = models.ImageField(null=True, blank=True, upload_to='achievements')

    def img_url(self):
        return self.img and self.img.url or ''


class Photo(BaseModel):
    teacher = models.ForeignKey(Teacher)
    img = models.ImageField(null=True, blank=True, upload_to='photos')
    order = models.PositiveIntegerField(default=0)
    public = models.BooleanField(default=False)

    def __str__(self):
        return '%s img (%s)' % (
                self.teacher, 'public' if self.public else 'private')

    def img_url(self):
        return self.img and self.img.url or ''


class Certificate(BaseModel):
    """
    资质认证,身份认证用了两个记录(因为身份认证有手持照),判断是否通过认证用
    """
    ID_HELD = 1
    ID_FRONT = 2
    ACADEMIC = 3
    TEACHING = 4
    ENGLISH = 5
    OTHER = 6

    TYPE_CHOICES = (
        (ID_HELD, '身份证手持照'),
        (ID_FRONT, '身份证正面'),
        (ACADEMIC, '学历认证'),
        (TEACHING, '教师资格证'),
        (ENGLISH, '英语水平证书'),
        (OTHER, '其他资质认证'),
    )

    teacher = models.ForeignKey(Teacher)
    name = models.CharField(max_length=100)
    type = models.IntegerField(null=True, blank=True, choices=TYPE_CHOICES)
    img = models.ImageField(null=True, blank=True, upload_to='certs')
    verified = models.BooleanField()

    def __str__(self):
        return '%s, %s : %s' % (self.teacher, self.name,
                                'V' if self.verified else '')

    def img_url(self):
        return self.img and self.img.url or ''


class InterviewRecord(BaseModel):
    PENDING = 'p'
    APPROVED = 'a'
    REJECTED = 'r'
    STATUS_CHOICES = (
        (PENDING, '待认证'),
        (APPROVED, '已认证'),
        (REJECTED, '已拒绝'),
    )

    teacher = models.ForeignKey(Teacher)
    created_at = models.DateTimeField(auto_now_add=True)
    reviewed_at = models.DateTimeField(auto_now=True)
    reviewed_by = models.ForeignKey(User, null=True, blank=True)
    review_msg = models.CharField(max_length=1000)
    status = models.CharField(max_length=1,
                              choices=STATUS_CHOICES,
                              default=PENDING)

    def __str__(self):
        return '%s by %s' % (self.teacher, self.reviewed_by)


class Account(BaseModel):
    """
    老师账户
    """
    teacher = models.OneToOneField(Teacher)
    balance = models.PositiveIntegerField(default=0)

    def __str__(self):
        return '%s : %d' % (self.teacher, self.balance)


class BankCard(BaseModel):
    bank_name = models.CharField(max_length=100)
    card_number = models.CharField(max_length=100, unique=True)
    account = models.ForeignKey(Account)

    def __str__(self):
        return '%s %s (%s)' % (self.bank_name, self.card_number,
                               self.account.teacher)


class BankCodeInfo(BaseModel):
    org_code = models.CharField(max_length=30)
    bank_name = models.CharField(max_length=30)
    card_name = models.CharField(max_length=30)
    card_type = models.CharField(max_length=2)
    card_number_length = models.PositiveIntegerField()
    bin_code_length = models.PositiveIntegerField()
    bin_code = models.CharField(max_length=30)

    def __str__(self):
        return '%s, %s, %s (%s)' % (self.bank_name, self.card_name,
                                    self.card_type, self.bin_code)


class AccountHistory(BaseModel):
    account = models.ForeignKey(Account)
    amount = models.PositiveIntegerField()
    bankcard = models.ForeignKey(BankCard)
    submit_time = models.DateTimeField()
    done = models.BooleanField()
    done_by = models.ForeignKey(User, related_name='processed_withdraws',
                                null=True, blank=True)
    done_at = models.DateTimeField()

    def __str__(self):
        return '%s %s : %s' % (self.account.teacher, self.amount,
                               'D' if self.done else '')


class Feedback(BaseModel):
    user = models.ForeignKey(User, null=True, blank=True)
    contact = models.CharField(max_length=30)
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return '%s %s %s' % (self.user, self.contact, self.created_at)


class Memberservice(BaseModel):
    name = models.CharField(max_length=30)
    detail = models.CharField(max_length=1000)

    def __str__(self):
        return '%s' % self.name


class Parent(BaseModel):
    user = models.OneToOneField(User)

    student_name = models.CharField(max_length=50)
    student_school_name = models.CharField(max_length=100, default='')

    def __str__(self):
        return "{child_name}'s parent [{parent_name}]".format(
                child_name=self.student_name, parent_name=self.user.username)


class Coupon(BaseModel):
    parent = models.ForeignKey(Parent, null=True, blank=True)
    name = models.CharField(max_length=50)
    amount = models.PositiveIntegerField()
    created_at = models.DateTimeField(auto_now_add=True)
    expired_at = models.DateTimeField()
    used = models.BooleanField()

    def __str__(self):
        return '%s, %s (%s) %s' % (self.parent, self.amount, self.expired_at,
                                   'D' if self.used else '')


class WeeklyTimeSlot(BaseModel):
    weekday = models.PositiveIntegerField()  # 1 - 7
    start = models.TimeField()  # [0:00 - 24:00)
    end = models.TimeField()

    class Meta:
        unique_together = ('weekday', 'start', 'end')

    def __str__(self):
        return '%s from %s to %s' % (self.weekday, self.start, self.end)

    @classproperty
    def DAILY_TIME_SLOTS(cls):
        return [
                dict(start=item.start, end=item.end) for item in
                cls.objects.filter(weekday=1).order_by('start')]


class OrderManager(models.Manager):
    use_in_migrations = True

    def create(self, parent, teacher, school, grade, subject, hours, coupon):
        ability = grade.ability_set.filter(subject=subject)[0]

        price = teacher.region.price_set.get(
                ability=ability, level=teacher.level).price

        discount_amount = coupon.amount if coupon is not None else 0

        total = price * hours - discount_amount

        order = super(OrderManager, self).create(
                parent=parent, teacher=teacher, school=school, grade=grade,
                subject=subject, price=price, hours=hours, total=total,
                coupon=coupon,)

        order.save()
        return order

    def _weekly_date_to_min(self, date):
        return date.weekday() * 24 * 60 + date.hour * 60 + date.minute

    def _delta_min(self, weekly_ts, cur_min):
        return (
                (weekly_ts.weekday - 1) * 24 * 60 + weekly_ts.start.hour * 60 +
                weekly_ts.start.minute - cur_min + 7 * 24 * 60) % (7 * 24 * 60)

    def _get_order_timeslots(self, order):
        grace_time = datetime.timedelta(days=2)
        date = timezone.now() + grace_time
        cur_min = self._weekly_date_to_min(date)

        weekly_time_slots = list(order.weekly_time_slots.all())
        weekly_time_slots.sort(
                key=lambda x: self._delta_min(x, cur_min))

        n = len(weekly_time_slots)
        h = order.hours
        i = 0
        ans = []
        while h > 0:
            weekly_ts = weekly_time_slots[i % n]
            start = date + datetime.timedelta(
                    minutes=self._delta_min(weekly_ts, cur_min)
                    ) + datetime.timedelta(days=7 * (i // n))

            end = start + datetime.timedelta(
                    minutes=(weekly_ts.end.hour - weekly_ts.start.hour) * 60 +
                    weekly_ts.end.minute - weekly_ts.start.minute)
            ans.append(dict(start=start, end=end))
            i = i + 1
            h = h - 1
        return ans

    def allocate_timeslots(self, order, force=False):
        timeslots = self._get_order_timeslots(order)
        return timeslots


class Order(BaseModel):
    PENDING = 'u'
    PAID = 'p'
    CANCLED = 'd'
    CONFIRMED = 'c'
    NOSHOW = 'n'
    STATUS_CHOICES = (
        (PENDING, '待付款'),
        (PAID, '已付款'),
        (CANCLED, '已取消'),
        (NOSHOW, '没出现'),
        (CONFIRMED, '已确认'),
    )

    objects = OrderManager()

    parent = models.ForeignKey(Parent, null=True, blank=True)
    teacher = models.ForeignKey(Teacher)
    school = models.ForeignKey(School)
    grade = models.ForeignKey(Grade)
    subject = models.ForeignKey(Subject)
    coupon = models.ForeignKey(Coupon, null=True, blank=True)
    weekly_time_slots = models.ManyToManyField(WeeklyTimeSlot)

    price = models.PositiveIntegerField()
    hours = models.PositiveIntegerField()
    charge_id = models.CharField(max_length=100)  # For Ping++ use
    order_id = models.CharField(max_length=64, default=uuid.uuid1)
    total = models.PositiveIntegerField()

    created_at = models.DateTimeField(auto_now_add=True)
    paid_at = models.DateTimeField(null=True, blank=True)

    status = models.CharField(max_length=2,
                              choices=STATUS_CHOICES,
                              default=PENDING, )

    def __str__(self):
        return '%s %s %s %s %s : %s' % (
                self.school, self.parent, self.teacher, self.grade,
                self.subject, self.total)

    def fit_statistical(self):
        return self.status == self.PAID

    def enum_timeslot(self, handler):
        for one_timeslot in self.timeslot_set.all():
            handler(one_timeslot)


class TimeSlotComplaint(BaseModel):
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)
    last_updated_at = models.DateTimeField(auto_now=True)
    last_updated_by = models.ForeignKey(User, null=True, blank=True)

    def __str__(self):
        return '%s' % (self.content)


class TimeSlotAttendance(BaseModel):
    NORMAL = 'a'
    ABSENT = 'b'
    LATE10 = 'c'
    LATE10_30 = 'd'
    LATE30 = 'e'
    TYPE_CHOICES = (
        (LATE10, '10分钟内'),
        (LATE10_30, '10-30分钟'),
        (LATE30, '30分钟以上'),
        (ABSENT, '缺勤'),
        (NORMAL, '正常出勤'),
    )

    created_at = models.DateTimeField(auto_now_add=True)
    last_updated_at = models.DateTimeField(auto_now=True)
    last_updated_by = models.ForeignKey(User, null=True, blank=True)
    record_type = models.CharField(max_length=1,
                                   choices=TYPE_CHOICES,
                                   default=NORMAL)

    def __str__(self):
        return '%s' % (self.get_record_type_display())


class TimeSlot(BaseModel):
    order = models.ForeignKey(Order)
    start = models.DateTimeField()
    end = models.DateTimeField()

    confirmed_by = models.ForeignKey(Parent, null=True, blank=True)
    transferred_from = models.ForeignKey('TimeSlot', null=True, blank=True)

    created_at = models.DateTimeField(auto_now_add=True)
    last_updated_at = models.DateTimeField(auto_now=True)
    last_updated_by = models.ForeignKey(User, null=True, blank=True)

    complaint = models.ForeignKey(TimeSlotComplaint, null=True, blank=True)
    attendance = models.ForeignKey(TimeSlotAttendance, null=True, blank=True)

    deleted = models.BooleanField(default=False)

    def __str__(self):
        return '%s - %s %s' % (self.start, self.end, self.last_updated_by)

    def is_complete(self, given_time):
        # 对于给定的时间,课程是否结束
        if self.end < given_time:
            return True
        return False

    def is_waiting(self, given_time):
        # 对于给定时间,课程是否处于等待
        if given_time < self.start:
            return True
        return False

    def is_running(self, given_time):
        # 对于给定时间,课程是否处于上课中
        if self.start < given_time < self.end:
            return True
        return False


class Comment(BaseModel):
    time_slot = models.ForeignKey(TimeSlot)
    # 评分, 评分低于3分是差评
    score = models.PositiveIntegerField()
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return '%s : %d, %d' % (self.time_slot, self.ma_degree, self.la_degree)

    def is_bad_comment(self):
        if self.score < 3:
            return True
        return False


class Message(BaseModel):
    SYSTEM = 's'
    FINANCE = 'f'
    COURSE = 'c'
    AUDIT = 'a'
    COMMENT = 'm'
    TYPE_CHOICES = (
        (SYSTEM, '系统消息'),
        (FINANCE, '收入消息'),
        (COURSE, '课程消息'),
        (AUDIT, '审核消息'),
        (COMMENT, '评论消息'),
    )

    SMS = 's'
    MAIL = 'm'
    NOTIFICATION = 'n'
    VIA_CHOICES = (
        (SMS, '短信'),
        (MAIL, '邮件'),
        (NOTIFICATION, '通知栏提醒'),
    )

    to = models.ForeignKey(User)
    viewed = models.BooleanField()
    deleted = models.BooleanField()
    title = models.CharField(max_length=100)
    content = models.CharField(max_length=1000)
    _type = models.CharField(max_length=1,
                             choices=TYPE_CHOICES,
                             )
    via = models.CharField(max_length=1,
                           choices=VIA_CHOICES,
                           )
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return '%s, %s, to %s, %s' % (
                self.get__type_display(), self.get_via_display(),
                self.to, self.title)


class Checkcode(BaseModel):
    phone = models.CharField(max_length=20, unique=True)
    checkcode = models.CharField(max_length=30)
    updated_at = models.DateTimeField(auto_now_add=True)
    verify_times = models.PositiveIntegerField(default=0)
    resend_at = models.DateTimeField(blank=True, null=True, default=None)

    @staticmethod
    def verify_sms(phone, code):
        try:
            Checkcode.objects.get(phone=phone, checkcode=code)
            return True
        except Checkcode.DoesNotExist:
            return False
