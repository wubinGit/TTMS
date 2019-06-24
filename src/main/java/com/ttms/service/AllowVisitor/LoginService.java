package com.ttms.service.AllowVisitor;

import com.ttms.Entity.SysMenus;
import com.ttms.Entity.SysUser;
import com.ttms.Vo.Menus;
import com.ttms.Vo.MenusItem;
import com.ttms.Vo.ModulesVo;
import com.ttms.service.SystemManage.SysMenusService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class LoginService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SysMenusService sysMenusService;

    public SysUser getUserByUserName(String username) {
        return sysMenusService.getUserByUserName(username);
    }

    public List<ModulesVo> getUserMenusVo() {
        //获取菜单树
        List<SysMenus> sysMenusTree = sysMenusService.getSysMenusTree();
        //获取该用户
        SysUser sysUser = (SysUser)SecurityUtils.getSubject().getPrincipal();
        //获取用户的所有权限菜单id
        List<Integer> allowedMenuIds = sysMenusService.getAllowedMenuidsByRoleid(sysUser.getRoleid());
        LinkedList<ModulesVo> modules = new LinkedList<>();
        fillModules(sysMenusTree,modules,allowedMenuIds);
        return modules;
    }

    //填充Modules
    private void fillModules(List<SysMenus> sysMenusTree, LinkedList<ModulesVo> modules, List<Integer> allowedMenuIds) {
        for (SysMenus sysMenus:sysMenusTree){
            ModulesVo modulesVo = new ModulesVo();
            modulesVo.setMenuId(sysMenus.getId());
            modulesVo.setModuleIcon("default");
            modulesVo.setModuleName(sysMenus.getName());
            modulesVo.setPath("/"+sysMenus.getUrl());
            if(allowedMenuIds.contains(sysMenus.getId())){
                //存在所有权限
                modulesVo.setMenus(fillMenus(sysMenus.getChildMenus(),allowedMenuIds,true,null));
                modules.add(modulesVo);
            }else{
                //不存在第一级的权限
                pointer modulePoint = new pointer();
                modulesVo.setMenus(fillMenus(sysMenus.getChildMenus(),allowedMenuIds,false,modulePoint));
                if(modulePoint.val()){
                    //存在子权限
                    modules.add(modulesVo);
                }
            }
        }
    }
    //填充Menus
    private List<Menus> fillMenus(List<SysMenus> subMenusTrees, List<Integer> allowedMenuIds,boolean recursive,pointer p){
        LinkedList<Menus> menus = new LinkedList<>();
        for (SysMenus sysMenus : subMenusTrees){
            Menus menu = new Menus();
            menu.setMenuId(sysMenus.getId());
            menu.setAction(sysMenus.getNote());
            menu.setTitle(sysMenus.getName());
            menu.setPath("/"+sysMenus.getUrl());
            if(recursive){
                //第一级目录所有权限
                menu.setMenusItems(fillMenusItem(sysMenus.getChildMenus(),allowedMenuIds,true,null));
                menus.add(menu);
            }else{
                //只要第二层权限 置pointer为true
                if(allowedMenuIds.contains(sysMenus.getId())){
                    //通知上层
                    p.set(true);
                    menu.setMenusItems(fillMenusItem(sysMenus.getChildMenus(),allowedMenuIds,true,null));
                    menus.add(menu);
                }else{
                    //放探针第三层
                    pointer menuPoint = new pointer();
                    menu.setMenusItems(fillMenusItem(sysMenus.getChildMenus(),allowedMenuIds,false,menuPoint));
                    if(menuPoint.val()){
                        p.set(true);
                        menus.add(menu);
                    }
                }
            }
        }
        return menus;
    }

    //填充MenusItem
    private List<MenusItem> fillMenusItem(List<SysMenus> ssubMenusTree, List<Integer> allowedMenuIds, boolean recursive, pointer p){
        List<MenusItem> menusItems = new LinkedList<>();
        for (SysMenus sysMenus : ssubMenusTree){
            MenusItem menusItem = new MenusItem();
            menusItem.setTitle(sysMenus.getName());
            menusItem.setMenuId(sysMenus.getId());
            menusItem.setPath("/"+sysMenus.getUrl());
            if(recursive){
                //第二级目录普配
                  menusItems.add(menusItem);
            }else{
                //只要第三层 置pointer为true
                if(allowedMenuIds.contains(sysMenus.getId())){
                    p.set(true);
                    menusItems.add(menusItem);
                }
            }
        }
        return menusItems;
    }

    /**
    * 功能描述: <br>
    * 〈〉修改密码
    * @Param: [newPassword, salt]
    * @Return: void
    * @Author: 吴彬
    * @Date: 14:53 14:53
     */
    public Boolean updatePwd(String newPassword, String salt) {
        Void pwd = this.sysMenusService.updatePwd(newPassword, salt);
        if(pwd==null){
            return true;
        }
        return false;
    }

    //权限探针
    class pointer{
        private boolean isNull = false;
        public void set(boolean b){
            isNull = b;
        }
        public boolean val(){
            return isNull;
        }
    }
}
