# security-template

## 使用技术
 - Session
 - Mybatis/Mybatis Plus/MySQL
 - Redis
 
## 功能支持
 - 认证
 - 授权
 - 记住我
 - Csrf 防御
 - 登录设备数量限制
 - 分布式 session
 
 ## 使用
 - 登录验证使用表单
 - 需要使用记住我功能时，表单传入 remember-me: on
 - 使用 csrf 防御功能时，需要先向接口 /csrf 发送 post 请求，获取cookie中的 XSRF-TOKEN 字段，并在登录时传入 _csrf:xxx
 - 登陆完成后，cookie 中的 XSRF-TOKEN 字段会被清除，后续的请求需要再次获取到 XSRF-TOKEN
