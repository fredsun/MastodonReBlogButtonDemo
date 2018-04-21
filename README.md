### 画一个仿 mastodon 的转发按钮
#### 目标
* 效果地址:mastodon殆知阁的猫站主页 https://mao.daizhige.org/web/getting-started
* 效果gif:
* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/mastodon_gif.gif)

#### 分析
* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/analysisFromMastodon.png)
* 使用 Chrome 的 More Tools -> Animations记录动画, 并用10%的速度播放后发现. 按钮可以拆分为两部分, 不动的圆角矩形和以圆角矩形作为轨迹移动的两个三角形
* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/roundRect.png)
* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/twoTriangle.png)
* 三角形移动的动画是一个在Animations分析下, 于450ms就超过了一半, 目测是一个减速的动画
* 实现方案 android 自定义 View.



#### 获取参数
> android 中 View 绘制时是设置的 View 整体的宽高

截图一桢并在 PS 下放大到 2000% 后如图获取到参数(单位: 格)
* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/ViewInPS.png)
* 圆角矩形包含边宽时, 宽30, 高24, 边宽6.
* 三角形边宽不另算, 底边18, 高 12. 伸出圆角矩形的部分为6.(*PS: 此处边宽是否计入会在后面解释*)
* 所以 View 整体的宽为圆角矩形的宽 + 左右两侧三角形伸出圆角矩形的部分 = 30 + 6 * 2 = 42, 高为圆角矩形的高 + 上下两侧(三角形的高 - 圆角矩形的边宽) = 24 + (12 - 6) * 2 = 36.
* 三角形与圆角矩形交接处宽度为2.
* 三角形与圆角矩形的1/2高度处的距离为1


#### 绘制 View
> android 中 View 绘制通过继承 View 后重写 ```onDraw``` 方法.```onDraw```中, ```canvas``` 理解为画布/画纸, ```paint``` 理解为画笔

> android 中画笔 paint 分为两种, STORKE 和 FILL, 直译就是一笔和画满, 具体的解释是如果指定的区域是闭合的, 用 FILL 会把这个区域填满. 而 STROKE只会画边框

* 首先把坐标系从默认的左上角移动到 View 的中心.
```
canvas.translate(mWidth/2, mHeight/2);//坐标系原点切到控件1/2处
```
* 圆角矩形的绘制只要边框即可, 不需要填满 使用的是 STROKE, 又因为 ```canvas.drawRoundRect(...)``` 方法会把画笔的起点扯到圆角矩形的左上角, 无法实现三角形在圆角矩形的左侧中间位置开始移动, 所以最终的绘画方案是通过依次绘制四个顶点来画出圆角矩形, 使用 ```canvas.drawPath```. paint 的宽度为6
* 三角形的绘制, 因为需要填满三角形内部, 所以用的是 FILL. 绘制方法同样是 ```canvas.drawPath```

* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/buttonWithoutBackgroundLine.png)

#### 透明边
此时三角形也画出来了, 但是原效果的三角形的边好像是外边框透明了? 那好, 来从画笔 paint 里找设置外边框的笔, 找了一圈。。。。。。emmm 没有.
那换一个角度, 并没有什么三角形的外边框, 而是在三角形的上面, 有一条透明的线.
好的, 那画好了透明色的线来看, 还是原来的样子.
因为 android 的绘制原则是覆盖, 所以一个区域如果有图案了，只能去覆盖它, 但是透明色的下面是原来的颜色, 所以覆盖上去了并不能透明.
所以看似透明边. 实际是背景色边而已.
但是为了保证三角形的圆角不会因误差被擦除, 需要将三角形和背景色边两个path重叠起来, 并且重叠出保留三角形. 用到了 ```PorterDuffXfermode``` 的 ```DST_OVER```, 保留了作为的DST三角形
* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/DST_OVER_tips.jpg)


#### 动画 Animation
> android 中动画的概念可以拆分为, 用一个插值器拿到 **此刻进度** 在动画总进度的百分比 + **此刻** 的 canvas 上画了啥. **插值器可以理解为动画执行到了哪一刻**
> 举个例子, 一条用 path 画的直线本身在一次 ```onDraw```内就可以直接完成, 现在把它的 path 路径用一个 1000 毫秒的动画来完成, 过程是: 默认的每10ms获取一次 **此刻** 插值器的值, 同时调用 ```postInvalidate()``` 方法触发 ```onDraw``` 来绘制， ```onDraw```里根据插值器的值获取到执行到了总进度的多少, 来决定怎么画.

* 根据分析, 我们直接使用 DecelerateInterpolator 这个减速插值器即可
* 让三角形沿着圆角矩形滚动可以看作让三角形沿着绘制圆角矩形时的 path 路径滚动.
  * 通过 ```mPathMeasure.setPath(path, true)``` 绑定 path,
  * ```onDraw```时不断调用```mPathMeasure.getPosTan```方法来得到, 此刻插值器的值 * 总进度走到的点的坐标, 以及趋势方向与x轴的夹脚

#### 问题
此时发现, 三角形一直保持着绘制时的标准坐标系, 并没有按照预想的旋转.

#### 旋转三角形
我们需要在左侧边时的三角形逆时针旋转90度, 在有侧边时的三角形顺时针旋转90度, 也就是-90 和 90.
根据动画 Animation 中提到的 ```mPathMeasure.getPosTan(distance, pos[], tan[])```方法, 参数依次是, 此刻的进度, 此刻的点坐标数组pos[] 以及此刻的切线值. 这里通过不详细解释, 直接通过 ```float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI)```, 拿到趋势方向与x轴的夹脚, 将画布旋转即可```canvas.rotate(degrees)```, 同理画出右侧三角形(此处可以展开讲讲, 但考虑到篇幅, 可根据注释理解)
* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/rotateTriangle.jpeg)


#### 矩形改成圆角矩形
之前绘制的是矩形, 而圆角可以用```path.arcTo(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean forceMoveTo```方法绘制. 如图的圆弧是占地 left, top 到 right, bottom 的圆的 -180 度开始划过了 90 度的部分
其中，只画left, top, right, bottom 确定的椭圆为
![](https://raw.githubusercontent.com/sunxlfred/RES/master/arcToMethod.png)
这次的view为![](https://raw.githubusercontent.com/sunxlfred/RES/master/roundRectCorner.jpeg)
于是效果从
* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/rect.png)
转换成了
* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/roundRect.png)

#### 最终效果
![](https://raw.githubusercontent.com/sunxlfred/RES/master/mastodonDemoClick.gif)


## 优化
#### 1.颜色
根据"数码去色计"的"显示原生值"设置获取按钮的各种颜色
#### 2.透明三角替换透明边
当三角形的圆角数值固定且view很小时, 三角形会矮于view导致三角形顶部多处一个"小角"
* 解决方案 将透明边改成透明三角形, 确保透明部分始终盖住底部矩形

#### 3.添加点击逻辑

##### 3.1.缩放
view 自带了 animate(). 直接调用来缩放整体.
* ACTION_DOWN变小
* ACTION_UP变回
* ACTION_CANCEL变回

##### 3.2. 当手指滑出View时
>普通的父布局不会管子view的 touch 事件, 但是 RecyclerView 会在 onInterceptTouchEvent 里判断当 event 区域超过子 view 后调用 setScrollStat(), 拦截掉接下来的 move 事件. 这样子 view 被ACTION_CANCEL, 父布局 RecyclerView 根据 move 来滑动

如果手指超出了 view 后发生位移(Recyclerview中), 可根据 Recyclerview 触发的 ACTION_CANCEL 获取到离开的标志
如果 view 无法位移(在普通 view 中), 只可用 onTouchEvent 根据 ACTION_UP 拿到手指离开的标志

#### 优化后的效果
* ![](https://raw.githubusercontent.com/sunxlfred/RES/master/18_4_13_mastodon_button_new.gif)



> 参考
> 感谢黄海奇的指点
> [Hencoder1-1](http://hencoder.com/ui-1-1/)
> [arcToMethod](https://blog.csdn.net/whyrjj3/article/details/7940385)
> [getBackgroundColor](https://blog.csdn.net/u013210543/article/details/51743401)
> [Path测量工具：PathMeasure](https://www.jianshu.com/p/82afb9c2e959)
> [安卓自定义View进阶-PathMeasure](http://www.gcssloop.com/customview/Path_PathMeasure/)
> [CSDN | 自定义view系列(3)--给自定义View添加点击事件](https://blog.csdn.net/qiang_xi/article/details/52298315)
> TODO
> [Android自定义View长按事件的实现](https://blog.csdn.net/baidu_35701759/article/details/69258760)
