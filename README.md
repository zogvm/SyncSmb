# SyncSmb
sync android pictures or dcim camera  to  pc samba directory

同步 手机里的dcim相册 和pictures目录 到 电脑的 samba（共享）协议的文件夹内



主要功能：

1.支持安卓手机

2.遍历 手机内的相册（DCIM目录） 和 微信保存文件的目录 和 截图（录视频）目录 

3.将待同步的目录 上传到 电脑的共享文件夹（采用samba协议）内

4.策略：云端如果存在该文件，并且文件大小一致，就不上传，否则就上传。

5.代码内无任何删除操作。

6.支持定时同步（默认2小时一次）

目前1.0版的 

默认支持（无需额外配置）

DCIM/

tencent/MicroMsg/WeiXin/

Pictures/WeiXin/

Pictures/Screenshots/

Pictures/VideoEditor/

目录进行同步，

其默认会忽略

DCIM/.（所有隐藏文件夹）

DCIM/Camera/.escheck.tmp

DCIM/Creative/temp

DCIM/Camera/cache/

用法：

1.软件打开后要给文件读取和网络权限。

2.记得开WIFI。

3.右上角有三个点，点击后 设置云端的IP，账号密码等，以及设置本地（几乎不用点）的定时同步时间和其他目录。

4.设置save好之后，回到界面，点击update就开始上传了。

5.如果上传错误会弹窗，但是不用担心是不是白传了，支持断点续传。

6.上传中途可以缩小或者切去干其他活。

7.上传完成后，可以选择点击stop server，停止定时同步的服务。然后退出app。（我就这样，感觉有个定时的悬空在那边很烦，自己做的自己都不爱用。。）

开发：

在“MainActivity.java”代码 里添加默认的同步文件夹，其会进行遍历然后同步到云端

在“SMBUploadFile.java”代码 里添加默认的忽略文件夹，其会进行忽略，避免已扔到回收站以及缓存的文件被同步。

地址：

https://github.com/zogvm/SyncSmb

APK下载：

https://github.com/zogvm/SyncSmb/releases/download/1.0/syncSmb1.0.apk

