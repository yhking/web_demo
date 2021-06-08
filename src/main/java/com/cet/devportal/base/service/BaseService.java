package com.cet.devportal.base.service;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public abstract class BaseService<T> implements IService<T> {

	@Autowired
	protected Mapper<T> mapper;

	public Mapper<T> getMapper() {
		return mapper;
	}

	@Override
	public List<T> selectAll() {
		return mapper.selectAll();
	}

	@Override
	public T selectByKey(Object key) {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	@Transactional
	public int save(T entity) {
		return mapper.insert(entity);
	}

	@Override
	@Transactional
	public int delete(Object key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	@Transactional
	public int batchDelete(List<Long> list, String property, Class<T> clazz) {
		Example example = new Example(clazz);
		example.createCriteria().andIn(property, list);
		return this.mapper.deleteByExample(example);
	}

	@Override
	@Transactional
	public int batchDeleteWithStringVal(List<String> list, String property, Class<T> clazz) {
		Example example = new Example(clazz);
		example.createCriteria().andIn(property, list);
		return this.mapper.deleteByExample(example);
	}

	@Override
	@Transactional
	public int updateAll(T entity) {
		return mapper.updateByPrimaryKey(entity);
	}

	@Override
	@Transactional
	public int updateNotNull(T entity) {
		return mapper.updateByPrimaryKeySelective(entity);
	}

	@Override
	@Transactional
	public int updateByExampleNotNull(T entity, Object example) {
		return mapper.updateByExampleSelective(entity, example);
	}

	@Override
	public List<T> selectByExample(Object example) {
		return mapper.selectByExample(example);
	}

	@Override
	public T selectOne(T entity) {
		return this.mapper.selectOne(entity);
	}

	protected T findOneByProperty(String propertyName, Object propertyValue) {
		List<T> results = findByProperty(propertyName, propertyValue, 1);
		if(results != null && !results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}

	protected List<T> findByProperty(String propertyName, Object propertyValue, Integer count) {
		Class < T >  entityClass  =  (Class < T > ) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		Example example = new Example(entityClass);
		Example.Criteria criteria = example.createCriteria();
		if(propertyValue == null) {
			criteria.andIsNull(propertyName);
		} else {
			criteria.andEqualTo(propertyName, propertyValue);
		}
		RowBounds rowBounds = new RowBounds(0, count);
		return mapper.selectByExampleAndRowBounds(example, rowBounds);
	}

	protected Example createExampleOnEqualCondition(Class exampleClass, List<Map.Entry<String, Object>> properties) {
		Example example = new Example(exampleClass);
		Example.Criteria criteria = example.createCriteria();

		for(Map.Entry<String, Object> entry : properties) {
			if(entry.getValue() == null) {
				criteria.andIsNull(entry.getKey());
			} else {
				criteria.andEqualTo(entry.getKey(), entry.getValue());
			}
		}

		return example;
	}
}
