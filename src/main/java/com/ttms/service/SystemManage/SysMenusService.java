package com.ttms.service.SystemManage;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ttms.Config.MenuIdPermsMap;
import com.ttms.Dto.TempRole;
import com.ttms.Entity.*;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Enum.RedisKeyPrefixEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Mapper.*;
import com.ttms.Vo.PageResult;
import com.ttms.utils.CodecUtils;
import com.ttms.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysMenusService {
    @Autowired
    private SysMenusMapper sysMenusMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMenusMapper sysRoleMenusMapper;
    @Autowired
    private SysRolesMapper sysRolesMapper;
    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MenuIdPermsMap menuIdPermsMap;
    /**
     * 功能描述: <br>根据pid查询sysmenu
     * 〈〉
     * @Param: [id]
     * @Return: java.util.List<com.ttms.Entity.SysMenus>
     * @Author: 万少波
     * @Date: 2019/5/26 12:50
     */
    public List<SysMenus> getSysMenusByPid(int id){
        SysMenus sysMenus = new SysMenus();
        sysMenus.setParentid(id);
        List<SysMenus> sysMenusList = sysMenusMapper.select(sysMenus);
        return sysMenusList;
    }

    /**
     * 查询sysMenus树  缓存中存在就现在缓存中找
     * @return
     */
    public List<SysMenus> getSysMenusTree(){
        List<SysMenus> parentSysMenus = null;
        String parentSysMenusStr = redisTemplate.opsForValue().get(RedisKeyPrefixEnum.SYSMENUS_TREE.val());
        if(StringUtils.isEmpty(parentSysMenusStr)) {
            parentSysMenus = getSysMenusByPid(0);
            getSysMensByPidRecursive(parentSysMenus);
            //将查询到的菜单项存入redis
            redisTemplate.opsForValue().set(RedisKeyPrefixEnum.SYSMENUS_TREE.val(), JsonUtils.serialize(parentSysMenus));
        }else
            parentSysMenus = JsonUtils.parseList(parentSysMenusStr,SysMenus.class);
        return parentSysMenus;
    }

    /**
     * 功能描述: <br> 递归查询子菜单
     * 〈〉
     * @Param: [sysMenuList]
     * @Return: java.util.List<com.ttms.Entity.SysMenus>
     * @Author: 万少波
     * @Date: 2019/5/26 12:50
     */
    public List<SysMenus> getSysMensByPidRecursive(List<SysMenus> sysMenuList){
        if(sysMenuList == null || sysMenuList.size() ==0)
            return sysMenuList;
        for (SysMenus sysMenu:sysMenuList){
            List<SysMenus> childMenus = getSysMenusByPid(sysMenu.getId());
            sysMenu.setChildMenus(getSysMensByPidRecursive(childMenus));
        }
        return sysMenuList;
    }

    /**
     * 功能描述: <br>获取所有拦截的url 和 需要给的permission
     * 〈〉
     * @Param: []
     * @Return: java.util.Map<java.lang.String,java.lang.String>
     * @Author: 万少波
     * @Date: 2019/5/26 12:46
     */
    public Map<String,String> getUrlPermissionMapping(){
        List<SysMenus> sysMenuTree = getSysMenusTree();
        HashMap<String, String> urlPermissionsMapping = new HashMap<>();
        Stack<String> urlStack = new Stack<>();
        Stack<String>  permissionStack= new Stack<>();
        getUrlPermissionMappingRecursive(urlPermissionsMapping,urlStack,permissionStack,sysMenuTree);
        return urlPermissionsMapping;
    }


    /**
     * 功能描述: <br>递归获取url --> Mapping映射s
     * 〈〉
     * @Param: [urlPermissionsMapping, urlStack, permissionStack, sysMenusList]
     * @Return: void
     * @Author: 万少波
     * @Date: 2019/5/26 12:50
     */
    public void getUrlPermissionMappingRecursive(Map<String,String> urlPermissionsMapping ,
                                                 Stack<String> urlStack ,
                                                 Stack<String> permissionStack,
                                                 List<SysMenus> sysMenusList){
        if(CollectionUtils.isEmpty(sysMenusList))
            return;
        for (SysMenus sysMenu : sysMenusList) {
            //获取一级分类id-perms Mapping
            //获取子菜单
            List<SysMenus> childMenus = sysMenu.getChildMenus();
            //将当前子菜单urlStack 和 permissionStack 压栈
            urlStack.push(sysMenu.getUrl());
            permissionStack.push(sysMenu.getPermission());
            if(CollectionUtils.isEmpty(childMenus)){
                //当前为子结点  /添加映射
                menuIdPermsMap.put(sysMenu.getId(),currPermsInStack(permissionStack,true));
                addUrlPermissionMapping(urlPermissionsMapping , urlStack , permissionStack);
            }else{
                //当前不为子结点
                menuIdPermsMap.put(sysMenu.getId(),currPermsInStack(permissionStack,false));
                getUrlPermissionMappingRecursive(urlPermissionsMapping,urlStack,permissionStack,childMenus);
            }
            //urlStack 和 permissionStack 出栈
            urlStack.pop();
            permissionStack.pop();
        }
    }

    /**
     * 功能描述: <br>
     * 〈〉       根据是否是叶子结点生成对应的perms
     * @Param: [permissionStack, isLeaf]
     * @Return: java.lang.String
     * @Author: 万少波
     * @Date: 2019/5/27 13:53
     */
    private String currPermsInStack(Stack<String> permissionStack ,boolean isLeaf){

        StringBuffer permissionBuffer = new StringBuffer();
        for (String spermission: permissionStack) {
            permissionBuffer.append(spermission+":");
        }
        if(isLeaf){
            //如果是叶子结点
            return permissionBuffer.substring(0,permissionBuffer.length()-1);
        }else{
            //如果不是叶子结点
            return permissionBuffer.append("*").toString();
        }
    }

    /**
     * 功能描述: <br>添加url permission到Map
     * 〈〉
     * @Param: [urlPermissionsMapping, urlStack, permissionStack]
     * @Return: void
     * @Author: 万少波
     * @Date: 2019/5/26 12:50
     */
    private void addUrlPermissionMapping(Map<String, String> urlPermissionsMapping, Stack<String> urlStack, Stack<String> permissionStack) {
        StringBuffer urlBuffer = new StringBuffer();
        for (String surl: urlStack) {
            urlBuffer.append("/"+surl);
        }
        urlBuffer.append("/**");


        StringBuffer permissionBuffer = new StringBuffer();
        permissionBuffer.append("authc,perms[");
        for (String spermission: permissionStack) {
            permissionBuffer.append(spermission+":");
        }
        permissionBuffer.setCharAt(permissionBuffer.length()-1,']');
        urlPermissionsMapping.put(urlBuffer.toString(),permissionBuffer.toString());
    }


    /*功能描述
     *@author罗占
     *@Description
     *Date15:09 2019/5/26
     *Param
     *return
     **/
    public PageResult<SysUser> queryUserByPage(Integer page,Integer rows,String name){
        PageHelper.startPage(page,rows);
        Example example = new Example(SysUser.class);
        if(StringUtils.isNotBlank(name)){
            example.createCriteria().andLike("username","%"+name+"%");
        }
        List<SysUser> list = sysUserMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            throw new TTMSException(ExceptionEnum.USER_NOT_FOUND);
        }
        PageInfo<SysUser> pageInfo = new PageInfo<>(list);
        PageResult<SysUser> result=new PageResult<>();
        //封装角色,部门相关信息
          //获取所需的角色id
        Set<Integer> idsset = list.stream().map(SysUser::getRoleid).collect(Collectors.toSet());
        Map<Integer, TempRole> tempRoleMap = this.getRoleAndDePartInfoByRoleIds(
                new LinkedList<>(idsset)).stream().collect(Collectors.toMap(TempRole::getId, item -> item));
        //查询好的相关数据赋给用户
        list.forEach(item->{
            item.setTempRole(tempRoleMap.get(item.getRoleid()));
        });
        result.setTotal(pageInfo.getTotal());
        result.setTotalPage(pageInfo.getPages());
        result.setItems(pageInfo.getList());
        return result;
    }

    /*
     *功能描述：根据id查询用户
     *@author罗占
     *@Description
     *Date15:36 2019/5/26
     *Param
     *return
     **/
    public SysUser getUserById(Integer id){
        SysUser user = sysUserMapper.selectByPrimaryKey(id);
        if(user == null){
            throw new TTMSException(ExceptionEnum.USER_NOT_FOUND);
        }
        return user;
    }
    /**
    * 功能描述: <br>
    * 〈〉根据主键id查询所有用户
    * @Param: [ids]
    * @Return: java.util.List<com.ttms.Entity.SysUser>
    * @Author: 吴彬
    * @Date: 10:35 10:35
     */
    public List<SysUser> getUserListById(List<Integer> ids){
        List<SysUser> user = sysUserMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(user)){
            throw new TTMSException(ExceptionEnum.USER_NOT_FOUND);
        }
        return user;
    }

    /**
     * 功能描述: <br>根据用户名查询用户
     * 〈〉
     * @Param: [username]
     * @Return: com.ttms.Entity.SysUser
     * @Author: 万少波
     * @Date: 2019/5/26 17:39
     */
    public SysUser getUserByUserName(String username){
        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);
        List<SysUser> users = sysUserMapper.select(sysUser);
        if(!CollectionUtils.isEmpty(users))
            return users.get(0);
        else
            return null;
    }
    /*
     *功能描述：删除用户
     *@author罗占
     *@Description
     *Date16:50 2019/5/26
     *Param
     *return
     **/
    public void deleteUserById( Integer id){
        int i =  this.sysUserMapper.deleteByPrimaryKey(id);
        if(i != 0 ){
            throw new TTMSException(ExceptionEnum.USER_DELETE_FAIL);
        }
    }

    /**
     * 功能描述: 查询所有角色
     * 〈〉
     * @Param: []
     * @Return: java.util.List<com.ttms.Entity.SysRoles>
     * @Author: 吴彬
     * @Date: 15:49 15:49
     */
    public List<SysRoles> getAllRoles() {
        List<SysRoles> list = this.sysRolesMapper.selectAll();
        if(CollectionUtils.isEmpty(list)){
            throw new TTMSException(ExceptionEnum.ROLERS_NOT_FOUND);
        }
        return list;
    }
    /**
     * @Description:    分页查询所有角色
     * @param
     * @Author:         吴彬
     * @UpdateRemark:   修改内容
     * @Version:        1.0
     */
    public PageResult<SysRoles> getRolesByPage(Integer page, Integer rows, String name) {
        PageHelper.startPage(page, rows);
        Example e=new Example(SysRoles.class);
        if(StringUtils.isNotBlank(name)){
            e.createCriteria().andLike("name", "%"+name+"%");
        }
        List<SysRoles> sysRoles = this.sysRolesMapper.selectByExample(e);
        if(CollectionUtils.isEmpty(sysRoles)){
            throw new TTMSException(ExceptionEnum.ROLERS_NOT_FOUND);
        }
        //封装部门信息
          //获取所有id
        Set<Long> roleIdSet = sysRoles.stream().map(SysRoles::getId).collect(Collectors.toSet());
         //获取部门信息
        Map<Integer,TempRole> tempRoleMap = (Map<Integer, TempRole>) this.getRoleAndDePartInfoByRoleIds(
                new LinkedList(roleIdSet)).stream().collect(Collectors.toMap(TempRole::getId, item->item));
         //遍历填充
        for (SysRoles sysRole : sysRoles) {
                sysRole.setTempRole(tempRoleMap.get(sysRole.getId().intValue()));
        }

        PageInfo<SysRoles> list=new PageInfo<>(sysRoles);
        PageResult<SysRoles> result = new PageResult<SysRoles>();
        result.setItems(sysRoles);
        result.setTotal(list.getTotal());
        result.setTotalPage(list.getPages());
        return result;
    }
    /**
     * 功能描述:添加角色和分配权限
     * 〈〉
     * @Param: [name, note, menuIds, username]
     * @Return: void
     * @Author: 吴彬
     * @Date: 17:32 17:32
     */
    @Transactional
    public void AddRole(String name, String note, List<Integer> menuIds, Integer create_userid, Integer departmentId) {
        //添加角色
        SysRoles role=new SysRoles();
        role.setName(name);
        role.setNote(note);
        Date now = new Date();
        role.setCreatedtime(now);
        role.setModifiedtime(now);
        role.setCreateduserid(create_userid);
        role.setModifieduserid(create_userid);
        role.setDepartmentid(departmentId);
        role.setModifiedtime(null);
        int i = this.sysRolesMapper.insert(role);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.INSERT_ROLERS_FAIL);
        }
        //添加角色和菜单的关联表
        addRoleAndMenus(role,menuIds);

    }

    private void addRoleAndMenus(SysRoles role, List<Integer> menuIds) {
        SysRoleMenus roleMenus = null;
        for (Integer id : menuIds) {
            roleMenus = new SysRoleMenus();
            String s = String.valueOf(role.getId());
            roleMenus.setRoleId(Integer.parseInt(s));
            roleMenus.setMenuId(id);
            int i = this.sysRoleMenusMapper.insert(roleMenus);
            if (i != 1) {
                throw new TTMSException(ExceptionEnum.INSERT_ROLERS_FAIL);
            }

        }
    }

        /**
         * 功能描述: 新增用户
         * 〈〉
         * @Param: [user]
         * @Return: void
         * @Author: lhf
         * @Date: 2019/5/26 17:40
         */
        @Transactional
        public void addSysUser( String username,  String image,
                                String password,  String mail,
                                String  phonenumber , Integer roleId) {
            //判断用户名是否重复
            SysUser user = new SysUser();
            user.setUsername(username);
            List<SysUser> duser = sysUserMapper.select(user);
            if (!CollectionUtils.isEmpty(duser)) {
                throw new TTMSException(ExceptionEnum.USER_NAME_DUPLICATED);
            }
            user.setImage(image);
            user.setSalt(CodecUtils.generateSalt());
            password = CodecUtils.md5Hex(password, user.getSalt());
            user.setPassword(password);
            user.setEmail(mail);
            user.setMobile(phonenumber);
            user.setValid((byte) 1);
            user.setRoleid(roleId);
            Date now = new Date();
            user.setCreatedtime(now);
            user.setModifiedtime(null);
            SysUser curUser = (SysUser)SecurityUtils.getSubject().getPrincipal();
            user.setCreateduserid(curUser.getId());
            //设置新增用户
            int i = this.sysUserMapper.insert(user);
            if (i != 1) {
                throw new TTMSException(ExceptionEnum.USER_ADD_FAILURE);
            }

        }

    /**
     * 功能描述: 修改用户
     * 〈〉
     * @Param: [id]
     * @Return: com.ttms.Entity.SysUser
     * @Author: lhf
     * @Date: 2019/5/26 19:39
     */
        public SysUser updateUserById(SysUser user) {
            int count = this.sysUserMapper.updateByPrimaryKey(user);
            if (count != 1) {
                throw new TTMSException(ExceptionEnum.USER_UPDATE_FAILURE);
            }
            return user;
        }

        /**
         * 功能描述: 启用和禁用用户
         * 〈〉
         * @Param: [id]
         * @Return: void
         * @Author: lhf
         * @Date: 2019/5/26 17:40
         */
        public void validOrInvalid(Integer id) {
            SysUser user = findUserById(id);
            System.out.println("user = " + user);
       /* int vid = ;
        vid = vid ^ 1;*/
            //vid = 1- vid;
            user.setValid((byte) (user.getValid() ^ 1));
            int i = this.sysUserMapper.updateByPrimaryKey(user);
            if (i != 1) {
                throw new TTMSException(ExceptionEnum.USER_VALID_MODIFY_ERROR);
            }
        }

        /**
         * 功能描述: 查找用户
         * 〈〉
         * @Param: [id]
         * @Return: com.ttms.Entity.SysUser
         * @Author: lhf
         * @Date: 2019/5/26 17:41
         */
        public SysUser findUserById(Integer id) {
            SysUser user = this.sysUserMapper.selectByPrimaryKey(id);
            if (user == null) {
                throw new TTMSException(ExceptionEnum.USER_NOT_EXIST);
            }
            return user;
        }

        /**
         * 功能描述: 新增部门
         * 〈〉
         * @Param: [sysdepartment]
         * @Return: void
         * @Author: lhf
         * @Date: 2019/5/27 14:44
         */
        public void addDepartment(String departmentName,  String departmentCode,
                                  String departmentNote,int parentId) {
            SysDepartment department = new SysDepartment();
            department.setDepartmentname(departmentName);
            department.setDepartmentcode(departmentCode);
            department.setNote(departmentNote);
            department.setParentid(parentId);
            department.setIsparent((byte) (parentId == 0 ? 1:0));
            department.setValid((byte) 1);
            //获取当前用户
            SysUser curUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
            Date now = new Date();
            department.setModifiytime(now);
            department.setModifyuserid(curUser.getId());
            department.setCreateuserid(curUser.getId());
            department.setCreatetime(now);
            int i = this.sysDepartmentMapper.insert(department);
            if (i != 1) {
                throw new TTMSException(ExceptionEnum.DEPARTMENT_ADD_FAILURE);
            }
        }

        public List<SysUser> getUsersByIds(List<Integer> userIds){
            List<SysUser> users = sysUserMapper.selectByIdList(userIds);
            if(org.apache.commons.collections.CollectionUtils.isEmpty(users))
                throw new TTMSException(ExceptionEnum.USER_NOT_FOUND);
            return users;
        }

    public List<SysDepartment> getDepartmentsByIds(ArrayList<Integer> departIdList) {
        List<SysDepartment> sysDepartments = sysDepartmentMapper.selectByIdList(departIdList);
        if (CollectionUtils.isEmpty(sysDepartments)) {
            throw new TTMSException(ExceptionEnum.DEPARTMENT_NOT_FOUND);
        }
        return sysDepartments;
    }

    /*
    *功能描述：分页查询所有机构
    *@author罗占
    *@Description
    *Date7:59 2019/5/30
    *Param
    *return
    **/
    public PageResult<SysDepartment> queryDepartment(Integer page, Integer rows,
                                                     String departmentname, Integer valid , Integer pid){
        PageHelper.startPage(page,rows);
        Example example = new Example(SysDepartment.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(departmentname)){
            criteria.andLike("departmentname","%"+departmentname+"%");
        }
        if(valid != -1){
            criteria.andEqualTo("valid",valid);
        }
        criteria.andEqualTo("parentid",pid);
        List<SysDepartment> list= this.sysDepartmentMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){

            throw new TTMSException(ExceptionEnum.DEPARTMENT_NOT_FOUND);
        }
        PageInfo<SysDepartment> pageInfo = new PageInfo<>(list);
        PageResult<SysDepartment> result = new PageResult<>();
        result.setItems(pageInfo.getList());
        result.setTotal(pageInfo.getTotal());
        result.setTotalPage(pageInfo.getPages());
        return result;
    }

    /*
    *功能描述：禁用和启用部门
    *@author罗占
    *@Description
    *Date8:56 2019/5/30
    *Param
    *return
    **/
    public void  updateDepartmentValidOrInvalid(Integer id) {
        SysDepartment department = this.sysDepartmentMapper.selectByPrimaryKey(id);
        department.setValid((byte) (department.getValid() ^ 1));
        int i = this.sysDepartmentMapper.updateByPrimaryKeySelective(department);
        //如果该部门是一级部门，将其中子部门修改为同样的状态
        if(department.getParentid() == 0){
            Example example = new Example(SysDepartment.class);
            example.createCriteria().andEqualTo("parentid",department.getId());
            SysDepartment sysDepartment = new SysDepartment();
            sysDepartment.setValid(department.getValid());
            sysDepartmentMapper.updateByExampleSelective(sysDepartment,example);
        }
        if (i != 1) {
            throw new TTMSException(ExceptionEnum.DEPARTMENT_VALID_MODIFY_ERROR);
        }
     }




        /*
        *功能描述：修改部门
        *@author罗占
        *@Description
        *Date9:13 2019/5/30
        *Param[id]
        *returnvoid
        **/
        public void updateDepartment(SysDepartment department){
            //获取当前用户
            SysUser curUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
            Date now = new Date();
            department.setModifiytime(now);
            department.setModifyuserid(curUser.getId());
            department.setCreateuserid(curUser.getId());
            department.setCreatetime(now);

            int i = this.sysDepartmentMapper.updateByPrimaryKeySelective(department);
            if (i != 1) {
                throw new TTMSException(ExceptionEnum.DEPARTMENT_ADD_FAILURE);
            }


        }

    //多条件查询部门
    public List<SysDepartment> getDepartmentsByCriteria(int parentId,String departmentName){
        SysDepartment sysDepartment = new SysDepartment();
        Example example = new Example(SysDepartment.class);
        Example.Criteria criteria = example.createCriteria();
        //parentId
        if(parentId != -1){
            criteria.andEqualTo("parentid",parentId);
        }
        //departmentName
        if(StringUtils.isEmpty(departmentName)){
            criteria.andEqualTo("departmentname",departmentName);
        }
        List<SysDepartment> sysDepartments = sysDepartmentMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(sysDepartments)) {
            throw new TTMSException(ExceptionEnum.DEPARTMENT_NOT_FOUND);
        }
        return sysDepartments;
    }

    //根据roleId返回可以访问的菜单
    public List<Integer> getAllowedMenuidsByRoleid(Integer roleid) {
        SysRoleMenus sysRoleMenus = new SysRoleMenus();
        sysRoleMenus.setRoleId(roleid);
        List<SysRoleMenus> sysRoleMenues = sysRoleMenusMapper.select(sysRoleMenus);
        if (CollectionUtils.isEmpty(sysRoleMenues)) {
            throw new TTMSException(ExceptionEnum.MENUS_ALLOW_ACCESS_IS_NULL);
        }
        return sysRoleMenues.stream().map(sysRoleMenu -> sysRoleMenu.getMenuId()).collect(Collectors.toList());
    }

    /**
    * 功能描述: <br>
    * 〈〉根据角色id查询角色
    * @Param: [roleId]
    * @Return: com.ttms.Entity.SysRoles
    * @Author: 吴彬
    * @Date: 9:43 9:43
     */
    public SysRoles getRoleById(Integer roleId){
        SysRoles sysRoles = this.sysRolesMapper.selectByPrimaryKey(roleId);
        if(sysRoles==null){
            throw new TTMSException(ExceptionEnum.ROLERS_NOT_FOUND);
        }
        return sysRoles;

    }

    /**
    * 功能描述: <br>
    * 〈〉查询所有父部门
    * @Param: []
    * @Return: java.util.List<com.ttms.Entity.SysDepartment>
    * @Author: 吴彬
    * @Date: 15:49 15:49
     */
    public List<SysDepartment> queryAllDepartment() {
        Example example=new Example(SysDepartment.class);
        example.createCriteria().andEqualTo("isparent", 1);
        List<SysDepartment> sysDepartments = this.sysDepartmentMapper.selectByExample(example);
        return sysDepartments;
    }

    /**
    * 功能描述: <br>
    * 〈〉根据父部门的id查询所有子部门
    * @Param: [pid]
    * @Return: java.util.List<com.ttms.Entity.SysDepartment>
    * @Author: 吴彬
    * @Date: 15:55 15:55
     */
    public List<SysDepartment> queryAllDepartmentBypid(Integer pid) {
        Example example=new Example(SysDepartment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentid", pid);
        criteria.andEqualTo("valid", 1);
        List<SysDepartment> sysDepartments = this.sysDepartmentMapper.selectByExample(example);
        return sysDepartments;
    }

    /**
     * 功能描述: <br>
     * 〈〉修改密码
     * @Param: [newPassword, salt]
     * @Return: java.lang.Void
     * @Author: 吴彬
     * @Date: 14:43 14:43
     */
    public Void updatePwd(String newPassword, String salt) {
        //根据salt查找用户设置相应的密码
        SysUser user=new SysUser();
        user.setSalt(CodecUtils.generateSalt());
        newPassword = CodecUtils.md5Hex(newPassword, user.getSalt());
        user.setPassword(newPassword);
        Example example=new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("salt", salt);
        int i = this.sysUserMapper.updateByExampleSelective(user, example);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.EDIT_PASSWORD_FAIL);
        }
        return null;
    }

    /**
    * 功能描述: <br>
    * 〈〉查询子部门下的所有角色
    * @Param: [departmentId]
    * @Return: java.util.List<com.ttms.Entity.SysRoles>
    * @Author: 吴彬
    * @Date: 10:58 10:58
     */
    public List<SysRoles> getRolesByDepartmentId(Integer departmentId) {
        List<SysRoles> roles = this.sysDepartmentMapper.getRolesByDepartmentId(departmentId);
        return roles;
    }

    /*
    *功能描述：修改角色
    *@author罗占
    *@Description
    *Date15:59 2019/6/10
    *Param
    *return
    **/
    public void updateRole(String name, String note, List<Integer> menuIds,Integer departmentId, Integer rid){
        SysRoles role = new SysRoles();
        role.setId(Long.valueOf(rid));
        role.setName(name);
        role.setNote(note);
        Date now = new Date();
        role.setCreatedtime(now);
        role.setModifiedtime(now);
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        role.setCreateduserid(user.getId());
        role.setModifieduserid(user.getId());
        role.setDepartmentid(departmentId);
        role.setModifiedtime(null);
        int i = this.sysRolesMapper.updateByPrimaryKey(role);
        if(i !=1){
            throw new TTMSException(ExceptionEnum.ROLERS_UPDATE_FAIL);
        }
        //添加角色和菜单的关联表
        updateRoleAndMenus(role,menuIds);

    }

    @Transactional
    public void updateRoleAndMenus(SysRoles role, List<Integer> menuIds) {
        SysRoleMenus roleMenus = null;
        Example example = new Example(SysRoleMenus.class);
        example.createCriteria().andEqualTo("roleId", role.getId());
        sysRoleMenusMapper.deleteByExample(example);
        for (Integer id : menuIds) {
            roleMenus = new SysRoleMenus();
            String s = String.valueOf(role.getId());
            roleMenus.setRoleId(Integer.parseInt(s));
            roleMenus.setMenuId(id);
            int i = this.sysRoleMenusMapper.insert(roleMenus);
            if (i != 1) {
                throw new TTMSException(ExceptionEnum.ROLERS_UPDATE_FAIL);
            }

        }
    }
    public List<Integer> getMenusIdByRoleId(Integer RoleId){
        //获取该角色id对应的所有对象
        //List<SysRoleMenus> sysRoleMenus1 = sysRoleMenusMapper.selectByExample(RoleId);
        List<SysMenus> menusListByRoleId = sysRoleMenusMapper.getMenusListByRoleId(RoleId);
        List<Integer> sys = new ArrayList<>();
        menusListByRoleId .forEach(n -> sys.add(n.getId()));
        return sys;
    }

    /**
    * 功能描述: <br>
    * 〈〉根据主键查询部门
    * @Param: [did]，[departmentid]
    * @Return: java.lang.Boolean
    * @Author: 吴彬
    * @Date: 16:53 16:53
     */
    public Boolean findDepartmentByid(Integer did, Integer departmentid){
        SysDepartment sysDepartment = getDeaprtmentByid(did);
        if(sysDepartment!=null) {
            //如果是父部门的话
            if (sysDepartment.getIsparent() == 1 ) {
                //查询所有的子部门
                List<SysDepartment> sysDepartments = queryAllDepartmentBypid(did);
                if (sysDepartments == null) {
                    return false;
                }
                if (sysDepartments.stream().map(sysDepartment1 -> sysDepartment1.getId()).collect(Collectors.toList()).contains(departmentid) || departmentid==1) {
                    return true;
                }else {
                    return false;
                }

            } else {
                SysDepartment deaprtmentByid = getDeaprtmentByid(sysDepartment.getParentid());
                //查询出部门
                List<SysDepartment> sysDepartments = queryAllDepartmentBypid(deaprtmentByid.getId());
                if (sysDepartments == null) {
                    return false;
                } else if (sysDepartments.stream().map(sysDepartment1 -> sysDepartment1.getId()).collect(Collectors.toList()).contains(departmentid) || departmentid==1 ) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
    /**
    * 功能描述: <br>
    * 〈〉根据主键查询部门
    * @Param: [pid]
    * @Return: com.ttms.Entity.SysDepartment
    * @Author: 吴彬
    * @Date: 17:20 17:20
     */
    public SysDepartment getDeaprtmentByid(Integer pid){
        SysDepartment sysDepartment = this.sysDepartmentMapper.selectByPrimaryKey(pid);
        return sysDepartment;
    }

    //通过roleid获取部门和角色名相关信息
    public List<TempRole> getRoleAndDePartInfoByRoleIds(List<Integer> rids){
        if(CollectionUtils.isEmpty(rids)){
            throw new TTMSException(ExceptionEnum.TEMPROLE_NOT_FOUND);
        }
        //封装
        StringBuffer sb = new StringBuffer();
        String idsstr = StringUtils.join(rids, ",");
        sb.append("(").append(idsstr).append(")");
        //查询
        List<TempRole> tempRoles = sysRolesMapper.getRoleAndDePartInfoByRoleId(sb.toString());
        if (CollectionUtils.isEmpty(tempRoles)) {
            throw new TTMSException(ExceptionEnum.TEMPROLE_NOT_FOUND);
        }
        return tempRoles;
    }

    /**
    * 功能描述: <br>
    * 〈〉查询用户所属的部门
    * @Param: [id]
    * @Return: java.lang.String
    * @Author: 吴彬
    * @Date: 10:25 10:25
     */
    public String selectUserDepartment(Integer id) {
        String s = this.sysDepartmentMapper.selectUserDepartment(id);
        return s;
    }

    public List<SysUser> getUsersByRoleId(Integer rid) {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("roleid",rid);
        List<SysUser> sysUsers = sysUserMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(sysUsers)) {
            throw new TTMSException(ExceptionEnum.ROLES_USER_NOT_FOUND);
        }
        return sysUsers;
    }
}
