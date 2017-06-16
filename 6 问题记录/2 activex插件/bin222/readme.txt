插件介绍：	插件为通用（IE）网页调用客户端插件，允许通过该插件检查客户端版本，启动客户端，并允许监听客户端关闭事件
创建时间： 2017-3-24
作者：
接口说明
	Boolean Init(string);  该接口为插件第一个接口，使用插件前需要调用该接口，参数为  注册表路径,注册表 根为：HKEY_LOCAL_MACHINE 若注册表路径存在 则返回 true，否则为false
	string GetClientVersion(); 调用该接口后，插件会对去“注册表”中的 version 值，作为客户端的版本号并返回
	void StartupClient(bool,string);调用接口后，插件会读取“注册表”中的path值，并作为客户端的安装路径，启动客户端；第一个参数为 客户端的可见性、第二个参数为启动客户端的命令行参数
	event OnClientExit(int); 该接口为客户端退出事件，在启动的客户端关闭时会触发该事件；参数为 客户端退出时的 exitcode
	
	Boolean ProtocolExists(string); 该接口主要通过检查注册表用以检测协议是否注册，注册表根为：HKEY_CLASSES_ROOT，参数为协议名称
注册：
	regsvr32 iFlytekClientActiveX.ocx