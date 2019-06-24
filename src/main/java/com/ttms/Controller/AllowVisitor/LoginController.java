package com.ttms.Controller.AllowVisitor;

import com.ttms.Config.DistributorLock;
import com.ttms.Config.MyThreadLocal;
import com.ttms.Entity.SysUser;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Enum.RedisKeyPrefixEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Vo.ModulesVo;
import com.ttms.service.AllowVisitor.LoginService;
import com.ttms.utils.CodecUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class LoginController {

    @Autowired
    private MyThreadLocal myThreadLocal;
    @Autowired
    private LoginService loginService;
    @Autowired
    private DistributorLock distributorLock;
    /**
     * 功能描述: <br>
     * 〈〉登陆逻辑
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<java.util.List<com.ttms.Entity.SysMenus>>
     * @Author: 万少波
     * @Date: 2019/5/26 17:05
     */
    @PostMapping("/login")
    public ResponseEntity<List<ModulesVo>> login(@RequestParam String username , @RequestParam String password){
        //获取subject
        Subject subject = SecurityUtils.getSubject();
        System.out.println("JSessionId"+subject.getSession().getId());
        //根据用户名查询到用户
        SysUser currUser = loginService.getUserByUserName(username);
        //如果没有该用户则一定登录失败
        if(currUser == null)
            throw new TTMSException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        //如果用户被禁用
        if(currUser.getValid() == 0){
            throw new TTMSException(ExceptionEnum.USER_HAVE_BEEN_LIMIT);
        }
        myThreadLocal.setTempUser(currUser);
        password = CodecUtils.md5Hex(password, currUser.getSalt());
        //封装用户名和密码
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        try {
            subject.login(usernamePasswordToken);
            //只要没有任何异常则表示登录成功
            //如果该用户已经登录  获取该分布式锁
            if(!distributorLock.setIfAbsent(RedisKeyPrefixEnum.USER_ONLINE_KEY.val()+
                    String.valueOf(currUser.getId()),String.valueOf(1),30)){
                subject.logout();
                throw new TTMSException(ExceptionEnum.WARMING_USER_HAVE_LOGIN);
            }
            log.debug("用户" + currUser.getUsername() + "登录");
            List<ModulesVo> result = loginService.getUserMenusVo();
            System.out.println("完成登录请求"+System.currentTimeMillis());
            //查询用户能访问到的菜单并返回
            return ResponseEntity.ok(result);
        }catch (LockedAccountException e) {
            //该账户已被锁定
            throw new TTMSException(ExceptionEnum.USER_ACCOUNT_LOCK);
        }catch (TTMSException e){
            throw e;
        }catch (Exception e) {
            //用户名不存在
            throw new TTMSException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
    }

    /**
     * 功能描述: <br>
     * 〈〉退出登录
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 万少波
     * @Date: 2019/6/2 13:51
     */
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(){
        Subject subject = SecurityUtils.getSubject();
        SysUser user = (SysUser)subject.getPrincipal();
        //Redis中退出
        distributorLock.expireNow(RedisKeyPrefixEnum.USER_ONLINE_KEY.val()+String.valueOf(user.getId()));
        if (subject.isAuthenticated()) {
            subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
              log.debug("用户" + user.getUsername() + "退出登录");
        }
        return ResponseEntity.ok(null);
    }

    /**
     * 功能描述: <br>
     * 〈〉心跳机制
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Author: 万少波
     * @Date: 2019/6/19 21:20
     */
    @GetMapping("/heartbeat")
    public ResponseEntity<Boolean> heartbeat(){
        SysUser user = (SysUser)SecurityUtils.getSubject().getPrincipal();
        if (user != null) {
            //心跳
            distributorLock.expire(RedisKeyPrefixEnum.USER_ONLINE_KEY.val()+String.valueOf(user.getId()),30, TimeUnit.SECONDS);
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.ok(false);
        }
    }
    /**
     * 功能描述: <br>
     * 〈〉获取当前用户
     * @Param: []
     * @Return: org.springframework.http.ResponseEntity<com.ttms.Entity.SysUser>
     * @Author: 万少波
     * @Date: 2019/6/4 8:14
     */
    @PostMapping("/getCuruser")
    public ResponseEntity<SysUser> getCuruser(){
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()){
            throw new TTMSException(ExceptionEnum.USER_UNLOGIN);
        }
        SysUser user = (SysUser)subject.getPrincipal();
        return ResponseEntity.ok(user);
    }

  /**
  * 功能描述: <br>
  * 〈〉修改用户的密码
  * @Param: [oldPassword, newPassword]
  * @Return: org.springframework.http.ResponseEntity<java.lang.Void>
  * @Author: 吴彬
  * @Date: 11:14 11:14
   */
    @PostMapping("/account/pwd")
    public ResponseEntity<Void> updatePwd(
             @RequestParam String oldPassword,@RequestParam String newPassword){
        if(StringUtils.isBlank(oldPassword)){
            throw new TTMSException(ExceptionEnum.PASSWORD_NOT_NULL);
        }
        if(StringUtils.isBlank(newPassword)){
            throw new TTMSException(ExceptionEnum.NEWPASSWORD_NOT_NULL);
        }
        Subject subject = SecurityUtils.getSubject();
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        // 校验原密码是否正确
        SysUser sysUser = loginService.getUserByUserName(user.getUsername());

        oldPassword = CodecUtils.md5Hex(oldPassword, sysUser.getSalt());
        if(!oldPassword.equals(sysUser.getPassword())){
            throw new TTMSException(ExceptionEnum.PASSWORD_ERROR);
        }
        // 修改密码加密处理
        Boolean aBoolean = this.loginService.updatePwd(newPassword, sysUser.getSalt());
        if(!aBoolean) {
            throw new TTMSException(ExceptionEnum.EDIT_PASSWORD_FAIL);
        }
        //退出
        SecurityUtils.getSubject().logout();
        return ResponseEntity.ok(null);
    }


}
