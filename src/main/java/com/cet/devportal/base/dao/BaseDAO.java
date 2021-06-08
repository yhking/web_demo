package com.cet.devportal.base.dao;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * DAO访问基类，封装最常用的访问接口
 * @param <T>
 */
public interface BaseDAO<T> extends Mapper<T>, MySqlMapper<T> {

}