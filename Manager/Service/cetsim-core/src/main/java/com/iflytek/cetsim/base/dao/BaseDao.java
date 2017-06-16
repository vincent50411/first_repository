package com.iflytek.cetsim.base.dao;

import java.util.List;


/**
 * <b>类 名：</b>BaseDao<br/>
 * <b>类描述：</b>通用DAO接口<br/>
 * <b>创建人：</b>mailto:haoshen3@iflytek.com<br/>
 * <b>创建时间：</b>2016年8月24日 上午10:16:10<br/>
 * <b>修改人：</b>mailto:haoshen3@iflytek.com<br/>
 * <b>修改时间：</b>2016年8月24日 上午10:16:10<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version 1.0<br/>
 *
 */
public interface BaseDao<T> {

	/**
	 * 获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	T selectByPrimaryKey(Integer id);

	/**
	 * 添加数据
	 * 
	 * @param entity
	 * @return
	 */
	int insert(T entity);
	
	/**
	 * 更新数据
	 * 
	 * @param entity
	 * @return
	 */
	int updateByPrimaryKey(T entity);

    /**
     * 删除数据(按id)
     * 
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);
}
