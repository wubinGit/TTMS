package com.ttms.service.ProductManage.ServiceImpl;

import com.ttms.Entity.MesMessage;
import com.ttms.Entity.SysDepartment;
import com.ttms.Entity.SysRoles;
import com.ttms.Entity.SysUser;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Mapper.MesMessageMapper;
import com.ttms.service.ProductManage.IPubNotifyService;
import com.ttms.service.SystemManage.SysMenusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PubNotifyService implements IPubNotifyService {

    @Autowired
    private MesMessageMapper mesMessageMapper;

    @Autowired
    private SysMenusService sysMenusService;
    /**
    * 功能描述: <br>
    * 〈〉发布通知
    * @Param: [messageClassName, messageTitle, messageContent]
    * @Return: java.lang.Void
    * @Author: 吴彬
    * @Date: 10:49 10:49
     */
    @Override
    @Transactional
    public Void publicMsg(Integer messageType,Integer toid ,Integer valid,String messageTitle, String messageContent, SysUser user) {
        MesMessage mesMessage=new MesMessage();
        mesMessage.setValid((byte) (valid == 0? 0:1));
        mesMessage.setSendtype(messageType);
        if(messageType != 0){
            mesMessage.setToid(toid);
        }
        mesMessage.setMessagecontent(messageContent);
        mesMessage.setMessagetitle(messageTitle);
        mesMessage.setSenderid(user.getId());
        mesMessage.setUpdatetime(null);
        mesMessage.setSendtime(new Date());
        int i = this.mesMessageMapper.insert(mesMessage);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.MSG_RELEASE_FALI);
        }

        return null;
    }

    @Override
    public List<SysDepartment> getDepartmentByPid(Integer pid) {
        return sysMenusService.queryAllDepartmentBypid(pid);
    }

    @Override
    public List<SysRoles> getRolesByDepartmentId(Integer did) {
        return sysMenusService.getRolesByDepartmentId(did);
    }

    @Override
    public List<SysUser> getSysuserByRoleId(Integer rid) {
        return sysMenusService.getUsersByRoleId(rid);
    }
}
