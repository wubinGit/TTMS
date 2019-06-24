package com.ttms.Mapper;

import com.ttms.Entity.MesMessage;
import com.ttms.Vo.MesMessageVo;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MesMessageMapper extends Mapper<MesMessage> {

    @Select("  SELECT m.*,u.username FROM mes_message m,sys_user u WHERE u.id=m.senderId AND m.valid='1'")
    public List<MesMessageVo> querybyUserMsg();
}
