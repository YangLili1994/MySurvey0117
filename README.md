
#问卷星app
##功能：
      1、问卷编辑上传  
      2、支持单选、多选、填空、程度题（均支持添加图片）  
      3、使用问卷并上传结果：有网状态直接上传服务器，无网保存至本地联网后自动批量上传
      4、问卷结果显示  
      5、问卷修改：问卷标题说明修改、题目内容修改、题目顺序修改等
     
      
##说明：
      1、网络框架使用volley、图片使用三级缓存
      2、使用开源CircleImageView   https://github.com/hdodenhof/CircleImageView
      3、自定义程度题方框显示view
      4、SQLite增删改查
     
     
##Bug记录：
      1、请求服务器数据和读本地数据的逻辑（有网无网、缓存有效性）
      2、大图片OOM
      3、volley内存泄露   http://blog.csdn.net/qq_32199531/article/details/54576618
      4、小米手机上选择照片路径为null  http://blog.csdn.net/coderinchina/article/details/50799501
      5、sqlite数据库String存储以0开始的字符串如"001"会自动去掉首位的0  解决：换成text
      6、listview刷新后位置恢复
      7、修改问卷后没有提交修改 备份原数据恢复
      
      
      
##功能截图：
<img src="http://img.blog.csdn.net/20170117175747387?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="首页" align=center />
<img src="http://img.blog.csdn.net/20170117181009763?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="图片名称" align=center />
<img src="http://img.blog.csdn.net/20170117181203838?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="图片名称" align=center />
<img src="http://img.blog.csdn.net/20170117181248939?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="修改单选题" align=center />
<img src="http://img.blog.csdn.net/20170117184953044?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="预览" align=center />
<img src="http://img.blog.csdn.net/20170117181355534?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="问卷预览" align=center />
<img src="http://img.blog.csdn.net/20170117181513146?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="图片名称" align=center />
<img src="http://img.blog.csdn.net/20170117181545209?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="图片名称" align=center />
<img src="http://img.blog.csdn.net/20170117181656976?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="图片名称" align=center />
<img src="http://img.blog.csdn.net/20170117181853933?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="图片名称" align=center />
<img src="http://img.blog.csdn.net/20170117181908107?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzIxOTk1MzE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width = "270" height = "480" alt="图片名称" align=center />
 
      
      
