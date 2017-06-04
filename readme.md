# Mago (Magnet Go!)

## Demo
上方的是邀请发起者，下方的是邀请接收者。</br>
![发起方](https://github.com/rushzhou/Mago/blob/master/raw/sender.gif)</br>
![接收方](https://github.com/rushzhou/Mago/blob/master/raw/receiver.gif)</br>

## APK下载地址
如果您看到这条消息就说明我们的项目仍在发布审核中。一旦审核通过，我们会及时在此更新下载地址。如果您由于时间原因必须现在查看我们的APK，请到百度云盘下载：
>链接: https://pan.baidu.com/s/1i55idSh 密码: xtr5

## 使用说明
正如您在Demo中看到的，Mago需要发起者和接收者两个角色，以下为测试步骤：
1. 您需要准备两部手机，并均安装Mago
2. **保持网络通畅，GPS信号较强**
3. 注册、登录
4. 两个测试者最好相距一段距离，因为Mago认为相距50米及以内的情况下，朋友之间可以很容易发现彼此，因此会结束导航
5. 登录后进入的界面我们称之为主界面，在这个界面中您可以看到自己的位置。在主界面中向右滑屏，将出现菜单
6. 点击我的好友
7. 点击右上角图标，并完成添加好友
8. 点击好友名片
9. 点击找到朋友，即可发出邀请。这时您是邀请发起者，当邀请接收者接听“电话”以后，双方将跳转进入导航界面
10. 享受Mago！

## 运行源代码
* 我们的项目使用Android Studio开发，使用了gradle进行配置，并且使用了[高德地图](http://lbs.amap.com/)和[Leancloud](https://leancloud.cn/)的SDK。如果您想自行编译源代码，请首先申请高德KEY，并将AndroidManifest.xml中第82行替换为您申请的高德KEY。
* （可选）如果您想使用自己的Leancloud后台的话，需要将util包下MyLeanCloudApp.java中的第92行替换为您自己的Leancloud KEY。