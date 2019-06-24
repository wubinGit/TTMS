package com.ttms.service.ProductManage.ServiceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ttms.Entity.MesMessage;
import com.ttms.Entity.SysUser;
import com.ttms.Enum.ExceptionEnum;
import com.ttms.Exception.TTMSException;
import com.ttms.Mapper.MesMessageMapper;
import com.ttms.Vo.PageResult;
import com.ttms.service.ProductManage.INotifyManageService;
import com.ttms.service.SystemManage.SysMenusService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class NotifyManageService implements INotifyManageService {
    @Autowired
    private MesMessageMapper mesMessageMapper;


    @Autowired
    private SysMenusService sysMenusService;

    /**
    * 功能描述: <br>
    * 〈〉查询所有消息
    * @Param: []
    * @Return: java.util.List<com.ttms.Entity.MesMessage>
    * @Author: 吴彬
    * @Date: 10:02 10:02
     */
    @Override
    public List<MesMessage> queryAllnew(int size) {
        PageHelper.startPage(1,size);
        Example example=new Example(MesMessage.class);
        example.setOrderByClause("sendTime DESC");
        example.createCriteria().andEqualTo("sendtype",0).andEqualTo("valid",1);
        List<MesMessage> mesMessages = this.mesMessageMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(mesMessages)) {
            throw new TTMSException(ExceptionEnum.MESSAGE_DID_NOT_EXIST);
        }
        return mesMessages;
    }

    /**
    * 功能描述: <br>
    * 〈〉查询所有有关自己的通知
    * @Param: [user]
    * @Return: java.util.List<com.ttms.Entity.MesMessage>
    * @Author: 吴彬
    * @Date: 10:06 10:06
     */
    @Override
    public List<MesMessage> querybyUser(SysUser user) {
        Example example=new Example(MesMessage.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("toid", user.getId());
        criteria.andEqualTo("valid", 1);
        List<MesMessage> mesMessages = this.mesMessageMapper.selectByExample(example);
        return mesMessages;
    }

    /**
    * 功能描述: <br>
    * 〈〉更新状态
    * @Param: [mid]
    * @Return: java.lang.Void
    * @Author: 吴彬
    * @Date: 10:15 10:15
     */
    @Override
    @Transactional
    public Void updateState(Integer mid) {
        MesMessage mesMessage = this.mesMessageMapper.selectByPrimaryKey(mid);
        mesMessage.setValid((byte)(mesMessage.getValid() ^ 1));
        int i = this.mesMessageMapper.updateByPrimaryKey(mesMessage);
        if(i!=1){
            throw new TTMSException(ExceptionEnum.MSG_UPDATE_FALI);
        }
        return null;
    }

    /**
    * 功能描述: <br>
    * 〈〉分页查询消息
    * @Param: [page, rows, messageclassname, messagetitle, sendName]
    * @Return: com.ttms.Vo.PageResult<com.ttms.Entity.MesMessage>
    * @Author: 吴彬
    * @Date: 8:34 8:34
     */
    @Override
    public PageResult<MesMessage> queryAllnewPage(Integer page, Integer rows, Integer messageclassname, String messagetitle, String sendName) {
        PageHelper.startPage(page, rows);
        Example example=new Example(MesMessage.class);
        Example.Criteria criteria = example.createCriteria();
        if(messageclassname!=null) {
            criteria.andEqualTo("sendtype", messageclassname);
        }
        if(messagetitle!=null){
            criteria.andLike("messagetitle", "%"+messagetitle+"%");
        }
        criteria.andIsNull("toid");
        // 根据发布人姓名查询id
   /*     SysUser user = this.sysMenusService.getUserByUserName(sendName);
        if(user!=null){
            criteria.andEqualTo("senderid", user.getId());
        }*/
        SysUser curUser  = (SysUser) SecurityUtils.getSubject().getPrincipal();
        String userDepartment = this.sysMenusService.selectUserDepartment(curUser.getId());
        List<MesMessage> mesMessages = this.mesMessageMapper.selectByExample(example);
        for(MesMessage msg:mesMessages){
         // if(msg.getToid()!=null) continue;
            msg.setSenderName(curUser.getUsername());
            msg.setUserDepartment(userDepartment);
        }
        PageInfo<MesMessage> info=new PageInfo<>(mesMessages);
        PageResult<MesMessage> result=new PageResult<>();
        result.setItems(info.getList());
        result.setTotal(info.getTotal());
        result.setTotalPage(info.getPages());
        return result;
    }

}
