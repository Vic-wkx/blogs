对于Android的资源文件的命名，Google官方并没有提供统一的规范，民间的命名方式可谓是百花齐放、兼容并包，比较知名的有阿里巴巴开发规范，以及Blankj的[AndroidStandardDevelop](https://github.com/Blankj/AndroidStandardDevelop)，以下内容是笔者参考了其他的一些命名规范，加上自己总结的一些命名方式所得。由于项目比较小，项目所涉及的资源文件类型并不多，所以总结得并不全面，仅供参考。

#### 图片命名规则：图片类型_逻辑名称_功能名称{_颜色}{_大小}{_状态}

{} 中的内容为可选；

## 1.常用图片类型表格：
| 中文名 | 名称         | 缩写                |
| ------ | -------------- | --------------------- |
| 背景 | background     | bg                    |
| 图标 | icon           | ic                    |
| 文字 | text           | txt（主要用于艺术字） |
## 2.常用逻辑名称表格：
| 中文名  | 名称       | 缩写                         |
| ---------- | ------------ | ------------------------------ |
| 按钮     | Button       | btn                            |
| 菜单     | Menu         | menu                           |
| 标签页  | Tab          | tab                            |
| 卡片     | Card         | card                           |
| 横幅     | Banner       | banner                         |
| 箭头     | Arrow        | arrow                          |
| 列表     | List         | list                           |
| 列表子项 | Item         | item                           |
| 布局     | Layout       | layout（整个页面布局或子布局） |
| 文本     | TextView     | tv                             |
| 可编辑文本 | EditText     | et                             |
| 多选框  | CheckBox     | cb                             |
| 单选框  | RadioButtion | rb                             |
| 进度条  | ProgressBar  | pb                             |
| 拖动条  | SeekBar      | sb                             |
| 下拉选择框 | Spinner      | spn                            |
## 3.功能名称
功能名称不使用缩写，如果由多个单词组成，不同的单词之间用下划线'_'隔开，否则Android Studio会警告单词拼写错误。

举个例子：
（1）菜单栏返回图标：ic_menu_back，back就是功能名称，代表此图标的功能是返回

（2）充值页横幅背景图：bg_banner_recharge，recharge就是功能名称，代表此横幅用于充值页面

（3）选择国家下拉框背景图：bg_spn_choose_country：choose_country就是功能名称，代表此下拉框用于选择国家，两个单词用下划线'_'隔开

## 4.颜色
如果同一张图片有多个颜色，则在命名中连缀颜色属性（例如：_black、_white、_yellow），否则不添加颜色属性。

## 5.大小
如果同一张图片分为小图和大图，则在命名中连缀大小属性（例如：_big、_small），否则不添加大小属性。
## 6.状态
如果同一张图片有多个状态，则在命名中连缀状态属性，否则不添加状态属性。
常用状态表格：
| 名称   | 说明                                 |
| -------- | -------------------------------------- |
| normal   | 普通状态（如果有多种状态，normal必写） |
| pressed  | 按下状态                           |
| selected | 选中状态                           |
| checked  | 勾选状态                           |
| disabled | 不可用状态                        |
| focused  | 聚焦状态                           |
状态属性放在命名最后，如果有一张图片有多种状态，则普通状态效果后必须连缀_normal，因为不同状态的图片会放在同一个选择器中，选择器将以这张图片无状态属性的名字命名。

举个例子：
确认按钮背景图按下状态：bg_btn_ok_pressed
确认按钮背景图普通状态：bg_btn_ok_normal
这两张背景图会放在一个选择器中，选择器名字为bg_btn_ok，这个选择器将被设置为按钮的背景图。如果普通状态效果后没有连缀_normal，则会和此选择器命名冲突。

## 7.再举几个例子

个人中心标签图标普通状态：ic_tab_me_normal

个人中心标签图标选择状态：ic_tab_me_selected

确认按钮背景普通状态：bg_btn_ok_normal

确认按钮背景按下状态：bg_btn_ok_pressed

列表空图：ic_list_empty

列表加载中图：ic_list_loading

列表加载失败图：ic_list_error

月卡背景图：bg_card_monthly

菜单栏充值历史记录图标：ic_menu_recharge_history

输入邀请码文本框背景图：bg_et_enter_invitation_code

登录页背景图：bg_layout_login

消息列表子项未读图标：ic_item_message_unread

消息列表子项已读图标：ic_item_message_have_read

充值列表单选框图标普通状态：ic_rb_recharge_normal

充值列表单选框图标选中状态：ic_rb_recharge_selected

......

以上，就是Android图片资源命名规范。

