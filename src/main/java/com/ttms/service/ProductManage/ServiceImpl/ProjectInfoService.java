package com.ttms.service.ProductManage.ServiceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ttms.Entity.ProProject;
import com.ttms.Entity.SysDepartment;
import com.ttms.Entity.SysUser;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Mapper.ProProjectMapper;
import com.ttms.Mapper.SysDepartmentMapper;
import com.ttms.Vo.PageResult;
import com.ttms.service.ProductManage.ICreateProductService;
import com.ttms.service.ProductManage.IGroupService;
import com.ttms.service.ProductManage.IProjectService;
import com.ttms.service.SystemManage.SysMenusService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectInfoService implements IProjectService {

    @Autowired
    private ProProjectMapper proProjectMapper;

    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;

    @Autowired
    private SysMenusService sysMenusService;

    @Autowired
    private IGroupService groupService;

    @Autowired
    private ICreateProductService productService;



    //根据部门名称查询部门
    private SysDepartment getSysDepartment(String departName,Integer departId) {
        Example departExample = new Example(SysDepartment.class);
        Example.Criteria criteria = departExample.createCriteria();
        if(StringUtils.isNotBlank(departName)) {
            criteria.andEqualTo("departmentname", departName);
        }else if(StringUtils.isNotBlank(String.valueOf(departId))){
            criteria.andEqualTo("id", departId);
        }
        List<SysDepartment> departList = this.sysDepartmentMapper.selectByExample(departExample);
        if (CollectionUtils.isEmpty(departList)) {
            throw new TTMSException(ExceptionEnum.GROUP_NOT_FOUND);
        }
        return departList.get(0);
    }
    //新增项目 表单提交的数据有 项目编号 . 名称 起始日期--结束日期. 部门(部门需要根据输入的)，备注 没有的数据自己设置
    /**
     * 功能描述: <br>
     * 〈〉新增项目
     * @Param: [project]
     * @Return: java.lang.Void
     * @Author: 吴彬
     * @Date: 15:57 15:57
     */
    @Override
    @Transactional
    public Void addProject(ProProject project, SysUser user, Integer departmentId) {

        project.setCreatetime(new Date());
        project.setCreateuserid(user.getId());
        project.setUpdatetime(null);
        project.setDepartmentid(departmentId);
        project.setValid((byte)1);
        int i = this.proProjectMapper.insert(project);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.PROJECT_INSERT_FAIL);
        }
        return null;
    }

    /**
     * 功能描述: <br>
     * 〈〉修改项目
     * @Param: [project, departName]
     * @Return: java.lang.Void
     * @Author: 吴彬
     * @Date: 19:33 19:33
     */
    @Transactional
    public Void editProject(ProProject project, SysUser user, Integer departId) {
        //更新产品中的项目名
        productService.updateRedundancyWordProjectNameProduct(project.getId(),project.getProjectname());
        project.setUpdatetime(new Date());
        project.setDepartmentid(departId);
        project.setValid((byte)1);
        project.setUpdateuserid(user.getId());
        int i = this.proProjectMapper.updateByPrimaryKeySelective(project);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.PROJECT_UPDATE_FAIL);
        }
        //更新团的冗余字段
        groupService.updateRedundancyWordProjectName(project.getId(),project.getProjectname());
        return null;
    }



    @Override
    public PageResult<ProProject> getAllProjectByPage(String projectNumber, String projectName, int departmentid,
                                                      Date startTime, Date endTime, int valid, int page, int rows) {
        //分页
        PageHelper.startPage(page,rows);
        //查询条件
        Example example = new Example(ProProject.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(projectNumber)){
            criteria.andLike("projectnumber","%"+projectNumber+"%");
        }
        if(!StringUtils.isEmpty(projectName)){
            criteria.andLike("projectname","%"+projectName+"%");
        }
        if(departmentid != -1){
            criteria.andEqualTo("departmentid",departmentid);
        }
        if(startTime != null ){
            criteria.andGreaterThan("starttime",startTime);
        }
        if(endTime != null ){
            criteria.andLessThan("endtime",endTime);
        }
        if(valid != -1){
            criteria.andEqualTo("valid",valid);
        }
        //查询select * from user where age > 1 limit  5,10;
        List<ProProject> proProjects = proProjectMapper.selectByExample(example);
        //创建返回结果
        PageResult<ProProject> result = new PageResult<>();
        PageInfo<ProProject> pageInfo = new PageInfo<>(proProjects);
        //封装部门名称
        //获取所有相关的部门id
        Set<Integer> departmentidsSet = proProjects.stream().map(ProProject::getDepartmentid).collect(Collectors.toSet());
        ArrayList<Integer> departIdList = new ArrayList<>(departmentidsSet);
        List<SysDepartment> sysDepartments = sysMenusService.getDepartmentsByIds(departIdList);
        //转化为map(id , String)
       Map<Integer, String> idNameMap = sysDepartments.stream().collect(Collectors.toMap(SysDepartment::getId, SysDepartment::getDepartmentname));
       for (ProProject proProject:proProjects){
           proProject.setDepartmentName(idNameMap.get(proProject.getDepartmentid()));
       }
        result.setItems(proProjects);
        result.setTotal(pageInfo.getTotal());
        result.setTotalPage(pageInfo.getPages());
        return result;
    }

    @Override
    public List<SysDepartment> getSubdepartmentProductDepartment() {
        //查询出生产部门
        SysDepartment productDepartment = sysMenusService.getDepartmentsByCriteria(0, "产品部").get(0);
        //查询出其子部门
        return  sysMenusService.getDepartmentsByCriteria(productDepartment.getId(), null);
    }

    /*
    * 功能描述: <br>
    * 〈〉判断角色是否为产品经理
    * @Param: [username]
    * @Return: java.lang.Boolean
    * @Author: 吴彬
    * @Date: 9:35 9:35
     */
    @Override
    public Boolean judge(String username) {
        String s = this.proProjectMapper.judgeRoleIsProduceManager(username);
        if(s.equals("产品经理")){
            return  true;
        }
        return false;
    }

    /**
    * 功能描述: <br>
    * 〈〉根据id启用或禁用项目
    * @Param: [pid]
    * @Return: void
    * @Author: 吴彬
    * @Date: 10:33 10:33
     */
    @Override
    public void ValidOrInvalidProject(Integer pid) {
        ProProject projectById = findProjectById(pid);
        projectById.setValid((byte)(projectById.getValid() ^ 1));
        int i = this.proProjectMapper.updateByPrimaryKey(projectById);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.PROJECT_PROHIBIT_OR_ENABLE_FAIL);
        }
        return;
    }

    /**
    * 功能描述: <br>
    * 〈〉根据id查询项目
    * @Param: [pid]
    * @Return: com.ttms.Entity.ProProject
    * @Author: 吴彬
    * @Date: 10:36 10:36
     */
    @Override
    public ProProject findProjectById(Integer pid) {
        ProProject proProject = this.proProjectMapper.selectByPrimaryKey(pid);
        if(proProject==null){
            throw new TTMSException(ExceptionEnum.PROJECT_NOT_EXIST);
        }
        return proProject;
    }

}